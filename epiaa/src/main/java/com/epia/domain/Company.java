package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Document("companies")
public class Company {

    @Id
    public String id;

    public String name;
    public String contactName;
    public String contactPhone;

    public List<Account> accounts = new ArrayList<>();
    public List<EvaluationItem> evaluationItems = new ArrayList<>();
    public List<ProcessingTask> processingTasks = new ArrayList<>();
    public List<TechnicalSystem> technicalSystems = new ArrayList<>();
    public List<SecuritySystem> securitySystems = new ArrayList<>();

    public Instant createdAt;
    public Instant updatedAt;

}