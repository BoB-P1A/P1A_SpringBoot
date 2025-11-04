package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document("technical_action_plans")
public class TechnicalActionPlan {
    @Id
    public String id;
    public String companyId;

    public Map<String, Plan> actionPlans; // key: systemName-no

    public static class Plan {
        public String systemName;
        public String code;
        public String actionPlan;
        public String actionPeriod;
        public String department;
        public String manager;
        public String actionDate;
    }
}