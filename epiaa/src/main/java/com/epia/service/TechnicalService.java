package com.epia.service;

import com.epia.domain.*;
import com.epia.repo.*;
import com.epia.seq.SequenceService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TechnicalService {

    private final TechnicalSystemRepo systemRepo;
    private final TechnicalChecklistRepo checklistRepo;
    private final TechnicalImprovementRepo improvementRepo;
    private final TechnicalActionPlanRepo actionPlanRepo;
    private final SequenceService seq;

    public TechnicalService(
            TechnicalSystemRepo systemRepo,
            TechnicalChecklistRepo checklistRepo,
            TechnicalImprovementRepo improvementRepo,
            TechnicalActionPlanRepo actionPlanRepo,
            SequenceService seq
    ) {
        this.systemRepo = systemRepo;
        this.checklistRepo = checklistRepo;
        this.improvementRepo = improvementRepo;
        this.actionPlanRepo = actionPlanRepo;
        this.seq = seq;
    }

    // ===== 시스템(대상) =====
    public List<TechnicalSystem> getSystems(String companyId) {
        return systemRepo.findByCompanyId(companyId);
    }

    public TechnicalSystem addSystem(TechnicalSystem s) {
        if (s.id == null) {  // ID가 없을 때만 생성
            s.id = seq.next("technical_systems");
            System.out.println("🔍 새 ID 생성: " + s.id);
        }
        System.out.println("🔍 저장 완료: " + s.systemName);
        return systemRepo.save(s);
    }

    public TechnicalSystem updateSystem(Integer id, String name) {
        TechnicalSystem s = systemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("시스템을 찾을 수 없습니다."));
        s.systemName = name;
        return systemRepo.save(s);
    }

    public void deleteSystem(Integer id) {
        TechnicalSystem system = systemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("시스템을 찾을 수 없습니다."));

        // 관련 체크리스트도 삭제
        checklistRepo.deleteBySystemName(system.systemName);
        systemRepo.deleteById(id);
    }

    // ===== 체크리스트 =====
    public List<TechnicalChecklistRow> getChecklists(String companyId) {
        return checklistRepo.findByCompanyId(companyId);
    }

    /**
     * 여러 체크리스트 일괄 저장 (프론트엔드의 handleSave 대응)
     */
    public void saveChecklists(String companyId, String systemName, List<TechnicalChecklistRow> items) {
        for (TechnicalChecklistRow item : items) {
            // evaluationItemId가 있으면 기존 체크리스트 조회
            if (item.evaluationItemId != null) {
                var existing = checklistRepo.findByCompanyIdAndSystemNameAndEvaluationItemId(
                        companyId, systemName, item.evaluationItemId);

                if (existing.isPresent()) {
                    // 기존 데이터 업데이트
                    TechnicalChecklistRow row = existing.get();
                    row.status = item.status;
                    row.evidence = item.evidence;
                    row.files = item.files;
                    checklistRepo.save(row);
                    continue;
                }
            }

            // 새 데이터 생성
            if (item.id == null) {
                item.id = seq.next("technical_checklists");
            }
            item.companyId = companyId;
            item.systemName = systemName;
            checklistRepo.save(item);
        }
    }

    // ===== 개선가이드 =====
    public List<TechnicalImprovement> getImprovements(String companyId) {
        return improvementRepo.findByCompanyId(companyId);
    }

    public void saveImprovements(TechnicalImprovement body) {
        improvementRepo.save(body);
    }

    // ===== 조치계획 =====
    public List<TechnicalActionPlan> getActionPlans(String companyId) {
        return actionPlanRepo.findByCompanyId(companyId);
    }

    public void saveActionPlans(TechnicalActionPlan body) {
        actionPlanRepo.save(body);
    }
}