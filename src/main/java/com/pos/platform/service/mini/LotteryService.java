package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.LotteryConfigResponse;
import com.pos.platform.dto.mini.LotteryRecordResponse;
import com.pos.platform.dto.mini.LotteryResultResponse;

import java.util.List;

/**
 * 抽奖服务接口
 */
public interface LotteryService {

    /**
     * 获取抽奖配置信息
     */
    LotteryConfigResponse getConfig(Long userId);

    /**
     * 执行抽奖
     */
    LotteryResultResponse draw(Long userId);

    /**
     * 获取用户抽奖记录列表
     */
    List<LotteryRecordResponse> getRecordList(Long userId, Integer page, Integer size);

    /**
     * 获取用户抽奖记录总数
     */
    Long getRecordCount(Long userId);
}
