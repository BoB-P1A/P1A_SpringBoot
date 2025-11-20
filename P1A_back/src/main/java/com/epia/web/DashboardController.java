package com.epia.web;

import com.epia.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 대시보드 통계 조회
     * - 영역별 이행률 (도넛 차트용)
     * - 분야별 이행률 (레이더 차트용)
     * - 세부분야별 이행률 (레이더 차트용)
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam String companyId) {
        System.out.println("GET /dashboard/stats - companyId: " + companyId);
        return dashboardService.getStats(companyId);
    }
}