package com.epia.domain;

import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.domain.embedded.Flow;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class ProcessingTask {
    @Field("_id")
    public ObjectId id;  // ← Integer에서 ObjectId로 변경, @Id 제거

    public String companyId;
    public String taskName;
    public String purpose;
    public String department;
    public String infomation;

    @CreatedDate
    public Instant createdAt;

    @LastModifiedDate
    public Instant updatedAt;

    public Flow flow;
    public List<ChecklistItem> lifecycleChecklist = new ArrayList<>();
    public List<ActionPlan> actionPlans = new ArrayList<>();

    public ProcessingTask() {
        if (this.flow == null) {
            this.flow = new Flow();
        }
    }
}