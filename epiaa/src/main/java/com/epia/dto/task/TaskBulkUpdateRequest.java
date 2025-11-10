package com.epia.dto.task;

import lombok.Data;

@Data
public class TaskBulkUpdateRequest {
    private String id;
    private String taskName;
    private String purpose;
    private String personalInfo;
    private String department;
}