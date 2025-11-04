package com.epia.repo;

import com.epia.domain.TechnicalChecklistRow;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TechnicalChecklistRepo extends MongoRepository<TechnicalChecklistRow, Integer> {
    List<TechnicalChecklistRow> findByCompanyId(String companyId);
    List<TechnicalChecklistRow> findByCompanyIdAndSystemName(String companyId, String systemName);
}