/**
 * axios 请求封装
 * - 统一设置 baseURL、超时时间
 * - 响应拦截统一处理业务错误码和 HTTP 状态码
 * - 401 未登录自动跳转登录页
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json;charset=UTF-8' }
})

// 响应拦截
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401) {
        // 未登录或会话过期，直接跳转登录页，不弹错误提示
        router.push('/login')
        return Promise.reject(new Error('未登录'))
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        router.push('/login')
        return Promise.reject(new Error('未登录'))
      }
      ElMessage.error(`请求失败(${status}): ${error.response.data?.msg || error.message}`)
    } else {
      // 网络错误（后端不可达、跨域等）
      ElMessage.error('服务暂不可用，请检查后端服务是否正常运行')
    }
    return Promise.reject(error)
  }
)

export default request
