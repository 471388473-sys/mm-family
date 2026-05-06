package com.pos.platform.service.impl.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pos.platform.common.error.ErrorCode;
import com.pos.platform.common.exception.BusinessException;
import com.pos.platform.dto.admin.MachineBindRequest;
import com.pos.platform.dto.admin.MachineQueryRequest;
import com.pos.platform.dto.admin.MachineResponse;
import com.pos.platform.entity.machine.Machine;
import com.pos.platform.entity.user.User;
import com.pos.platform.mapper.MachineMapper;
import com.pos.platform.mapper.UserMapper;
import com.pos.platform.service.admin.AdminMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端机具服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMachineServiceImpl implements AdminMachineService {

    private final MachineMapper machineMapper;
    private final UserMapper userMapper;

    /** 状态描述映射 */
    private static final Map<Integer, String> STATUS_DESC_MAP = new HashMap<>();
    
    static {
        STATUS_DESC_MAP.put(0, "未绑定");
        STATUS_DESC_MAP.put(1, "已绑定");
    }

    @Override
    public List<MachineResponse> getMachineList(MachineQueryRequest query) {
        int page = query.getPage() == null || query.getPage() < 1 ? 1 : query.getPage();
        int size = query.getSize() == null || query.getSize() < 1 ? 10 : query.getSize();
        
        Page<Machine> recordPage = new Page<>(page, size);
        LambdaQueryWrapper<Machine> queryWrapper = buildQueryWrapper(query);
        queryWrapper.orderByDesc(Machine::getCreatedAt);
        
        Page<Machine> result = machineMapper.selectPage(recordPage, queryWrapper);
        
        return result.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public Long getMachineCount(MachineQueryRequest query) {
        LambdaQueryWrapper<Machine> queryWrapper = buildQueryWrapper(query);
        return machineMapper.selectCount(queryWrapper);
    }

    @Override
    public MachineResponse getMachineById(Long id) {
        Machine machine = machineMapper.selectById(id);
        if (machine == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "机具不存在");
        }
        return convertToResponse(machine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMachine(MachineBindRequest request, Long operatorId) {
        Machine machine;
        
        if (request.getMachineId() != null) {
            machine = machineMapper.selectById(request.getMachineId());
        } else if (StringUtils.hasText(request.getMachineCode())) {
            machine = getMachineByCode(request.getMachineCode());
        } else {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "机具ID或编码不能为空");
        }
        
        if (machine == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "机具不存在");
        }
        
        if (machine.getStatus() != null && machine.getStatus() == 1) {
            throw new BusinessException(ErrorCode.MACHINE_ALREADY_BIND, "机具已绑定");
        }
        
        if (request.getCustomerId() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "客户ID不能为空");
        }
        
        User customer = userMapper.selectById(request.getCustomerId());
        if (customer == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "客户不存在");
        }
        
        Machine updateMachine = new Machine();
        updateMachine.setId(machine.getId());
        updateMachine.setCustomerId(request.getCustomerId());
        updateMachine.setBindPhone(request.getPhone() != null ? request.getPhone() : customer.getPhone());
        updateMachine.setStatus(1);
        updateMachine.setBindAt(LocalDateTime.now());
        updateMachine.setUpdatedAt(LocalDateTime.now());
        
        machineMapper.updateById(updateMachine);
        
        log.info("机具绑定成功: machineId={}, customerId={}, operatorId={}", 
            machine.getId(), request.getCustomerId(), operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindMachine(Long machineId, Long operatorId) {
        Machine machine = machineMapper.selectById(machineId);
        if (machine == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "机具不存在");
        }
        
        if (machine.getStatus() == null || machine.getStatus() == 0) {
            throw new BusinessException(ErrorCode.MACHINE_NOT_BIND, "机具未绑定");
        }
        
        Machine updateMachine = new Machine();
        updateMachine.setId(machineId);
        updateMachine.setCustomerId(null);
        updateMachine.setBindPhone(null);
        updateMachine.setStatus(0);
        updateMachine.setBindAt(null);
        updateMachine.setUpdatedAt(LocalDateTime.now());
        
        machineMapper.updateById(updateMachine);
        
        log.info("机具解绑成功: machineId={}, operatorId={}", machineId, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importMachines(List<String> machineCodes, Long operatorId) {
        if (machineCodes == null || machineCodes.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "机具编码列表不能为空");
        }
        
        int importedCount = 0;
        for (String code : machineCodes) {
            if (!StringUtils.hasText(code)) {
                continue;
            }
            
            // 检查是否已存在
            Machine existMachine = getMachineByCode(code.trim());
            if (existMachine != null) {
                log.warn("机具编码已存在，跳过: {}", code);
                continue;
            }
            
            Machine machine = new Machine();
            machine.setMachineCode(code.trim());
            machine.setStatus(0);
            machine.setCreatedAt(LocalDateTime.now());
            machine.setUpdatedAt(LocalDateTime.now());
            
            machineMapper.insert(machine);
            importedCount++;
        }
        
        log.info("批量导入机具完成: total={}, imported={}, operatorId={}", 
            machineCodes.size(), importedCount, operatorId);
    }

    @Override
    public Machine getMachineByCode(String machineCode) {
        return machineMapper.selectOne(
            new LambdaQueryWrapper<Machine>()
                .eq(Machine::getMachineCode, machineCode)
        );
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Machine> buildQueryWrapper(MachineQueryRequest query) {
        LambdaQueryWrapper<Machine> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(query.getMachineCode())) {
            queryWrapper.like(Machine::getMachineCode, query.getMachineCode());
        }
        
        if (StringUtils.hasText(query.getBindPhone())) {
            queryWrapper.like(Machine::getBindPhone, query.getBindPhone());
        }
        
        if (query.getStatus() != null) {
            queryWrapper.eq(Machine::getStatus, query.getStatus());
        }
        
        return queryWrapper;
    }

    /**
     * 转换为响应DTO
     */
    private MachineResponse convertToResponse(Machine machine) {
        if (machine == null) {
            return null;
        }
        
        MachineResponse response = new MachineResponse();
        response.setId(machine.getId());
        response.setMachineCode(machine.getMachineCode());
        response.setProductId(machine.getProductId());
        response.setCustomerId(machine.getCustomerId());
        response.setBindPhone(machine.getBindPhone());
        response.setStatus(machine.getStatus());
        response.setStatusDesc(STATUS_DESC_MAP.getOrDefault(machine.getStatus(), "未知"));
        response.setBindAt(machine.getBindAt());
        response.setCreatedAt(machine.getCreatedAt());
        
        // 查询客户名称
        if (machine.getCustomerId() != null) {
            User customer = userMapper.selectById(machine.getCustomerId());
            if (customer != null) {
                response.setCustomerName(customer.getName());
            }
        }
        
        return response;
    }
}
