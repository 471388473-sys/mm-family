package com.pos.platform.dto.mini;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 客服信息响应
 */
@Data
@Builder
public class CustomerServiceResponse {
    
    /** 客服热线 */
    private String hotline;
    
    /** 客服微信号 */
    private String wechat;
    
    /** 客服二维码URL */
    private String qrcodeUrl;
    
    /** 工作时间 */
    private String workHours;
    
    /** 常见问题列表 */
    private List<FaqResponse> faqs;
}
