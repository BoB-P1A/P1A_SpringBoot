package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("technical_systems")
public class TechnicalSystem {
    @Id
    public Integer id;
    public String companyId;
    public String systemName;
}