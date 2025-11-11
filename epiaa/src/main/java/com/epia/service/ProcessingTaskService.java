// com/epia/service/ProcessingTaskService.java
package com.epia.service;

import com.epia.dto.task.*;
import com.epia.repo.TaskRepo;
import com.epia.support.ApiException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProcessingTaskService {

    @Autowired
    private TaskRepo taskRepo;

    // Document -> DTO 매핑
    private TaskDtos toDto(String companyId, Document d) {
        TaskDtos t = new TaskDtos();
        t.setId(d.getObjectId("_id").toHexString());
        t.setCompanyId(companyId);
        t.setTaskName(d.getString("taskName"));
        t.setPurpose(d.getString("purpose"));
        // DB의 'infomation' 오타 대비
        String pi = d.getString("personalInfo");
        if (pi == null) pi = d.getString("infomation");
        t.setPersonalInfo(pi);
        t.setDepartment(d.getString("department"));
        return t;
    }

    public List<TaskDtos> getTasksByCompanyId(String companyId) {
        List<Document> docs = taskRepo.findAllByCompanyId(companyId);
        List<TaskDtos> list = new ArrayList<>();
        for (Document d : docs) list.add(toDto(companyId, d));
        return list;
    }
    public List<Map<String, Object>> getFlowSheets(String companyId) {
        return taskRepo.findFlowSheets(companyId);
    }

    public void saveFlowSheets(String companyId, Map<String, Object> data) {
        taskRepo.updateFlowSheets(companyId, data);
    }
    
    public TaskDtos createTask(TaskCreateRequest req) {
        if (req.getCompanyId() == null || req.getCompanyId().isBlank())
            throw new ApiException(400, "companyId가 필요합니다.");

        Document now = new Document("createdAt", new Date()).append("updatedAt", new Date());
        Document newDoc = new Document()
                .append("_id", new ObjectId())
                .append("taskName", nvl(req.getTaskName()))
                .append("purpose", nvl(req.getPurpose()))
                .append("personalInfo", nvl(req.getPersonalInfo()))
                .append("department", nvl(req.getDepartment()))
                .append("createdAt", new Date())
                .append("updatedAt", new Date());

        Document created = taskRepo.create(req.getCompanyId(), newDoc);
        return toDto(req.getCompanyId(), created);
    }

    public TaskDtos updateTask(String taskId, TaskUpdateRequest req) {
        Document set = new Document();
        if (req.getTaskName() != null) set.append("taskName", req.getTaskName());
        if (req.getPurpose() != null) set.append("purpose", req.getPurpose());
        if (req.getPersonalInfo() != null) set.append("personalInfo", req.getPersonalInfo());
        if (req.getDepartment() != null) set.append("department", req.getDepartment());

        boolean ok = taskRepo.update(taskId, set);
        if (!ok) throw new ApiException(404, "처리업무를 찾을 수 없습니다.");

        Document d = taskRepo.findById(taskId);
        if (d == null) throw new ApiException(404, "처리업무를 찾을 수 없습니다.");
        // companyId를 알아내기 위해선 별도 조회가 필요하지만, 호출측에서 이미 알고 있음
        return toDto(nullSafeCompanyId(d), d);
    }

    public void deleteTask(String taskId) {
        if (!taskRepo.delete(taskId))
            throw new ApiException(404, "처리업무를 찾을 수 없습니다.");
    }

    // 전체 갈아끼우기(프론트에서 화면 상태 그대로 저장)
    public BulkUpdateResponse bulkReplace(String companyId, List<TaskBulkUpdateRequest> reqs) {
        List<Document> newTasks = new ArrayList<>();
        Date now = new Date();
        for (TaskBulkUpdateRequest r : reqs) {
            ObjectId id = (r.getId() == null || r.getId().isBlank())
                    ? new ObjectId()
                    : new ObjectId(r.getId());
            Document d = new Document()
                    .append("_id", id)
                    .append("taskName", nvl(r.getTaskName()))
                    .append("purpose", nvl(r.getPurpose()))
                    .append("personalInfo", nvl(r.getPersonalInfo()))
                    .append("department", nvl(r.getDepartment()))
                    .append("updatedAt", now);
            if (r.getId() == null || r.getId().isBlank()) d.append("createdAt", now);
            newTasks.add(d);
        }
        taskRepo.replaceAll(companyId, newTasks);

        BulkUpdateResponse res = new BulkUpdateResponse();
        res.setUpdatedCount(newTasks.size());
        return res;
    }

    private String nvl(String s) { return s == null ? "" : s; }

    // companyId를 안전하게 추출(없으면 null)
    private String nullSafeCompanyId(Document anyTaskDoc) {
        // 단건 조회 경로에서는 companyId를 못 실어오므로 null 허용
        return null;
    }
}