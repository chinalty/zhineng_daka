package com.sailtrack.backend.repository;

import com.sailtrack.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    List<User> findByDepartmentIdAndStatus(Long departmentId, Integer status);
    List<User> findByRoleIdAndStatus(Long roleId, Integer status);
    List<User> findByStatus(Integer status);
    
    @Query("SELECT u FROM User u WHERE u.departmentId = :departmentId AND u.roleId = 2")
    List<User> findDepartmentManagers(@Param("departmentId") Long departmentId);
}