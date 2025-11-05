package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("processing_tasks")
public class ProcessingTask {

    @Id
    public Integer id;
    public String companyId;
    public String taskName;
    public String purpose;
    public String personalData;
    public String department;
}