package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.AnnouncementResponse;
import com.pos.platform.entity.announcement.Announcement;
import com.pos.platform.mapper.AnnouncementMapper;
import com.pos.platform.service.mini.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 公告服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    @Override
    public List<AnnouncementResponse> getAnnouncementList(String type, int page, int size) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)  // 已发布
                    .orderByDesc(Announcement::getIsTop)  // 置顶优先
                    .orderByDesc(Announcement::getPublishedAt);  // 然后按发布时间
        
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Announcement::getType, type);
        }
        
        Page<Announcement> result = announcementMapper.selectPage(new Page<>(page, size), queryWrapper);
        
        return result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementResponse getAnnouncementDetail(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        if (announcement.getStatus() != 1) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_OFFLINE);
        }
        return convertToResponse(announcement);
    }

    @Override
    public List<AnnouncementResponse> getLatestAnnouncements(int limit) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getStatus, 1)  // 已发布
                    .orderByDesc(Announcement::getIsTop)  // 置顶优先
                    .orderByDesc(Announcement::getPublishedAt)  // 然后按发布时间
                    .last("LIMIT " + limit);
        
        List<Announcement> announcements = announcementMapper.selectList(queryWrapper);
        return announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AnnouncementResponse convertToResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .summary(announcement.getSummary())
                .type(announcement.getType())
                .typeDesc(getTypeDesc(announcement.getType()))
                .isTop(announcement.getIsTop())
                .publishedAt(announcement.getPublishedAt())
                .build();
    }

    private String getTypeDesc(String type) {
        if (type == null) {
            return "公告";
        }
        switch (type) {
            case "important":
                return "重要通知";
            case "notice":
                return "系统公告";
            case "activity":
                return "活动公告";
            default:
                return "公告";
        }
    }
}
