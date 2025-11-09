package com.epia.service;

import com.epia.domain.*;
import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.LifecycleChecklistDetailDto;
import com.epia.repo.CompanyRepo;
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
public class LifecycleService {

    private final CompanyRepo companyRepo;

    public LifecycleService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    // ===== 처리업무 =====
    public List<ProcessingTask> getTasks(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        if (company.processingTasks == null) {
            company.processingTasks = new ArrayList<>();
        }
        return company.processingTasks;
    }

    // ===== 체크리스트 =====
    public List<ChecklistItem> getChecklists(String companyId, ObjectId taskId) {
        Company comp = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 정보를 찾을 수 없습니다"));

        return comp.processingTasks.stream()
                .filter(task -> task.id.equals(taskId))
                .findFirst()
                .map(task -> task.lifecycleChecklist)
                .orElseGet(ArrayList::new);
    }

    public void saveChecklists(String companyId, ObjectId taskId, List<ChecklistItem> items) {
        Company comp = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 정보를 찾을 수 없습니다"));

        ProcessingTask targetTask = comp.processingTasks.stream()
                .filter(task -> task.id.equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "처리업무를 찾을 수 없습니다"));

        targetTask.lifecycleChecklist = items;
        companyRepo.save(comp);
    }

    /**
     * 체크리스트와 평가항목을 조인하여 상세 정보 반환
     */
    public List<LifecycleChecklistDetailDto> getChecklistsWithDetails(
            String companyId,
            ObjectId taskId,     // null이면 모든 업무
            List<String> statusFilter) {  // ["미이행", "부분이행"]

        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        // evaluationItems를 Map으로 변환 (빠른 검색을 위해)
        Map<String, EvaluationItem> evalMap = company.evaluationItems.stream()
                .collect(Collectors.toMap(item -> item.no, item -> item));

        List<LifecycleChecklistDetailDto> result = new ArrayList<>();

        // taskId가 null이면 모든 업무, 아니면 특정 업무만
        Stream<ProcessingTask> taskStream = company.processingTasks.stream();
        if (taskId != null) {
            taskStream = taskStream.filter(task -> task.id.equals(taskId));
        }

        taskStream.forEach(task -> {
            if (task.lifecycleChecklist != null) {
                task.lifecycleChecklist.forEach(item -> {
                    // 상태 필터링
                    if (statusFilter != null && !statusFilter.isEmpty()) {
                        if (item.status == null || !statusFilter.contains(item.status)) {
                            return;
                        }
                    }

                    EvaluationItem evalItem = evalMap.get(item.no);
                    if (evalItem != null) {
                        LifecycleChecklistDetailDto dto = new LifecycleChecklistDetailDto();
                        dto.taskId = task.id.toHexString();
                        dto.taskName = task.taskName;
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

    // ===== 개선가이드 (ReadOnly) =====
    public Map<String, Object> getImprovementsMap(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();

        // evaluationItems를 Map으로 변환
        Map<String, EvaluationItem> evalMap = company.evaluationItems.stream()
                .collect(Collectors.toMap(item -> item.no, item -> item));

        if (company.processingTasks != null) {
            for (ProcessingTask task : company.processingTasks) {
                if (task.lifecycleChecklist != null) {
                    for (ChecklistItem item : task.lifecycleChecklist) {
                        // 부분이행 또는 미이행만 포함
                        if (item.status != null &&
                                (item.status.equals("부분이행") || item.status.equals("미이행"))) {

                            String key = task.id.toHexString() + "-" + item.no;
                            EvaluationItem evalItem = evalMap.get(item.no);

                            if (evalItem != null) {
                                Map<String, String> improvementMap = new HashMap<>();
                                improvementMap.put("relatedLaw", evalItem.law != null ? evalItem.law : "");
                                improvementMap.put("riskFactor", evalItem.riskFactors != null ? evalItem.riskFactors : "");
                                improvementMap.put("improvementPlan", evalItem.improvementGuides != null ? evalItem.improvementGuides : "");

                                result.put(key, improvementMap);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("✅ 개선가이드 조회: " + result.size() + "개");
        return result;
    }

    // ===== 조치계획 =====
    public Map<String, Object> getActionPlansMap(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();

        if (company.processingTasks != null) {
            for (ProcessingTask task : company.processingTasks) {
                if (task.actionPlans != null) {
                    for (ActionPlan plan : task.actionPlans) {
                        // key를 taskId-no 형식으로 생성
                        String key = task.id.toHexString() + "-" + plan.no;

                        Map<String, String> planMap = new HashMap<>();
                        planMap.put("taskId", task.id.toHexString());
                        planMap.put("taskName", task.taskName);
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

        if (company.processingTasks == null) {
            company.processingTasks = new ArrayList<>();
        }

        // 각 업무별로 조치계획을 분류하여 저장 (taskId 기반)
        Map<String, List<ActionPlan>> taskPlansMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : actionPlansMap.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, String> planData = (Map<String, String>) entry.getValue();

            String taskId = planData.get("taskId");
            String code = planData.get("code");

            ActionPlan plan = new ActionPlan();
            plan.no = code;
            plan.title = planData.get("actionPlan");
            plan.period = planData.get("actionPeriod");
            plan.department = planData.get("department");
            plan.owner = planData.get("manager");
            plan.date = planData.get("actionDate");

            taskPlansMap.computeIfAbsent(taskId, k -> new ArrayList<>()).add(plan);
        }

        // 각 업무의 조치계획 업데이트
        for (ProcessingTask task : company.processingTasks) {
            List<ActionPlan> plans = taskPlansMap.get(task.id.toHexString());
            if (plans != null) {
                // 기존 조치계획 중 업데이트되지 않은 항목은 유지
                if (task.actionPlans == null) {
                    task.actionPlans = new ArrayList<>();
                }

                // 업데이트: 동일한 no를 가진 항목은 교체, 없으면 추가
                for (ActionPlan newPlan : plans) {
                    boolean found = false;
                    for (int i = 0; i < task.actionPlans.size(); i++) {
                        if (task.actionPlans.get(i).no.equals(newPlan.no)) {
                            task.actionPlans.set(i, newPlan);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        task.actionPlans.add(newPlan);
                    }
                }

                System.out.println("✅ " + task.taskName + " 조치계획 저장: " + plans.size() + "개");
            }
        }

        companyRepo.save(company);
        System.out.println("✅ 전체 조치계획 저장 완료");
    }
}