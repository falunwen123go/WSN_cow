<template>
  <div class="devices-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#409EFF"><Setting /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总设备数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#67C23A"><CircleCheck /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.active }}</div>
              <div class="stat-label">运行中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#E6A23C"><Cpu /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.autoMode }}</div>
              <div class="stat-label">自动模式</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#909399"><VideoPlay /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.offline }}</div>
              <div class="stat-label">离线</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备卡片网格 -->
    <el-card shadow="never">
      <template #header>
        <span>设备列表</span>
      </template>
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="device in deviceList" :key="device.id">
          <el-card shadow="hover" class="device-card">
            <div class="device-header">
              <div>
                <div class="device-name">{{ device.deviceName }}</div>
                <div class="device-id">{{ device.deviceId }}</div>
              </div>
              <el-tag :type="getDeviceTypeColor(device.deviceType)" size="large">
                {{ formatDeviceType(device.deviceType) }}
              </el-tag>
            </div>
            <el-divider />
            <div class="device-body">
              <div class="device-info">
                <div class="info-item">
                  <span class="label">状态:</span>
                  <el-tag :type="device.status === 1 ? 'success' : 'info'" size="small">
                    {{ device.status === 1 ? '运行中' : '已停止' }}
                  </el-tag>
                </div>
                <div class="info-item">
                  <span class="label">模式:</span>
                  <el-tag :type="device.controlMode === 1 ? 'warning' : 'info'" size="small">
                    {{ device.controlMode === 1 ? '自动' : '手动' }}
                  </el-tag>
                </div>
              </div>
              <div class="device-controls">
                <DeviceSwitch
                  :device-id="device.deviceId"
                  :device-name="device.deviceName"
                  :status="device.status"
                  :mode="device.controlMode"
                  @control-change="handleControlChange"
                />
                <el-tooltip 
                  v-if="device.controlMode === 1" 
                  content="设备处于自动模式，需要先切换到手动模式才能控制" 
                  placement="top"
                >
                  <el-button
                    :type="device.controlMode === 1 ? 'warning' : 'primary'"
                    size="small"
                    @click="toggleMode(device)"
                  >
                    切换到{{ device.controlMode === 1 ? '手动' : '自动' }}
                  </el-button>
                </el-tooltip>
                <el-button
                  v-else
                  :type="device.controlMode === 1 ? 'warning' : 'primary'"
                  size="small"
                  @click="toggleMode(device)"
                >
                  切换到{{ device.controlMode === 1 ? '手动' : '自动' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 控制日志 -->
    <el-card shadow="never" style="margin-top: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>控制日志</span>
          <el-button type="primary" size="small" @click="fetchControlLogs">刷新</el-button>
        </div>
      </template>
      <el-table :data="controlLogs" stripe v-loading="logLoading">
        <el-table-column prop="deviceId" label="设备ID" width="150" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-tag :type="getActionColor(row.controlAction)" size="small">
              {{ formatAction(row.controlAction) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="控制模式" width="120">
          <template #default="{ row }">
            <el-tag :type="row.controlMode === 1 ? 'warning' : 'info'" size="small">
              {{ row.controlMode === 1 ? '自动' : '手动' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.controlTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" show-overflow-tooltip />
      </el-table>
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="logPagination.page"
          v-model:page-size="logPagination.size"
          :page-sizes="[10, 20, 50]"
          :total="logPagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchControlLogs"
          @current-change="fetchControlLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, CircleCheck, Cpu, VideoPlay } from '@element-plus/icons-vue'
import { getDeviceList, switchDeviceMode, getDeviceControlLogs } from '@/api/device'
import DeviceSwitch from '@/components/DeviceSwitch.vue'
import type { DeviceInfo, DeviceControlLog } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const logLoading = ref(false)
const deviceList = ref<DeviceInfo[]>([])
const controlLogs = ref<DeviceControlLog[]>([])

const stats = reactive({
  total: 0,
  active: 0,
  autoMode: 0,
  offline: 0
})

const logPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchDeviceList = async () => {
  loading.value = true
  try {
    const res = await getDeviceList()
    deviceList.value = res || []
    
    // 计算统计数据
    stats.total = deviceList.value.length
    stats.active = deviceList.value.filter(d => d.status === 1).length
    stats.autoMode = deviceList.value.filter(d => d.controlMode === 1).length
    stats.offline = deviceList.value.filter(d => d.status === 0).length
  } catch (error) {
    ElMessage.error('获取设备列表失败')
  } finally {
    loading.value = false
  }
}

const fetchControlLogs = async () => {
  loading.value = true
  try {
    const res = await getDeviceControlLogs({
      pageNum: logPagination.page,
      pageSize: logPagination.size
    })
    controlLogs.value = res.list || []
    logPagination.total = res.total || 0
  } catch (error) {
    ElMessage.error('获取控制日志失败')
  } finally {
    logLoading.value = false
  }
}

const toggleMode = async (device: DeviceInfo) => {
  const newMode = device.controlMode === 1 ? 0 : 1
  const modeName = newMode === 1 ? '自动' : '手动'
  
  try {
    await ElMessageBox.confirm(
      `确认切换设备 ${device.deviceName} 到${modeName}模式？`,
      '确认操作',
      { type: 'warning' }
    )
    
    await switchDeviceMode(device.deviceId, newMode)
    ElMessage.success(`已切换到${modeName}模式`)
    fetchDeviceList()
    fetchControlLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('切换模式失败')
    }
  }
}

const handleControlChange = () => {
  fetchDeviceList()
  fetchControlLogs()
}

const formatDeviceType = (type: string) => {
  const map: Record<string, string> = {
    FAN: '排风扇',
    CURTAIN: '卷帘',
    HEATING: '供热',
    SPRAY: '喷雾'
  }
  return map[type] || type
}

const getDeviceTypeColor = (type: string) => {
  const map: Record<string, string> = {
    FAN: 'primary',
    CURTAIN: 'success',
    HEATING: 'danger',
    SPRAY: 'warning'
  }
  return map[type] || 'info'
}

const formatAction = (action: string) => {
  const map: Record<string, string> = {
    START: '启动',
    STOP: '停止',
    ADJUST: '调整'
  }
  return map[action] || action
}

const getActionColor = (action: string) => {
  const map: Record<string, string> = {
    START: 'success',
    STOP: 'info',
    ADJUST: 'warning'
  }
  return map[action] || 'info'
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchDeviceList()
  fetchControlLogs()
})
</script>

<style scoped>
.devices-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.device-card {
  margin-bottom: 20px;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.device-id {
  font-size: 12px;
  color: #909399;
}

.device-body {
  margin-top: 10px;
}

.device-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  font-size: 14px;
  color: #606266;
}

.device-controls {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>
