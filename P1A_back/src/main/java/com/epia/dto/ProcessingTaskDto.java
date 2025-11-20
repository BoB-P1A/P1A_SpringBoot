package com.epia.dto;

public class ProcessingTaskDto {
    public String id;
    public String taskName;
    public String purpose;
    public String department;
    public String infomation;
    public String companyId;

    public ProcessingTaskDto() {}

    public ProcessingTaskDto(String id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }

    public ProcessingTaskDto(String id, String taskName, String purpose,
                             String department, String infomation, String companyId) {
        this.id = id;
        this.taskName = taskName;
        this.purpose = purpose;
        this.department = department;
        this.infomation = infomation;
        this.companyId = companyId;
    }
}