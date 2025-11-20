package com.epia.repo;

import com.epia.domain.SecurityImprovement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SecurityImprovementRepo extends MongoRepository<SecurityImprovement, String> {
    List<SecurityImprovement> findByCompanyId(String companyId);
}