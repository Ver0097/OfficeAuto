-- =====================================================
-- OA管理系统 - 数据库初始化脚本
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS oa_system
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- 切换到 oa_system 数据库
USE oa_system;

-- =====================================================
-- 用户表 (sys_user)
-- =====================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password        VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name       VARCHAR(50) COMMENT '真实姓名',
    email           VARCHAR(100) COMMENT '邮箱',
    phone           VARCHAR(20) COMMENT '手机号',
    dept_id         BIGINT COMMENT '部门ID',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    avatar          VARCHAR(255) COMMENT '头像URL',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入默认管理员用户
-- 用户名: admin
-- 密码: 123456 (BCrypt 加密，成本因子 10)
INSERT INTO sys_user (username, password, real_name, status)
VALUES (
    'admin',
    '$2a$10$EqKcp1WFKbdQg6at4QTa/.fISJI/qkTqY5Dd/HtYaK0o4UvVQO.3C',
    '系统管理员',
    1
);

-- =====================================================
-- 说明
-- =====================================================
--
-- 1. 本脚本创建 OA 管理系统所需的基础用户表
-- 2. 默认管理员账号：admin / 123456
-- 3. 密码使用 BCrypt 算法加密，成本因子为 10
-- 4. 后续可根据需要添加角色表、部门表等
--
-- =====================================================