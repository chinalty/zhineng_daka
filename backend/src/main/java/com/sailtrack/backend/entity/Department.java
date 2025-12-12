package com.sailtrack.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "departments")
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "department_name", unique = true, nullable = false, length = 50)
    private String departmentName;
    
    @Column(name = "manager_id")
    private Long managerId;
    
    @Column(length = 200)
    private String description;
    
    @Column(nullable = false)
    private Integer status = 1;
    
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }
}