package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("technical_checklists")
public class TechnicalChecklistRow {

    @Id
    public Integer id;  // Auto-increment
    public String companyId;
    public String systemName; // ★ 기술 영역에서는 시스템명으로 연결됨

    public String field;
    public String subField;
    public String no;
    public String item;
    public String status;
    public String evidence;

    public List<Object> files;

    public TechnicalChecklistRow() {}

    // 평가항목 저장 시 자동 생성
    public TechnicalChecklistRow(String companyId, EvaluationItem i) {
        this.companyId = companyId;
        this.systemName = null; // ★ 시스템 생성 시 매핑됨
        this.field = i.field;
        this.subField = i.subField;
        this.no = i.no;
        this.item = i.item;
        this.status = null;
        this.evidence = null;
        this.files = null;
    }
}