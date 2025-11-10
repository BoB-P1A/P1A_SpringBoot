package com.epia.dto.task;

import lombok.Data;

@Data
public class TaskUpdateRequest {
    private String taskName;
    private String purpose;
    private String personalInfo;
    private String department;
}