import request from './request'

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request.get('/auth/captcha')
}

/**
 * 登录
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 登出
 */
export function logout() {
  return request.post('/auth/logout')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request.get('/auth/user/info')
}