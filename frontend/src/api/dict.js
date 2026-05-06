import request from '@/utils/request'

/**
 * 字典管理 API
 */

// 字典类型接口

// 获取字典类型列表（分页）
export function getDictTypeList(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

// 新增字典类型
export function createDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

// 编辑字典类型
export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

// 删除字典类型
export function deleteDictType(id) {
  return request({
    url: `/system/dict/type/${id}`,
    method: 'delete'
  })
}

// 字典数据接口

// 获取字典数据列表（分页）
export function getDictDataList(params) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params
  })
}

// 根据字典类型获取字典数据
export function getDictDataByType(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}

// 新增字典数据
export function createDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

// 编辑字典数据
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

// 删除字典数据
export function deleteDictData(id) {
  return request({
    url: `/system/dict/data/${id}`,
    method: 'delete'
  })
}