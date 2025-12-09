package com.epia.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("historyLogs")
public class HistoryLog {
    @Id
    public ObjectId id;

    // 회사 정보
    public String companyId;

    // 평가항목 정보
    public String area;           // 평가영역
    public String field;          // 평가분야
    public String subField;       // 세부분야
    public String no;             // 평가항목 번호
    public String item;           // 평가항목 (질의문)

    // 평가대상 정보
    public String evaluationType; // "lifecycle" | "technical" | "security"
    public String targetId;       // taskId 또는 systemId
    public String targetName;     // taskName 또는 systemName

    // 평가 결과 변경 내역
    public String previousStatus;    // 이전 이행 여부
    public String newStatus;         // 새로운 이행 여부
    public String previousEvidence;  // 이전 평가 근거
    public String newEvidence;       // 새로운 평가 근거

    // 변경자 정보
    public ChangedBy changedBy;
    public Instant changedAt;

    public HistoryLog() {}

    public static class ChangedBy {
        public String accountId;
        public String loginId;
        public String name;

        public ChangedBy() {}

        public ChangedBy(String accountId, String loginId, String name) {
            this.accountId = accountId;
            this.loginId = loginId;
            this.name = name;
        }
    }
}