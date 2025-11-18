import { request } from '@/utils/http'
import type { AlarmInfo, PageParams, PageResult } from '@/types'

// 获取报警列表(分页)
export const getAlarmList = (params?: PageParams & { 
  nodeId?: string
  alarmType?: string
  alarmLevel?: number
  handleStatus?: number
  startTime?: string
  endTime?: string 
}) => {
  return request.get<PageResult<AlarmInfo>>('/alarm/list', params)
}

// 获取未处理报警列表
export const getUnhandledAlarms = (params?: PageParams) => {
  return request.get<PageResult<AlarmInfo>>('/alarm/unhandled', params)
}

// 根据ID获取报警详情
export const getAlarmById = (id: number) => {
  return request.get<AlarmInfo>(`/alarm/${id}`)
}

// 创建报警记录
export const createAlarm = (data: Partial<AlarmInfo>) => {
  return request.post<void>('/alarm', data)
}

// 处理报警
export const handleAlarm = (id: number, handleRemark: string) => {
  return request.put<void>(`/alarm/handle/${id}`, null, { 
    params: { handleRemark } 
  })
}

// 批量处理报警
export const handleBatchAlarms = (ids: number[], handleRemark?: string) => {
  return request.put<void>('/alarm/handle/batch', null, {
    params: { ids: ids.join(','), handleStatus: 1, handleRemark }
  })
}

// 获取未处理报警数量
export const getUnhandledAlarmCount = () => {
  return request.get<number>('/alarm/unhandled/count')
}

// 获取报警统计信息
export const getAlarmStatistics = (params?: { startTime?: string; endTime?: string }) => {
  return request.get<any>('/alarm/statistics', params)
}

// 删除历史报警
export const deleteHistoryAlarms = (beforeTime: string) => {
  return request.delete<number>('/alarm/history', { beforeTime })
}

// 删除单条报警
export const deleteAlarm = (id: number) => {
  return request.delete<void>(`/alarm/${id}`)
}
