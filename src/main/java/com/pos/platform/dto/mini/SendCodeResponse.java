package com.pos.platform.dto.mini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送验证码响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCodeResponse {
    
    /** 发送状态 */
    private Boolean success;
    
    /** 提示信息 */
    private String message;
    
    /** 剩余次数 */
    private Integer remainCount;
}
