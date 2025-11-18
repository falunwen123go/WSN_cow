import { request } from '@/utils/http'
import type { AlarmInfo, PageParams, PageResult } from '@/types'

// 获取报警列表(分页)
export const getAlarmList = (params?: PageParams & { alarmType?: number; alarmLevel?: number; alarmStatus?: number; startTime?: string; endTime?: string }) => {
  return request.get<PageResult<AlarmInfo>>('/alarm/list', params)
}

// 根据ID获取报警详情
export const getAlarmById = (id: number) => {
  return request.get<AlarmInfo>(`/alarm/${id}`)
}

// 处理报警
export const handleAlarm = (data: { id: number; handleUser: string; handleRemark?: string }) => {
  return request.post<void>('/alarm/handle', data)
}

// 获取未处理报警数量
export const getUnhandledAlarmCount = () => {
  return request.get<number>('/alarm/unhandled/count')
}

// 获取报警统计(按级别)
export const getAlarmStatsByLevel = () => {
  return request.get<{ level1: number; level2: number; level3: number }>('/alarm/stats/level')
}

// 获取报警统计(按类型)
export const getAlarmStatsByType = () => {
  return request.get<{ temperature: number; humidity: number; ammonia: number; offline: number }>('/alarm/stats/type')
}

// 批量处理报警
export const handleBatchAlarms = (data: { ids: number[]; handleUser: string; handleRemark?: string }) => {
  return request.post<void>('/alarm/handle/batch', data)
}
