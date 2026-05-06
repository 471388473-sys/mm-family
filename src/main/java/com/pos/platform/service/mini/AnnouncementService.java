package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.AnnouncementResponse;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {

    /**
     * 获取公告列表
     * @param type 类型筛选（可选）：important/notice/activity
     * @param page 页码
     * @param size 每页数量
     * @return 公告列表
     */
    List<AnnouncementResponse> getAnnouncementList(String type, int page, int size);

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    AnnouncementResponse getAnnouncementDetail(Long id);

    /**
     * 获取最新公告（用于首页展示）
     * @param limit 数量限制
     * @return 最新公告列表
     */
    List<AnnouncementResponse> getLatestAnnouncements(int limit);
}
