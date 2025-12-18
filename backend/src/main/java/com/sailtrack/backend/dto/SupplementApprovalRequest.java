package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotNull;

public class SupplementApprovalRequest {
    
    @NotNull(message = "申请ID不能为空")
    private Long applyId;
    
    @NotNull(message = "审批状态不能为空")
    private Integer status; // 1-批准, 2-拒绝
    
    private String remark;

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
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