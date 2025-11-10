package com.epia.dto.task;

import lombok.Data;

@Data
public class TaskCreateRequest {
    private String companyId;
    private String taskName;
    private String purpose;
    private String personalInfo;
    private String department;
}