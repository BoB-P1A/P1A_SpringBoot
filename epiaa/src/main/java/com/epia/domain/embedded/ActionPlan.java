package com.epia.domain.embedded;

public class ActionPlan {
  public String id;
  public String action;     // 조치 내용
  public String owner;      // 담당자
  public String dueDate;    // YYYY-MM-DD
  public String status;     // OPEN|IN_PROGRESS|DONE
}