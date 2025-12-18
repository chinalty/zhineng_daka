package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LeaveApprovalRequest {
    @NotNull(message = "请假记录ID不能为空")
    private Long leaveId;
    
    @NotNull(message = "审批状态不能为空")
    private Integer status; // 1-已批准, 2-已拒绝
    
    @Size(max = 200, message = "审批备注不能超过200个字符")
    private String remark;
    
    public Long getLeaveId() {
        return leaveId;
    }
    
    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}