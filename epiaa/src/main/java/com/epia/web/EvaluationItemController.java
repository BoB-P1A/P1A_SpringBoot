package com.epia.web;

import com.epia.domain.EvaluationItem;
import com.epia.service.EvaluationItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluations")
public class EvaluationItemController {

    private final EvaluationItemService service;

    public EvaluationItemController(EvaluationItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<EvaluationItem> list(@RequestParam String companyId) {
        return service.list(companyId);
    }

    @PostMapping
    public Map<String, String> create(@RequestBody EvaluationItem item) {
        service.save(item);
        return Map.of("message", "평가항목이 저장되었습니다");
    }

    @PutMapping("/{id}")
    public Map<String, String> update(@PathVariable Integer id, @RequestBody EvaluationItem item) {
        item.id = id;
        service.save(item);
        return Map.of("message", "평가항목이 수정되었습니다");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Integer id) {
        service.delete(id);
        return Map.of("message", "평가항목이 삭제되었습니다");
    }
}