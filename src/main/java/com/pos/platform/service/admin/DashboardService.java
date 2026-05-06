package com.pos.platform.service.admin;

import com.pos.platform.dto.admin.DashboardResponse;

/**
 * 数据概览服务接口
 */
public interface DashboardService {

    /**
     * 获取数据概览
     * @return 数据概览响应
     */
    DashboardResponse getDashboard();

    /**
     * 获取今日数据
     * @return 今日数据响应
     */
    DashboardResponse getTodayData();
}
