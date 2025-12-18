package com.sailtrack.backend.repository;

import com.sailtrack.backend.entity.LeaveRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {
    List<LeaveRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<LeaveRecord> findByStatusOrderByCreatedAtDesc(Integer status);
    
    @Query("SELECT lr FROM LeaveRecord lr WHERE lr.status = 0 AND lr.userId IN " +
           "(SELECT u.id FROM User u WHERE u.departmentId = :departmentId)")
    List<LeaveRecord> findPendingByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT COUNT(lr) FROM LeaveRecord lr WHERE lr.userId = :userId AND lr.status = 1")
    long countApprovedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(lr.leaveDays) FROM LeaveRecord lr WHERE lr.userId = :userId AND lr.status = 1")
    Double sumLeaveDaysByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(lr) > 0 FROM LeaveRecord lr WHERE lr.userId = :userId AND lr.startDate <= :endDate AND lr.endDate >= :startDate")
    boolean existsByUserIdAndDateRange(@Param("userId") Long userId, @Param("endDate") LocalDate endDate, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT lr FROM LeaveRecord lr WHERE lr.userId IN " +
           "(SELECT u.id FROM User u WHERE u.departmentId = :departmentId) " +
           "ORDER BY lr.createdAt DESC")
    List<LeaveRecord> findByDepartmentIdOrderByCreatedAtDesc(@Param("departmentId") Long departmentId);
}