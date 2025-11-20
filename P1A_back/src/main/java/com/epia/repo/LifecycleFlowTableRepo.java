package com.epia.repo;

import com.epia.domain.LifecycleFlowTable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LifecycleFlowTableRepo extends MongoRepository<LifecycleFlowTable, String> {
    List<LifecycleFlowTable> findByCompanyId(String companyId);
}