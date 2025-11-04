package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("security_improvements")
public class SecurityImprovement {
    @Id
    public String id;
    public String companyId;

    // key: "targetName-no"
    public Map<String, Info> improvements;

    public static class Info {
        public String relatedLaw;
        public String riskFactor;
        public String improvementPlan;
    }
}