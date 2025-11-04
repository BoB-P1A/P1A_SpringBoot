package com.epia.repo;

import com.epia.domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepo extends MongoRepository<Task, Integer> {
    List<Task> findByCompanyId(String companyId);
}