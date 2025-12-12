package com.sailtrack.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @Column(nullable = false)
    private String password; // 存哈希后的密文

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "real_name", length = 50)
    private String realName;
    
    @Column(name = "department_id")
    private Long departmentId;
    
    @Column(name = "role_id", nullable = false)
    private Long roleId = 3L; // 默认为普通员工
    
    @Column(nullable = false)
    private Integer status = 1; // 默认启用
    
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}