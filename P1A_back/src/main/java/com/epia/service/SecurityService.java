
package com.epia.service;

import com.epia.domain.*;
import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.SecurityChecklistDetailDto;
import com.epia.repo.CompanyRepo;
import com.epia.repo.SecurityImprovementRepo;
import com.epia.support.ApiException;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SecurityService {

    private final CompanyRepo companyRepo;
    private final SecurityImprovementRepo improvementRepo;
    private final HistoryLogService historyLogService;

    public SecurityService(
            CompanyRepo companyRepo,
            SecurityImprovementRepo improvementRepo,
            HistoryLogService historyLogService
    ) {
        this.companyRepo = companyRepo;
        this.improvementRepo = improvementRepo;
        this.historyLogService = historyLogService;
    }

    // ===== 시스템(대상) =====
    public List<SecuritySystem> getSystems(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.securitySystems == null) {
            company.securitySystems = new ArrayList<>();
        }
        return company.securitySystems;
    }

    public SecuritySystem addSystem(String companyId, SecuritySystem s) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.securitySystems == null) {
            company.securitySystems = new ArrayList<>();
        }

        // ObjectId 자동 생성
        if (s.id == null) {
            s.id = new ObjectId();
        }

        // 평가항목의 no만 저장 (area가 "3."로 시작하는 항목)
        List<String> securityNos = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("3."))
                .map(item -> item.no)
                .collect(Collectors.toList());

        s.securityChecklist = securityNos.stream()
                .map(no -> {
                    ChecklistItem checklistItem = new ChecklistItem();
                    checklistItem.no = no;  // ← no만 저장
                    checklistItem.status = null;
                    checklistItem.evidence = "";
                    checklistItem.files = new ArrayList<>();
                    return checklistItem;
                })
                .collect(Collectors.toList());

        // 조치계획 초기화
        if (s.actionPlans == null) {
            s.actionPlans = new ArrayList<>();
        }

        company.securitySystems.add(s);
        companyRepo.save(company);

        System.out.println("✅ 보안시스템 저장 완료: " + s.systemName + " (체크리스트: " + s.securityChecklist.size() + "개)");
        return s;
    }

    public SecuritySystem updateSystem(ObjectId id, String name) {
        List<Company> companies = companyRepo.findAll();

        for (Company company : companies) {
            if (company.securitySystems != null) {
                for (SecuritySystem system : company.securitySystems) {
                    if (system.id.equals(id)) {
                        system.systemName = name;
                        companyRepo.save(company);

                        System.out.println("✅ 보안시스템명 변경: → " + name);
                        return system;
                    }
                }
            }
        }

        throw new RuntimeException("시스템을 찾을 수 없습니다.");
    }

    public void deleteSystem(ObjectId id) {
        List<Company> companies = companyRepo.findAll();

        for (Company company : companies) {
            if (company.securitySystems != null) {
                boolean removed = company.securitySystems.removeIf(sys -> sys.id.equals(id));

                if (removed) {
                    companyRepo.save(company);
                    System.out.println("✅ 보안시스템 삭제 완료 (ID: " + id + ")");
                    return;
                }
            }
        }

        throw new RuntimeException("시스템을 찾을 수 없습니다.");
    }

    // ===== 체크리스트 =====
    public List<ChecklistItem> getChecklists(String companyId, ObjectId systemId) {
        Company comp = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 정보를 찾을 수 없습니다"));

        return comp.securitySystems.stream()
                .filter(sys -> sys.id.equals(systemId))
                .findFirst()
                .map(sys -> sys.securityChecklist)
                .orElseGet(ArrayList::new);
    }

    public void saveChecklists(String companyId, ObjectId systemId, List<ChecklistItem> items, Account currentAccount) {
        Company comp = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 정보를 찾을 수 없습니다"));

        SecuritySystem targetSystem = comp.securitySystems.stream()
                .filter(sys -> sys.id.equals(systemId))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "시스템을 찾을 수 없습니다"));

        // 기존 체크리스트를 Map으로 변환 (변경사항 추적용)
        Map<String, ChecklistItem> previousItemsMap = new HashMap<>();
        if (targetSystem.securityChecklist != null) {
            for (ChecklistItem item : targetSystem.securityChecklist) {
                previousItemsMap.put(item.no, item);
            }
        }

        targetSystem.securityChecklist = items;
        companyRepo.save(comp);

        // 변경 이력 로깅 (currentAccount 전달)
        logChecklistChanges(companyId, targetSystem, items, previousItemsMap, currentAccount);
    }

    /**
     * 체크리스트와 평가항목을 조인하여 상세 정보 반환
     */
    public List<SecurityChecklistDetailDto> getChecklistsWithDetails(
            String companyId,
            ObjectId systemId,     // null이면 모든 시스템
            List<String> statusFilter) {  // ["미이행", "부분이행"]

        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        // evaluationItems를 Map으로 변환 (빠른 검색을 위해)
        Map<String, EvaluationItem> evalMap = company.evaluationItems.stream()
                .collect(Collectors.toMap(item -> item.no, item -> item));

        List<SecurityChecklistDetailDto> result = new ArrayList<>();

        // systemId가 null이면 모든 시스템, 아니면 특정 시스템만
        Stream<SecuritySystem> systemStream = company.securitySystems.stream();
        if (systemId != null) {
            systemStream = systemStream.filter(sys -> sys.id.equals(systemId));
        }

        systemStream.forEach(sys -> {
            if (sys.securityChecklist != null) {
                sys.securityChecklist.forEach(item -> {
                    // 상태 필터링
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        if (item.status == null || !statusFilter.contains(item.status)) {
                            return;
                        }
                    }

                    EvaluationItem evalItem = evalMap.get(item.no);
                    if (evalItem != null) {
                        SecurityChecklistDetailDto dto = new SecurityChecklistDetailDto();
                        dto.systemId = sys.id.toHexString();
                        dto.systemName = sys.systemName;
                        dto.no = item.no;
                        dto.status = item.status;
                        dto.evidence = item.evidence;
                        dto.files = item.files;
                        dto.item = evalItem.item;
                        dto.law = evalItem.law;
                        dto.riskFactors = evalItem.riskFactors;
                        dto.improvementGuides = evalItem.improvementGuides;
                        dto.subField = evalItem.subField;
                        dto.area = evalItem.area;
                        dto.field = evalItem.field;
                        result.add(dto);
                    }
                });
            }
        });

        return result;
    }

    // ===== 개선가이드 =====
    public List<SecurityImprovement> getImprovements(String companyId) {
        return improvementRepo.findByCompanyId(companyId);
    }

    public void saveImprovements(SecurityImprovement body) {
        improvementRepo.save(body);
    }

    // ===== 조치계획 =====
    public Map<String, Object> getActionPlansMap(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();

        if (company.securitySystems != null) {
            for (SecuritySystem system : company.securitySystems) {
                if (system.actionPlans != null) {
                    for (ActionPlan plan : system.actionPlans) {
                        // key를 systemId-no 형식으로 변경
                        String key = system.id.toHexString() + "-" + plan.no;

                        Map<String, String> planMap = new HashMap<>();
                        planMap.put("systemId", system.id.toHexString());
                        planMap.put("systemName", system.systemName);
                        planMap.put("code", plan.no);
                        planMap.put("actionPlan", plan.title != null ? plan.title : "");
                        planMap.put("actionPeriod", plan.period != null ? plan.period : "");
                        planMap.put("department", plan.department != null ? plan.department : "");
                        planMap.put("manager", plan.owner != null ? plan.owner : "");
                        planMap.put("actionDate", plan.date != null ? plan.date : "");

                        result.put(key, planMap);
                    }
                }
            }
        }

        System.out.println("✅ 조치계획 조회: " + result.size() + "개");
        return result;
    }

    public void saveActionPlansFromMap(String companyId, Map<String, Object> actionPlansMap) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.securitySystems == null) {
            company.securitySystems = new ArrayList<>();
        }

        // 각 시스템별로 조치계획을 분류하여 저장 (systemId 기반)
        Map<String, List<ActionPlan>> systemPlansMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : actionPlansMap.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, String> planData = (Map<String, String>) entry.getValue();

            String systemId = planData.get("systemId");
            String code = planData.get("code");

            ActionPlan plan = new ActionPlan();
            plan.no = code;
            plan.title = planData.get("actionPlan");
            plan.period = planData.get("actionPeriod");
            plan.department = planData.get("department");
            plan.owner = planData.get("manager");
            plan.date = planData.get("actionDate");

            systemPlansMap.computeIfAbsent(systemId, k -> new ArrayList<>()).add(plan);
        }

        // 각 시스템의 조치계획 업데이트
        for (SecuritySystem system : company.securitySystems) {
            List<ActionPlan> plans = systemPlansMap.get(system.id.toHexString());
            if (plans != null) {
                // 기존 조치계획 중 업데이트되지 않은 항목은 유지
                if (system.actionPlans == null) {
                    system.actionPlans = new ArrayList<>();
                }

                // 업데이트: 동일한 no를 가진 항목은 교체, 없으면 추가
                for (ActionPlan newPlan : plans) {
                    boolean found = false;
                    for (int i = 0; i < system.actionPlans.size(); i++) {
                        if (system.actionPlans.get(i).no.equals(newPlan.no)) {
                            system.actionPlans.set(i, newPlan);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        system.actionPlans.add(newPlan);
                    }
                }

                System.out.println("✅ " + system.systemName + " 조치계획 저장: " + plans.size() + "개");
            }
        }

        companyRepo.save(company);
        System.out.println("✅ 전체 조치계획 저장 완료");
    }

    /**
     * 체크리스트 변경 이력을 HistoryLog에 저장
     */
    private void logChecklistChanges(
            String companyId,
            SecuritySystem system,
            List<ChecklistItem> newItems,
            Map<String, ChecklistItem> previousItemsMap,
            Account account) {

        for (ChecklistItem newItem : newItems) {
            ChecklistItem previousItem = previousItemsMap.get(newItem.no);

            historyLogService.logChecklistChange(
                    companyId,
                    "security",
                    system.id.toHexString(),
                    system.systemName,
                    newItem.no,
                    previousItem,
                    newItem,
                    account != null ? account.id : "SYSTEM",
                    account != null ? account.loginId : "system",
                    account != null ? account.name : "시스템"
            );
        }
    }
}