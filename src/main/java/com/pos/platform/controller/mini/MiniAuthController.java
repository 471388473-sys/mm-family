package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.*;
import com.pos.platform.service.mini.MiniAuthService;
import com.pos.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 小程序认证控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.AUTH)
@RequiredArgsConstructor
public class MiniAuthController {

    private final MiniAuthService miniAuthService;
    private final JwtUtil jwtUtil;

    /**
     * 微信登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request,
                                               HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        LoginResponse response = miniAuthService.wxLogin(request, ip);
        return ApiResponse.success(response);
    }

    /**
     * 绑定手机号
     */
    @PostMapping("/bind-phone")
    public ApiResponse<UserInfoResponse> bindPhone(@Valid @RequestBody BindPhoneRequest request,
                                                    @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        UserInfoResponse response = miniAuthService.bindPhone(request, userId);
        return ApiResponse.success(response);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user-info")
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        UserInfoResponse response = miniAuthService.getUserInfo(userId);
        return ApiResponse.success(response);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/user-info")
    public ApiResponse<UserInfoResponse> updateUserInfo(@RequestHeader("Authorization") String token,
                                                        @RequestParam(required = false) String nickname,
                                                        @RequestParam(required = false) String avatar) {
        Long userId = getUserIdFromToken(token);
        UserInfoResponse response = miniAuthService.updateUserInfo(userId, nickname, avatar);
        return ApiResponse.success(response);
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            Claims claims = jwtUtil.parseToken(token);
            return claims.get("userId", Long.class);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        }
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
