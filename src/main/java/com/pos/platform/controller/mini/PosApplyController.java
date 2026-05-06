package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.dto.mini.*;
import com.pos.platform.service.mini.PosApplyService;
import com.pos.platform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * POS机领取控制器
 */
@RestController
@RequestMapping(ApiPath.Mini.POS)
@RequiredArgsConstructor
public class PosApplyController {

    private final PosApplyService posApplyService;
    private final JwtUtil jwtUtil;

    /**
     * 提交POS机领取申请
     */
    @PostMapping("/apply")
    public ApiResponse<PosApplyRecordResponse> submitApply(@Valid @RequestBody PosApplyRequest request,
                                                            @RequestHeader("Authorization") String token,
                                                            HttpServletRequest httpRequest) {
        Long userId = getUserIdFromToken(token);
        String ip = getClientIp(httpRequest);
        PosApplyRecordResponse response = posApplyService.submitApply(request, userId, ip);
        return ApiResponse.success(response);
    }

    /**
     * 获取申请记录列表
     */
    @GetMapping("/apply-records")
    public ApiResponse<List<PosApplyRecordResponse>> getApplyRecords(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserIdFromToken(token);
        List<PosApplyRecordResponse> records = posApplyService.getApplyRecords(userId, page, size);
        return ApiResponse.success(records);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/apply-detail/{applyId}")
    public ApiResponse<PosApplyRecordResponse> getApplyDetail(
            @RequestHeader("Authorization") String token,
            @PathVariable Long applyId) {
        Long userId = getUserIdFromToken(token);
        PosApplyRecordResponse response = posApplyService.getApplyDetail(userId, applyId);
        return ApiResponse.success(response);
    }

    /**
     * 检查是否有待处理的申请
     */
    @GetMapping("/apply-status")
    public ApiResponse<Boolean> checkApplyStatus(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        boolean hasPending = posApplyService.hasPendingApplication(userId);
        return ApiResponse.success(hasPending);
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
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
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
