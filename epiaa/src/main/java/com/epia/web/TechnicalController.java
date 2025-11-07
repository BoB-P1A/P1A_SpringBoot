package com.epia.web;

import com.epia.domain.*;
import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.TechnicalSystemDto;
import com.epia.service.TechnicalService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/technical")
public class TechnicalController {

    private final TechnicalService svc;

    public TechnicalController(TechnicalService svc) {
        this.svc = svc;
    }

    // ===== System =====
    @GetMapping("/systems")
    public List<TechnicalSystemDto> systems(@RequestParam String companyId) {
        System.out.println("GET /technical/systems - companyId: " + companyId);
        return svc.getSystems(companyId).stream()
                .map(sys -> new TechnicalSystemDto(sys.id.toHexString(), sys.systemName))
                .collect(Collectors.toList());
    }

    @PostMapping("/systems")
    public TechnicalSystemDto addSystem(@RequestBody Map<String, String> body) {
        System.out.println("POST /technical/systems - body: " + body);
        TechnicalSystem sys = new TechnicalSystem();
        sys.systemName = body.get("systemName");
        TechnicalSystem saved = svc.addSystem(body.get("companyId"), sys);
        return new TechnicalSystemDto(saved.id.toHexString(), saved.systemName);
    }

    @PutMapping("/systems/{id}")
    public TechnicalSystemDto updateSystem(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        System.out.println("PUT /technical/systems/" + id + " - body: " + body);
        TechnicalSystem updated = svc.updateSystem(new ObjectId(id), body.get("systemName"));
        return new TechnicalSystemDto(updated.id.toString(), updated.systemName);
    }

    @DeleteMapping("/systems/{id}")
    public Map<String, String> deleteSystem(@PathVariable String id) {
        System.out.println("DELETE /technical/systems/" + id);
        svc.deleteSystem(new ObjectId(id));
        return Map.of("message", "시스템이 삭제되었습니다");
    }

    // ===== Checklist =====
    @GetMapping("/checklists")
    public List<ChecklistItem> checklists(
            @RequestParam String companyId,
            @RequestParam String systemName) {  // ← systemName 필수
        System.out.println(" GET /technical/checklists - companyId: " + companyId + ", systemName: " + systemName);
        return svc.getChecklists(companyId, systemName);
    }

    @PostMapping("/checklists")
    public Map<String, String> saveChecklists(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        String systemName = (String) body.get("systemName");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");

        System.out.println(" POST /technical/checklists - companyId: " + companyId + ", systemName: " + systemName);

        List<ChecklistItem> items = dataList.stream()
                .map(this::mapToChecklistItem)
                .collect(Collectors.toList());

        svc.saveChecklists(companyId, systemName, items);
        return Map.of("message", "저장되었습니다");
    }

    private ChecklistItem mapToChecklistItem(Map<String, Object> map) {
        ChecklistItem item = new ChecklistItem();

        item.no = (String) map.get("no");
        item.status = (String) map.get("status");
        item.evidence = (String) map.get("evidence");
        @SuppressWarnings("unchecked")
        List<Object> files = (List<Object>) map.get("files");
        item.files = files != null ? files : new ArrayList<>();

        return item;
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
    public List<ActionPlan> plans(
            @RequestParam String companyId,
            @RequestParam String systemName) {
        return svc.getActionPlans(companyId, systemName);
    }

    @PostMapping("/actionplans")
    public Map<String, String> savePlans(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        String systemName = (String) body.get("systemName");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");

        List<ActionPlan> plans = dataList.stream()
                .map(this::mapToActionPlan)
                .collect(Collectors.toList());

        svc.saveActionPlans(companyId, systemName, plans);
        return Map.of("message", "저장되었습니다");
    }

    private ActionPlan mapToActionPlan(Map<String, Object> map) {
        ActionPlan plan = new ActionPlan();
        plan.no = (String) map.get("no");
        plan.title = (String) map.get("title");
        plan.period = (String) map.get("period");
        plan.department = (String) map.get("department");
        plan.owner = (String) map.get("owner");
        plan.date = (String) map.get("date");
        return plan;
    }
}