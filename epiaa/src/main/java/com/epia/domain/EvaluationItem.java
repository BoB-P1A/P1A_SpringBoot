package com.epia.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class EvaluationItem {
    public Integer id;              // 회사별 auto-increment
    public String  companyId;       
    public String  area;
    public String  field;
    public String  subField;
    public String  no;
    public String  item;

    public String  riskFactors;
    public String  improvementGuides;
    public String  law;
}