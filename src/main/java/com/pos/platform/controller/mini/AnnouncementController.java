package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.AnnouncementResponse;
import com.pos.platform.service.mini.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序公告控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.ANNOUNCEMENT)
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * 获取公告列表
     * @param type 类型筛选（可选）：important/notice/activity
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     */
    @GetMapping
    public ApiResponse<List<AnnouncementResponse>> getAnnouncementList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<AnnouncementResponse> list = announcementService.getAnnouncementList(type, page, size);
        return ApiResponse.success(list);
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     */
    @GetMapping("/{id}")
    public ApiResponse<AnnouncementResponse> getAnnouncementDetail(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.getAnnouncementDetail(id);
        return ApiResponse.success(response);
    }
}
