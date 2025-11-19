<template>
  <div class="dashboard-page">
    <!-- 统计卡片区 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <DataCard
          title="平均温度"
          :value="stats.avgTemperature?.toFixed(1) || '0.0'"
          unit="℃"
          :icon="Sunny"
          icon-color="#f56c6c"
          :trend="2.5"
        />
      </el-col>
      <el-col :span="6">
        <DataCard
          title="平均湿度"
          :value="stats.avgHumidity?.toFixed(1) || '0.0'"
          unit="%"
          :icon="Drizzling"
          icon-color="#409eff"
          :trend="-1.2"
        />
      </el-col>
      <el-col :span="6">
        <DataCard
          title="氨气浓度"
          :value="stats.avgNh3?.toFixed(1) || '0.0'"
          unit="ppm"
          :icon="Warning"
          icon-color="#e6a23c"
        />
      </el-col>
      <el-col :span="6">
        <DataCard
          title="硫化氢浓度"
          :value="stats.avgH2s?.toFixed(1) || '0.0'"
          unit="ppm"
          :icon="WarnTriangleFilled"
          icon-color="#909399"
        />
      </el-col>
    </el-row>

    <!-- 节点和设备统计 -->
    <el-row :gutter="20" class="status-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Connection /></el-icon>
              <span>节点状态</span>
            </div>
          </template>
          <div class="status-content">
            <div class="status-item">
              <span class="status-label">总节点数</span>
              <span class="status-value">{{ nodeStats.total }}</span>
            </div>
            <div class="status-item online">
              <span class="status-label">在线节点</span>
              <span class="status-value">{{ nodeStats.online }}</span>
            </div>
            <div class="status-item offline">
              <span class="status-label">离线节点</span>
              <span class="status-value">{{ nodeStats.offline }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>设备状态</span>
            </div>
          </template>
          <div class="status-content">
            <div class="status-item">
              <span class="status-label">总设备数</span>
              <span class="status-value">{{ deviceStats.total }}</span>
            </div>
            <div class="status-item online">
              <span class="status-label">运行中</span>
              <span class="status-value">{{ deviceStats.online }}</span>
            </div>
            <div class="status-item offline">
              <span class="status-label">已停止</span>
              <span class="status-value">{{ deviceStats.offline }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图表区 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><DataLine /></el-icon>
              <span>温湿度趋势</span>
            </div>
          </template>
          <div ref="tempHumiChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>气体浓度趋势</span>
            </div>
          </template>
          <div ref="gasChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新报警信息 -->
    <el-row :gutter="20" class="alarm-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span>最新报警信息</span>
              <el-tag :type="unhandledCount > 0 ? 'danger' : 'success'" size="small">
                未处理: {{ unhandledCount }}
              </el-tag>
            </div>
          </template>
          <el-table :data="alarmList" stripe style="width: 100%">
            <el-table-column prop="alarmTime" label="时间" width="180" />
            <el-table-column label="节点ID" width="120">
              <template #default="{ row }">
                <el-tag size="small">{{ row.nodeId }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getAlarmTypeColor(row.alarmType)" size="small">
                  {{ getAlarmTypeName(row.alarmType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="级别" width="120">
              <template #default="{ row }">
                <AlarmBadge :level="row.alarmLevel" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="内容">
              <template #default="{ row }">
                {{ generateAlarmContent(row) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.handleStatus === 1 ? 'success' : 'warning'" size="small">
                  {{ row.handleStatus === 1 ? '已处理' : '未处理' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.handleStatus === 0"
                  type="primary"
                  size="small"
                  @click="handleAlarmClick(row.id)"
                >
                  处理
                </el-button>
                <span v-else class="handled-text">已处理</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import dayjs from 'dayjs'
import {
  Sunny,
  Drizzling,
  Warning,
  WarnTriangleFilled,
  Connection,
  Setting,
  DataLine,
  DataAnalysis,
  Bell
} from '@element-plus/icons-vue'
import DataCard from '@/components/DataCard.vue'
import AlarmBadge from '@/components/AlarmBadge.vue'
import { getLatestSensorData, getSensorStatistics, getHistorySensorData } from '@/api/sensor'
import { getNodeList, getOnlineNodeCount } from '@/api/node'
import { getDeviceList, getRunningDeviceCount } from '@/api/device'
import { getAlarmList, handleAlarm, getUnhandledAlarmCount } from '@/api/alarm'
import type { SensorData, AlarmInfo, NodeInfo, DeviceInfo } from '@/types'

// 统计数据
const stats = ref({
  avgTemperature: 0,
  avgHumidity: 0,
  avgNh3: 0,
  avgH2s: 0
})

// 节点状态统计
const nodeStats = ref({
  total: 0,
  online: 0,
  offline: 0
})

// 设备状态统计
const deviceStats = ref({
  total: 0,
  online: 0,
  offline: 0
})

// 报警列表
const alarmList = ref<AlarmInfo[]>([])
const unhandledCount = ref(0)

// 图表实例
const tempHumiChartRef = ref<HTMLDivElement>()
const gasChartRef = ref<HTMLDivElement>()
let tempHumiChart: ECharts | null = null
let gasChart: ECharts | null = null

// 定时器
let refreshTimer: number | null = null

// 获取统计数据
const fetchStats = async () => {
  try {
    const latestData = await getLatestSensorData()
    
    if (latestData && latestData.length > 0) {
      // 计算平均值
      const count = latestData.length
      const sum = latestData.reduce((acc, item) => ({
        temperature: acc.temperature + item.temperature,
        humidity: acc.humidity + item.humidity,
        nh3: acc.nh3 + item.nh3Concentration,
        h2s: acc.h2s + item.h2sConcentration
      }), { temperature: 0, humidity: 0, nh3: 0, h2s: 0 })
      
      stats.value.avgTemperature = Math.round(sum.temperature / count * 10) / 10
      stats.value.avgHumidity = Math.round(sum.humidity / count * 10) / 10
      stats.value.avgNh3 = Math.round(sum.nh3 / count * 10) / 10
      stats.value.avgH2s = Math.round(sum.h2s / count * 10) / 10
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取节点状态
const fetchNodeStats = async () => {
  try {
    const nodeList = await getNodeList({ pageNum: 1, pageSize: 1000 })
    const onlineCount = nodeList.filter((node: NodeInfo) => node.status === 1).length
    const offlineCount = nodeList.filter((node: NodeInfo) => node.status === 0).length
    
    nodeStats.value = {
      total: nodeList.length,
      online: onlineCount,
      offline: offlineCount
    }
  } catch (error) {
    console.error('获取节点状态失败:', error)
  }
}

// 获取设备状态
const fetchDeviceStats = async () => {
  try {
    const deviceList = await getDeviceList()
    const runningCount = deviceList.filter((device: DeviceInfo) => device.status === 1).length
    const stoppedCount = deviceList.filter((device: DeviceInfo) => device.status === 0).length
    
    deviceStats.value = {
      total: deviceList.length,
      online: runningCount,
      offline: stoppedCount
    }
  } catch (error) {
    console.error('获取设备状态失败:', error)
  }
}

// 获取报警列表
const fetchAlarms = async () => {
  try {
    const result = await getAlarmList({ pageNum: 1, pageSize: 10 })
    alarmList.value = result.list || []
    
    const count = await getUnhandledAlarmCount()
    unhandledCount.value = count
  } catch (error) {
    console.error('获取报警列表失败:', error)
  }
}

// 初始化温湿度图表
const initTempHumiChart = async () => {
  // 等待DOM渲染
  await nextTick()
  
  if (!tempHumiChartRef.value) {
    console.warn('温湿度图表容器未找到')
    return
  }
  
  // 检查DOM尺寸
  const checkDOM = () => {
    const width = tempHumiChartRef.value?.clientWidth || 0
    const height = tempHumiChartRef.value?.clientHeight || 0
    return width > 0 && height > 0
  }
  
  // 等待DOM尺寸就绪
  if (!checkDOM()) {
    await new Promise(resolve => setTimeout(resolve, 100))
    if (!checkDOM()) {
      console.warn('温湿度图表容器尺寸为0')
      return
    }
  }
  
  // 销毁旧图表
  if (tempHumiChart) {
    try {
      tempHumiChart.dispose()
    } catch (e) {
      console.warn('销毁旧图表失败:', e)
    }
  }
  
  tempHumiChart = echarts.init(tempHumiChartRef.value)
  
  try {
    // 获取最近1小时的历史数据
    const endTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const startTime = dayjs().subtract(1, 'hour').format('YYYY-MM-DD HH:mm:ss')
    
    // 使用第一个节点的数据
    const historyData = await getHistorySensorData('NODE_001', startTime, endTime, 1, 20)
    const dataList = historyData.list || []
    
    if (dataList.length === 0) {
      console.warn('没有历史传感器数据')
      // 如果没有历史数据，使用最新数据
      const latestData = await getLatestSensorData()
      if (latestData && latestData.length > 0) {
        const times = latestData.map((item: SensorData) => 
          dayjs(item.collectTime).format('HH:mm')
        )
        const temps = latestData.map((item: SensorData) => item.temperature)
        const humis = latestData.map((item: SensorData) => item.humidity)
        
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'cross' },
            backgroundColor: 'rgba(50, 50, 50, 0.9)',
            borderColor: '#333',
            textStyle: { color: '#fff' }
          },
          legend: {
            data: ['温度', '湿度'],
            top: 10
          },
          grid: {
            left: '60px',
            right: '60px',
            top: '60px',
            bottom: '40px'
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: times,
            axisLabel: {
              rotate: 0,
              interval: 0
            }
          },
          yAxis: [
            {
              type: 'value',
              name: '温度(℃)',
              position: 'left',
              axisLabel: { formatter: '{value} ℃' },
              nameTextStyle: { padding: [0, 0, 0, 0] }
            },
            {
              type: 'value',
              name: '湿度(%)',
              position: 'right',
              axisLabel: { formatter: '{value} %' },
              nameTextStyle: { padding: [0, 0, 0, 0] }
            }
          ],
          series: [
            {
              name: '温度',
              type: 'line',
              smooth: true,
              data: temps,
              itemStyle: { color: '#f56c6c' },
              areaStyle: { opacity: 0.2 },
              label: {
                show: false
              }
            },
            {
              name: '湿度',
              type: 'line',
              smooth: true,
              yAxisIndex: 1,
              data: humis,
              itemStyle: { color: '#409eff' },
              areaStyle: { opacity: 0.2 },
              label: {
                show: false
              }
            }
          ]
        }
        tempHumiChart.setOption(option)
      }
      return
    }
    
    const times = dataList.map((item: SensorData) => 
      dayjs(item.collectTime).format('HH:mm')
    )
    const temps = dataList.map((item: SensorData) => item.temperature)
    const humis = dataList.map((item: SensorData) => item.humidity)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        backgroundColor: 'rgba(50, 50, 50, 0.9)',
        borderColor: '#333',
        textStyle: { color: '#fff' }
      },
      legend: {
        data: ['温度', '湿度'],
        top: 10
      },
      grid: {
        left: '60px',
        right: '60px',
        top: '60px',
        bottom: '40px'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: times,
        axisLabel: {
          rotate: 0,
          interval: Math.floor(times.length / 5) // 自动间隔显示
        }
      },
      yAxis: [
        {
          type: 'value',
          name: '温度(℃)',
          position: 'left',
          axisLabel: { formatter: '{value} ℃' },
          nameTextStyle: { padding: [0, 0, 0, 0] }
        },
        {
          type: 'value',
          name: '湿度(%)',
          position: 'right',
          axisLabel: { formatter: '{value} %' },
          nameTextStyle: { padding: [0, 0, 0, 0] }
        }
      ],
      series: [
        {
          name: '温度',
          type: 'line',
          smooth: true,
          data: temps,
          itemStyle: { color: '#f56c6c' },
          areaStyle: { opacity: 0.2 },
          label: {
            show: false
          }
        },
        {
          name: '湿度',
          type: 'line',
          smooth: true,
          yAxisIndex: 1,
          data: humis,
          itemStyle: { color: '#409eff' },
          areaStyle: { opacity: 0.2 },
          label: {
            show: false
          }
        }
      ]
    }
    
    tempHumiChart.setOption(option)
  } catch (error) {
    console.error('初始化温湿度图表失败:', error)
  }
}

// 初始化气体浓度图表
const initGasChart = async () => {
  // 等待DOM渲染
  await nextTick()
  
  if (!gasChartRef.value) {
    console.warn('气体浓度图表容器未找到')
    return
  }
  
  // 检查DOM尺寸
  const checkDOM = () => {
    const width = gasChartRef.value?.clientWidth || 0
    const height = gasChartRef.value?.clientHeight || 0
    return width > 0 && height > 0
  }
  
  // 等待DOM尺寸就绪
  if (!checkDOM()) {
    await new Promise(resolve => setTimeout(resolve, 100))
    if (!checkDOM()) {
      console.warn('气体浓度图表容器尺寸为0')
      return
    }
  }
  
  // 销毁旧图表
  if (gasChart) {
    try {
      gasChart.dispose()
    } catch (e) {
      console.warn('销毁旧图表失败:', e)
    }
  }
  
  gasChart = echarts.init(gasChartRef.value)
  
  try {
    // 获取最近1小时的历史数据
    const endTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const startTime = dayjs().subtract(1, 'hour').format('YYYY-MM-DD HH:mm:ss')
    
    // 使用第一个节点的数据
    const historyData = await getHistorySensorData('NODE_001', startTime, endTime, 1, 20)
    const dataList = historyData.list || []
    
    if (dataList.length === 0) {
      console.warn('没有历史传感器数据')
      // 如果没有历史数据，使用最新数据
      const latestData = await getLatestSensorData()
      if (latestData && latestData.length > 0) {
        const times = latestData.map((item: SensorData) => 
          dayjs(item.collectTime).format('HH:mm')
        )
        const nh3 = latestData.map((item: SensorData) => item.nh3Concentration)
        const h2s = latestData.map((item: SensorData) => item.h2sConcentration)
        
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'cross' },
            backgroundColor: 'rgba(50, 50, 50, 0.9)',
            borderColor: '#333',
            textStyle: { color: '#fff' }
          },
          legend: {
            data: ['氨气(NH3)', '硫化氢(H2S)'],
            top: 10
          },
          grid: {
            left: '60px',
            right: '60px',
            top: '60px',
            bottom: '40px'
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: times,
            axisLabel: {
              rotate: 0,
              interval: 0
            }
          },
          yAxis: {
            type: 'value',
            name: '浓度(ppm)',
            axisLabel: { formatter: '{value} ppm' },
            nameTextStyle: { padding: [0, 0, 0, 0] }
          },
          series: [
            {
              name: '氨气(NH3)',
              type: 'line',
              smooth: true,
              data: nh3,
              itemStyle: { color: '#e6a23c' },
              areaStyle: { opacity: 0.2 },
              label: {
                show: false
              }
            },
            {
              name: '硫化氢(H2S)',
              type: 'line',
              smooth: true,
              data: h2s,
              itemStyle: { color: '#909399' },
              areaStyle: { opacity: 0.2 },
              label: {
                show: false
              }
            }
          ]
        }
        gasChart.setOption(option)
      }
      return
    }
    
    const times = dataList.map((item: SensorData) => 
      dayjs(item.collectTime).format('HH:mm')
    )
    const nh3 = dataList.map((item: SensorData) => item.nh3Concentration)
    const h2s = dataList.map((item: SensorData) => item.h2sConcentration)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        backgroundColor: 'rgba(50, 50, 50, 0.9)',
        borderColor: '#333',
        textStyle: { color: '#fff' }
      },
      legend: {
        data: ['氨气(NH3)', '硫化氢(H2S)'],
        top: 10
      },
      grid: {
        left: '60px',
        right: '60px',
        top: '60px',
        bottom: '40px'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: times,
        axisLabel: {
          rotate: 0,
          interval: Math.floor(times.length / 5) // 自动间隔显示
        }
      },
      yAxis: {
        type: 'value',
        name: '浓度(ppm)',
        axisLabel: { formatter: '{value} ppm' },
        nameTextStyle: { padding: [0, 0, 0, 0] }
      },
      series: [
        {
          name: '氨气(NH3)',
          type: 'line',
          smooth: true,
          data: nh3,
          itemStyle: { color: '#e6a23c' },
          areaStyle: { opacity: 0.2 },
          label: {
            show: false
          }
        },
        {
          name: '硫化氢(H2S)',
          type: 'line',
          smooth: true,
          data: h2s,
          itemStyle: { color: '#909399' },
          areaStyle: { opacity: 0.2 },
          label: {
            show: false
          }
        }
      ]
    }
    
    gasChart.setOption(option)
  } catch (error) {
    console.error('初始化气体图表失败:', error)
  }
}

// 报警类型名称
const getAlarmTypeName = (type: string) => {
  const map: Record<string, string> = {
    TEMP: '温度',
    HUMI: '湿度',
    NH3: '氨气',
    H2S: '硫化氢'
  }
  return map[type] || type
}

// 报警类型颜色
const getAlarmTypeColor = (type: string) => {
  const map: Record<string, any> = {
    TEMP: 'danger',
    HUMI: 'primary',
    NH3: 'warning',
    H2S: 'info'
  }
  return map[type] || ''
}

// 生成报警内容
const generateAlarmContent = (alarm: AlarmInfo) => {
  const typeName = getAlarmTypeName(alarm.alarmType)
  const unit = alarm.alarmType === 'TEMP' ? '℃' : 
               alarm.alarmType === 'HUMI' ? '%' : 'ppm'
  return `${typeName}超标: 当前值${alarm.currentValue}${unit}, 阈值${alarm.threshold}${unit}`
}

// 处理报警
const handleAlarmClick = async (id: number) => {
  try {
    await handleAlarm(id, '已确认')
    ElMessage.success('处理成功')
    await fetchAlarms()
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

// 刷新所有数据
const refreshData = async () => {
  await Promise.all([
    fetchStats(),
    fetchNodeStats(),
    fetchDeviceStats(),
    fetchAlarms(),
    initTempHumiChart(),
    initGasChart()
  ])
}

onMounted(async () => {
  await refreshData()
  
  // 每30秒刷新一次
  refreshTimer = window.setInterval(refreshData, 30000)
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    tempHumiChart?.resize()
    gasChart?.resize()
  })
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  tempHumiChart?.dispose()
  gasChart?.dispose()
})
</script>

<style scoped>
.dashboard-page {
  min-height: 100%;
}

.stats-row,
.status-row,
.chart-row,
.alarm-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.card-header .el-tag {
  margin-left: auto;
}

.status-content {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.status-item {
  text-align: center;
  flex: 1;
}

.status-label {
  display: block;
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.status-value {
  display: block;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}

.status-item.online .status-value {
  color: #67c23a;
}

.status-item.offline .status-value {
  color: #f56c6c;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.handled-text {
  color: #909399;
  font-size: 12px;
}
</style>
