import { request } from '@/utils/http'
import type { SystemConfig } from '@/types'

// 获取所有系统配置
export const getConfigList = () => {
  return request.get<SystemConfig[]>('/config/list')
}

// 根据配置键获取配置值
export const getConfigByKey = (configKey: string) => {
  return request.get<string>(`/config/${configKey}`)
}

// 创建配置项
export const createConfig = (data: Partial<SystemConfig>) => {
  return request.post<void>('/config', data)
}

// 更新系统配置
export const updateConfig = (configKey: string, configValue: string) => {
  return request.put<void>(`/config/${configKey}`, null, { 
    params: { configValue } 
  })
}

// 批量更新系统配置
export const updateBatchConfig = (data: Record<string, string>) => {
  return request.put<void>('/config/batch', data)
}

// 获取报警阈值配置
export const getAlarmThresholds = () => {
  return request.get<Record<string, string>>('/config/alarm/thresholds')
}

// 刷新配置缓存
export const refreshConfig = () => {
  return request.post<void>('/config/refresh')
}
