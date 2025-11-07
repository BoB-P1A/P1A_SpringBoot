package com.epia.service;

import com.epia.domain.*;
import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.TechnicalChecklistDetailDto;
import com.epia.repo.*;
import com.epia.seq.SequenceService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TechnicalService {

    private final CompanyRepo companyRepo;
    private final TechnicalImprovementRepo improvementRepo;
    private final SequenceService seq;

    public TechnicalService(
            CompanyRepo companyRepo,
            TechnicalImprovementRepo improvementRepo,
            SequenceService seq
    ) {
        this.companyRepo = companyRepo;
        this.improvementRepo = improvementRepo;
        this.seq = seq;
    }

    // ===== 시스템(대상) =====
    public List<TechnicalSystem> getSystems(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.technicalSystems == null) {
            company.technicalSystems = new ArrayList<>();
        }
        return company.technicalSystems;
    }

    public TechnicalSystem addSystem(String companyId, TechnicalSystem s) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.technicalSystems == null) {
            company.technicalSystems = new ArrayList<>();
        }

        // ObjectId 자동 생성
        if (s.id == null) {
            s.id = new ObjectId();
        }

        // 평가항목의 no만 저장 (area가 "2."로 시작하는 항목)
        List<String> technicalNos = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("2."))
                .map(item -> item.no)
                .collect(Collectors.toList());

        s.technicalChecklist = technicalNos.stream()
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

        company.technicalSystems.add(s);
        companyRepo.save(company);

        System.out.println(" 저장 완료: " + s.systemName + " (체크리스트: " + s.technicalChecklist.size() + "개)");
        return s;
    }

    public TechnicalSystem updateSystem(ObjectId id, String name) {
        List<Company> companies = companyRepo.findAll();

        for (Company company : companies) {
            if (company.technicalSystems != null) {
                for (TechnicalSystem system : company.technicalSystems) {
                    if (system.id.equals(id)) {
                        system.systemName = name;
                        companyRepo.save(company);

                        System.out.println(" 시스템명 변경: → " + name);
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
            if (company.technicalSystems != null) {
                boolean removed = company.technicalSystems.removeIf(sys -> sys.id.equals(id));

                if (removed) {
                    companyRepo.save(company);
                    System.out.println(" 시스템 삭제 완료 (ID: " + id + ")");
                    return;
                }
            }
        }

        throw new RuntimeException("시스템을 찾을 수 없습니다.");
    }

    // ===== 체크리스트 =====
    public List<ChecklistItem> getChecklists(String companyId, String systemName) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        return company.technicalSystems.stream()
                .filter(sys -> sys.systemName.equals(systemName))
                .findFirst()
                .map(sys -> sys.technicalChecklist)
                .orElse(new ArrayList<>());
    }

    public void saveChecklists(String companyId, String systemName, List<ChecklistItem> items) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        for (TechnicalSystem system : company.technicalSystems) {
            if (system.systemName.equals(systemName)) {
                system.technicalChecklist = items;
                companyRepo.save(company);
                System.out.println(" 체크리스트 저장: " + items.size() + "개");
                return;
            }
        }

        throw new RuntimeException("시스템을 찾을 수 없습니다: " + systemName);
    }

    // ===== 조치계획 =====
    public List<ActionPlan> getActionPlans(String companyId, String systemName) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        return company.technicalSystems.stream()
                .filter(sys -> sys.systemName.equals(systemName))
                .findFirst()
                .map(sys -> sys.actionPlans)
                .orElse(new ArrayList<>());
    }

    public void saveActionPlans(String companyId, String systemName, List<ActionPlan> plans) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        for (TechnicalSystem system : company.technicalSystems) {
            if (system.systemName.equals(systemName)) {
                system.actionPlans = plans;
                companyRepo.save(company);
                System.out.println(" 조치계획 저장: " + plans.size() + "개");
                return;
            }
        }

        throw new RuntimeException("시스템을 찾을 수 없습니다: " + systemName);
    }

    // ===== 개선가이드 =====
    public List<TechnicalImprovement> getImprovements(String companyId) {
        return improvementRepo.findByCompanyId(companyId);
    }

    /**
     * 체크리스트와 평가항목을 조인하여 상세 정보 반환
     */
    public List<TechnicalChecklistDetailDto> getChecklistsWithDetails(
            String companyId,
            String systemName,      // null이면 모든 시스템
            List<String> statusFilter) {  // ["미이행", "부분이행"]

        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        // evaluationItems를 Map으로 변환 (빠른 검색을 위해)
        Map<String, EvaluationItem> evalMap = company.evaluationItems.stream()
                .collect(Collectors.toMap(item -> item.no, item -> item));

        List<TechnicalChecklistDetailDto> result = new ArrayList<>();

        // 모든 시스템 순회
        for (TechnicalSystem system : company.technicalSystems) {
            // systemName 필터 (있으면 해당 시스템만)
            if (systemName != null && !system.systemName.equals(systemName)) {
                continue;
            }

            // 체크리스트 순회
            for (ChecklistItem item : system.technicalChecklist) {
                // status 필터 (미이행, 부분이행만)
                if (statusFilter != null && !statusFilter.isEmpty()) {
                    if (item.status == null || !statusFilter.contains(item.status)) {
                        continue;
                    }
                }

                // 평가항목 찾기
                EvaluationItem evalItem = evalMap.get(item.no);

                // DTO 생성
                TechnicalChecklistDetailDto dto = new TechnicalChecklistDetailDto();
                dto.systemName = system.systemName;
                dto.no = item.no;
                dto.status = item.status;
                dto.evidence = item.evidence;
                dto.files = item.files;

                if (evalItem != null) {
                    dto.item = evalItem.item;  // 질의문
                    dto.law = evalItem.law;    // 관련법률
                    dto.riskFactors = evalItem.riskFactors;  // 침해요인
                    dto.improvementGuides = evalItem.improvementGuides;  // 개선가이드
                    dto.subField = evalItem.subField;
                    dto.area = evalItem.area;
                    dto.field = evalItem.field;
                }

                result.add(dto);
            }
        }

        return result;
    }

    public void saveImprovements(TechnicalImprovement body) {
        improvementRepo.save(body);
    }
}