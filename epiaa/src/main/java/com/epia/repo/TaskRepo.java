// com/epia/repo/TaskRepo.java
package com.epia.repo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TaskRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> companies() {
        return mongoTemplate.getCollection("companies");
    }

    // companies.processingTasks[] 전부 반환
    public List<Document> findAllByCompanyId(String companyId) {
        Document company = companies().find(Filters.eq("_id", new ObjectId(companyId))).first();
        if (company == null) return Collections.emptyList();
        List<Document> arr = company.getList("processingTasks", Document.class);
        return arr != null ? arr : Collections.emptyList();
    }

    // processingTasks 에 push
    public Document create(String companyId, Document newTask) {
        Bson filter = Filters.eq("_id", new ObjectId(companyId));
        companies().updateOne(filter, Updates.push("processingTasks", newTask));
        return newTask;
    }

    // processingTasks.$[task] 단건 수정
    public boolean update(String taskId, Document updateFields) {
        Bson filter = Filters.eq("processingTasks._id", new ObjectId(taskId));

        List<Bson> sets = new ArrayList<>();
        sets.add(Updates.set("processingTasks.$[task].updatedAt", new Date()));
        for (Map.Entry<String, Object> e : updateFields.entrySet()) {
            sets.add(Updates.set("processingTasks.$[task]." + e.getKey(), e.getValue()));
        }

        com.mongodb.client.model.UpdateOptions options = new com.mongodb.client.model.UpdateOptions()
                .arrayFilters(List.of(Filters.eq("task._id", new ObjectId(taskId))));
        return companies().updateOne(filter, Updates.combine(sets), options).getMatchedCount() > 0;
    }

    public boolean delete(String taskId) {
        Bson filter = Filters.eq("processingTasks._id", new ObjectId(taskId));
        Bson update = Updates.pull("processingTasks", new Document("_id", new ObjectId(taskId)));
        return companies().updateOne(filter, update).getMatchedCount() > 0;
    }

    // 통째로 교체(안정적)
    public void replaceAll(String companyId, List<Document> newTasks) {
        Bson filter = Filters.eq("_id", new ObjectId(companyId));
        companies().updateOne(filter, Updates.set("processingTasks", newTasks));
    }

    // 단건 조회(옵션)
    public Document findById(String taskId) {
        Document company = companies().find(Filters.eq("processingTasks._id", new ObjectId(taskId))).first();
        if (company == null) return null;
        List<Document> tasks = company.getList("processingTasks", Document.class);
        if (tasks == null) return null;
        for (Document d : tasks) {
            ObjectId _id = d.getObjectId("_id");
            if (_id != null && _id.toHexString().equals(taskId)) return d;
        }
        return null;
    }
}