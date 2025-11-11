package com.epia.web;

import com.epia.service.ProcessingTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lifecycle/tasks")
public class LifecycleTaskController {

    private final ProcessingTaskService service;

    public LifecycleTaskController(ProcessingTaskService service) {
        this.service = service;
    }

    /**
     * 처리업무 목록 조회 (생애주기 메뉴 전용)
     * GET /lifecycle/tasks?companyId=xxx
     */
    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam String companyId) {
        if (companyId == null || companyId.isBlank()) {
            return ResponseEntity.badRequest().body("companyId가 필요합니다.");
        }
        return ResponseEntity.ok(service.getTasksByCompanyId(companyId));
    }
}