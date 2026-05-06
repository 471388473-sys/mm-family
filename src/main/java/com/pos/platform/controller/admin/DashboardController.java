package com.pos.platform.controller.admin;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.admin.DashboardResponse;
import com.pos.platform.service.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 数据概览控制器
 */
@RestController
@RequestMapping(ApiPath.Admin.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取数据概览
     */
    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard() {
        DashboardResponse response = dashboardService.getDashboard();
        return ApiResponse.success(response);
    }

    /**
     * 获取今日数据
     */
    @GetMapping("/today")
    public ApiResponse<DashboardResponse> getTodayData() {
        DashboardResponse response = dashboardService.getTodayData();
        return ApiResponse.success(response);
    }
}
