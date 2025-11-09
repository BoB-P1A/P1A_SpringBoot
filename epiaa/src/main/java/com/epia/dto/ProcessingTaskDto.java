package com.epia.dto;

public class ProcessingTaskDto {
    public String id;
    public String taskName;

    public ProcessingTaskDto() {}

    public ProcessingTaskDto(String id, String taskName) {
        this.id = id;
        this.taskName = taskName;
    }
}