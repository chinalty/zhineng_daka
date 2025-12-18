package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class SupplementApplyRequest {
    
    @NotNull(message = "目标日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "日期格式必须为YYYY-MM-DD")
    private String targetDate;
    
    @NotNull(message = "打卡类型不能为空")
    private Integer checkType; // 1-签到, 2-签退
    
    @NotBlank(message = "打卡时间不能为空")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "时间格式必须为HH:mm")
    private String checkTime;
    
    @NotBlank(message = "申请原因不能为空")
    private String reason;

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getCheckType() {
        return checkType;
    }

    public void setCheckType(Integer checkType) {
        this.checkType = checkType;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}