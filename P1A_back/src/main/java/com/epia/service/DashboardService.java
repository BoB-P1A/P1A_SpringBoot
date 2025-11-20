package com.epia.service;

import com.epia.domain.*;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.repo.CompanyRepo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final CompanyRepo companyRepo;

    public DashboardService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    /**
     * 대시보드 통계 데이터 반환
     */
    public Map<String, Object> getStats(String companyId) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();

        // 1. 영역별 이행률 (도넛 차트용)
        result.put("areaRates", calculateAreaRates(company));

        // 2. 분야별 이행률 (레이더 차트용)
        result.put("fieldRates", calculateFieldRates(company));

        // 3. 세부분야별 이행률 (레이더 차트용)
        result.put("subFieldRates", calculateSubFieldRates(company));

        return result;
    }

    /**
     * 영역별 이행률 계산
     */
    private Map<String, Object> calculateAreaRates(Company company) {
        Map<String, Object> areaRates = new HashMap<>();

        // Lifecycle (영역이 1로 시작)
        List<ChecklistItem> lifecycleChecklists = getAllLifecycleChecklists(company);
        double lifecycleRate = calculateImplementationRate(lifecycleChecklists);
        areaRates.put("lifecycle", lifecycleRate);

        // Admin (영역이 2로 시작)
        List<ChecklistItem> adminChecklists = getAllTechnicalChecklists(company);
        double adminRate = calculateImplementationRate(adminChecklists);
        areaRates.put("admin", adminRate);

        // 보안성 검토 (영역이 3으로 시작)
        List<ChecklistItem> securityChecklists = getAllSecurityChecklists(company);
        double securityRate = calculateImplementationRate(securityChecklists);
        areaRates.put("security", securityRate);

        return areaRates;
    }

    /**
     * 분야별 이행률 계산
     */
    private Map<String, List<Map<String, Object>>> calculateFieldRates(Company company) {
        Map<String, List<Map<String, Object>>> fieldRates = new HashMap<>();

        // Lifecycle 분야별
        List<EvaluationItem> lifecycleItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("1."))
                .collect(Collectors.toList());
        List<ChecklistItem> lifecycleChecklists = getAllLifecycleChecklists(company);
        fieldRates.put("lifecycle", calculateRatesByField(lifecycleChecklists, lifecycleItems));

        // Admin 분야별
        List<EvaluationItem> adminItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("2."))
                .collect(Collectors.toList());
        List<ChecklistItem> adminChecklists = getAllTechnicalChecklists(company);
        fieldRates.put("admin", calculateRatesByField(adminChecklists, adminItems));

        // Security 분야별
        List<EvaluationItem> securityItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("3."))
                .collect(Collectors.toList());
        List<ChecklistItem> securityChecklists = getAllSecurityChecklists(company);
        fieldRates.put("security", calculateRatesByField(securityChecklists, securityItems));

        return fieldRates;
    }

    /**
     * 세부분야별 이행률 계산
     */
    private Map<String, List<Map<String, Object>>> calculateSubFieldRates(Company company) {
        Map<String, List<Map<String, Object>>> subFieldRates = new HashMap<>();

        // Lifecycle 세부분야별
        List<EvaluationItem> lifecycleItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("1."))
                .collect(Collectors.toList());
        List<ChecklistItem> lifecycleChecklists = getAllLifecycleChecklists(company);
        subFieldRates.put("lifecycle", calculateRatesBySubField(lifecycleChecklists, lifecycleItems));

        // Admin 세부분야별
        List<EvaluationItem> adminItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("2."))
                .collect(Collectors.toList());
        List<ChecklistItem> adminChecklists = getAllTechnicalChecklists(company);
        subFieldRates.put("admin", calculateRatesBySubField(adminChecklists, adminItems));

        // Security 세부분야별
        List<EvaluationItem> securityItems = company.evaluationItems.stream()
                .filter(item -> item.area != null && item.area.startsWith("3."))
                .collect(Collectors.toList());
        List<ChecklistItem> securityChecklists = getAllSecurityChecklists(company);
        subFieldRates.put("security", calculateRatesBySubField(securityChecklists, securityItems));

        return subFieldRates;
    }

    /**
     * 모든 Lifecycle 체크리스트 수집
     */
    private List<ChecklistItem> getAllLifecycleChecklists(Company company) {
        List<ChecklistItem> allChecklists = new ArrayList<>();
        if (company.processingTasks != null) {
            for (ProcessingTask task : company.processingTasks) {
                if (task.lifecycleChecklist != null) {
                    allChecklists.addAll(task.lifecycleChecklist);
                }
            }
        }
        return allChecklists;
    }

    /**
     * 모든 Technical 체크리스트 수집
     */
    private List<ChecklistItem> getAllTechnicalChecklists(Company company) {
        List<ChecklistItem> allChecklists = new ArrayList<>();
        if (company.technicalSystems != null) {
            for (TechnicalSystem system : company.technicalSystems) {
                if (system.technicalChecklist != null) {
                    allChecklists.addAll(system.technicalChecklist);
                }
            }
        }
        return allChecklists;
    }

    /**
     * 모든 Security 체크리스트 수집
     */
    private List<ChecklistItem> getAllSecurityChecklists(Company company) {
        List<ChecklistItem> allChecklists = new ArrayList<>();
        if (company.securitySystems != null) {
            for (SecuritySystem system : company.securitySystems) {
                if (system.securityChecklist != null) {
                    allChecklists.addAll(system.securityChecklist);
                }
            }
        }
        return allChecklists;
    }

    /**
     * 이행률 계산 (해당없음 제외)
     * 공식: (이행 + 부분이행*0.5) / (이행 + 부분이행 + 미이행) * 100
     */
    private double calculateImplementationRate(List<ChecklistItem> checklists) {
        if (checklists == null || checklists.isEmpty()) {
            return 0.0;
        }

        // "해당없음" 제외
        List<ChecklistItem> validChecklists = checklists.stream()
                .filter(item -> item.status != null && !item.status.equals("해당없음"))
                .collect(Collectors.toList());

        if (validChecklists.isEmpty()) {
            return 0.0;
        }

        long implemented = validChecklists.stream()
                .filter(item -> "이행".equals(item.status))
                .count();

        long partial = validChecklists.stream()
                .filter(item -> "부분이행".equals(item.status))
                .count();

        long notImplemented = validChecklists.stream()
                .filter(item -> "미이행".equals(item.status))
                .count();

        long total = implemented + partial + notImplemented;
        if (total == 0) {
            return 0.0;
        }

        double rate = (implemented + partial * 0.5) / total * 100;
        return Math.round(rate * 10.0) / 10.0; // 소수점 첫째자리
    }

    /**
     * 분야별 이행률 계산
     */
    private List<Map<String, Object>> calculateRatesByField(
            List<ChecklistItem> checklists,
            List<EvaluationItem> evaluationItems) {

        Map<String, List<ChecklistItem>> fieldMap = new HashMap<>();

        for (ChecklistItem checklist : checklists) {
            EvaluationItem evalItem = evaluationItems.stream()
                    .filter(item -> item.no.equals(checklist.no))
                    .findFirst()
                    .orElse(null);

            if (evalItem != null && evalItem.field != null) {
                fieldMap.computeIfAbsent(evalItem.field, k -> new ArrayList<>()).add(checklist);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ChecklistItem>> entry : fieldMap.entrySet()) {
            Map<String, Object> fieldData = new HashMap<>();
            fieldData.put("subject", entry.getKey());
            fieldData.put("value", calculateImplementationRate(entry.getValue()));
            result.add(fieldData);
        }

        // 데이터가 없으면 빈 리스트 반환
        return result;
    }

    /**
     * 세부분야별 이행률 계산
     */
    private List<Map<String, Object>> calculateRatesBySubField(
            List<ChecklistItem> checklists,
            List<EvaluationItem> evaluationItems) {

        Map<String, List<ChecklistItem>> subFieldMap = new HashMap<>();

        for (ChecklistItem checklist : checklists) {
            EvaluationItem evalItem = evaluationItems.stream()
                    .filter(item -> item.no.equals(checklist.no))
                    .findFirst()
                    .orElse(null);

            if (evalItem != null && evalItem.subField != null) {
                subFieldMap.computeIfAbsent(evalItem.subField, k -> new ArrayList<>()).add(checklist);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<ChecklistItem>> entry : subFieldMap.entrySet()) {
            Map<String, Object> subFieldData = new HashMap<>();
            subFieldData.put("subject", entry.getKey());
            subFieldData.put("value", calculateImplementationRate(entry.getValue()));
            result.add(subFieldData);
        }

        // 데이터가 없으면 빈 리스트 반환
        return result;
    }
}