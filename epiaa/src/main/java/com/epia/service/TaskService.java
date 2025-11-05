package com.epia.service;

import com.epia.domain.ProcessingTask;
import com.epia.repo.TaskRepo;
import com.epia.seq.SequenceService;
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
            if (t.id == null) t.id = seq.next("processing_tasks");
            repo.save(t);
        }
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}