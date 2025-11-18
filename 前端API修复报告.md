# 前端API修复报告

## 修复时间
2024年 - 第7天后续修复

## 问题描述
前端运行时出现以下错误:
1. **数据监控界面**: 显示"请求地址不存在"
2. **节点管理界面**: 显示"获取节点列表失败"和"节点不存在"

根本原因: 前端API层的路径和参数与后端Controller不匹配

## 修复内容

### 1. Sensor API (frontend/src/api/sensor.ts)

#### 修复项:
- ✅ `getSensorDataByNode`: 路径从 `/sensor/data/{nodeId}` 改为 `/sensor/latest/{nodeId}`
- ✅ `getSensorHistory` 重命名为 `getHistorySensorData`，使用显式参数 (nodeId, startTime, endTime, page, size)
- ✅ `getSensorStatistics`: 使用显式参数 (nodeId?, hours?) 代替泛型params对象

#### 签名对比:
```typescript
// 修复前
export const getSensorHistory = (params: any) => {
  return request.get('/sensor/history', params)
}

// 修复后
export const getHistorySensorData = (
  nodeId: string,
  startTime: string,
  endTime: string,
  page?: number,
  size?: number
) => {
  return request.get<PageResult<SensorData>>('/sensor/history', {
    nodeId, startTime, endTime, page, size
  })
}
```

### 2. Node API (frontend/src/api/node.ts)

#### 修复项:
- ✅ `getNodeList`: 返回类型从 `NodeInfo[]` 改为 `PageResult<NodeInfo>`，添加status/keyword参数
- ✅ `getNodeById`: 参数从 `string nodeId` 改为 `number id`
- ✅ 新增 `getNodeByNodeId(nodeId: string)` 用于字符串查询，路径为 `/node/nodeId/{nodeId}`
- ✅ `updateNode`: 参数从 `string nodeId` 改为 `number id`
- ✅ `deleteNode`: 参数从 `string nodeId` 改为 `number id`

#### 签名对比:
```typescript
// 修复前
export const deleteNode = (nodeId: string) => {
  return request.delete(`/node/${nodeId}`)
}

// 修复后
export const deleteNode = (id: number) => {
  return request.delete<void>(`/node/${id}`)
}

// 新增
export const getNodeByNodeId = (nodeId: string) => {
  return request.get<NodeInfo>(`/node/nodeId/${nodeId}`)
}
```

### 3. Alarm API (frontend/src/api/alarm.ts)

#### 修复项:
- ✅ `handleAlarm`: 移除 `handleStatus: 1` 参数(后端自动设置)，handleRemark改为必填

#### 签名对比:
```typescript
// 修复前
export const handleAlarm = (id: number, handleRemark?: string) => {
  return request.put(`/alarm/handle/${id}`, null, { 
    params: { handleStatus: 1, handleRemark } 
  })
}

// 修复后
export const handleAlarm = (id: number, handleRemark: string) => {
  return request.put<void>(`/alarm/handle/${id}`, null, { 
    params: { handleRemark } 
  })
}
```

### 4. Device API (frontend/src/api/device.ts)

#### 修复项:
- ✅ `getDeviceList`: 添加PageParams类型和status参数
- ✅ `getDeviceById`: 参数从 `string deviceId` 改为 `number id`
- ✅ 新增 `getDeviceByDeviceId(deviceId: string)` 用于字符串查询
- ✅ `switchDeviceMode`: 参数从 `autoMode` 改为 `mode`，使用params传参
- ✅ `controlDevice`: 更新签名为 (deviceId, action, operator?, reason?)
- ✅ `getDeviceControlLogs`: 返回类型修正为 `PageResult<DeviceControlLog>`

#### 签名对比:
```typescript
// 修复前
export const switchDeviceMode = (deviceId: string, autoMode: boolean) => {
  return request.put(`/device/${deviceId}/mode`, { autoMode })
}

// 修复后
export const switchDeviceMode = (deviceId: string, mode: number) => {
  return request.put<void>(`/device/${deviceId}/mode`, null, {
    params: { mode }
  })
}
```

### 5. Config API (frontend/src/api/config.ts)

#### 修复项:
- ✅ `updateConfig`: 参数名从 `value` 改为 `configValue`

#### 签名对比:
```typescript
// 修复前
export const updateConfig = (configKey: string, value: string) => {
  return request.put(`/config/${configKey}`, null, { 
    params: { value } 
  })
}

// 修复后
export const updateConfig = (configKey: string, configValue: string) => {
  return request.put<void>(`/config/${configKey}`, null, { 
    params: { configValue } 
  })
}
```

## 视图组件验证

### 已验证正确的组件:

#### ✅ NodesView.vue
- `deleteNode(row.id)` - 使用数字ID ✓
- `updateNode(currentNode.value.id, formData.value)` - 使用数字ID ✓
- `addNode(formData.value)` - 正确 ✓

#### ✅ HistoryView.vue
- `getHistorySensorData(nodeId, startTime, endTime, page, size)` - 显式参数 ✓

#### ✅ SettingsView.vue
- `updateConfig(update.key, update.value)` - 参数名正确(update.value是变量名) ✓

#### ✅ DevicesView.vue
- `switchDeviceMode(device.deviceId, newMode)` - newMode是数字 ✓

#### ✅ AlarmsView.vue
- `handleAlarmApi(currentAlarm.value.id!, handleForm.handleRemark)` - handleRemark必填 ✓

#### ✅ DashboardView.vue
- `handleAlarm(id, '已确认')` - handleRemark提供 ✓

## 后端API路径总结

### Sensor相关
- `GET /api/sensor/latest` - 获取所有最新数据
- `GET /api/sensor/latest/{nodeId}` - 获取指定节点最新数据
- `GET /api/sensor/history` - 获取历史数据(分页)
- `GET /api/sensor/statistics` - 获取统计数据

### Node相关
- `GET /api/node/list` - 获取节点列表(分页)
- `GET /api/node/all` - 获取所有节点
- `GET /api/node/{id}` - 根据数字ID获取节点
- `GET /api/node/nodeId/{nodeId}` - 根据字符串节点ID获取节点
- `POST /api/node` - 添加节点
- `PUT /api/node/{id}` - 更新节点
- `DELETE /api/node/{id}` - 删除节点

### Alarm相关
- `GET /api/alarm/list` - 获取告警列表(分页)
- `PUT /api/alarm/handle/{id}` - 处理告警

### Device相关
- `GET /api/device/list` - 获取设备列表
- `GET /api/device/{id}` - 根据数字ID获取设备
- `GET /api/device/deviceId/{deviceId}` - 根据字符串设备ID获取设备
- `POST /api/device/control` - 控制设备
- `PUT /api/device/{deviceId}/mode` - 切换控制模式
- `GET /api/device/log` - 获取控制日志

### Config相关
- `GET /api/config/list` - 获取所有配置
- `GET /api/config/{configKey}` - 获取指定配置
- `PUT /api/config/{configKey}` - 更新配置(使用configValue参数)

## 类型定义验证

✅ **frontend/src/types/index.ts** - 所有类型定义正确:
- `ApiResponse<T>` ✓
- `PageResult<T>` ✓
- `PageParams` ✓
- `SensorData` ✓
- `NodeInfo` ✓
- `AlarmInfo` ✓
- `DeviceInfo` ✓
- `DeviceControlLog` ✓
- `SystemConfig` ✓

## 测试建议

### 1. 节点管理测试
```bash
# 重启前端服务
cd frontend
npm run dev
```

访问 http://localhost:5173/nodes 验证:
- ✅ 节点列表正常加载
- ✅ 添加节点功能正常
- ✅ 编辑节点功能正常 (使用数字ID)
- ✅ 删除节点功能正常 (使用数字ID)

### 2. 数据监控测试
访问 http://localhost:5173/ 验证:
- ✅ 实时数据正常显示
- ✅ 统计信息正常加载
- ✅ 图表正常渲染

### 3. 历史数据测试
访问 http://localhost:5173/history 验证:
- ✅ 查询功能正常
- ✅ 时间范围筛选正常
- ✅ 趋势图正常显示

### 4. 告警管理测试
访问 http://localhost:5173/alarms 验证:
- ✅ 告警列表正常加载
- ✅ 处理告警功能正常 (handleRemark必填)

### 5. 设备控制测试
访问 http://localhost:5173/devices 验证:
- ✅ 设备列表正常显示
- ✅ 模式切换功能正常 (使用mode参数)
- ✅ 控制日志正常显示

### 6. 系统设置测试
访问 http://localhost:5173/settings 验证:
- ✅ 配置加载正常
- ✅ 保存配置功能正常 (使用configValue参数)

## 关键改进点

1. **类型安全**: 所有ID相关操作严格区分 `number` (数据库主键) 和 `string` (业务标识)
2. **API路径**: 确保前端路径与后端Controller @RequestMapping完全一致
3. **参数名称**: 参数名与后端 @RequestParam 名称精确匹配
4. **返回类型**: 使用泛型明确指定返回类型，提高类型安全性
5. **分页支持**: 正确使用 `PageResult<T>` 处理分页数据

## 预期结果

修复后应该:
1. ❌ 不再出现"请求地址不存在"错误
2. ❌ 不再出现"获取节点列表失败"错误
3. ❌ 不再出现"节点不存在"错误
4. ✅ 所有6个页面功能正常
5. ✅ 前后端API完全对接

## 修复完成清单

- [x] 修复 sensor.ts (3个方法)
- [x] 修复 node.ts (5个方法 + 1个新增)
- [x] 修复 alarm.ts (1个方法)
- [x] 修复 device.ts (5个方法 + 1个新增)
- [x] 修复 config.ts (1个方法)
- [x] 验证 NodesView.vue
- [x] 验证 HistoryView.vue
- [x] 验证 SettingsView.vue
- [x] 验证 DevicesView.vue
- [x] 验证 AlarmsView.vue
- [x] 验证 DashboardView.vue
- [x] 验证类型定义

---

**修复完成! 请重启前端服务测试各功能是否正常。**
