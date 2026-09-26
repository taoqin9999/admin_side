/**
 * 角色管理 API
 * 封装角色模块的增删改查、分配权限等接口
 */
import request from '../utils/request'

export function getRoleList(params) {
  return request.get('/role', { params })
}

export function getRole(id) {
  return request.get(`/role/${id}`)
}

export function addRole(data) {
  return request.post('/role', data)
}

export function updateRole(id, data) {
  return request.put(`/role/${id}`, data)
}

export function deleteRole(id) {
  return request.delete(`/role/${id}`)
}

export function assignRolePermissions(id, permissionIds) {
  return request.post(`/role/${id}/permissions`, { permissionIds })
}

