package com.pos.platform.controller.admin;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.admin.AdminLoginRequest;
import com.pos.platform.dto.admin.AdminLoginResponse;
import com.pos.platform.service.admin.AdminAuthService;
import com.pos.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 管理端认证控制器
 */
@RestController
@RequestMapping(ApiPath.Admin.AUTH)
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final JwtUtil jwtUtil;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ApiResponse<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                                  HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        AdminLoginResponse response = adminAuthService.login(request, ip);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public ApiResponse<AdminLoginResponse> getAdminInfo(@RequestHeader("Authorization") String token) {
        Long adminId = getAdminIdFromToken(token);
        AdminLoginResponse response = adminAuthService.getAdminInfo(adminId);
        return ApiResponse.success(response);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        Long adminId = getAdminIdFromToken(token);
        adminAuthService.logout(adminId);
        return ApiResponse.success();
    }

    /**
     * 从Token中获取管理员ID
     */
    private Long getAdminIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = jwtUtil.parseToken(token);
        return claims.get("userId", Long.class);
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
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
