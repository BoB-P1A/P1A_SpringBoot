package com.epia.web;

import com.epia.service.ProcessingTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lifecycle/flowtables")
public class FlowTableController {

    private final ProcessingTaskService service;

    public FlowTableController(ProcessingTaskService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam String companyId) {
        if (companyId == null || companyId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "companyId가 필요합니다."));
        return ResponseEntity.ok(service.getFlowSheets(companyId));
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> body) {

        String companyId = (String) body.get("companyId");
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");

        boolean result = processingTaskService.saveFlowSheets(companyId, dataList);

        return ResponseEntity.ok(Map.of("success", result));
    }
}