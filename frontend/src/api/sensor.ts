import { request } from '@/utils/http'
import type { SensorData, PageParams, PageResult } from '@/types'

// 获取所有节点最新数据
export const getLatestSensorData = () => {
  return request.get<SensorData[]>('/sensor/latest')
}

// 获取指定节点最新数据
export const getSensorDataByNode = (nodeId: string) => {
  return request.get<SensorData>(`/sensor/data/${nodeId}`)
}

// 获取历史传感器数据(分页)
export const getHistorySensorData = (
  nodeId: string,
  startTime: string,
  endTime: string,
  page: number,
  size: number
) => {
  return request.get<PageResult<SensorData>>('/sensor/history', {
    nodeId,
    startTime,
    endTime,
    pageNum: page,
    pageSize: size
  })
}

// 获取传感器统计数据
export const getSensorStatistics = (nodeId?: string, hours?: number) => {
  return request.get<any>('/sensor/statistics', { nodeId, hours })
}
