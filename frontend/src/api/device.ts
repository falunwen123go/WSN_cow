import { request } from '@/utils/http'
import type { DeviceInfo, DeviceControlLog, PageParams, PageResult } from '@/types'

// 获取设备列表
export const getDeviceList = (params?: PageParams & { deviceType?: string; status?: number }) => {
  return request.get<DeviceInfo[]>('/device/list', params)
}

// 获取所有设备(不分页)
export const getAllDevices = () => {
  return request.get<DeviceInfo[]>('/device/all')
}

// 根据ID获取设备信息
export const getDeviceById = (id: number) => {
  return request.get<DeviceInfo>(`/device/${id}`)
}

// 根据设备ID获取设备信息
export const getDeviceByDeviceId = (deviceId: string) => {
  return request.get<DeviceInfo>(`/device/deviceId/${deviceId}`)
}

// 手动控制设备
export const controlDevice = (
  deviceId: string,
  action: 'START' | 'STOP' | 'ADJUST',
  operator?: string,
  reason?: string
) => {
  return request.post<void>('/device/control', {
    deviceId,
    action,
    operator: operator || 'admin',
    reason
  })
}

// 切换控制模式
export const switchDeviceMode = (deviceId: string, autoMode: number) => {
  return request.put<void>(`/device/${deviceId}/mode`, { autoMode })
}

// 查询控制日志(分页)
export const getDeviceControlLogs = (params: PageParams & { 
  deviceId?: string
  controlMode?: number
  startTime?: string
  endTime?: string 
}) => {
  return request.get<PageResult<DeviceControlLog>>('/device/log', params)
}

// 获取设备最新日志
export const getDeviceLatestLog = (deviceId: string) => {
  return request.get<DeviceControlLog>(`/device/${deviceId}/latest-log`)
}

// 获取运行中设备数量
export const getRunningDeviceCount = () => {
  return request.get<number>('/device/running/count')
}
