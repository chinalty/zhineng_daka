-- ========================================
-- 添加人脸照片URL字段
-- 执行日期: 2025-12-17
-- ========================================

USE sailtrack;

-- 添加 face_image_url 字段到 users 表
ALTER TABLE users 
ADD COLUMN face_image_url VARCHAR(500) COMMENT '人脸照片URL（阿里云OSS）' AFTER role_id;

-- 验证字段添加成功
DESCRIBE users;
