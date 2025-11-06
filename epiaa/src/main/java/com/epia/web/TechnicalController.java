package com.epia.web;

import com.epia.domain.*;
import com.epia.service.TechnicalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        System.out.println("🔍 GET /technical/systems - companyId: " + companyId);
        return svc.getSystems(companyId);
    }

    @PostMapping("/systems")
    public TechnicalSystem addSystem(@RequestBody Map<String, String> body) {
        System.out.println("🔍 POST /technical/systems - body: " + body);
        TechnicalSystem sys = new TechnicalSystem();
        sys.companyId = body.get("companyId");
        sys.systemName = body.get("systemName");
        return svc.addSystem(sys);
    }

    @PutMapping("/systems/{id}")
    public TechnicalSystem updateSystem(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        System.out.println("🔍 PUT /technical/systems/" + id + " - body: " + body);
        return svc.updateSystem(id, body.get("systemName"));
    }

    @DeleteMapping("/systems/{id}")
    public Map<String, String> deleteSystem(@PathVariable Integer id) {
        System.out.println("🔍 DELETE /technical/systems/" + id);
        svc.deleteSystem(id);
        return Map.of("message", "시스템이 삭제되었습니다");
    }

    // ===== Checklist =====
    @GetMapping("/checklists")
    public List<TechnicalChecklistRow> checklists(
            @RequestParam String companyId,
            @RequestParam(required = false) List<String> status) {
        // status 파라미터는 프론트에서 빈 배열로 전달, 백엔드에서는 전체 조회
        System.out.println("🔍 GET /technical/checklists - companyId: " + companyId);
        return svc.getChecklists(companyId);
    }

    @PostMapping("/checklists")
    public Map<String, String> saveChecklists(
            @RequestParam String companyId,
            @RequestParam String systemName,
            @RequestBody List<TechnicalChecklistRow> items) {
        System.out.println("🔍 POST /technical/checklists - companyId: " + companyId + ", systemName: " + systemName);
        svc.saveChecklists(companyId, systemName, items);
        return Map.of("message", "저장되었습니다");
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