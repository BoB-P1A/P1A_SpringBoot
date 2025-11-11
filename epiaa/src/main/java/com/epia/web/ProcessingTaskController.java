package com.epia.web;

import com.epia.dto.task.*;
import com.epia.service.ProcessingTaskService;
import com.epia.support.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks") // << 중복 방지: 메서드에서 "/tasks" 금지
public class ProcessingTaskController {

    @Autowired
    private ProcessingTaskService service;

    // GET /tasks?companyId=...
    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam String companyId) {
        if (companyId == null || companyId.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "companyId가 필요합니다."));
        return ResponseEntity.ok(service.getTasksByCompanyId(companyId));
    }

    // POST /tasks
    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody TaskCreateRequest req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createTask(req));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "서버 오류"));
        }
    }

    // PUT /tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody TaskUpdateRequest req) {
        try {
            return ResponseEntity.ok(service.updateTask(id, req));
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "서버 오류"));
        }
    }

    // DELETE /tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "서버 오류"));
        }
    }

    // PUT /tasks/bulk?companyId=...
    @PutMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestParam String companyId,
                                  @RequestBody List<TaskBulkUpdateRequest> reqs) {
        if (reqs == null) return ResponseEntity.badRequest().body(Map.of("error", "요청 본문이 비었습니다."));
        return ResponseEntity.ok(service.bulkReplace(companyId, reqs));
    }
}