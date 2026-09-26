/**
 * 登录认证 API
 * 封装登录、获取用户信息、退出等接口
 */
import request from '../utils/request'

export function login(data) {
  return request.post('/login', data)
}

export function userInfo() {
  return request.get('/user/info')
}

export function logout() {
  return request.post('/logout')
}
