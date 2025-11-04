package com.epia.repo;

import com.epia.domain.EvaluationItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EvaluationItemRepo extends MongoRepository<EvaluationItem, Integer> {
    List<EvaluationItem> findByCompanyId(String companyId);
}