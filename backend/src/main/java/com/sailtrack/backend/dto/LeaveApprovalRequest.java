package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveApprovalRequest {
    @NotNull(message = "请假记录ID不能为空")
    private Long leaveId;
    
    @NotNull(message = "审批状态不能为空")
    private Integer status; // 1-批准，2-拒绝
    
    private String remark; // 审批备注
}