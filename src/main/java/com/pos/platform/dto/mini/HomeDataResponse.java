package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页数据响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataResponse {

    /** 用户ID */
    private Long userId;

    /** 用户昵称 */
    private String nickname;

    /** 用户头像 */
    private String avatar;

    /** 当前积分 */
    private Integer points;

    /** 当前余额（元） */
    private BigDecimal balance;

    /** 累计兑换金额（元） */
    private BigDecimal totalExchanged;

    /** 累计提现金额（元） */
    private BigDecimal totalWithdrawn;

    /** 公告列表（最新5条） */
    private List<AnnouncementResponse> announcements;

    /** 是否已领取过POS机 */
    private Boolean hasAppliedPos;

    /** POS申请状态：null-未申请 1-已提交 2-已处理 */
    private Integer posApplyStatus;

    /** 抽奖剩余次数 */
    private Integer lotteryChances;
}
