package com.sailtrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaveRequest {
    @NotBlank(message = "请假类型不能为空")
    private String leaveType; // 事假、病假、年假
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    @NotNull(message = "请假天数不能为空")
    private BigDecimal leaveDays;
    
    @NotBlank(message = "请假原因不能为空")
    private String reason;
}