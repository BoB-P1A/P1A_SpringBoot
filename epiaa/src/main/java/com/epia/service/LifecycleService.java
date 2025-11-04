package com.epia.service;

import com.epia.domain.LifecycleChecklistRow;
import com.epia.repo.LifecycleChecklistRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LifecycleService {

    private final LifecycleChecklistRepo repo;

    public LifecycleService(LifecycleChecklistRepo repo) {
        this.repo = repo;
    }

    public List<LifecycleChecklistRow> list(String companyId, String taskName) {
        return repo.findByCompanyIdAndTaskName(companyId, taskName);
    }

    public void saveAll(String companyId, List<Map<String,Object>> rows) {
        for (Map<String,Object> m : rows) {
            Integer id = (Integer)m.get("id");
            LifecycleChecklistRow r = repo.findById(id).orElseThrow();

            r.status = (String)m.get("status");
            r.evidence = (String)m.get("evidence");

            r.files = (List<Object>)m.get("files");

            repo.save(r);
        }
    }
}