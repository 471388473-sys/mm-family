package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.HomeDataResponse;
import com.pos.platform.service.mini.HomeService;
import com.pos.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序首页控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.HOME)
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final JwtUtil jwtUtil;

    /**
     * 获取首页数据
     * @param token 用户Token
     */
    @GetMapping
    public ApiResponse<HomeDataResponse> getHomeData(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        HomeDataResponse response = homeService.getHomeData(userId);
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
}
