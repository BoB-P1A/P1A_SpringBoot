package com.epia.repo;

import com.epia.domain.ProcessingTask;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepo extends MongoRepository<ProcessingTask, ObjectId> {
    List<ProcessingTask> findByCompanyId(String companyId);
}