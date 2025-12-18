package com.sailtrack.backend.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    @Column(name = "check_in_ip", length = 45)
    private String checkInIp;
    
    @Column(name = "check_out_ip", length = 45)
    private String checkOutIp;
    
    @Column(precision = 4, scale = 2)
    private BigDecimal workHours;
    
    @Column(name = "overtime_hours")
    private Double overtimeHours; // 加班时长（小时）
    
    @Column(nullable = false)
    private Integer status = 1; // 1-正常, 2-迟到, 3-早退, 4-缺卡
    
    @Column(name = "is_late")
    private Boolean isLate = false;
    
    @Column(name = "is_early_leave")
    private Boolean isEarlyLeave = false;
    
    @Column(name = "late_minutes")
    private Integer lateMinutes;
    
    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes;
    
    @Column(length = 500)
    private String remark;
    
    // 补卡相关字段
    @Column(name = "is_supplement")
    private Boolean isSupplement = false; // 是否为补卡记录
    
    @Column(name = "supplement_status")
    private Integer supplementStatus; // 0-待审批, 1-已批准, 2-已拒绝
    
    @Column(name = "supplement_reason", length = 500)
    private String supplementReason; // 补卡原因
    
    @Column(name = "approver_id")
    private Long approverId; // 审批人ID
    
    @Column(name = "approval_time")
    private LocalDateTime approvalTime; // 审批时间
    
    @Column(name = "approval_remark", length = 500)
    private String approvalRemark; // 审批意见
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public LocalDateTime getExpectedCheckOutTime() {
        return expectedCheckOutTime;
    }

    public void setExpectedCheckOutTime(LocalDateTime expectedCheckOutTime) {
        this.expectedCheckOutTime = expectedCheckOutTime;
    }

    public String getCheckInIp() {
        return checkInIp;
    }

    public void setCheckInIp(String checkInIp) {
        this.checkInIp = checkInIp;
    }

    public String getCheckOutIp() {
        return checkOutIp;
    }

    public void setCheckOutIp(String checkOutIp) {
        this.checkOutIp = checkOutIp;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }

    public Boolean getIsEarlyLeave() {
        return isEarlyLeave;
    }

    public void setIsEarlyLeave(Boolean isEarlyLeave) {
        this.isEarlyLeave = isEarlyLeave;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // 补卡相关字段的getter和setter
    public Boolean getIsSupplement() {
        return isSupplement;
    }

    public void setIsSupplement(Boolean isSupplement) {
        this.isSupplement = isSupplement;
    }

    public Integer getSupplementStatus() {
        return supplementStatus;
    }

    public void setSupplementStatus(Integer supplementStatus) {
        this.supplementStatus = supplementStatus;
    }

    public String getSupplementReason() {
        return supplementReason;
    }

    public void setSupplementReason(String supplementReason) {
        this.supplementReason = supplementReason;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }
}