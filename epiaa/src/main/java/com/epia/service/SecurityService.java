package com.epia.service;

import com.epia.domain.*;
import com.epia.repo.*;
import com.epia.seq.SequenceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityService {

    private final SecurityTargetRepo targetRepo;
    private final SecurityChecklistRepo checklistRepo;
    private final SecurityImprovementRepo improvementRepo;
    private final SecurityActionPlanRepo actionPlanRepo;
    private final SequenceService seq;

    public SecurityService(
            SecurityTargetRepo targetRepo,
            SecurityChecklistRepo checklistRepo,
            SecurityImprovementRepo improvementRepo,
            SecurityActionPlanRepo actionPlanRepo,
            SequenceService seq
    ) {
        this.targetRepo = targetRepo;
        this.checklistRepo = checklistRepo;
        this.improvementRepo = improvementRepo;
        this.actionPlanRepo = actionPlanRepo;
        this.seq = seq;
    }

    // ===== Targets =====
    public List<SecurityTarget> getTargets(String companyId) {
        return targetRepo.findByCompanyId(companyId);
    }

    public SecurityTarget addTarget(SecurityTarget t) {
        t.id = seq.next("security_targets");
        return targetRepo.save(t);
    }

    public SecurityTarget updateTarget(Integer id, String name) {
        SecurityTarget t = targetRepo.findById(id).orElseThrow();
        t.targetName = name;
        return targetRepo.save(t);
    }

    public void deleteTarget(Integer id) {
        targetRepo.deleteById(id);
    }

    // ===== Checklists =====
    public List<SecurityChecklistRow> getChecklists(String companyId) {
        return checklistRepo.findByCompanyId(companyId);
    }

    public void saveChecklists(List<SecurityChecklistRow> arr) {
        for (SecurityChecklistRow r : arr) {
            if (r.id == null) r.id = seq.next("security_checklists");
            checklistRepo.save(r);
        }
    }

    // ===== Improvements =====
    public List<SecurityImprovement> getImprovements(String companyId) {
        return improvementRepo.findByCompanyId(companyId);
    }

    public void saveImprovements(SecurityImprovement body) {
        improvementRepo.save(body);
    }

    // ===== Action Plans =====
    public List<SecurityActionPlan> getActionPlans(String companyId) {
        return actionPlanRepo.findByCompanyId(companyId);
    }

    public void saveActionPlans(SecurityActionPlan body) {
        actionPlanRepo.save(body);
    }
}