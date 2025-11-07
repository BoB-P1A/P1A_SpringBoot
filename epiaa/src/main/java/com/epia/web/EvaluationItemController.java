package com.epia.web;

import com.epia.dto.EvaluationItemDto;
import com.epia.service.EvaluationItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations")
public class EvaluationItemController {

    private final EvaluationItemService service;

    public EvaluationItemController(EvaluationItemService service) {
        this.service = service;
    }

    // GET /evaluations?companyId={companyId}
    @GetMapping
    public List<EvaluationItemDto> list(@RequestParam String companyId) {
        return service.list(companyId);
    }

    // GET /evaluations/{id}?companyId={companyId}
    @GetMapping("/{id}")
    public EvaluationItemDto detail(@PathVariable Integer id,
                                    @RequestParam String companyId) {
        return service.getOne(companyId, id);
    }
}