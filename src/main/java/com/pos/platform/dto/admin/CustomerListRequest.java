package com.pos.platform.dto.admin;

import lombok.Data;

/**
 * 客户列表查询请求
 */
@Data
public class CustomerListRequest {

    /** 页码 */
    private Integer page = 1;

    /** 每页数量 */
    private Integer pageSize = 20;

    /** 关键词(手机号/昵称/openid) */
    private String keyword;

    /** 状态：1-正常 0-禁用 */
    private Integer status;

    /** 注册开始时间 */
    private String startDate;

    /** 注册结束时间 */
    private String endDate;
}
