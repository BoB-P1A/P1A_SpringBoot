/*package com.epia.service;

import com.epia.domain.ProcessingTask;
import com.epia.repo.TaskRepo;
import com.epia.seq.SequenceService;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepo repo;
    private final SequenceService seq;

    public TaskService(TaskRepo repo, SequenceService seq) {
        this.repo = repo;
        this.seq = seq;
    }

    public List<ProcessingTask> list(String companyId) {
        return repo.findByCompanyId(companyId);
    }

    public void save(List<ProcessingTask> arr) {
        for (ProcessingTask t : arr) {
            repo.save(t);
        }
    }

    public void delete(ObjectId id) {  // ← Integer → ObjectId
        repo.deleteById(id);
    }
}*/