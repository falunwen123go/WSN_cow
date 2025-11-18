import { request } from '@/utils/http'
import type { DeviceInfo, DeviceControlLog, PageParams, PageResult } from '@/types'

// 获取设备列表(分页)
export const getDeviceList = (params?: PageParams & { deviceType?: number; deviceStatus?: number; nodeId?: number }) => {
  return request.get<PageResult<DeviceInfo>>('/device/list', params)
}

// 获取所有设备(不分页)
export const getAllDevices = () => {
  return request.get<DeviceInfo[]>('/device/all')
}

// 根据ID获取设备信息
export const getDeviceById = (id: number) => {
  return request.get<DeviceInfo>(`/device/${id}`)
}

// 控制设备(开/关)
export const controlDevice = (data: { deviceId: number; controlValue: string; controlUser?: string }) => {
  return request.post<void>('/device/control', data)
}

// 切换设备控制模式(自动/手动)
export const switchDeviceMode = (data: { deviceId: number; controlMode: number }) => {
  return request.post<void>('/device/mode/switch', data)
}

// 获取设备控制日志(分页)
export const getDeviceControlLogs = (params: PageParams & { deviceId?: number; startTime?: string; endTime?: string }) => {
  return request.get<PageResult<DeviceControlLog>>('/device/logs', params)
}

// 获取设备状态统计
export const getDeviceStatusStats = () => {
  return request.get<{ online: number; offline: number; total: number }>('/device/status/stats')
}

// 批量控制设备
export const controlBatchDevices = (data: { deviceIds: number[]; controlValue: string; controlUser?: string }) => {
  return request.post<void>('/device/control/batch', data)
}
