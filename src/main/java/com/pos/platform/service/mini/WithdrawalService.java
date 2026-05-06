package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.WithdrawalConfigResponse;
import com.pos.platform.dto.mini.WithdrawalRecordResponse;
import com.pos.platform.dto.mini.WithdrawalRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现服务接口
 */
public interface WithdrawalService {

    /**
     * 获取提现配置
     */
    WithdrawalConfigResponse getConfig();

    /**
     * 申请提现
     */
    WithdrawalRecordResponse applyWithdrawal(Long userId, WithdrawalRequest request);

    /**
     * 获取用户提现记录列表
     */
    List<WithdrawalRecordResponse> getRecordList(Long userId, Integer page, Integer size);

    /**
     * 获取用户提现记录总数
     */
    Long getRecordCount(Long userId);

    /**
     * 获取可提现余额
     */
    BigDecimal getWithdrawableBalance(Long userId);
}
