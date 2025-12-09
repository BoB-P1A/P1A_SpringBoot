package com.epia.service;

import com.epia.domain.Company;
import com.epia.domain.EvaluationItem;
import com.epia.domain.HistoryLog;
import com.epia.domain.embedded.ChecklistItem;
import com.epia.dto.HistoryLogDto;
import com.epia.dto.HistoryLogFilterOptionsDto;
import com.epia.dto.PaginatedResponse;
import com.epia.repo.CompanyRepo;
import com.epia.repo.HistoryLogRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryLogService {

    private final HistoryLogRepo historyLogRepo;
    private final CompanyRepo companyRepo;
    private final MongoTemplate mongoTemplate;

    public HistoryLogService(HistoryLogRepo historyLogRepo, CompanyRepo companyRepo, MongoTemplate mongoTemplate) {
        this.historyLogRepo = historyLogRepo;
        this.companyRepo = companyRepo;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 체크리스트 변경 이력을 저장합니다.
     */
    public void logChecklistChange(
            String companyId,
            String evaluationType, // "lifecycle", "technical", "security"
            String targetId,
            String targetName,
            String no,
            ChecklistItem previousItem,
            ChecklistItem newItem,
            String accountId,
            String loginId,
            String accountName) {

        // previousItem이 없고 newItem도 비어있으면 로그 제외 (초기 저장 시)
        if (previousItem == null &&
                (newItem.status == null || newItem.status.isEmpty()) &&
                (newItem.evidence == null || newItem.evidence.isEmpty())) {
            return; // 초기값이 모두 비어있으면 로그 제외
        }

        // 변경사항이 없으면 로그를 남기지 않음
        boolean statusChanged = !isSameValue(
                previousItem != null ? previousItem.status : null,
                newItem.status
        );
        boolean evidenceChanged = !isSameValue(
                previousItem != null ? previousItem.evidence : null,
                newItem.evidence
        );

        if (!statusChanged && !evidenceChanged) {
            return; // 변경사항 없음
        }

        // 평가항목 정보 조회
        Company company = companyRepo.findById(companyId).orElse(null);
        if (company == null) {
            return;
        }

        EvaluationItem evalItem = company.evaluationItems.stream()
                .filter(item -> item.no.equals(no))
                .findFirst()
                .orElse(null);

        if (evalItem == null) {
            return;
        }

        // HistoryLog 생성
        HistoryLog log = new HistoryLog();
        log.companyId = companyId;

        // 평가항목 정보
        log.area = evalItem.area;
        log.field = evalItem.field;
        log.subField = evalItem.subField;
        log.no = evalItem.no;
        log.item = evalItem.item;

        // 평가대상 정보
        log.evaluationType = evaluationType;
        log.targetId = targetId;
        log.targetName = targetName;

        // 변경 내역
        log.previousStatus = previousItem != null ? previousItem.status : null;
        log.newStatus = newItem.status;
        log.previousEvidence = previousItem != null ? previousItem.evidence : "";
        log.newEvidence = newItem.evidence;

        // 변경자 정보
        log.changedBy = new HistoryLog.ChangedBy(accountId, loginId, accountName);
        log.changedAt = Instant.now();

        // 저장
        historyLogRepo.save(log);

        System.out.println("HistoryLog 저장 완료: " + evaluationType + " / " + targetName + " / " + no);
    }

    /**
     * 필터 옵션 조회
     */
    public HistoryLogFilterOptionsDto getFilterOptions(String companyId) {
        // 고유한 평가영역 조회
        List<String> areas = mongoTemplate.query(HistoryLog.class)
                .distinct("area")
                .matching(Query.query(Criteria.where("companyId").is(companyId)))
                .as(String.class)
                .all()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        // 고유한 평가대상 조회
        List<String> targetNames = mongoTemplate.query(HistoryLog.class)
                .distinct("targetName")
                .matching(Query.query(Criteria.where("companyId").is(companyId)))
                .as(String.class)
                .all()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        // 고유한 변경자 이름 조회
        List<String> changedByNames = mongoTemplate.query(HistoryLog.class)
                .distinct("changedBy.name")
                .matching(Query.query(Criteria.where("companyId").is(companyId)))
                .as(String.class)
                .all()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        return new HistoryLogFilterOptionsDto(areas, targetNames, changedByNames);
    }

    /**
     * 필터링 및 페이지네이션을 적용한 히스토리 로그 조회
     */
    public PaginatedResponse<HistoryLogDto> getFilteredHistoryLogs(
            String companyId,
            String area,
            String no,
            String targetName,
            String previousStatus,
            String newStatus,
            String changedByName,
            String changedAtFrom,
            String changedAtTo,
            int page,
            int pageSize) {

        // 동적 쿼리 생성
        Query query = new Query();
        query.addCriteria(Criteria.where("companyId").is(companyId));

        if (area != null && !area.isEmpty()) {
            query.addCriteria(Criteria.where("area").is(area));
        }

        if (no != null && !no.isEmpty()) {
            query.addCriteria(Criteria.where("no").regex(no, "i")); // 대소문자 구분 없이 부분 일치
        }

        if (targetName != null && !targetName.isEmpty()) {
            query.addCriteria(Criteria.where("targetName").is(targetName));
        }

        if (previousStatus != null && !previousStatus.isEmpty()) {
            query.addCriteria(Criteria.where("previousStatus").is(previousStatus));
        }

        if (newStatus != null && !newStatus.isEmpty()) {
            query.addCriteria(Criteria.where("newStatus").is(newStatus));
        }

        if (changedByName != null && !changedByName.isEmpty()) {
            query.addCriteria(Criteria.where("changedBy.name").is(changedByName));
        }

        // 날짜 범위 필터
        if (changedAtFrom != null && !changedAtFrom.isEmpty()) {
            try {
                // ISO DateTime 형식을 파싱 (예: "2025-12-08T00:00:00")
                Instant fromInstant;
                if (changedAtFrom.contains("T")) {
                    // ISO DateTime 형식
                    fromInstant = Instant.parse(changedAtFrom.endsWith("Z") ? changedAtFrom : changedAtFrom + "Z");
                } else {
                    // ISO Date 형식 (예: "2025-12-08")
                    fromInstant = LocalDate.parse(changedAtFrom, DateTimeFormatter.ISO_DATE)
                            .atStartOfDay(ZoneId.of("Asia/Seoul"))
                            .toInstant();
                }
                query.addCriteria(Criteria.where("changedAt").gte(fromInstant));
            } catch (Exception e) {
                System.err.println("날짜 파싱 오류 (changedAtFrom): " + changedAtFrom + " - " + e.getMessage());
            }
        }

        if (changedAtTo != null && !changedAtTo.isEmpty()) {
            try {
                // ISO DateTime 형식을 파싱
                Instant toInstant;
                if (changedAtTo.contains("T")) {
                    // ISO DateTime 형식
                    toInstant = Instant.parse(changedAtTo.endsWith("Z") ? changedAtTo : changedAtTo + "Z");
                } else {
                    // ISO Date 형식 - 당일 23:59:59까지 포함
                    toInstant = LocalDate.parse(changedAtTo, DateTimeFormatter.ISO_DATE)
                            .plusDays(1)
                            .atStartOfDay(ZoneId.of("Asia/Seoul"))
                            .toInstant();
                }
                query.addCriteria(Criteria.where("changedAt").lt(toInstant));
            } catch (Exception e) {
                System.err.println("날짜 파싱 오류 (changedAtTo): " + changedAtTo + " - " + e.getMessage());
            }
        }

        // 전체 개수 조회
        long total = mongoTemplate.count(query, HistoryLog.class);

        // 정렬 및 페이지네이션 적용
        query.with(Sort.by(Sort.Direction.DESC, "changedAt"));
        query.with(PageRequest.of(page - 1, pageSize));

        // 결과 조회
        List<HistoryLog> logs = mongoTemplate.find(query, HistoryLog.class);
        List<HistoryLogDto> dtos = logs.stream()
                .map(HistoryLogDto::from)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(dtos, page, pageSize, total);
    }

    /**
     * 특정 히스토리 로그 조회
     */
    public HistoryLogDto getHistoryLogById(String id) {
        HistoryLog log = historyLogRepo.findById(new org.bson.types.ObjectId(id))
                .orElseThrow(() -> new IllegalArgumentException("히스토리 로그를 찾을 수 없습니다: " + id));
        return HistoryLogDto.from(log);
    }

    /**
     * 회사별 전체 이력 조회
     */
    public List<HistoryLog> getHistoryByCompany(String companyId) {
        return historyLogRepo.findByCompanyIdOrderByChangedAtDesc(companyId);
    }

    /**
     * 특정 평가대상의 이력 조회
     */
    public List<HistoryLog> getHistoryByTarget(String companyId, String targetId) {
        return historyLogRepo.findByCompanyIdAndTargetIdOrderByChangedAtDesc(companyId, targetId);
    }

    /**
     * 특정 평가항목의 이력 조회
     */
    public List<HistoryLog> getHistoryByEvaluationItem(String companyId, String no) {
        return historyLogRepo.findByCompanyIdAndNoOrderByChangedAtDesc(companyId, no);
    }

    /**
     * 두 값이 같은지 비교 (null 처리 포함)
     */
    private boolean isSameValue(String oldValue, String newValue) {
        if (oldValue == null && newValue == null) return true;
        if (oldValue == null || newValue == null) return false;
        return oldValue.equals(newValue);
    }
}