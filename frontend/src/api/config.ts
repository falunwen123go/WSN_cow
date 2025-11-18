import { request } from '@/utils/http'
import type { SystemConfig } from '@/types'

// 获取所有系统配置
export const getConfigList = () => {
  return request.get<SystemConfig[]>('/config/list')
}

// 根据配置键获取配置值
export const getConfigByKey = (configKey: string) => {
  return request.get<SystemConfig>(`/config/${configKey}`)
}

// 更新系统配置
export const updateConfig = (data: { configKey: string; configValue: string }) => {
  return request.put<void>('/config/update', data)
}

// 批量更新系统配置
export const updateBatchConfig = (data: { configKey: string; configValue: string }[]) => {
  return request.put<void>('/config/update/batch', data)
}

// 获取报警阈值配置
export const getAlarmThresholds = () => {
  return request.get<{
    temperatureMax: number
    temperatureMin: number
    humidityMax: number
    humidityMin: number
    ammoniaMax: number
  }>('/config/alarm/thresholds')
}

// 更新报警阈值配置
export const updateAlarmThresholds = (data: {
  temperatureMax?: number
  temperatureMin?: number
  humidityMax?: number
  humidityMin?: number
  ammoniaMax?: number
}) => {
  return request.put<void>('/config/alarm/thresholds', data)
}

// 获取数据采集间隔配置
export const getDataCollectionInterval = () => {
  return request.get<number>('/config/data/interval')
}

// 更新数据采集间隔配置
export const updateDataCollectionInterval = (interval: number) => {
  return request.put<void>('/config/data/interval', { interval })
}
