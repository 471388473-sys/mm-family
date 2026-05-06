package com.pos.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据概览响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 今日新增用户数 */
    private Long todayNewUsers;

    /** 昨日新增用户数 */
    private Long yesterdayNewUsers;

    /** 累计用户数 */
    private Long totalUsers;

    /** 今日交易额 */
    private String todayTransactionAmount;

    /** 昨日交易额 */
    private String yesterdayTransactionAmount;

    /** 累计交易额 */
    private String totalTransactionAmount;

    /** 今日交易笔数 */
    private Long todayTransactionCount;

    /** 昨日交易笔数 */
    private Long yesterdayTransactionCount;

    /** 累计交易笔数 */
    private Long totalTransactionCount;

    /** 今日提现申请数 */
    private Long todayWithdrawals;

    /** 待审核提现数 */
    private Long pendingWithdrawals;

    /** 今日抽奖参与次数 */
    private Long todayLotteryCount;

    /** 机具绑定数 */
    private Long boundMachines;

    /** 机具总数 */
    private Long totalMachines;

    /** 今日新增积分 */
    private Long todayPoints;

    /** 累计积分 */
    private Long totalPoints;
}
