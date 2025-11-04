package com.epia.web;

import com.epia.domain.*;
import com.epia.service.TechnicalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/technical")
public class TechnicalController {

    private final TechnicalService svc;

    public TechnicalController(TechnicalService svc) {
        this.svc = svc;
    }

    // ===== System =====
    @GetMapping("/systems")
    public List<TechnicalSystem> systems(@RequestParam String companyId) {
        return svc.getSystems(companyId);
    }

    @PostMapping("/systems")
    public TechnicalSystem addSystem(@RequestBody TechnicalSystem body) {
        return svc.addSystem(body);
    }

    @PutMapping("/systems/{id}")
    public TechnicalSystem updateSystem(@PathVariable Integer id, @RequestBody TechnicalSystem body) {
        return svc.updateSystem(id, body.systemName);
    }

    @DeleteMapping("/systems/{id}")
    public void deleteSystem(@PathVariable Integer id) {
        svc.deleteSystem(id);
    }

    // ===== Checklist =====
    @GetMapping("/checklists")
    public List<TechnicalChecklistRow> checklists(@RequestParam String companyId) {
        return svc.getChecklists(companyId);
    }

    @PostMapping("/checklists")
    public void saveChecklists(@RequestBody List<TechnicalChecklistRow> arr) {
        svc.saveChecklists(arr);
    }

    // ===== Improvement =====
    @GetMapping("/improvements")
    public List<TechnicalImprovement> improvements(@RequestParam String companyId) {
        return svc.getImprovements(companyId);
    }

    @PostMapping("/improvements")
    public void saveImprovements(@RequestBody TechnicalImprovement body) {
        svc.saveImprovements(body);
    }

    // ===== Action Plans =====
    @GetMapping("/actionplans")
    public List<TechnicalActionPlan> plans(@RequestParam String companyId) {
        return svc.getActionPlans(companyId);
    }

    @PostMapping("/actionplans")
    public void savePlans(@RequestBody TechnicalActionPlan body) {
        svc.saveActionPlans(body);
    }
}