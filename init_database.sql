-- ========================================
-- 智能打卡系统数据库初始化脚本
-- 版本: v2.0 (简化版)
-- 创建日期: 2025年12月12日
-- ========================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS sailtrack 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE sailtrack;

-- 禁用外键检查，以便重新创建表
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 角色表 (roles)
-- ========================================
DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(30) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ========================================
-- 2. 部门表 (departments)
-- ========================================
DROP TABLE IF EXISTS departments;
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    department_name VARCHAR(50) NOT NULL UNIQUE COMMENT '部门名称',
    manager_id BIGINT COMMENT '部门经理ID',
    description VARCHAR(200) COMMENT '部门描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_manager_id (manager_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ========================================
-- 3. 用户表 (users)
-- ========================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(30) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) COMMENT '真实姓名',
    department_id BIGINT COMMENT '部门ID',
    role_id BIGINT DEFAULT 3 COMMENT '角色ID（默认普通员工）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_department_id (department_id),
    INDEX idx_role_id (role_id),
    INDEX idx_status (status),
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========================================
-- 4. 考勤记录表 (attendance_records)
-- ========================================
DROP TABLE IF EXISTS attendance_records;
CREATE TABLE attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    check_in_time TIMESTAMP NULL COMMENT '签到时间',
    check_out_time TIMESTAMP NULL COMMENT '签退时间',
    expected_check_out_time TIMESTAMP NULL COMMENT '预期签退时间（弹性模式）',
    check_in_ip VARCHAR(50) COMMENT '签到IP地址',
    check_out_ip VARCHAR(50) COMMENT '签退IP地址',
    work_hours DECIMAL(5, 2) COMMENT '实际工作时长（小时）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，2-迟到，3-早退，4-缺卡',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟到：0-否，1-是',
    is_early_leave TINYINT DEFAULT 0 COMMENT '是否早退：0-否，1-是',
    late_minutes INT DEFAULT 0 COMMENT '迟到分钟数',
    early_leave_minutes INT DEFAULT 0 COMMENT '早退分钟数',
    remark VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_user_date (user_id, attendance_date),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- ========================================
-- 5. 请假记录表 (leave_records)
-- ========================================
DROP TABLE IF EXISTS leave_records;
CREATE TABLE leave_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '请假ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '请假类型：事假、病假、年假',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    leave_days DECIMAL(4, 1) NOT NULL COMMENT '请假天数',
    reason VARCHAR(500) NOT NULL COMMENT '请假原因',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审批，1-已批准，2-已拒绝',
    approver_id BIGINT COMMENT '审批人ID',
    approval_time TIMESTAMP NULL COMMENT '审批时间',
    approval_remark VARCHAR(200) COMMENT '审批备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_leave_type (leave_type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假记录表';

-- ========================================
-- 添加部门表的外键约束（在创建用户表后）
-- ========================================
-- 注意：manager_id 外键指向 users 表，但 users 表已经创建
-- 需要手动添加外键约束
ALTER TABLE departments 
ADD CONSTRAINT fk_departments_manager 
FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 初始化数据
-- ========================================

-- 1. 初始化角色数据
INSERT INTO roles (id, role_name, role_code, description, status) VALUES
(1, '系统管理员', 'ADMIN', '拥有系统所有权限，管理用户和考勤规则', 1),
(2, '部门经理', 'MANAGER', '管理本部门员工考勤和请假审批', 1),
(3, '普通员工', 'EMPLOYEE', '基本考勤打卡和请假申请功能', 1);

-- 2. 初始化部门数据
INSERT INTO departments (department_name, description, status) VALUES
('技术部', '技术研发部门', 1),
('市场部', '市场营销部门', 1),
('行政部', '行政管理部门', 1),
('财务部', '财务管理部门', 1),
('人事部', '人力资源管理', 1);

-- 3. 创建默认管理员账户
-- 密码: admin123 (BCrypt加密: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH)
INSERT INTO users (username, password, email, real_name, role_id, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 
 'admin@sailtrack.com', '系统管理员', 1, 1);

-- 4. 创建测试用户（密码都是 123456）
-- BCrypt加密后的密码: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
INSERT INTO users (username, password, email, real_name, department_id, role_id, status) VALUES
('zhangsan', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
 'zhangsan@example.com', '张三', 1, 3, 1),
('lisi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
 'lisi@example.com', '李四', 1, 3, 1),
('wangwu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
 'wangwu@example.com', '王五', 1, 2, 1),
('zhaoliu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
 'zhaoliu@example.com', '赵六', 2, 3, 1),
('qianqi', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
 'qianqi@example.com', '钱七', 2, 2, 1);

-- 5. 设置部门经理
UPDATE departments SET manager_id = 3 WHERE id = 1;  -- 王五为技术部经理
UPDATE departments SET manager_id = 5 WHERE id = 2;  -- 钱七为市场部经理

-- ========================================
-- 验证数据
-- ========================================

-- 查看角色
SELECT * FROM roles;

-- 查看部门
SELECT d.*, u.real_name AS manager_name 
FROM departments d 
LEFT JOIN users u ON d.manager_id = u.id;

-- 查看用户
SELECT u.*, d.department_name, r.role_name 
FROM users u 
LEFT JOIN departments d ON u.department_id = d.id 
LEFT JOIN roles r ON u.role_id = r.id;

-- ========================================
-- 使用说明
-- ========================================

/*
1. 默认管理员账户:
   - 用户名: admin
   - 密码: admin123
   - 邮箱: admin@sailtrack.com

2. 测试用户账户:
   - 所有测试用户密码: 123456
   - 张三、李四: 技术部员工
   - 王五: 技术部经理
   - 赵六、钱七: 市场部
   - 钱七: 市场部经理

3. 角色说明:
   - ADMIN (id=1): 系统管理员
   - MANAGER (id=2): 部门经理
   - EMPLOYEE (id=3): 普通员工
   - role_id 允许为 NULL，但默认值为 3（普通员工）

4. 部门说明:
   - id=1: 技术部 (经理: 王五)
   - id=2: 市场部 (经理: 钱七)
   - id=3: 行政部
   - id=4: 财务部
   - id=5: 人事部

5. 生产环境注意事项:
   - 修改默认管理员密码
   - 删除测试账户
   - 根据实际需求调整部门和用户
*/

-- ========================================
-- 脚本执行完成
-- ========================================