package com.sailtrack.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Column(name = "expected_check_out_time")
    private LocalDateTime expectedCheckOutTime;
    
    @Column(name = "check_in_ip", length = 50)
    private String checkInIp;
    
    @Column(name = "check_out_ip", length = 50)
    private String checkOutIp;
    
    @Column(name = "work_hours", precision = 5, scale = 2)
    private BigDecimal workHours;
    
    @Column(nullable = false)
    private Integer status = 1; // 1-正常，2-迟到，3-早退，4-缺卡
    
    @Column(name = "is_late")
    private Boolean isLate = false;
    
    @Column(name = "is_early_leave")
    private Boolean isEarlyLeave = false;
    
    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;
    
    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;
    
    @Column(length = 500)
    private String remark;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}