package com.pos.platform.controller.mini;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.mini.WithdrawalConfigResponse;
import com.pos.platform.dto.mini.WithdrawalRecordResponse;
import com.pos.platform.dto.mini.WithdrawalRequest;
import com.pos.platform.service.mini.WithdrawalService;
import com.pos.platform.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 小程序端提现控制器
 */
@Tag(name = "提现模块", description = "小程序端提现相关接口")
@RestController
@RequestMapping(ApiPath.Mini.WITHDRAWAL)
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取提现配置")
    @GetMapping("/config")
    public ApiResponse<WithdrawalConfigResponse> getConfig() {
        WithdrawalConfigResponse config = withdrawalService.getConfig();
        return ApiResponse.success(config);
    }

    @Operation(summary = "获取可提现余额")
    @GetMapping("/balance")
    public ApiResponse<BigDecimal> getWithdrawableBalance(HttpServletRequest request) {
        Long userId = getUserId(request);
        BigDecimal balance = withdrawalService.getWithdrawableBalance(userId);
        return ApiResponse.success(balance);
    }

    @Operation(summary = "申请提现")
    @PostMapping("/apply")
    public ApiResponse<WithdrawalRecordResponse> applyWithdrawal(
            HttpServletRequest request,
            @Valid @RequestBody WithdrawalRequest withdrawalRequest) {
        Long userId = getUserId(request);
        WithdrawalRecordResponse response = withdrawalService.applyWithdrawal(userId, withdrawalRequest);
        return ApiResponse.success("提现申请已提交", response);
    }

    @Operation(summary = "获取提现记录列表")
    @GetMapping("/records")
    public ApiResponse<PageResponse<WithdrawalRecordResponse>> getRecordList(
            HttpServletRequest request,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getUserId(request);
        List<WithdrawalRecordResponse> records = withdrawalService.getRecordList(userId, page, size);
        Long total = withdrawalService.getRecordCount(userId);
        
        PageResponse<WithdrawalRecordResponse> pageResponse = PageResponse.of(records, page, size, total);
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
