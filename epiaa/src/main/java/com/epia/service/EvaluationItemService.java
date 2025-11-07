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
    private final EvaluationItemRepo itemRepo;
    private final LifecycleChecklistRepo lifecycleRepo;
    private final SecurityChecklistRepo securityRepo;
    private final SequenceService seq;

    public EvaluationItemService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    public EvaluationItemService(
            EvaluationItemRepo itemRepo,
            LifecycleChecklistRepo lifecycleRepo,
            SecurityChecklistRepo securityRepo,
            SequenceService seq
    ) {
        this.itemRepo = itemRepo;
        this.lifecycleRepo = lifecycleRepo;
        this.securityRepo = securityRepo;
        this.seq = seq;
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
        itemRepo.save(item);

        String prefix = item.no.substring(0, 1);

        switch (prefix) {
            case "1" -> {
                var row = new LifecycleChecklistRow(item.companyId, item);
                row.id = seq.next("lifecycle_checklists");
                lifecycleRepo.save(row);
            }
            case "3" -> {
                var row = new SecurityChecklistRow(item.companyId, item);
                row.id = seq.next("security_checklists");
                securityRepo.save(row);
            }
        }
    }

    public void delete(Integer id) {
        itemRepo.deleteById(id);
    }
}