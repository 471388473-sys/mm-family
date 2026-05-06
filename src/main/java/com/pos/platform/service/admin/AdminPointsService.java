package com.pos.platform.service.admin;

import com.pos.platform.dto.admin.*;
import com.pos.platform.entity.points.PointsAccount;

import java.util.List;

/**
 * 管理端积分服务接口
 */
public interface AdminPointsService {

    /**
     * 分页查询积分记录列表
     */
    List<AdminPointsRecordResponse> getPointsRecordList(PointsRecordQueryRequest query);

    /**
     * 获取积分记录总数
     */
    Long getPointsRecordCount(PointsRecordQueryRequest query);

    /**
     * 查询用户积分账户信息
     */
    AdminPointsAccountResponse getUserPointsAccount(Long userId);

    /**
     * 分页查询用户积分账户列表
     */
    List<AdminPointsAccountResponse> getUserPointsAccountList(String phone, Integer page, Integer size);

    /**
     * 获取用户积分账户总数
     */
    Long getUserPointsAccountCount(String phone);

    /**
     * 管理员调整用户积分/余额
     */
    void adjustPoints(PointsAdjustRequest request, Long operatorId);

    /**
     * 手动导入积分
     */
    void importPoints(Long userId, Integer points, String description, Long operatorId);
}
