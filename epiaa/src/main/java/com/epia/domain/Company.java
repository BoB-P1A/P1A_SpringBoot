package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Document("companies")
public class Company {

    @Id
    public String id;

    public String name;
    public String contactName;
    public String contactPhone;

    public List<Account> accounts;
    
    public List<EvaluationItem> evaluationItems = new ArrayList<>();

    public Instant createdAt;
    public Instant updatedAt;
}