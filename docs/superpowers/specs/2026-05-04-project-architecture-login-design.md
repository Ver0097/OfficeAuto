# OA管理系统 - 项目架构与用户登录模块设计

> 设计日期：2026-05-04
> 状态：已批准，待实现

---

## 1. 项目概述

### 1.1 项目目标

开发单体版 OA 管理系统，首先实现：
- 基础项目架构搭建
- 用户登录模块（含验证码、JWT认证）

### 1.2 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3 + Spring Security + JWT + MyBatis + MySQL |
| 前端 | Vite + Vue3 + Element Plus + Pinia + Axios |
| 认证 | Spring Security + JWT（无状态）+ BCrypt 密码加密 + 图形验证码 |

### 1.3 设计决策记录

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 项目结构 | 分离目录（backend/ + frontend/） | 独立构建、部署灵活 |
| 前端构建工具 | Vite | 快速、现代开发体验 |
| 认证方案 | Spring Security + JWT | 单体系统标准方案，扩展性好 |
| 用户账户来源 | 仅管理员创建 | 企业内部 OA 系统特点 |
| 登录安全措施 | 图形验证码 | 防止暴力破解，简单有效 |
| 密码存储 | BCrypt | Spring Security 原生支持，安全性高 |

---

## 2. 项目目录结构

```
OfficeAuto/
├── backend/                           # 后端 Spring Boot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/oa/
│   │   │   │   ├── common/            # 公共模块
│   │   │   │   │   ├── config/        # 配置类（Security、Cors、MyBatis）
│   │   │   │   │   ├── exception/     # 全局异常处理
│   │   │   │   │   ├── result/        # 统一响应 Result<T>
│   │   │   │   │   └── utils/         # 工具类
│   │   │   │   ├── security/          # 安全认证模块
│   │   │   │   │   ├── jwt/           # JWT 生成/解析/验证
│   │   │   │   │   ├── filter/        # JWT 认证过滤器
│   │   │   │   │   ├── handler/       # 登录成功/失败处理
│   │   │   │   │   └── captcha/       # 验证码生成
│   │   │   │   ├── system/            # 系统管理模块
│   │   │   │   │   ├── user/          # 用户管理
│   │   │   │   │   ├── role/          # 角色管理（后续）
│   │   │   │   │   ├── menu/          # 菜单管理（后续）
│   │   │   │   │   └── dept/          # 部门管理（后续）
│   │   │   │   └── OaApplication.java # 启动类
│   │   │   └── resources/
│   │   │       ├── application.yml    # 配置文件
│   │   │       └── mapper/            # MyBatis XML
│   │   └── test/                      # 测试
│   └── pom.xml
│
├── frontend/                          # 前端 Vite + Vue3 项目
│   ├── src/
│   │   ├── api/                       # API 接口封装
│   │   │   └── auth.js                # 认证相关 API
│   │   ├── views/
│   │   │   └ login/                   # 登录页面
│   │   ├── router/                    # 路由配置
│   │   ├── store/                     # Pinia 状态管理
│   │   │   └── user.js                # 用户状态（Token存储）
│   │   ├── utils/
│   │   │   ├── request.js             # Axios 封装（携带Token）
│   │   │   └ auth.js                  # Token 存取工具
│   │   └ App.vue
│   ├── package.json
│   └── vite.config.js
│
└── docs/                              # 设计文档
```

---

## 3. 数据库设计

### 3.1 sys_user 用户表

```sql
CREATE TABLE sys_user (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username        VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password        VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name       VARCHAR(50) COMMENT '真实姓名',
    email           VARCHAR(100) COMMENT '邮箱',
    phone           VARCHAR(20) COMMENT '手机号',
    dept_id         BIGINT COMMENT '部门ID（后续关联）',
    status          TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    avatar          VARCHAR(255) COMMENT '头像URL',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',

    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 3.2 初始管理员数据

```sql
-- 密码为 BCrypt 加密的 "123456"
INSERT INTO sys_user (username, password, real_name, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl7', '系统管理员', 1);
```

---

## 4. 登录流程设计

### 4.1 流程图

```
前端                     后端                     数据库/Redis
  │                        │                        │
  │  1.请求验证码          │                        │
  │───────────────────────▶│                        │
  │                        │  2.生成验证码          │
  │                        │  (存入Redis)           │
  │  3.返回验证码图片      │                        │
  │◀───────────────────────│                        │
  │                        │                        │
  │  4.提交登录表单        │                        │
  │  (username,password,   │                        │
  │   captchaCode,captchaKey)                       │
  │───────────────────────▶│                        │
  │                        │  5.验证验证码          │
  │                        │  (从Redis取)           │
  │                        │                        │
  │                        │  6.查询用户            │
  │                        │───────────────────────▶│
  │                        │  7.返回用户信息        │
  │                        │◀───────────────────────│
  │                        │                        │
  │                        │  8.BCrypt校验密码      │
  │                        │                        │
  │                        │  9.生成JWT Token       │
  │                        │                        │
  │  10.返回Token          │                        │
  │   + 用户基本信息       │                        │
  │◀───────────────────────│                        │
  │                        │                        │
  │  11.存储Token          │                        │
  │  (localStorage)        │                        │
```

### 4.2 Token 认证流程

每次请求携带 Token：

```
前端                     后端
  │                        │
  │  请求 + Token Header   │
  │───────────────────────▶│
  │                        │  JwtAuthenticationFilter
  │                        │  1.提取Token
  │                        │  2.验证Token
  │                        │  3.解析用户信息
  │                        │  4.设置SecurityContext
  │                        │
  │                        │  业务处理
  │  响应                  │
  │◀───────────────────────│
```

---

## 5. 后端认证模块设计

### 5.1 Security 模块类结构

```
security/
├── jwt/
│   ├── JwtUtils.java           # JWT 工具类：生成/解析/验证 Token
│   └── JwtProperties.java      # JWT 配置属性（密钥、过期时间等）
│
├── filter/
│   └── JwtAuthenticationFilter.java  # JWT 认证过滤器
│
├── handler/
│   ├── LoginSuccessHandler.java      # 登录成功处理
│   ├── LoginFailureHandler.java      # 登录失败处理
│   └── LogoutHandler.java            # 登出处理
│
├── captcha/
│   ├── CaptchaService.java           # 验证码生成服务
│   └── CaptchaController.java        # 验证码接口
│
└── config/
│   └── SecurityConfig.java           # Spring Security 配置类
```

### 5.2 核心类职责

| 类名 | 职责 |
|------|------|
| JwtUtils | 生成 JWT Token、解析 Token 获取用户信息、验证 Token 有效性 |
| JwtProperties | 从 application.yml 读取配置：密钥、过期时间、Token 头名称 |
| JwtAuthenticationFilter | 每次请求拦截，从 Header 提取 Token，验证并设置 SecurityContext |
| LoginSuccessHandler | 登录成功后生成 Token，返回用户信息 JSON |
| LoginFailureHandler | 登录失败返回错误信息（验证码错误、密码错误等） |
| CaptchaService | 生成图形验证码，存入 Redis，设置过期时间（5分钟） |
| SecurityConfig | 配置过滤器链、哪些路径需要认证、哪些路径公开 |

### 5.3 JWT 配置

```yaml
jwt:
  secret: oa-system-secret-key-2024  # 密钥（生产环境应使用环境变量）
  expiration: 86400000               # Token 有效期：24小时（毫秒）
  header: Authorization              # Token 请求头名称
  prefix: "Bearer "                  # Token 前缀
```

---

## 6. 前端登录模块设计

### 6.1 前端文件结构

```
frontend/src/
├── api/
│   └── auth.js                 # 认证 API：登录、登出、获取验证码
│
├── views/
│   └ login/
│       └── index.vue           # 登录页面组件
│
├── store/
│   └── user.js                 # 用户状态管理（Pinia）
│
├── utils/
│   ├── request.js              # Axios 封装（请求拦截携带 Token）
│   ├── auth.js                 # Token 存取工具
│   └ permission.js             # 权限指令（后续）
│
├── router/
│   └── index.js                # 路由配置（登录页、首页）
│
└── App.vue                     # 根组件
```

### 6.2 登录页面布局

```
┌─────────────────────────────────────────┐
│                                         │
│         ┌─────────────────────┐         │
│         │   OA 管理系统        │         │
│         │                     │         │
│         │  ┌───────────────┐  │         │
│         │  │ 用户名        │  │         │
│         │  └───────────────┘  │         │
│         │                     │         │
│         │  ┌───────────────┐  │         │
│         │  │ 密码          │  │         │
│         │  └───────────────┘  │         │
│         │                     │         │
│         │  ┌─────┐ ┌────────┐ │         │
│         │  │验证码│ │ 输入框 │ │         │
│         │  │图片 │ └────────┘ │         │
│         │  └─────┘            │         │
│         │                     │         │
│         │  ┌───────────────┐  │         │
│         │  │    登 录      │  │         │
│         │  └───────────────┘  │         │
│         │                     │         │
│         └─────────────────────┘         │
│                                         │
└─────────────────────────────────────────┘
```

### 6.3 核心功能点

| 功能 | 说明 |
|------|------|
| 验证码显示 | 调用 `/api/auth/captcha` 获取图片和 key，点击图片可刷新 |
| 表单验证 | 用户名、密码、验证码均为必填项 |
| 登录提交 | 调用 `/api/auth/login`，成功后存储 Token 到 localStorage |
| Token 携带 | request.js 在请求拦截器中自动从 localStorage 取 Token 添加到 Header |
| 路由守卫 | 未登录时跳转到登录页，已登录时跳转到首页 |
| 登出 | 清除 localStorage 中的 Token，跳转到登录页 |

---

## 7. 接口设计

### 7.1 接口列表

| 接口 | 方法 | 路径 | 是否需要认证 | 说明 |
|------|------|------|--------------|------|
| 获取验证码 | GET | `/api/auth/captcha` | 否 | 返回验证码图片和 key |
| 登录 | POST | `/api/auth/login` | 否 | 用户名+密码+验证码登录 |
| 登出 | POST | `/api/auth/logout` | 是 | 清除登录状态 |
| 获取用户信息 | GET | `/api/auth/user/info` | 是 | 返回当前登录用户信息 |

### 7.2 接口详细定义

#### 获取验证码

```
GET /api/auth/captcha

响应：
{
    "code": 200,
    "data": {
        "captchaKey": "uuid-xxx",
        "captchaImage": "data:image/png;base64,iVBORw0KGgo..."
    }
}
```

#### 登录

```
POST /api/auth/login

请求体：
{
    "username": "admin",
    "password": "123456",
    "captchaCode": "abc123",
    "captchaKey": "uuid-xxx"
}

成功响应（200）：
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIs...",
        "userInfo": {
            "id": 1,
            "username": "admin",
            "realName": "管理员",
            "avatar": null
        }
    }
}

失败响应（401）：
{
    "code": 401,
    "message": "验证码错误"
}
```

#### 登出

```
POST /api/auth/logout

请求头：
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

响应：
{
    "code": 200,
    "message": "登出成功"
}
```

#### 获取用户信息

```
GET /api/auth/user/info

请求头：
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

响应：
{
    "code": 200,
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "avatar": null,
        "deptId": 1,
        "roles": ["admin"]
    }
}
```

---

## 8. 实现范围

### 8.1 本次实现内容

1. **项目架构搭建**
   - 创建 backend/ 和 frontend/ 目录结构
   - 配置 Spring Boot 3 项目
   - 配置 Vite + Vue3 项目
   - 配置 MySQL 数据库连接

2. **用户登录模块**
   - 后端：验证码生成、JWT 认证、登录接口
   - 前端：登录页面、Token 管理、路由守卫
   - 数据库：sys_user 表创建、初始管理员数据

### 8.2 后续迭代内容（不在本次范围）

- 用户管理 CRUD
- 角色管理
- 菜单管理
- 动态路由
- 部门管理
- 审批流程

---

## 9. 技术依赖

### 9.1 后端依赖

```xml
<!-- Spring Boot 3 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
</parent>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.x</version>
    </dependency>

    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.x</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- Redis（验证码存储） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- 验证码生成 -->
    <dependency>
        <groupId>com.github.penggle</groupId>
        <artifactId>kaptcha</artifactId>
        <version>2.3.2</version>
    </dependency>
</dependencies>
```

### 9.2 前端依赖

```json
{
    "dependencies": {
        "vue": "^3.4.x",
        "vue-router": "^4.x",
        "pinia": "^2.x",
        "axios": "^1.x",
        "element-plus": "^2.x"
    },
    "devDependencies": {
        "vite": "^5.x",
        "@vitejs/plugin-vue": "^5.x"
    }
}
```

---

## 10. 验收标准

### 10.1 功能验收

- [ ] 后端项目可正常启动
- [ ] 前端项目可正常启动
- [ ] 验证码接口返回图片和 key
- [ ] 正确凭证登录成功，返回 Token
- [ ] 错误凭证登录失败，返回正确错误信息
- [ ] 验证码错误时登录失败
- [ ] Token 携带时可访问需要认证的接口
- [ ] 无 Token 时访问认证接口返回 401
- [ ] 登出后 Token 失效

### 10.2 安全验收

- [ ] 密码使用 BCrypt 加密存储
- [ ] Token 有效期 24 小时
- [ ] 验证码 5 分钟过期
- [ ] 登录失败不泄露具体原因（用户不存在/密码错误统一提示）

---

## 11. 附录

### 11.1 相关文档

- [01-模块划分.md](../01-模块划分.md)
- [02-数据库设计.md](../02-数据库设计.md)
- [03-权限模型设计.md](../03-权限模型设计.md)
- [04-接口设计.md](../04-接口设计.md)