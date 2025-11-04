package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("lifecycle_checklists")
public class LifecycleChecklistRow {
    @Id
    public Integer id;  // Auto-increment
    public String companyId;
    public String taskName;  // 처리업무명 (나중에 연결됨)

    public String field;
    public String subField;
    public String no;
    public String item;
    public String status;
    public String evidence;

    public List<Object> files;

    public LifecycleChecklistRow() {}

    public LifecycleChecklistRow(String companyId, EvaluationItem i) {
        this.companyId = companyId;
        this.taskName = null; // 업무 생성 시 매핑됨
        this.field = i.field;
        this.subField = i.subField;
        this.no = i.no;
        this.item = i.item;
        this.status = null;
        this.evidence = null;
        this.files = null;
    }
}