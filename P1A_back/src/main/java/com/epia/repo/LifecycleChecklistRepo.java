package com.epia.repo;

import com.epia.domain.LifecycleChecklistRow;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LifecycleChecklistRepo extends MongoRepository<LifecycleChecklistRow, Integer> {
    List<LifecycleChecklistRow> findByCompanyId(String companyId);
    List<LifecycleChecklistRow> findByCompanyIdAndTaskName(String companyId, String taskName);
    List<LifecycleChecklistRow> findByCompanyIdAndTaskNameIsNull(String companyId);
}