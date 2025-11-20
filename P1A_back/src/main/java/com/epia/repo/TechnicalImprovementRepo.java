package com.epia.repo;

import com.epia.domain.TechnicalImprovement;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TechnicalImprovementRepo extends MongoRepository<TechnicalImprovement, String> {
    List<TechnicalImprovement> findByCompanyId(String companyId);
}