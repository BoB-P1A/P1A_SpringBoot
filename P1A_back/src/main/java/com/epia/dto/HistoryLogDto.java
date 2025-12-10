
package com.epia.dto;

import com.epia.domain.HistoryLog;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HistoryLogDto {
    public String _id;
    public String companyId;

    // 평가항목 정보
    public String area;
    public String field;
    public String subField;
    public String no;
    public String item;

    // 평가대상 정보
    public String evaluationType;
    public String targetId;
    public String targetName;

    // 평가 결과 변경 내역
    public String previousStatus;
    public String newStatus;
    public String previousEvidence;
    public String newEvidence;

    // 변경자 정보
    public ChangedByDto changedBy;
    public String changedAt;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.of("Asia/Seoul"));

    public static HistoryLogDto from(HistoryLog log) {
        HistoryLogDto dto = new HistoryLogDto();
        dto._id = log.id != null ? log.id.toHexString() : null;
        dto.companyId = log.companyId;

        dto.area = log.area;
        dto.field = log.field;
        dto.subField = log.subField;
        dto.no = log.no;
        dto.item = log.item;

        dto.evaluationType = log.evaluationType;
        dto.targetId = log.targetId;
        dto.targetName = log.targetName;

        dto.previousStatus = log.previousStatus;
        dto.newStatus = log.newStatus;
        dto.previousEvidence = log.previousEvidence;
        dto.newEvidence = log.newEvidence;

        if (log.changedBy != null) {
            dto.changedBy = new ChangedByDto();
            dto.changedBy.accountId = log.changedBy.accountId;
            dto.changedBy.loginId = log.changedBy.loginId;
            dto.changedBy.name = log.changedBy.name;
        }

        dto.changedAt = log.changedAt != null ? FORMATTER.format(log.changedAt) : null;

        return dto;
    }

    public static class ChangedByDto {
        public String accountId;
        public String loginId;
        public String name;
    }
}