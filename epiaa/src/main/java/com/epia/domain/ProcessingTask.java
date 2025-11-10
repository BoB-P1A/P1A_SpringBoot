package com.epia.domain;

import lombok.Data;
import org.bson.types.ObjectId;
import com.epia.domain.embedded.*;
import java.util.Date;
import java.util.List;

@Data
public class ProcessingTask {
    private ObjectId _id;
    private String taskName;
    private String department;
    private String responsiblePerson;
    private String purpose;
    private String personalInfoItems;
    private String retentionPeriod;
    private Date createdAt;
    private Date updatedAt;
    private List<FlowTableEntry> flowTable;
    private List<FlowChartNode> flowChart;
    private List<ChecklistItem> lifecycleChecklist;
    private List<ChecklistItem> technicalChecklist;
    private List<ChecklistItem> securityChecklist;
    private List<Improvement> improvements;
    private List<ActionPlan> actionPlans;
}