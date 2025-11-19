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
  list: T[]  // 后端使用list而不是records
  total: number
  pageSize: number
  pageNum: number
  pages: number
}

// 传感器数据
export interface SensorData {
  id: number
  nodeId: string                    // ⚠️ String类型
  temperature: number
  humidity: number
  nh3Concentration: number          // ⚠️ 氨气浓度
  h2sConcentration: number          // ⚠️ 硫化氢浓度
  milkYield?: number                // 🆕 产奶量 (kg/天)
  dataStatus: number
  collectTime: string               // ⚠️ 采集时间
  createTime: string
  // 计算字段
  thi?: number                      // 🆕 温湿度指数 (THI)
  aqi?: number                      // 🆕 空气质量指数 (AQI)
  environmentScore?: number         // 🆕 环境评分 (0-100)
}

// 节点信息
export interface NodeInfo {
  id: number
  nodeId: string                    // ⚠️ String类型
  nodeName: string
  location: string
  status: number                    // ⚠️ 状态字段名
  batteryLevel: number
  signalStrength: number
  lastCommTime: string
  installDate: string
  remark: string
  lastOnlineTime: string
  createTime: string
  updateTime: string
}

// 报警信息
export interface AlarmInfo {
  id: number
  nodeId: string                    // ⚠️ String类型
  alarmType: string                 // ⚠️ String类型: TEMP/HUMI/NH3/H2S
  alarmLevel: number
  currentValue: number              // ⚠️ 当前值
  threshold: number                 // ⚠️ 阈值
  alarmTime: string
  handleStatus: number              // ⚠️ 处理状态字段名
  handleTime?: string
  handleRemark?: string
}

// 设备信息
export interface DeviceInfo {
  id: number
  deviceId: string                  // ⚠️ String类型
  deviceName: string
  deviceType: string                // ⚠️ String类型: FAN/CURTAIN/HEATING/SPRAY
  status: number                    // ⚠️ 状态字段名
  autoMode: number                  // ⚠️ 自动模式: 0-手动, 1-自动
  controlMode: number
  createTime: string
  updateTime: string
}

// 设备控制日志
export interface DeviceControlLog {
  id: number
  deviceId: string                  // ⚠️ String类型
  controlAction: string             // ⚠️ 控制动作: START/STOP/ADJUST
  controlMode: number               // ⚠️ 控制模式: 0-手动, 1-自动
  operator: string
  controlTime: string
  reason: string
}

// 系统配置
export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  description: string               // ⚠️ 字段名
  configType: string
  createTime: string
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
