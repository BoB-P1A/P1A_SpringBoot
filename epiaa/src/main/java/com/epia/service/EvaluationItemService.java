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
    private final SecurityChecklistRepo securityRepo;
    private final SequenceService seq;

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