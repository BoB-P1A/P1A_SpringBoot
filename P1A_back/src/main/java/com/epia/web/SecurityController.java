package com.epia.web;

import com.epia.domain.*;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.SecurityChecklistDetailDto;
import com.epia.dto.SecuritySystemDto;
import com.epia.service.SecurityService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService svc;

    public SecurityController(SecurityService svc) {
        this.svc = svc;
    }

    // ===== Targets (Systems) =====
    @GetMapping("/targets")
    public List<SecuritySystemDto> targets(@RequestParam String companyId) {
        System.out.println("GET /security/targets - companyId: " + companyId);
        return svc.getSystems(companyId).stream()
                .map(sys -> new SecuritySystemDto(sys.id.toHexString(), sys.systemName))
                .collect(Collectors.toList());
    }

    @PostMapping("/targets")
    public SecuritySystemDto addTarget(@RequestBody Map<String, String> body) {
        System.out.println("POST /security/targets - body: " + body);
        SecuritySystem sys = new SecuritySystem();
        sys.systemName = body.get("targetName");
        SecuritySystem saved = svc.addSystem(body.get("companyId"), sys);
        return new SecuritySystemDto(saved.id.toHexString(), saved.systemName);
    }

    @PutMapping("/targets/{id}")
    public SecuritySystemDto updateTarget(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        System.out.println("PUT /security/targets/" + id + " - body: " + body);
        SecuritySystem updated = svc.updateSystem(new ObjectId(id), body.get("targetName"));
        return new SecuritySystemDto(updated.id.toString(), updated.systemName);
    }

    @DeleteMapping("/targets/{id}")
    public Map<String, String> deleteTarget(@PathVariable String id) {
        System.out.println("DELETE /security/targets/" + id);
        svc.deleteSystem(new ObjectId(id));
        return Map.of("message", "검토대상이 삭제되었습니다");
    }

    // ===== Checklists =====
    @GetMapping("/checklists")
    public List<SecurityChecklistDetailDto> checklists(
            @RequestParam String companyId,
            @RequestParam(required = false) String systemId,
            @RequestParam(required = false) List<String> status) {
        System.out.println("GET /security/checklists - companyId: " + companyId + ", systemId: " + systemId);

        ObjectId systemObjectId = systemId != null ? new ObjectId(systemId) : null;
        return svc.getChecklistsWithDetails(companyId, systemObjectId, status);
    }

    @PostMapping("/checklists")
    public Map<String, String> saveChecklists(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        String systemId = (String) body.get("systemId");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");

        System.out.println("POST /security/checklists - companyId: " + companyId + ", systemId: " + systemId);

        List<ChecklistItem> items = dataList.stream()
                .map(this::mapToChecklistItem)
                .collect(Collectors.toList());

        svc.saveChecklists(companyId, new ObjectId(systemId), items);
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
    @GetMapping("/action-plans")
    public Map<String, Object> getActionPlans(@RequestParam String companyId) {
        System.out.println("GET /security/action-plans - companyId: " + companyId);
        return svc.getActionPlansMap(companyId);
    }

    @PostMapping("/action-plans")
    public Map<String, String> saveActionPlans(@RequestBody Map<String, Object> body) {
        String companyId = (String) body.get("companyId");
        @SuppressWarnings("unchecked")
        Map<String, Object> actionPlans = (Map<String, Object>) body.get("actionPlans");

        System.out.println("POST /security/action-plans - companyId: " + companyId);

        svc.saveActionPlansFromMap(companyId, actionPlans);
        return Map.of("message", "저장되었습니다");
    }

    /**
     * 대시보드용: 모든 System과 Checklist 반환
     */
    @GetMapping("/systems-with-checklists")
    public List<Map<String, Object>> getSystemsWithChecklists(@RequestParam String companyId) {
        System.out.println("GET /security/systems-with-checklists - companyId: " + companyId);
        return svc.getSystems(companyId).stream()
                .map(sys -> {
                    Map<String, Object> systemData = new java.util.HashMap<>();
                    systemData.put("systemId", sys.id.toHexString());
                    systemData.put("systemName", sys.systemName);
                    systemData.put("securityChecklist", sys.securityChecklist != null ? sys.securityChecklist : new ArrayList<>());
                    systemData.put("actionPlans", sys.actionPlans != null ? sys.actionPlans : new ArrayList<>());
                    return systemData;
                })
                .collect(Collectors.toList());
    }
}