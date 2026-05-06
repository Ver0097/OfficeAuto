import request from '@/utils/request'

/**
 * 用户管理 API
 */

// 获取用户列表（分页）
export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUserDetail(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function createUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 编辑用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

// 重置密码
export function resetPassword(id, password) {
  return request({
    url: `/system/user/resetPwd/${id}`,
    method: 'put',
    data: { password }
  })
}

// 切换状态
export function changeStatus(id, status) {
  return request({
    url: `/system/user/status/${id}`,
    method: 'put',
    data: { status }
  })
}