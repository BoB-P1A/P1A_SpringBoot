// domain/Task.java
package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
public class Task {
    @Id
    public Integer id;       // Auto-increment
    public String companyId;
    public String taskName;
    public String purpose;
    public String personalInfo;
    public String department;
}