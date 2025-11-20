package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document("technical_improvements")
public class TechnicalImprovement {
    @Id
    public String id;
    public String companyId;

    public Map<String, Info> improvements; // key: systemId-no

    public static class Info {
        public String relatedLaw;
        public String riskFactor;
        public String improvementPlan;
    }
}