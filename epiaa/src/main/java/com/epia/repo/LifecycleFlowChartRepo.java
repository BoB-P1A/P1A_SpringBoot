package com.epia.repo;

import com.epia.domain.LifecycleFlowChart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LifecycleFlowChartRepo extends MongoRepository<LifecycleFlowChart, String> {
    List<LifecycleFlowChart> findByCompanyId(String companyId);
}