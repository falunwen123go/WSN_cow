import { request } from '@/utils/http'
import type { SensorData, PageParams, PageResult, Statistics } from '@/types'

// 获取最新传感器数据
export const getLatestSensorData = (nodeId?: number) => {
  return request.get<SensorData[]>('/sensor/latest', { nodeId })
}

// 获取历史传感器数据(分页)
export const getSensorHistory = (params: PageParams & { nodeId?: number; startTime?: string; endTime?: string }) => {
  return request.get<PageResult<SensorData>>('/sensor/history', params)
}

// 获取传感器统计数据
export const getSensorStatistics = (params?: { nodeId?: number; startTime?: string; endTime?: string }) => {
  return request.get<Statistics>('/sensor/statistics', params)
}

// 按节点ID获取传感器数据
export const getSensorDataByNode = (nodeId: number) => {
  return request.get<SensorData[]>(`/sensor/node/${nodeId}`)
}

// 获取温度趋势数据
export const getTemperatureTrend = (params: { nodeId?: number; hours?: number }) => {
  return request.get<{ time: string; value: number }[]>('/sensor/temperature/trend', params)
}

// 获取湿度趋势数据
export const getHumidityTrend = (params: { nodeId?: number; hours?: number }) => {
  return request.get<{ time: string; value: number }[]>('/sensor/humidity/trend', params)
}

// 获取氨气趋势数据
export const getAmmoniaTrend = (params: { nodeId?: number; hours?: number }) => {
  return request.get<{ time: string; value: number }[]>('/sensor/ammonia/trend', params)
}
