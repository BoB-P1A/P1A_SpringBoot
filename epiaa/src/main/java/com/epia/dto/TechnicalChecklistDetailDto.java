package com.epia.dto;

import java.util.List;

public class TechnicalChecklistDetailDto {
    // 시스템 정보
    public String systemName;

    // 체크리스트 정보
    public String no;
    public String status;
    public String evidence;
    public List<Object> files;

    // 평가항목 정보 (Company.evaluationItems에서 가져옴)
    public String item;              // 질의문
    public String law;               // 관련법률
    public String riskFactors;       // 침해요인
    public String improvementGuides; // 개선가이드
    public String subField;
    public String area;
    public String field;

    public TechnicalChecklistDetailDto() {
    }

    public TechnicalChecklistDetailDto(String systemName, String no, String item, String status,
                                       String evidence, List<Object> files, String law,
                                       String riskFactors, String improvementGuides,
                                       String subField, String area, String field) {
        this.systemName = systemName;
        this.no = no;
        this.item = item;
        this.status = status;
        this.evidence = evidence;
        this.files = files;
        this.law = law;
        this.riskFactors = riskFactors;
        this.improvementGuides = improvementGuides;
        this.subField = subField;
        this.area = area;
        this.field = field;
    }
}