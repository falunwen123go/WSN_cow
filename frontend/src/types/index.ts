// 通用响应结构
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页参数
export interface PageParams {
  pageNum: number
  pageSize: number
}

// 分页响应
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 传感器数据
export interface SensorData {
  id: number
  nodeId: number
  temperature: number
  humidity: number
  ammonia: number
  light: number
  dataStatus: number
  createTime: string
}

// 节点信息
export interface NodeInfo {
  id: number
  nodeName: string
  nodeType: number
  nodeStatus: number
  batteryLevel: number
  signalStrength: number
  lastOnlineTime: string
  createTime: string
  updateTime: string
}

// 报警信息
export interface AlarmInfo {
  id: number
  nodeId: number
  alarmType: number
  alarmLevel: number
  alarmContent: string
  alarmStatus: number
  alarmTime: string
  handleTime?: string
  handleUser?: string
  handleRemark?: string
}

// 设备信息
export interface DeviceInfo {
  id: number
  deviceName: string
  deviceType: number
  nodeId: number
  deviceStatus: number
  controlMode: number
  createTime: string
  updateTime: string
}

// 设备控制日志
export interface DeviceControlLog {
  id: number
  deviceId: number
  controlType: number
  controlValue: string
  controlTime: string
  controlUser?: string
  controlResult?: string
}

// 系统配置
export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  configDesc: string
  updateTime: string
}

// 统计数据
export interface Statistics {
  totalNodes: number
  onlineNodes: number
  totalDevices: number
  activeDevices: number
  totalAlarms: number
  unhandledAlarms: number
  avgTemperature: number
  avgHumidity: number
  avgAmmonia: number
}
