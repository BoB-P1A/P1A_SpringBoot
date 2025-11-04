package com.epia.domain.embedded;

import java.util.*;

public class ChecklistItem {
  public String id;
  public String no;      // "1.x.x"/"2.x.x"/"3.x.x"
  public String item;
  public String status;  // 이행|부분이행|미이행|해당없음
  public String evidence;
  public List<String> files = new ArrayList<>();
}