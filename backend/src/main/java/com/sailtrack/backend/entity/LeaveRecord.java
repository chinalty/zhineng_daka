package com.sailtrack.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "leave_records")
public class LeaveRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "leave_type", nullable = false, length = 20)
    private String leaveType; // 事假、病假、年假
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "leave_days", nullable = false, precision = 4, scale = 1)
    private BigDecimal leaveDays;
    
    @Column(nullable = false, length = 500)
    private String reason;
    
    @Column(nullable = false)
    private Integer status = 0; // 0-待审批，1-已批准，2-已拒绝
    
    @Column(name = "approver_id")
    private Long approverId;
    
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;
    
    @Column(name = "approval_remark", length = 200)
    private String approvalRemark;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}