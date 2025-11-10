package com.epia.dto.task;

import lombok.Data;
import java.util.List;

@Data
public class TaskDtos {
    private String id;
    private String companyId;
    private String taskName;
    private String purpose;
    private String personalInfo;
    private String department;
}

