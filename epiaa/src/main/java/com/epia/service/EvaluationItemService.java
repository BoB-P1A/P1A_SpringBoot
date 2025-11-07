package com.epia.service;

import com.epia.domain.Company;
import com.epia.domain.EvaluationItem;
import com.epia.dto.EvaluationItemDto;
import com.epia.repo.CompanyRepo;
import com.epia.support.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationItemService {

    private final CompanyRepo companyRepo;

    public EvaluationItemService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    /** 목록 */
    public List<EvaluationItemDto> list(String companyId) {
        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));
        return c.evaluationItems.stream()
                .map(e -> EvaluationItemDto.of(e, c.id))
                .toList();
    }

    /** 상세 */
    public EvaluationItemDto getOne(String companyId, Integer id) {
        Company c = companyRepo.findById(companyId)
                .orElseThrow(() -> new ApiException(404, "회사 없음"));

        EvaluationItem target = c.evaluationItems.stream()
                .filter(it -> it.id != null && it.id.equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException(404, "평가항목 없음"));

        return EvaluationItemDto.of(target, c.id);
    }
}