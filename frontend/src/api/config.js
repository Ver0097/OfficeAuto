import request from '@/utils/request'

/**
 * 参数管理 API
 */

// 获取参数列表（分页）
export function getConfigList(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

// 根据键名获取参数值
export function getConfigByKey(configKey) {
  return request({
    url: `/system/config/key/${configKey}`,
    method: 'get'
  })
}

// 新增参数
export function createConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

// 编辑参数
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

// 删除参数
export function deleteConfig(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'delete'
  })
}