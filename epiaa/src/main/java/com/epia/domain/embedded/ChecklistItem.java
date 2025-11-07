package com.epia.domain.embedded;

import java.util.*;
import java.util.ArrayList;

public class ChecklistItem {
    public String no;              // 평가항목 번호 (예: "2.1.1")
    public String status;       // 이행 상태
    public String evidence;     // 평가 근거 및 의견
    public List<Object> files;  // 첨부파일
}