-- ========================================
-- 修复已注册用户的 role_id 为 NULL 的问题
-- 执行日期: 2025-12-17
-- ========================================

USE sailtrack;

-- 1. 查看当前 role_id 为 NULL 的用户
SELECT id, username, email, role_id, created_at 
FROM users 
WHERE role_id IS NULL;

-- 2. 将所有 role_id 为 NULL 的用户设置为普通员工（role_id = 3）
UPDATE users 
SET role_id = 3 
WHERE role_id IS NULL;

-- 3. 验证修复结果
SELECT id, username, email, role_id, created_at 
FROM users 
WHERE username = 'gaojian';

-- 4. 确认所有用户都有角色
SELECT 
    COUNT(*) as total_users,
    COUNT(role_id) as users_with_role,
    COUNT(*) - COUNT(role_id) as users_without_role
FROM users;
