package com.epia.repo;

import com.epia.domain.TechnicalChecklistRow;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TechnicalChecklistRepo extends MongoRepository<TechnicalChecklistRow, Integer> {
    List<TechnicalChecklistRow> findByCompanyId(String companyId);
    List<TechnicalChecklistRow> findByCompanyIdAndSystemName(String companyId, String systemName);
    Optional<TechnicalChecklistRow> findByCompanyIdAndSystemNameAndEvaluationItemId(
            String companyId, String systemName, Integer evaluationItemId);
    void deleteBySystemName(String systemName);
}