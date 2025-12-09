package com.epia.repo;

import com.epia.domain.HistoryLog;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface HistoryLogRepo extends MongoRepository<HistoryLog, ObjectId> {
    List<HistoryLog> findByCompanyIdOrderByChangedAtDesc(String companyId);
    List<HistoryLog> findByCompanyIdAndTargetIdOrderByChangedAtDesc(String companyId, String targetId);
    List<HistoryLog> findByCompanyIdAndNoOrderByChangedAtDesc(String companyId, String no);
    List<HistoryLog> findByCompanyIdAndEvaluationTypeOrderByChangedAtDesc(String companyId, String evaluationType);

    // 필터링을 위한 distinct 조회
    @Query(value = "{ 'companyId': ?0 }", fields = "{ 'area': 1 }")
    List<HistoryLog> findDistinctAreasByCompanyId(String companyId);

    @Query(value = "{ 'companyId': ?0 }", fields = "{ 'targetName': 1 }")
    List<HistoryLog> findDistinctTargetNamesByCompanyId(String companyId);

    @Query(value = "{ 'companyId': ?0 }", fields = "{ 'changedBy': 1 }")
    List<HistoryLog> findDistinctChangedByNamesByCompanyId(String companyId);
}