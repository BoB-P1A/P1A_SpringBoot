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
        s.id = seq.next("technical_systems");
        return systemRepo.save(s);
    }

    public TechnicalSystem updateSystem(Integer id, String name) {
        TechnicalSystem s = systemRepo.findById(id).orElseThrow();
        s.systemName = name;
        return systemRepo.save(s);
    }

    public void deleteSystem(Integer id) {
        systemRepo.deleteById(id);
    }

    // ===== 체크리스트 =====
    public List<TechnicalChecklistRow> getChecklists(String companyId) {
        return checklistRepo.findByCompanyId(companyId);
    }

    public void saveChecklists(List<TechnicalChecklistRow> arr) {
        for (TechnicalChecklistRow r : arr) {
            if (r.id == null) r.id = seq.next("technical_checklists");
            checklistRepo.save(r);
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