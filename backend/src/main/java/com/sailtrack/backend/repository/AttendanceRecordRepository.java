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
    
    List<AttendanceRecord> findByUserIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
        Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ar FROM AttendanceRecord ar JOIN User u ON ar.userId = u.id " +
           "WHERE u.departmentId = :departmentId AND ar.attendanceDate = :date")
    List<AttendanceRecord> findByDepartmentIdAndDate(@Param("departmentId") Long departmentId, 
                                                    @Param("date") LocalDate date);
    
    List<AttendanceRecord> findByAttendanceDateBetweenOrderByAttendanceDateDesc(
        LocalDate startDate, LocalDate endDate);
}