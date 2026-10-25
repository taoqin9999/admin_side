/**
 * 用户管理 API
 * 封装用户模块的增删改查接口
 */
import request from '../utils/request'

export function getUserList(params) {
  return request.get('/user', { params })
}

export function getUser(id) {
  return request.get(`/user/${id}`)
}

export function addUser(data) {
  return request.post('/user', data)
}

export function updateUser(id, data) {
  return request.put(`/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

export function assignUserRoles(id, roleIds) {
  return request.post(`/user/${id}/roles`, { roleIds })
}

// 当前登录用户修改自己的密码
export function updatePassword(data) {
  return request.post('/user/password', data)
}

