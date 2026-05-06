package com.pos.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 微信openid */
    private String openid;

    /** 手机号 */
    private String phone;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 状态：1-正常 0-禁用 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 积分余额 */
    private Long pointsBalance;

    /** 可提现余额 */
    private BigDecimal withdrawableBalance;

    /** 绑定机具数量 */
    private Integer machineCount;

    /** 注册时间 */
    private LocalDateTime createdAt;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;
}
