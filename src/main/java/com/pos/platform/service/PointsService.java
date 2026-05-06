package com.pos.platform.service;

import com.pos.platform.dto.response.PointsAccountResponse;
import com.pos.platform.dto.response.PointsRecordResponse;
import com.pos.platform.entity.points.PointsAccount;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分服务接口
 */
public interface PointsService {

    /**
     * 获取用户积分账户信息
     */
    PointsAccountResponse getAccountInfo(Long userId);

    /**
     * 获取积分兑换比例
     */
    BigDecimal getExchangeRate();

    /**
     * 积分兑换余额
     */
    PointsAccountResponse exchange(Long userId, Integer points);

    /**
     * 获取积分记录列表
     */
    List<PointsRecordResponse> getRecordList(Long userId, String type, Integer page, Integer size);

    /**
     * 获取总记录数
     */
    Long getRecordCount(Long userId, String type);
}
