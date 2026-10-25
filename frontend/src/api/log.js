/**
 * 操作日志查询 API
 * 仅提供只读查询与 CSV 导出
 */
import request from '../utils/request'

export function getLogList(params) {
  return request.get('/log', { params })
}

export function getLogExport(params) {
  return request.get('/log/export', { params })
}
