package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.mini.LotteryConfigResponse;
import com.pos.platform.dto.mini.LotteryRecordResponse;
import com.pos.platform.dto.mini.LotteryResultResponse;
import com.pos.platform.service.mini.LotteryService;
import com.pos.platform.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 小程序端抽奖控制器
 */
@Tag(name = "抽奖模块", description = "小程序端抽奖相关接口")
@RestController
@RequestMapping(ApiPath.Mini.LOTTERY)
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryService lotteryService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取抽奖配置信息")
    @GetMapping("/config")
    public ApiResponse<LotteryConfigResponse> getConfig(HttpServletRequest request) {
        Long userId = getUserId(request);
        LotteryConfigResponse config = lotteryService.getConfig(userId);
        return ApiResponse.success(config);
    }

    @Operation(summary = "执行抽奖")
    @PostMapping("/draw")
    public ApiResponse<LotteryResultResponse> draw(HttpServletRequest request) {
        Long userId = getUserId(request);
        LotteryResultResponse result = lotteryService.draw(userId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "获取抽奖记录列表")
    @GetMapping("/records")
    public ApiResponse<PageResponse<LotteryRecordResponse>> getRecordList(
            HttpServletRequest request,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getUserId(request);
        List<LotteryRecordResponse> records = lotteryService.getRecordList(userId, page, size);
        Long total = lotteryService.getRecordCount(userId);
        
        PageResponse<LotteryRecordResponse> pageResponse = PageResponse.of(records, page, size, total);
        return ApiResponse.success(pageResponse);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserId(token);
    }
}
