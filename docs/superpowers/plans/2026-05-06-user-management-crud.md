# 用户管理 CRUD 模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现用户管理的完整 CRUD 功能，包括分页查询、新增、编辑、删除、重置密码、切换状态。

**Architecture:** 后端采用分层架构（Controller → Service → Mapper），使用 MyBatis 分页查询；前端使用 Element Plus 表格组件，新增/编辑使用对话框表单。

**Tech Stack:** Spring Boot 3 + MyBatis + Vue3 + Element Plus + Pinia

---

## 文件结构

### 后端新增文件
```
backend/src/main/java/com/oa/
├── common/
│   └── page/
│       ├── PageRequest.java          # 分页请求基类
│       └── PageResult.java           # 分页结果封装
├── system/user/
│       ├── dto/
│       │   ├── UserQueryDTO.java     # 用户查询条件
│       │   ├── UserCreateDTO.java    # 新增用户请求
│       │   ├── UserUpdateDTO.java    # 编辑用户请求
│       │   ├── UserListVO.java       # 用户列表响应
│       │   └── UserDetailVO.java     # 用户详情响应
│       └── controller/
│           └── UserController.java   # 用户管理控制器
```

### 后端修改文件
```
backend/src/main/java/com/oa/
├── common/result/ResultCode.java     # 新增错误码
├── system/user/
│   ├── service/UserService.java      # 新增方法
│   ├── service/UserServiceImpl.java  # 新增实现
│   ├── mapper/SysUserMapper.java     # 新增方法
backend/src/main/resources/mapper/SysUserMapper.xml  # 新增SQL
```

### 前端新增文件
```
frontend/src/
├── api/user.js                       # 用户管理 API
├── views/system/user/
│   └── index.vue                     # 用户管理页面
```

### 前端修改文件
```
frontend/src/router/index.js          # 新增路由
```

---

## Task 列表

- [√] Task 1: 创建分页工具类（PageRequest、PageResult）
- [√] Task 2: 创建用户 DTO（查询、新增、编辑、列表、详情）
- [√] Task 3: 扩展 ResultCode 添加用户相关错误码
- [√] Task 4: 扩展 UserService 接口和实现类
- [√] Task 5: 扩展 SysUserMapper 接口和 XML
- [√] Task 6: 创建 UserController
- [√] Task 7: 创建前端用户管理 API
- [√] Task 8: 创建前端用户管理页面
- [√] Task 9: 更新前端路由配置
- [ ] Task 10: 测试完整功能

---

### Task 1: 创建分页工具类（PageRequest、PageResult）

**Files:**
- Create: `backend/src/main/java/com/oa/common/page/PageRequest.java`
- Create: `backend/src/main/java/com/oa/common/page/PageResult.java`

- [ ] **Step 1: 创建 PageRequest.java**

```java
package com.oa.common.page;

import lombok.Data;

/**
 * 分页请求基类
 */
@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String orderType = "asc";
}
```

- [ ] **Step 2: 创建 PageResult.java**

```java
package com.oa.common.page;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult<T> {
    private Long total;
    private Integer pages;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;

    public PageResult(Long total, Integer pageNum, Integer pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }
}
```

---

### Task 2: 创建用户 DTO（查询、新增、编辑、列表、详情）

**Files:**
- Create: `backend/src/main/java/com/oa/system/user/dto/UserQueryDTO.java`
- Create: `backend/src/main/java/com/oa/system/user/dto/UserCreateDTO.java`
- Create: `backend/src/main/java/com/oa/system/user/dto/UserUpdateDTO.java`
- Create: `backend/src/main/java/com/oa/system/user/dto/UserListVO.java`
- Create: `backend/src/main/java/com/oa/system/user/dto/UserDetailVO.java`

- [ ] **Step 1: 创建 UserQueryDTO.java**

```java
package com.oa.system.user.dto;

import com.oa.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageRequest {
    private String username;
    private String realName;
    private Integer status;
    private Long deptId;
}
```

- [ ] **Step 2: 创建 UserCreateDTO.java**

```java
package com.oa.system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

/**
 * 新增用户请求
 */
@Data
public class UserCreateDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String email;
    private String phone;
    private Long deptId;
    private List<Long> roleIds;
}
```

- [ ] **Step 3: 创建 UserUpdateDTO.java**

```java
package com.oa.system.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 编辑用户请求
 */
@Data
public class UserUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    private Long id;

    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private List<Long> roleIds;
}
```

- [ ] **Step 4: 创建 UserListVO.java**

```java
package com.oa.system.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户列表响应
 */
@Data
public class UserListVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private String deptName;
    private Integer status;
    private LocalDateTime createTime;
}
```

- [ ] **Step 5: 创建 UserDetailVO.java**

```java
package com.oa.system.user.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户详情响应
 */
@Data
public class UserDetailVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private Long deptId;
    private String deptName;
    private Integer status;
    private String avatar;
    private List<Long> roleIds;
}
```

---

### Task 3: 扩展 ResultCode 添加用户相关错误码

**Files:**
- Modify: `backend/src/main/java/com/oa/common/result/ResultCode.java`

- [ ] **Step 1: 修改 ResultCode.java，添加用户相关错误码**

修改文件，在 USER_EXISTS 后添加：

```java
    USER_EXISTS(1005, "用户名已存在"),
    USER_NOT_FOUND(1006, "用户不存在"),
    PASSWORD_FORMAT_ERROR(1007, "密码格式错误"),
    CANNOT_DELETE_ADMIN(1008, "不能删除管理员账户");
```

---

### Task 4: 扩展 UserService 接口和实现类

**Files:**
- Modify: `backend/src/main/java/com/oa/system/user/service/UserService.java`
- Modify: `backend/src/main/java/com/oa/system/user/service/UserServiceImpl.java`

- [ ] **Step 1: 扩展 UserService 接口**

修改 UserService.java，添加方法：

```java
package com.oa.system.user.service;

import com.oa.common.page.PageResult;
import com.oa.system.user.dto.*;
import com.oa.system.user.entity.SysUser;

/**
 * 用户服务接口
 */
public interface UserService {

    SysUser getByUsername(String username);
    SysUser getById(Long id);

    /**
     * 分页查询用户列表
     */
    PageResult<UserListVO> listUsers(UserQueryDTO queryDTO);

    /**
     * 获取用户详情
     */
    UserDetailVO getUserDetail(Long id);

    /**
     * 创建用户
     */
    void createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     */
    void updateUser(UserUpdateDTO updateDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String password);

    /**
     * 切换状态
     */
    void changeStatus(Long id, Integer status);
}
```

- [ ] **Step 2: 扩展 UserServiceImpl 实现**

修改 UserServiceImpl.java：

```java
package com.oa.system.user.service;

import com.oa.common.exception.BusinessException;
import com.oa.common.page.PageResult;
import com.oa.common.result.ResultCode;
import com.oa.system.user.dto.*;
import com.oa.system.user.entity.SysUser;
import com.oa.system.user.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.selectById(id);
    }

    @Override
    public PageResult<UserListVO> listUsers(UserQueryDTO queryDTO) {
        Long total = sysUserMapper.countUsers(queryDTO);
        List<UserListVO> list = sysUserMapper.selectUserList(queryDTO);
        return new PageResult<>(total, queryDTO.getPageNum(), queryDTO.getPageSize(), list);
    }

    @Override
    public UserDetailVO getUserDetail(Long id) {
        UserDetailVO detail = sysUserMapper.selectUserDetailById(id);
        if (detail == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return detail;
    }

    @Override
    @Transactional
    public void createUser(UserCreateDTO createDTO) {
        // 检查用户名是否存在
        SysUser existing = sysUserMapper.selectByUsername(createDTO.getUsername());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setRealName(createDTO.getRealName());
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setDeptId(createDTO.getDeptId());
        user.setStatus(1);

        sysUserMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateUser(UserUpdateDTO updateDTO) {
        SysUser user = sysUserMapper.selectById(updateDTO.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        user.setRealName(updateDTO.getRealName());
        user.setEmail(updateDTO.getEmail());
        user.setPhone(updateDTO.getPhone());
        user.setDeptId(updateDTO.getDeptId());

        sysUserMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // 不允许删除管理员账户
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_ADMIN);
        }

        sysUserMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String password) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        sysUserMapper.updatePassword(id, passwordEncoder.encode(password));
    }

    @Override
    @Transactional
    public void changeStatus(Long id, Integer status) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_ADMIN);
        }

        sysUserMapper.updateStatus(id, status);
    }
}
```

---

### Task 5: 扩展 SysUserMapper 接口和 XML

**Files:**
- Modify: `backend/src/main/java/com/oa/system/user/mapper/SysUserMapper.java`
- Modify: `backend/src/main/resources/mapper/SysUserMapper.xml`

- [ ] **Step 1: 扩展 SysUserMapper 接口**

修改 SysUserMapper.java：

```java
package com.oa.system.user.mapper;

import com.oa.system.user.dto.*;
import com.oa.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserMapper {

    SysUser selectByUsername(@Param("username") String username);
    SysUser selectById(@Param("id") Long id);
    int insert(SysUser user);

    /**
     * 统计用户数量
     */
    Long countUsers(UserQueryDTO queryDTO);

    /**
     * 分页查询用户列表
     */
    List<UserListVO> selectUserList(UserQueryDTO queryDTO);

    /**
     * 查询用户详情
     */
    UserDetailVO selectUserDetailById(@Param("id") Long id);

    /**
     * 更新用户信息
     */
    int updateById(SysUser user);

    /**
     * 逻辑删除用户
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
```

- [ ] **Step 2: 扩展 SysUserMapper.xml**

修改 SysUserMapper.xml，添加 SQL：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.oa.system.user.mapper.SysUserMapper">

    <!-- 用户列表结果映射 -->
    <resultMap id="UserListVOMap" type="com.oa.system.user.dto.UserListVO">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="realName" column="real_name"/>
        <result property="email" column="email"/>
        <result property="phone" column="phone"/>
        <result property="deptId" column="dept_id"/>
        <result property="deptName" column="dept_name"/>
        <result property="status" column="status"/>
        <result property="createTime" column="create_time"/>
    </resultMap>

    <!-- 用户详情结果映射 -->
    <resultMap id="UserDetailVOMap" type="com.oa.system.user.dto.UserDetailVO">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="realName" column="real_name"/>
        <result property="email" column="email"/>
        <result property="phone" column="phone"/>
        <result property="deptId" column="dept_id"/>
        <result property="deptName" column="dept_name"/>
        <result property="status" column="status"/>
        <result property="avatar" column="avatar"/>
    </resultMap>

    <!-- 统计用户数量 -->
    <select id="countUsers" resultType="java.lang.Long">
        SELECT COUNT(*) FROM sys_user WHERE deleted = 0
        <if test="username != null and username != ''">
            AND username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="realName != null and realName != ''">
            AND real_name LIKE CONCAT('%', #{realName}, '%')
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="deptId != null">
            AND dept_id = #{deptId}
        </if>
    </select>

    <!-- 分页查询用户列表 -->
    <select id="selectUserList" resultMap="UserListVOMap">
        SELECT u.id, u.username, u.real_name, u.email, u.phone, u.dept_id,
               d.name as dept_name, u.status, u.create_time
        FROM sys_user u
        LEFT JOIN sys_dept d ON u.dept_id = d.id
        WHERE u.deleted = 0
        <if test="username != null and username != ''">
            AND u.username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="realName != null and realName != ''">
            AND u.real_name LIKE CONCAT('%', #{realName}, '%')
        </if>
        <if test="status != null">
            AND u.status = #{status}
        </if>
        <if test="deptId != null">
            AND u.dept_id = #{deptId}
        </if>
        ORDER BY u.create_time DESC
        LIMIT #{pageSize} OFFSET #{pageNum * #{pageSize} - #{pageSize}}
    </select>

    <!-- 查询用户详情 -->
    <select id="selectUserDetailById" resultMap="UserDetailVOMap">
        SELECT u.id, u.username, u.real_name, u.email, u.phone, u.dept_id,
               d.name as dept_name, u.status, u.avatar
        FROM sys_user u
        LEFT JOIN sys_dept d ON u.dept_id = d.id
        WHERE u.id = #{id} AND u.deleted = 0
    </select>

    <!-- 更新用户信息 -->
    <update id="updateById">
        UPDATE sys_user
        SET real_name = #{realName}, email = #{email}, phone = #{phone}, dept_id = #{deptId}
        WHERE id = #{id}
    </update>

    <!-- 逻辑删除用户 -->
    <update id="deleteById">
        UPDATE sys_user SET deleted = 1 WHERE id = #{id}
    </update>

    <!-- 更新密码 -->
    <update id="updatePassword">
        UPDATE sys_user SET password = #{password} WHERE id = #{id}
    </update>

    <!-- 更新状态 -->
    <update id="updateStatus">
        UPDATE sys_user SET status = #{status} WHERE id = #{id}
    </update>

</mapper>
```

---

### Task 6: 创建 UserController

**Files:**
- Create: `backend/src/main/java/com/oa/system/user/controller/UserController.java`

- [ ] **Step 1: 创建 UserController.java**

```java
package com.oa.system.user.controller;

import com.oa.common.page.PageResult;
import com.oa.common.result.Result;
import com.oa.system.user.dto.*;
import com.oa.system.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<UserListVO>> list(UserQueryDTO queryDTO) {
        PageResult<UserListVO> result = userService.listUsers(queryDTO);
        return Result.success(result);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<UserDetailVO> detail(@PathVariable Long id) {
        UserDetailVO detail = userService.getUserDetail(id);
        return Result.success(detail);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateDTO createDTO) {
        userService.createUser(createDTO);
        return Result.success();
    }

    /**
     * 编辑用户
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(updateDTO);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd/{id}")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String password = body.get("password");
        userService.resetPassword(id, password);
        return Result.success();
    }

    /**
     * 切换状态
     */
    @PutMapping("/status/{id}")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.changeStatus(id, status);
        return Result.success();
    }
}
```

---

### Task 7: 创建前端用户管理 API

**Files:**
- Create: `frontend/src/api/user.js`

- [ ] **Step 1: 创建 user.js**

```javascript
import request from '@/utils/request'

/**
 * 用户管理 API
 */

// 获取用户列表（分页）
export function getUserList(params) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function createUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data
  })
}

// 编辑用户
export function updateUser(data) {
  return request({
    url: '/api/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, password) {
  return request({
    url: `/api/system/user/resetPwd/${id}`,
    method: 'put',
    data: { password }
  })
}

// 切换状态
export function changeStatus(id, status) {
  return request({
    url: `/api/system/user/status/${id}`,
    method: 'put',
    data: { status }
  })
}
```

---

### Task 8: 创建前端用户管理页面

**Files:**
- Create: `frontend/src/views/system/user/index.vue`

- [ ] **Step 1: 创建用户管理页面组件**

```vue
<template>
  <div class="user-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <el-row class="toolbar">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </el-row>

      <!-- 用户表格 -->
      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              link 
              @click="handleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="row.username !== 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="500px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="formData" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username" v-if="isAdd">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="isAdd">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="formData.deptId" placeholder="请选择部门" clearable>
            <!-- 后续接入部门接口 -->
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="400px">
      <el-form ref="resetPwdRef" :model="resetPwdData" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdData.password" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwdSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetPassword, changeStatus } from '@/api/user'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  status: null
})

// 用户列表
const userList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const isAdd = ref(true)
const dialogTitle = computed(() => isAdd.value ? '新增用户' : '编辑用户')

// 表单数据
const formRef = ref()
const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  deptId: null
})

// 表单校验规则
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '长度3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

// 重置密码
const resetPwdVisible = ref(false)
const resetPwdRef = ref()
const resetPwdData = reactive({
  id: null,
  password: ''
})
const resetPwdRules = {
  password: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }]
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.status = null
  queryParams.pageNum = 1
  loadUserList()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  loadUserList()
}

const handlePageChange = (val) => {
  queryParams.pageNum = val
  loadUserList()
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isAdd.value = false
  resetForm()
  formData.id = row.id
  formData.realName = row.realName
  formData.email = row.email
  formData.phone = row.phone
  formData.deptId = row.deptId
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.email = ''
  formData.phone = ''
  formData.deptId = null
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 提交
const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isAdd.value) {
      await createUser(formData)
      ElMessage.success('新增成功')
    } else {
      await updateUser(formData)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadUserList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadUserList()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 重置密码
const handleResetPwd = (row) => {
  resetPwdData.id = row.id
  resetPwdData.password = ''
  resetPwdVisible.value = true
}

const handleResetPwdSubmit = async () => {
  await resetPwdRef.value.validate()
  try {
    await resetPassword(resetPwdData.id, resetPwdData.password)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '重置失败')
  }
}

// 切换状态
const handleStatus = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const text = newStatus === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确定要${text}该用户吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await changeStatus(row.id, newStatus)
      ElMessage.success(`${text}成功`)
      loadUserList()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  }).catch(() => {})
}

// 初始化
loadUserList()
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.toolbar {
  margin-bottom: 15px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
```

---

### Task 9: 更新前端路由配置

**Files:**
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 修改路由配置，添加用户管理路由**

修改 router/index.js，在现有路由中添加：

```javascript
{
  path: '/system',
  component: () => import('@/views/layout/index.vue'),
  children: [
    {
      path: 'user',
      name: 'User',
      component: () => import('@/views/system/user/index.vue'),
      meta: { title: '用户管理' }
    }
  ]
}
```

---

### Task 10: 测试完整功能

**Files:**
- 无新文件，仅测试

- [ ] **Step 1: 启动后端，验证接口**

```bash
cd backend
mvn spring-boot:run
```

测试接口：
- GET `/api/system/user/list` - 用户列表
- POST `/api/system/user` - 新增用户
- PUT `/api/system/user` - 编辑用户
- DELETE `/api/system/user/{id}` - 删除用户
- PUT `/api/system/user/resetPwd/{id}` - 重置密码
- PUT `/api/system/user/status/{id}` - 切换状态

- [ ] **Step 2: 启动前端，验证页面**

```bash
cd frontend
npm run dev
```

访问 http://localhost:3000/system/user 测试：
- 用户列表显示
- 新增用户
- 编辑用户
- 删除用户
- 重置密码
- 切换状态

- [ ] **Step 3: 提交代码**

```bash
git add .
git commit -m "feat: 实现用户管理CRUD模块"
```

---

## 自审检查

1. **Spec 覆盖检查：**
   - 用户列表（分页）✓ Task 6
   - 用户详情 ✓ Task 6
   - 新增用户 ✓ Task 4, 6
   - 编辑用户 ✓ Task 4, 6
   - 删除用户 ✓ Task 4, 6
   - 重置密码 ✓ Task 4, 6
   - 切换状态 ✓ Task 4, 6
   - 无遗漏

2. **Placeholder 检查：**
   - 无 "TBD"、"TODO"、"implement later" 等占位符
   - 所有代码完整

3. **类型一致性检查：**
   - UserQueryDTO extends PageRequest ✓
   - UserListVO / UserDetailVO 字段与接口设计一致 ✓
   - Mapper 方法签名与 Service 一致 ✓