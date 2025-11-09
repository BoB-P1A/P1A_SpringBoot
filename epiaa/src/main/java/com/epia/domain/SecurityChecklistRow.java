//package com.epia.domain;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import java.util.List;
//
//@Document("security_checklists")
//public class SecurityChecklistRow {
//
//    @Id
//    public Integer id;  // Auto-increment
//    public String companyId;
//    public String targetName; // ★ 보안성 검토에서는 검토 대상명
//
//    public String field;
//    public String subField;
//    public String no;
//    public String item;
//    public String status;
//    public String evidence;
//
//    public List<Object> files;
//
//    public SecurityChecklistRow() {}
//
//    // 평가항목 저장 시 자동 생성됨
//    public SecurityChecklistRow(String companyId, EvaluationItem i) {
//        this.companyId = companyId;
//        this.targetName = null; // ★ 대상 선택 시 매핑됨
//        this.field = i.field;
//        this.subField = i.subField;
//        this.no = i.no;
//        this.item = i.item;
//        this.status = null;
//        this.evidence = null;
//        this.files = null;
//    }
//}