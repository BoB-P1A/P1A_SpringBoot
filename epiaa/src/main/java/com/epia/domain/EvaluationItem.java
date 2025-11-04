package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("evaluation_items")
public class EvaluationItem {

    @Id
    public Integer id; // auto-increment

    public String companyId;

    public String area;
    public String field;
    public String subField;

    public String no;      // ex: "1.2.3"
    public String item;    // 내용
}