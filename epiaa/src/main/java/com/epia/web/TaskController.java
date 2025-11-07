package com.epia.web;

import com.epia.domain.ProcessingTask;
import com.epia.service.TaskService;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lifecycle/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // 목록 조회
    @GetMapping
    public List<ProcessingTask> list(@RequestParam String companyId) {
        return service.list(companyId);
    }

    // 저장 (전체 배열로 저장)
    @PostMapping
    public Map<String, String> save(@RequestBody List<ProcessingTask> body) {
        service.save(body);
        return Map.of("message", "처리업무표가 저장되었습니다");
    }

    // 삭제
    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable String id) {
        service.delete(new ObjectId(id));
        return Map.of("message", "삭제되었습니다");
    }
}