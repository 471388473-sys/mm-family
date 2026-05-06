package com.pos.platform.service.impl.mini;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.mini.*;
import com.pos.platform.entity.pos.PosApplication;
import com.pos.platform.entity.system.SmsLog;
import com.pos.platform.entity.user.User;
import com.pos.platform.mapper.PosApplicationMapper;
import com.pos.platform.mapper.SmsLogMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.service.mini.PosApplyService;
import com.pos.platform.service.mini.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * POS机领取服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PosApplyServiceImpl implements PosApplyService {

    private final PosApplicationMapper posApplicationMapper;
    private final UserMapper userMapper;
    private final SmsService smsService;
    private final SmsLogMapper smsLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PosApplyRecordResponse submitApply(PosApplyRequest request, Long userId, String ip) {
        // 1. 检查是否已提交过申请（有待处理的申请）
        if (hasPendingApplication(userId)) {
            throw new BusinessException(ErrorCode.POS_APPLICATION_EXISTS);
        }
        
        // 2. 验证验证码
        boolean valid = smsService.verifyAndConsumeCode(request.getPhone(), request.getCode(), "pos_apply");
        if (!valid) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }
        
        // 3. 创建申请记录
        PosApplication application = new PosApplication();
        application.setUserId(userId);
        application.setName(request.getName());
        application.setPhone(request.getPhone());
        application.setAddress(request.getAddress());
        application.setStatus(1); // 已提交
        posApplicationMapper.insert(application);
        
        log.info("POS机申请提交成功: userId={}, applicationId={}", userId, application.getId());
        
        return buildResponse(application);
    }

    @Override
    public List<PosApplyRecordResponse> getApplyRecords(Long userId, int page, int size) {
        LambdaQueryWrapper<PosApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PosApplication::getUserId, userId)
                .orderByDesc(PosApplication::getCreatedAt);
        
        // MyBatis Plus分页
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PosApplication> pageObj = 
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.IPage<PosApplication> result = 
                posApplicationMapper.selectPage(pageObj, queryWrapper);
        
        return result.getRecords().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PosApplyRecordResponse getApplyDetail(Long userId, Long applyId) {
        PosApplication application = posApplicationMapper.selectById(applyId);
        if (application == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        }
        
        // 验证是否属于当前用户
        if (!application.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION);
        }
        
        return buildResponse(application);
    }

    @Override
    public boolean hasPendingApplication(Long userId) {
        LambdaQueryWrapper<PosApplication> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PosApplication::getUserId, userId)
                .eq(PosApplication::getStatus, 1); // 待处理状态
        Long count = posApplicationMapper.selectCount(queryWrapper);
        return count > 0;
    }
    
    private PosApplyRecordResponse buildResponse(PosApplication application) {
        String statusDesc;
        switch (application.getStatus()) {
            case 1:
                statusDesc = "已提交";
                break;
            case 2:
                statusDesc = "已处理";
                break;
            default:
                statusDesc = "未知";
        }
        
        return PosApplyRecordResponse.builder()
                .id(application.getId())
                .name(application.getName())
                .phone(application.getPhone())
                .address(application.getAddress())
                .status(application.getStatus())
                .statusDesc(statusDesc)
                .processedAt(application.getProcessedAt())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
