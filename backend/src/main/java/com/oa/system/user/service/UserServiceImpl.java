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