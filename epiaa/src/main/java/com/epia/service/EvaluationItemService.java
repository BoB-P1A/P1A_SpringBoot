package com.epia.service;

import com.epia.domain.*;
import com.epia.repo.*;
import com.epia.seq.SequenceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluationItemService {

    private final EvaluationItemRepo itemRepo;
    private final LifecycleChecklistRepo lifecycleRepo;
    private final TechnicalChecklistRepo technicalRepo;
    private final SecurityChecklistRepo securityRepo;
    private final SequenceService seq;

    public EvaluationItemService(
            EvaluationItemRepo itemRepo,
            LifecycleChecklistRepo lifecycleRepo,
            TechnicalChecklistRepo technicalRepo,
            SecurityChecklistRepo securityRepo,
            SequenceService seq
    ) {
        this.itemRepo = itemRepo;
        this.lifecycleRepo = lifecycleRepo;
        this.technicalRepo = technicalRepo;
        this.securityRepo = securityRepo;
        this.seq = seq;
    }

    public List<EvaluationItem> list(String companyId) {
        return itemRepo.findByCompanyId(companyId);
    }

    public void save(EvaluationItem item) {

        if (item.id == null) item.id = seq.next("evaluation_items");

        itemRepo.save(item);

        String prefix = item.no.substring(0, 1);

        switch (prefix) {
            case "1" -> {
                var row = new LifecycleChecklistRow(item.companyId, item);
                row.id = seq.next("lifecycle_checklists");
                lifecycleRepo.save(row);
            }
            case "2" -> {
                var row = new TechnicalChecklistRow(item.companyId, item);
                row.id = seq.next("technical_checklists");
                technicalRepo.save(row);
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