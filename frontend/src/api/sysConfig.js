/**
 * 系统参数管理 API
 * 封装系统参数的查询和修改值接口
 */
import request from '../utils/request'

export function getSysConfigList(params) {
  return request.get('/sysConfig', { params })
}

export function getSysConfig(id) {
  return request.get(`/sysConfig/${id}`)
}

export function updateSysConfig(id, data) {
  return request.put(`/sysConfig/${id}`, data)
}
