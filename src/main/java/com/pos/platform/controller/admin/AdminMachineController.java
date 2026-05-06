package com.pos.platform.controller.admin;

import com.pos.platform.common.constant.ApiPath;
import com.pos.platform.common.result.ApiResponse;
import com.pos.platform.common.result.PageResponse;
import com.pos.platform.dto.admin.MachineBindRequest;
import com.pos.platform.dto.admin.MachineQueryRequest;
import com.pos.platform.dto.admin.MachineResponse;
import com.pos.platform.entity.admin.AdminUser;
import com.pos.platform.service.admin.AdminMachineService;
import com.pos.platform.mapper.AdminUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端机具控制器
 */
@Tag(name = "机具管理", description = "管理端机具相关接口")
@RestController
@RequestMapping(ApiPath.Admin.MACHINE)
@RequiredArgsConstructor
public class AdminMachineController {

    private final AdminMachineService machineService;
    private final AdminUserMapper adminUserMapper;

    @Operation(summary = "分页查询机具列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<MachineResponse>> getMachineList(
            @ModelAttribute MachineQueryRequest query) {
        List<MachineResponse> list = machineService.getMachineList(query);
        Long total = machineService.getMachineCount(query);
        
        PageResponse<MachineResponse> pageResponse = PageResponse.of(
            list, query.getPage(), query.getSize(), total);
        
        return ApiResponse.success(pageResponse);
    }

    @Operation(summary = "获取机具详情")
    @GetMapping("/{id}")
    public ApiResponse<MachineResponse> getMachineById(
            @Parameter(description = "机具ID", required = true)
            @PathVariable Long id) {
        MachineResponse machine = machineService.getMachineById(id);
        return ApiResponse.success(machine);
    }

    @Operation(summary = "绑定机具到客户")
    @PostMapping("/bind")
    public ApiResponse<Void> bindMachine(
            HttpServletRequest request,
            @RequestBody MachineBindRequest bindRequest) {
        Long operatorId = getOperatorId(request);
        machineService.bindMachine(bindRequest, operatorId);
        return ApiResponse.success("绑定成功", null);
    }

    @Operation(summary = "解绑机具")
    @PostMapping("/unbind/{id}")
    public ApiResponse<Void> unbindMachine(
            HttpServletRequest request,
            @Parameter(description = "机具ID", required = true)
            @PathVariable Long id) {
        Long operatorId = getOperatorId(request);
        machineService.unbindMachine(id, operatorId);
        return ApiResponse.success("解绑成功", null);
    }

    @Operation(summary = "批量导入机具")
    @PostMapping("/import")
    public ApiResponse<Void> importMachines(
            HttpServletRequest request,
            @Parameter(description = "机具编码列表", required = true)
            @RequestParam List<String> machineCodes) {
        Long operatorId = getOperatorId(request);
        machineService.importMachines(machineCodes, operatorId);
        return ApiResponse.success("导入成功", null);
    }

    @Operation(summary = "Excel批量导入机具")
    @PostMapping("/import/excel")
    public ApiResponse<Void> importMachinesByExcel(
            HttpServletRequest request,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        Long operatorId = getOperatorId(request);
        
        try {
            // 简单解析Excel第一列的机具编码
            List<String> machineCodes = parseExcelCodes(file);
            machineService.importMachines(machineCodes, operatorId);
            return ApiResponse.success("导入成功", null);
        } catch (Exception e) {
            return ApiResponse.error(500, "导入失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据SN码查询机具")
    @GetMapping("/search")
    public ApiResponse<MachineResponse> searchByCode(
            @Parameter(description = "机具编码", required = true)
            @RequestParam String machineCode) {
        var machine = machineService.getMachineByCode(machineCode);
        if (machine == null) {
            return ApiResponse.error(404, "机具不存在");
        }
        MachineResponse response = new MachineResponse();
        response.setId(machine.getId());
        response.setMachineCode(machine.getMachineCode());
        response.setStatus(machine.getStatus());
        response.setBindPhone(machine.getBindPhone());
        response.setCustomerId(machine.getCustomerId());
        return ApiResponse.success(response);
    }

    /**
     * 简单解析Excel文件中的机具编码
     */
    private List<String> parseExcelCodes(MultipartFile file) throws Exception {
        // 这里需要使用POI或EasyExcel来解析Excel
        // 简化实现，实际使用时需要完善
        throw new UnsupportedOperationException("请使用标准Excel导入接口");
    }

    /**
     * 从请求中获取操作员ID
     */
    private Long getOperatorId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 从token中解析管理员ID
        // 这里需要根据实际JWT实现来完成
        // 简化实现
        String adminId = request.getHeader("X-Admin-Id");
        if (adminId != null) {
            try {
                return Long.parseLong(adminId);
            } catch (NumberFormatException ignored) {
            }
        }
        
        // 默认返回1，实际应根据JWT解析
        return 1L;
    }
}
