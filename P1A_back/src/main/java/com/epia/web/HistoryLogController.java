
package com.epia.web;

import com.epia.dto.HistoryLogDto;
import com.epia.dto.HistoryLogFilterOptionsDto;
import com.epia.dto.PaginatedResponse;
import com.epia.service.HistoryLogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history-logs")
public class HistoryLogController {

    private final HistoryLogService historyLogService;

    public HistoryLogController(HistoryLogService historyLogService) {
        this.historyLogService = historyLogService;
    }

    /**
     * 히스토리 로그 필터링 조회
     * GET /api/history-logs?companyId=xxx&area=xxx&page=1&pageSize=20
     */
    @GetMapping
    public PaginatedResponse<HistoryLogDto> getHistoryLogs(
            @RequestParam String companyId,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String no,
            @RequestParam(required = false) String targetName,
            @RequestParam(required = false) String previousStatus,
            @RequestParam(required = false) String newStatus,
            @RequestParam(required = false) String changedByName,
            @RequestParam(required = false) String changedAtFrom,
            @RequestParam(required = false) String changedAtTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        System.out.println("GET /api/history-logs - companyId: " + companyId +
                ", area: " + area + ", page: " + page + ", pageSize: " + pageSize);

        return historyLogService.getFilteredHistoryLogs(
                companyId, area, no, targetName, previousStatus, newStatus,
                changedByName, changedAtFrom, changedAtTo, page, pageSize
        );
    }

    /**
     * 특정 히스토리 로그 상세 조회
     * GET /api/history-logs/{id}
     */
    @GetMapping("/{id}")
    public HistoryLogDto getHistoryLogById(@PathVariable String id) {
        System.out.println("GET /api/history-logs/" + id);
        return historyLogService.getHistoryLogById(id);
    }

    /**
     * 필터 옵션 조회 (드롭다운용)
     * GET /api/history-logs/filter-options?companyId=xxx
     */
    @GetMapping("/filter-options")
    public HistoryLogFilterOptionsDto getFilterOptions(@RequestParam String companyId) {
        System.out.println("GET /api/history-logs/filter-options - companyId: " + companyId);
        return historyLogService.getFilterOptions(companyId);
    }
}