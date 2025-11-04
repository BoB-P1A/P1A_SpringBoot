package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("security_action_plans")
public class SecurityActionPlan {
    @Id
    public String id;
    public String companyId;

    // key: "targetName-no"
    public Map<String, Plan> actionPlans;

    public static class Plan {
        public String targetName;
        public String code;
        public String actionPlan;
        public String actionPeriod;
        public String department;
        public String manager;
        public String actionDate;
    }
}