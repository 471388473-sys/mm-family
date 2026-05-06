package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.response.PointsAccountResponse;
import com.pos.platform.dto.response.PointsRecordResponse;
import com.pos.platform.service.PointsService;
import com.pos.platform.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端积分控制器
 */
@Tag(name = "积分模块", description = "小程序端积分相关接口")
@RestController
@RequestMapping(ApiPath.Mini.POINTS)
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取积分账户信息")
    @GetMapping("/account")
    public ApiResponse<PointsAccountResponse> getAccountInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        PointsAccountResponse account = pointsService.getAccountInfo(userId);
        return ApiResponse.success(account);
    }

    @Operation(summary = "获取积分兑换比例")
    @GetMapping("/exchange-rate")
    public ApiResponse<BigDecimal> getExchangeRate() {
        BigDecimal rate = pointsService.getExchangeRate();
        return ApiResponse.success(rate);
    }

    @Operation(summary = "积分兑换余额")
    @PostMapping("/exchange")
    public ApiResponse<PointsAccountResponse> exchange(
            HttpServletRequest request,
            @Parameter(description = "兑换积分数", required = true)
            @RequestParam Integer points) {
        Long userId = getUserId(request);
        PointsAccountResponse account = pointsService.exchange(userId, points);
        return ApiResponse.success("兑换成功", account);
    }

    @Operation(summary = "获取积分记录列表")
    @GetMapping("/records")
    public ApiResponse<PageResponse<PointsRecordResponse>> getRecords(
            HttpServletRequest request,
            @Parameter(description = "记录类型：import/exchange/lottery/adjust")
            @RequestParam(required = false) String type,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getUserId(request);
        List<PointsRecordResponse> records = pointsService.getRecordList(userId, type, page, size);
        Long total = pointsService.getRecordCount(userId, type);
        
        PageResponse<PointsRecordResponse> pageResponse = PageResponse.of(records, page, size, total);
        
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
