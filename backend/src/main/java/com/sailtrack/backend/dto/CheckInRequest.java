package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotNull;

public class CheckInRequest {
    @NotNull(message = "打卡类型不能为空")
    private Integer type; // 1-签到, 2-签退
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
}