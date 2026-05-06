package com.pos.platform.service.admin;

import com.pos.platform.dto.admin.*;
import com.pos.platform.entity.machine.Machine;

import java.util.List;

/**
 * 管理端机具服务接口
 */
public interface AdminMachineService {

    /**
     * 分页查询机具列表
     */
    List<MachineResponse> getMachineList(MachineQueryRequest query);

    /**
     * 获取机具总数
     */
    Long getMachineCount(MachineQueryRequest query);

    /**
     * 根据ID获取机具详情
     */
    MachineResponse getMachineById(Long id);

    /**
     * 绑定机具到客户
     */
    void bindMachine(MachineBindRequest request, Long operatorId);

    /**
     * 解绑机具
     */
    void unbindMachine(Long machineId, Long operatorId);

    /**
     * 批量导入机具
     */
    void importMachines(List<String> machineCodes, Long operatorId);

    /**
     * 根据SN码查询机具
     */
    Machine getMachineByCode(String machineCode);
}
