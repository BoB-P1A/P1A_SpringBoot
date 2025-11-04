package com.epia.web;

import com.epia.domain.*;
import com.epia.service.SecurityService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService svc;

    public SecurityController(SecurityService svc) {
        this.svc = svc;
    }

    // ===== Targets =====
    @GetMapping("/targets")
    public List<SecurityTarget> targets(@RequestParam String companyId) {
        return svc.getTargets(companyId);
    }

    @PostMapping("/targets")
    public SecurityTarget addTarget(@RequestBody SecurityTarget body) {
        return svc.addTarget(body);
    }

    @PutMapping("/targets/{id}")
    public SecurityTarget updateTarget(@PathVariable Integer id, @RequestBody SecurityTarget body) {
        return svc.updateTarget(id, body.targetName);
    }

    @DeleteMapping("/targets/{id}")
    public void deleteTarget(@PathVariable Integer id) {
        svc.deleteTarget(id);
    }

    // ===== Checklists =====
    @GetMapping("/checklists")
    public List<SecurityChecklistRow> checklists(@RequestParam String companyId) {
        return svc.getChecklists(companyId);
    }

    @PostMapping("/checklists")
    public void saveChecklists(@RequestBody List<SecurityChecklistRow> arr) {
        svc.saveChecklists(arr);
    }

    // ===== Improvements =====
    @GetMapping("/improvements")
    public List<SecurityImprovement> improvements(@RequestParam String companyId) {
        return svc.getImprovements(companyId);
    }

    @PostMapping("/improvements")
    public void saveImprovements(@RequestBody SecurityImprovement body) {
        svc.saveImprovements(body);
    }

    // ===== Action Plans =====
    @GetMapping("/actionplans")
    public List<SecurityActionPlan> plans(@RequestParam String companyId) {
        return svc.getActionPlans(companyId);
    }

    @PostMapping("/actionplans")
    public void savePlans(@RequestBody SecurityActionPlan body) {
        svc.saveActionPlans(body);
    }
}