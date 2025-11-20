
package com.epia.web;

import com.epia.dto.ProcessingTaskDto;
import com.epia.service.ProcessingTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 처리업무표 페이지 전용 컨트롤러
 * 엔드포인트: /tasks
 */
@RestController
@RequestMapping("/tasks")
public class ProcessingTaskController {

    private final ProcessingTaskService service;

    public ProcessingTaskController(ProcessingTaskService service) {
        this.service = service;
    }

    /**
     * 처리업무 목록 조회
     * GET /tasks?companyId={companyId}
     */
    @GetMapping
    public ResponseEntity<List<ProcessingTaskDto>> getAllTasks(
            @RequestParam(required = false) String companyId) {

        if (companyId == null || companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<ProcessingTaskDto> tasks = service.getAllTasksByCompanyId(companyId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 처리업무 생성
     * POST /tasks
     */
    @PostMapping
    public ResponseEntity<ProcessingTaskDto> createTask(@RequestBody ProcessingTaskDto dto) {
        if (dto.companyId == null || dto.companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ProcessingTaskDto created = service.createTask(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * 처리업무 일괄 저장 (생성 + 수정)
     * POST /tasks/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, String>> bulkSave(@RequestBody List<ProcessingTaskDto> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String companyId = tasks.get(0).companyId;
        if (companyId == null || companyId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        service.bulkSave(companyId, tasks);
        return ResponseEntity.ok(Map.of("message", "저장되었습니다"));
    }

    /**
     * 처리업무 수정
     * PUT /tasks/{taskId}
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<ProcessingTaskDto> updateTask(
            @PathVariable String taskId,
            @RequestBody ProcessingTaskDto dto) {

        ProcessingTaskDto updated = service.updateTask(taskId, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 처리업무 삭제
     * DELETE /tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable String taskId) {
        boolean deleted = service.deleteTask(taskId);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}