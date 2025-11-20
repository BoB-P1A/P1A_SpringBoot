package com.epia.dto;

import com.epia.domain.EvaluationItem;

public class EvaluationItemDto {
    public Integer id;
    public String  companyId;
    public String  area;
    public String  field;
    public String  subField;
    public String  no;
    public String  item;

    public static EvaluationItemDto of(EvaluationItem e, String outerCompanyId) {
        var d = new EvaluationItemDto();
        d.id        = e.id;
        d.companyId = outerCompanyId;
        d.area      = e.area;
        d.field     = e.field;
        d.subField  = e.subField;
        d.no        = e.no;
        d.item      = e.item;
        return d;
    }
}