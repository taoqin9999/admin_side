/**
 * 权限管理 API
 * 封装权限模块的增删改查接口
 */
import request from '../utils/request'

export function getPermissionList(params) {
  return request.get('/permission', { params })
}

export function getPermission(id) {
  return request.get(`/permission/${id}`)
}

export function addPermission(data) {
  return request.post('/permission', data)
}

export function updatePermission(id, data) {
  return request.put(`/permission/${id}`, data)
}

export function deletePermission(id) {
  return request.delete(`/permission/${id}`)
}

