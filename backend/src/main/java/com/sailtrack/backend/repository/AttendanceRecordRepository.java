package com.sailtrack.backend.repository;

import com.sailtrack.backend.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate date);
    
    List<AttendanceRecord> findByUserIdOrderByAttendanceDateDesc(Long userId);
    
    List<AttendanceRecord> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.userId = :userId AND ar.status = 1")
    long countByUserIdAndNormalStatus(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.userId = :userId AND ar.checkInTime IS NOT NULL")
    long countTotalAttendanceDays(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.userId = :userId AND ar.isLate = true")
    long countLateByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(ar.workHours) FROM AttendanceRecord ar WHERE ar.userId = :userId AND ar.workHours IS NOT NULL")
    Double sumWorkHoursByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.userId = :userId ORDER BY ar.attendanceDate DESC")
    List<AttendanceRecord> findRecentByUserId(@Param("userId") Long userId);
}