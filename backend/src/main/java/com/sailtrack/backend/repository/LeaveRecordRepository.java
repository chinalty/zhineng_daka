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
    
    @Query("SELECT lr FROM LeaveRecord lr JOIN User u ON lr.userId = u.id " +
           "WHERE u.departmentId = :departmentId AND lr.status = :status")
    List<LeaveRecord> findByDepartmentIdAndStatus(@Param("departmentId") Long departmentId, 
                                                 @Param("status") Integer status);
    
    @Query("SELECT COUNT(lr) FROM LeaveRecord lr " +
           "WHERE lr.userId = :userId AND lr.status = 1 AND " +
           "((lr.startDate <= :startDate AND lr.endDate >= :startDate) OR " +
           "(lr.startDate <= :endDate AND lr.endDate >= :endDate) OR " +
           "(lr.startDate >= :startDate AND lr.endDate <= :endDate))")
    Long countOverlappingLeaves(@Param("userId") Long userId, 
                               @Param("startDate") LocalDate startDate, 
                               @Param("endDate") LocalDate endDate);
}