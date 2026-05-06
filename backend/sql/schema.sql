-- =====================================================
-- OA管理系统 - 数据库初始化脚本
-- =====================================================

-- 创建数据库（如果不存在）
-- 【状态：已执行】
CREATE DATABASE IF NOT EXISTS oa_system
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- 切换到 oa_system 数据库
USE oa_system;

-- =====================================================
-- 用户表 (sys_user)
-- 【状态：已执行】
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
-- 部门表 (sys_dept)
-- 【状态：已执行】
-- =====================================================
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    name            VARCHAR(50) NOT NULL COMMENT '部门名称',
    parent_id       BIGINT DEFAULT 0 COMMENT '父部门ID（0为顶级）',
    sort            INT DEFAULT 0 COMMENT '排序号',
    leader          VARCHAR(50) COMMENT '负责人',
    phone           VARCHAR(20) COMMENT '联系电话',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除',

    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入默认管理员用户
-- 【状态：已执行】
-- 用户名: admin
-- 密码: 123456 (BCrypt 加密，成本因子 10)
INSERT INTO sys_user (username, password, real_name, status)
VALUES (
    'admin',
    '$2a$10$5/nGx1ZbWRwf12oUH08cneYyddkR9rZ0V8APsoJc5RMB3SbZrPruC',
    '系统管理员',
    1
);

-- 插入默认顶级部门
-- 【状态：已执行】
INSERT INTO sys_dept (name, parent_id, sort, leader, phone, status)
VALUES ('总公司', 0, 1, '张总', '13800138000', 1);

-- =====================================================
-- 系统参数表 (sys_config)
-- 【状态：已执行】
-- =====================================================
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    config_key      VARCHAR(50) NOT NULL COMMENT '参数键名',
    config_value    VARCHAR(500) NOT NULL COMMENT '参数键值',
    config_name     VARCHAR(100) NOT NULL COMMENT '参数名称',
    config_type     VARCHAR(20) DEFAULT 'text' COMMENT '参数类型：text/number/boolean/json',
    remark          VARCHAR(200) DEFAULT NULL COMMENT '备注',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    UNIQUE KEY uk_config_key (config_key, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

-- =====================================================
-- 字典类型表 (sys_dict_type)
-- 【状态：已执行】
-- =====================================================
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    dict_type       VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    dict_name       VARCHAR(100) NOT NULL COMMENT '字典类型名称',
    remark          VARCHAR(200) DEFAULT NULL COMMENT '备注',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    UNIQUE KEY uk_dict_type (dict_type, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- =====================================================
-- 字典数据表 (sys_dict_data)
-- 【状态：已执行】
-- =====================================================
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    dict_type       VARCHAR(50) NOT NULL COMMENT '字典类型编码',
    dict_label      VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value      VARCHAR(100) NOT NULL COMMENT '字典键值',
    dict_sort       INT DEFAULT 0 COMMENT '字典排序',
    remark          VARCHAR(200) DEFAULT NULL COMMENT '备注',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    INDEX idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- =====================================================
-- 说明
-- =====================================================
--
-- 1. 本脚本创建 OA 管理系统所需的基础表结构
-- 2. 默认管理员账号：admin / 123456
-- 3. 密码使用 BCrypt 算法加密，成本因子为 10
-- 4. sys_config: 系统参数配置表
-- 5. sys_dict_type: 字典类型定义表
-- 6. sys_dict_data: 字典数据项表
-- 7. 执行状态说明：
--    - 【状态：已执行】 表示已在数据库执行
--    - 【状态：待执行】 表示需要手动执行
--
-- =====================================================