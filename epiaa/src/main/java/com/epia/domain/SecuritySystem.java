package com.epia.domain;

import com.epia.domain.embedded.ActionPlan;
import com.epia.domain.embedded.ChecklistItem;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SecuritySystem {
    @Field("_id")
    public ObjectId id;

    public String systemName;

    @CreatedDate
    public Instant createdAt;

    @LastModifiedDate
    public Instant updatedAt;

    public List<ChecklistItem> securityChecklist = new ArrayList<>();
    public List<ActionPlan> actionPlans = new ArrayList<>();
}