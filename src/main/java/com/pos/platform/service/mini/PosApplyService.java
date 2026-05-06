package com.pos.platform.service.mini;

import com.pos.platform.dto.mini.*;

/**
 * POS机领取服务接口
 */
public interface PosApplyService {

    /**
     * 提交POS机领取申请
     * @param request 申请请求
     * @param userId 用户ID
     * @param ip IP地址
     * @return 申请记录
     */
    PosApplyRecordResponse submitApply(PosApplyRequest request, Long userId, String ip);

    /**
     * 获取申请记录列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 申请记录列表
     */
    java.util.List<PosApplyRecordResponse> getApplyRecords(Long userId, int page, int size);

    /**
     * 获取申请详情
     * @param userId 用户ID
     * @param applyId 申请ID
     * @return 申请详情
     */
    PosApplyRecordResponse getApplyDetail(Long userId, Long applyId);

    /**
     * 检查用户是否已提交过申请
     * @param userId 用户ID
     * @return true-已提交
     */
    boolean hasPendingApplication(Long userId);
}
