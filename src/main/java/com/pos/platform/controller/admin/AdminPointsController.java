package com.pos.platform.controller.admin;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.admin.*;
import com.pos.platform.service.admin.AdminPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 管理端积分控制器
 */
@Tag(name = "积分管理", description = "管理端积分相关接口")
@RestController
@RequestMapping(ApiPath.Admin.POINTS)
@RequiredArgsConstructor
public class AdminPointsController {

    private final AdminPointsService pointsService;

    @Operation(summary = "分页查询积分记录列表")
    @GetMapping("/records")
    public ApiResponse<PageResponse<AdminPointsRecordResponse>> getPointsRecordList(
            @ModelAttribute PointsRecordQueryRequest query) {
        List<AdminPointsRecordResponse> list = pointsService.getPointsRecordList(query);
        Long total = pointsService.getPointsRecordCount(query);
        
        PageResponse<AdminPointsRecordResponse> pageResponse = PageResponse.of(
            list, query.getPage(), query.getSize(), total);
        
        return ApiResponse.success(pageResponse);
    }

    @Operation(summary = "查询用户积分账户信息")
    @GetMapping("/account/{userId}")
    public ApiResponse<AdminPointsAccountResponse> getUserPointsAccount(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        AdminPointsAccountResponse account = pointsService.getUserPointsAccount(userId);
        return ApiResponse.success(account);
    }

    @Operation(summary = "分页查询用户积分账户列表")
    @GetMapping("/accounts")
    public ApiResponse<PageResponse<AdminPointsAccountResponse>> getUserPointsAccountList(
            @Parameter(description = "用户手机号")
            @RequestParam(required = false) String phone,
            @Parameter(description = "页码，默认1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数，默认10")
            @RequestParam(defaultValue = "10") Integer size) {
        List<AdminPointsAccountResponse> list = pointsService.getUserPointsAccountList(phone, page, size);
        Long total = pointsService.getUserPointsAccountCount(phone);
        
        PageResponse<AdminPointsAccountResponse> pageResponse = PageResponse.of(list, page, size, total);
        
        return ApiResponse.success(pageResponse);
    }

    @Operation(summary = "管理员调整用户积分/余额")
    @PostMapping("/adjust")
    public ApiResponse<Void> adjustPoints(
            HttpServletRequest request,
            @RequestBody PointsAdjustRequest adjustRequest) {
        Long operatorId = getOperatorId(request);
        pointsService.adjustPoints(adjustRequest, operatorId);
        return ApiResponse.success("调整成功", null);
    }

    @Operation(summary = "手动导入积分")
    @PostMapping("/import")
    public ApiResponse<Void> importPoints(
            HttpServletRequest request,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "积分数量", required = true)
            @RequestParam Integer points,
            @Parameter(description = "描述")
            @RequestParam(required = false) String description) {
        Long operatorId = getOperatorId(request);
        pointsService.importPoints(userId, points, description, operatorId);
        return ApiResponse.success("导入成功", null);
    }

    /**
     * 从请求中获取操作员ID
     */
    private Long getOperatorId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        String adminId = request.getHeader("X-Admin-Id");
        if (adminId != null) {
            try {
                return Long.parseLong(adminId);
            } catch (NumberFormatException ignored) {
            }
        }
        
        return 1L;
    }
}
