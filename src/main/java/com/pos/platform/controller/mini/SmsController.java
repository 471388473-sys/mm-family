package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.SendCodeRequest;
import com.pos.platform.dto.mini.SendCodeResponse;
import com.pos.platform.service.mini.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 短信控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.SMS)
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<SendCodeResponse> sendCode(@Valid @RequestBody SendCodeRequest request,
                                                   HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        SendCodeResponse response = smsService.sendCode(request, ip);
        return ApiResponse.success(response);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify-code")
    public ApiResponse<Boolean> verifyCode(@RequestParam String phone,
                                           @RequestParam String code,
                                           @RequestParam String scene) {
        boolean valid = smsService.verifyCode(phone, code, scene);
        return ApiResponse.success(valid);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
