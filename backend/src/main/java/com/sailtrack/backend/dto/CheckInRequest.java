package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotNull(message = "打卡类型不能为空")
    private Integer type; // 1-签到，2-签退
}