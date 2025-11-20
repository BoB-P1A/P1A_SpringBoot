package com.epia.web;

import com.epia.domain.Company;
import com.epia.domain.EvaluationItem;
import com.epia.repo.CompanyRepo;
import com.epia.support.ApiException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/evaluations")
public class EvaluationItemController {

    private final CompanyRepo companyRepo;

    public EvaluationItemController(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @GetMapping
    public List<EvaluationItem> list(@RequestParam String companyId) {
        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));

        return c.evaluationItems;
    }

    @PostMapping
    public Map<String, String> create(@RequestParam String companyId, @RequestBody EvaluationItem item) {

        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));

        // auto increment → evaluationItems.size + 1
        item.id = c.evaluationItems.size() + 1;

        c.evaluationItems.add(item);
        companyRepo.save(c);

        return Map.of("message", "평가항목이 저장되었습니다");
    }

    @PutMapping("/{id}")
    public Map<String, String> update(@RequestParam String companyId, @PathVariable Integer id,
                                      @RequestBody EvaluationItem item) {

        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));

        var target = c.evaluationItems.stream()
                .filter(i -> i.id == id)
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "해당 평가항목 없음"));

        target.area = item.area;
        target.field = item.field;
        target.subField = item.subField;
        target.no = item.no;
        target.item = item.item;

        companyRepo.save(c);

        return Map.of("message", "평가항목이 수정되었습니다");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@RequestParam String companyId, @PathVariable Integer id) {

        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));

        boolean removed = c.evaluationItems.removeIf(i -> i.id == id);

        if (!removed) throw new ApiException(404, "해당 평가항목 없음");

        companyRepo.save(c);

        return Map.of("message", "평가항목이 삭제되었습니다");
    }
}