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

    <!-- 🆕 产奶量、THI、AQI统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <DataCard
          title="平均产奶量"
          :value="stats.avgMilkYield?.toFixed(2) || '0.00'"
          unit="kg/天"
          :icon="Milk"
          icon-color="#67c23a"
          :trend="3.8"
        />
      </el-col>
      <el-col :span="6">
        <div @click="showFormulaDialog('THI')" style="cursor: pointer;">
          <DataCard
            title="平均THI指数"
            :value="stats.avgTHI?.toFixed(1) || '0.0'"
            :icon="TrendCharts"
            icon-color="#ff9800"
          />
        </div>
      </el-col>
      <el-col :span="6">
        <div @click="showFormulaDialog('AQI')" style="cursor: pointer;">
          <DataCard
            title="平均AQI指数"
            :value="stats.avgAQI?.toFixed(3) || '0.000'"
            :icon="Odometer"
            icon-color="#9c27b0"
          />
        </div>
      </el-col>
      <el-col :span="6">
        <div @click="showFormulaDialog('SCORE')" style="cursor: pointer;">
          <DataCard
            title="环境综合评分"
            :value="stats.avgScore?.toFixed(0) || '0'"
            unit="分"
            :icon="Medal"
            :icon-color="getScoreColor(stats.avgScore)"
          />
        </div>
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

    <!-- 🆕 产奶量趋势图和环境评分图 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span>产奶量趋势</span>
            </div>
          </template>
          <div ref="milkYieldChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Odometer /></el-icon>
              <span>环境评分仪表盘</span>
            </div>
          </template>
          <div ref="scoreGaugeRef" class="chart-container"></div>
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

    <!-- 🆕 指标计算公式对话框 -->
    <el-dialog
      v-model="formulaDialogVisible"
      :title="formulaInfo.title"
      width="600px"
      :close-on-click-modal="true"
    >
      <div class="formula-content">
        <el-divider content-position="left">
          <el-icon><TrendCharts /></el-icon>
          <span style="margin-left: 8px;">计算公式</span>
        </el-divider>
        <div class="formula-box">
          <code>{{ formulaInfo.formula }}</code>
        </div>

        <el-divider content-position="left">
          <el-icon><Document /></el-icon>
          <span style="margin-left: 8px;">说明</span>
        </el-divider>
        <div class="formula-description">
          <p v-html="formulaInfo.description"></p>
        </div>

        <el-divider content-position="left">
          <el-icon><InfoFilled /></el-icon>
          <span style="margin-left: 8px;">评估标准</span>
        </el-divider>
        <div class="formula-levels">
          <el-tag
            v-for="level in formulaInfo.levels"
            :key="level.label"
            :type="level.type"
            size="large"
            style="margin: 4px;"
          >
            {{ level.label }}: {{ level.range }}
          </el-tag>
        </div>

        <el-divider content-position="left">
          <el-icon><Promotion /></el-icon>
          <span style="margin-left: 8px;">应用理由</span>
        </el-divider>
        <div class="formula-reason">
          <el-alert
            :title="formulaInfo.reason"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="formulaDialogVisible = false">
          我知道了
        </el-button>
      </template>
    </el-dialog>
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
  Bell,
  TrendCharts,
  Odometer,
  Medal,
  Document,
  InfoFilled,
  Promotion
} from '@element-plus/icons-vue'
import DataCard from '@/components/DataCard.vue'
import AlarmBadge from '@/components/AlarmBadge.vue'
import { getLatestSensorData, getSensorStatistics, getHistorySensorData } from '@/api/sensor'
import { getNodeList, getOnlineNodeCount } from '@/api/node'
import { getDeviceList, getRunningDeviceCount } from '@/api/device'
import { getAlarmList, handleAlarm, getUnhandledAlarmCount } from '@/api/alarm'
import type { SensorData, AlarmInfo, NodeInfo, DeviceInfo } from '@/types'

// 计算THI函数
const calculateTHI = (temp: number, humi: number): number => {
  return (1.8 * temp + 32) - ((0.55 - 0.0055 * humi) * (1.8 * temp - 26))
}

// 计算AQI函数
const calculateAQI = (nh3: number, h2s: number): number => {
  return 0.6 * (nh3 / 50) + 0.4 * (h2s / 20)
}

// 计算环境评分
const calculateScore = (thi: number, aqi: number): number => {
  const thiScore = thi < 68 ? 100 : thi < 72 ? 85 : thi < 79 ? 70 : 50
  const aqiScore = aqi < 0.3 ? 100 : aqi < 0.6 ? 80 : 60
  return thiScore * 0.6 + aqiScore * 0.4
}

// 获取评分颜色
const getScoreColor = (score: number): string => {
  if (score >= 85) return '#67c23a'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

// 假设后端添加了一个Milk icon 的符号, 如果没有就用TrendCharts代替
const Milk = TrendCharts

// 统计数据
const stats = ref({
  avgTemperature: 0,
  avgHumidity: 0,
  avgNh3: 0,
  avgH2s: 0,
  avgMilkYield: 0,
  avgTHI: 0,
  avgAQI: 0,
  avgScore: 0
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

// 🆕 指标公式对话框
const formulaDialogVisible = ref(false)
const formulaInfo = ref({
  title: '',
  formula: '',
  description: '',
  levels: [] as Array<{ label: string; range: string; type: string }>,
  reason: ''
})

// 🆕 显示公式说明对话框
const showFormulaDialog = (type: 'THI' | 'AQI' | 'SCORE') => {
  if (type === 'THI') {
    formulaInfo.value = {
      title: 'THI指数 (Temperature-Humidity Index)',
      formula: 'THI = (1.8 × T + 32) - [(0.55 - 0.0055 × RH) × (1.8 × T - 26)]',
      description: `
        <p><strong>温湿度指数(THI)</strong>是评估奶牛热应激程度的关键指标。</p>
        <p>其中:</p>
        <ul>
          <li><strong>T</strong> = 环境温度(℃)</li>
          <li><strong>RH</strong> = 相对湿度(%)</li>
        </ul>
        <p>该指数综合考虑了温度和湿度对奶牛舒适度的影响,湿度越高,奶牛对高温的耐受性越差。</p>
      `,
      levels: [
        { label: '无应激', range: 'THI < 68', type: 'success' },
        { label: '轻度应激', range: '68 ≤ THI < 72', type: 'info' },
        { label: '中度应激', range: '72 ≤ THI < 79', type: 'warning' },
        { label: '重度应激', range: 'THI ≥ 79', type: 'danger' }
      ],
      reason: '热应激会导致奶牛采食量下降、产奶量减少、繁殖性能降低。通过监测THI指数,可以及时采取降温措施(如开启风扇、喷淋),减少经济损失。当THI超过72时,产奶量可下降10-20%。'
    }
  } else if (type === 'AQI') {
    formulaInfo.value = {
      title: 'AQI指数 (Air Quality Index)',
      formula: 'AQI = 0.6 × (NH₃/50) + 0.4 × (H₂S/20)',
      description: `
        <p><strong>空气质量指数(AQI)</strong>用于评估牛舍内有害气体浓度水平。</p>
        <p>其中:</p>
        <ul>
          <li><strong>NH₃</strong> = 氨气浓度(ppm),权重60%</li>
          <li><strong>H₂S</strong> = 硫化氢浓度(ppm),权重40%</li>
        </ul>
        <p>氨气和硫化氢是牛舍主要有害气体,会刺激呼吸道、降低免疫力。</p>
      `,
      levels: [
        { label: '优秀', range: 'AQI < 0.3', type: 'success' },
        { label: '良好', range: '0.3 ≤ AQI < 0.6', type: 'info' },
        { label: '中等', range: '0.6 ≤ AQI < 1.0', type: 'warning' },
        { label: '较差', range: 'AQI ≥ 1.0', type: 'danger' }
      ],
      reason: '高浓度氨气(>25ppm)会导致奶牛呼吸道疾病、眼部炎症、产奶量下降5-15%。硫化氢(>10ppm)具有强烈毒性,可引起急性中毒。通过监测AQI可优化通风系统、及时清理粪污,改善牛舍环境。'
    }
  } else if (type === 'SCORE') {
    formulaInfo.value = {
      title: '环境综合评分',
      formula: '评分 = THI评分 × 0.6 + AQI评分 × 0.4',
      description: `
        <p><strong>环境综合评分</strong>整合了热舒适度和空气质量两大维度,给出0-100分的直观评价。</p>
        <p><strong>THI评分规则:</strong></p>
        <ul>
          <li>THI < 68: 100分</li>
          <li>68 ≤ THI < 72: 85分</li>
          <li>72 ≤ THI < 79: 70分</li>
          <li>THI ≥ 79: 50分</li>
        </ul>
        <p><strong>AQI评分规则:</strong></p>
        <ul>
          <li>AQI < 0.3: 100分</li>
          <li>0.3 ≤ AQI < 0.6: 80分</li>
          <li>AQI ≥ 0.6: 60分</li>
        </ul>
      `,
      levels: [
        { label: '优秀', range: '评分 ≥ 85', type: 'success' },
        { label: '良好', range: '70 ≤ 评分 < 85', type: 'info' },
        { label: '一般', range: '60 ≤ 评分 < 70', type: 'warning' },
        { label: '较差', range: '评分 < 60', type: 'danger' }
      ],
      reason: '综合评分让管理者一目了然地掌握牛舍环境状态。评分低于70时应采取干预措施(降温、通风、清洁),评分高于85表示环境适宜,奶牛处于最佳生产状态。该评分与产奶量呈正相关,可作为精细化管理的重要依据。'
    }
  }
  
  formulaDialogVisible.value = true
}

// 图表实例
const tempHumiChartRef = ref<HTMLDivElement>()
const gasChartRef = ref<HTMLDivElement>()
const milkYieldChartRef = ref<HTMLDivElement>()
const scoreGaugeRef = ref<HTMLDivElement>()
let tempHumiChart: ECharts | null = null
let gasChart: ECharts | null = null
let milkYieldChart: ECharts | null = null
let scoreGauge: ECharts | null = null

// 定时器
let refreshTimer: number | null = null

// 获取统计数据
const fetchStats = async () => {
  try {
    const latestData = await getLatestSensorData()
    
    if (latestData && latestData.length > 0) {
      // 计算平均值
      const count = latestData.length
      const sum = latestData.reduce((acc, item) => {
        const thi = calculateTHI(item.temperature, item.humidity)
        const aqi = calculateAQI(item.nh3Concentration, item.h2sConcentration)
        const score = calculateScore(thi, aqi)
        
        return {
          temperature: acc.temperature + item.temperature,
          humidity: acc.humidity + item.humidity,
          nh3: acc.nh3 + item.nh3Concentration,
          h2s: acc.h2s + item.h2sConcentration,
          milkYield: acc.milkYield + (item.milkYield || 0),
          thi: acc.thi + thi,
          aqi: acc.aqi + aqi,
          score: acc.score + score
        }
      }, { temperature: 0, humidity: 0, nh3: 0, h2s: 0, milkYield: 0, thi: 0, aqi: 0, score: 0 })
      
      stats.value.avgTemperature = Math.round(sum.temperature / count * 10) / 10
      stats.value.avgHumidity = Math.round(sum.humidity / count * 10) / 10
      stats.value.avgNh3 = Math.round(sum.nh3 / count * 10) / 10
      stats.value.avgH2s = Math.round(sum.h2s / count * 10) / 10
      stats.value.avgMilkYield = Math.round(sum.milkYield / count * 100) / 100
      stats.value.avgTHI = Math.round(sum.thi / count * 10) / 10
      stats.value.avgAQI = Math.round(sum.aqi / count * 1000) / 1000
      stats.value.avgScore = Math.round(sum.score / count)
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
    // 获取最近24小时的历史数据(扩大时间范围以确保有数据)
    const endTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const startTime = dayjs().subtract(24, 'hour').format('YYYY-MM-DD HH:mm:ss')
    
    // 尝试多个节点查询,直到找到有数据的节点
    const nodeIds = ['NODE_001', 'NODE_002', 'NODE_003']
    let dataList = []
    let foundNodeId = ''
    
    for (const nodeId of nodeIds) {
      console.log(`🔍 尝试查询 ${nodeId} 的温湿度数据`)
      
      let historyData = await getHistorySensorData({
        nodeId: nodeId,
        startTime: '2025-01-01 00:00:00',  // 直接查询所有历史数据
        endTime: endTime,
        pageNum: 1,
        pageSize: 100
      })
      
      console.log(`📄 ${nodeId} 数据返回:`, historyData?.list?.length || 0, '条')
      
      if (historyData?.list && historyData.list.length > 0) {
        dataList = historyData.list
        foundNodeId = nodeId
        console.log(`✅ 使用 ${nodeId} 的数据 (${dataList.length}条)`)
        break
      }
    }
    
    if (dataList.length === 0) {
      console.warn('没有历史传感器数据，请等待系统采集数据')
      // 如果没有历史数据，使用最新数据
      const latestData = await getLatestSensorData()
      if (latestData && latestData.length > 0) {
        const times = latestData.map((item: SensorData) => 
          dayjs(item.collectTime).format('HH:mm')
        )
        const temps = latestData.map((item: SensorData) => item.temperature || 0)
        const humis = latestData.map((item: SensorData) => item.humidity || 0)
        
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
    // 获取最近24小时的历史数据(扩大时间范围)
    const endTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const startTime = dayjs().subtract(24, 'hour').format('YYYY-MM-DD HH:mm:ss')
    
    // 尝试多个节点查询,直到找到有数据的节点
    const nodeIds = ['NODE_001', 'NODE_002', 'NODE_003']
    let dataList = []
    let foundNodeId = ''
    
    for (const nodeId of nodeIds) {
      console.log(`🔍 尝试查询 ${nodeId} 的气体数据`)
      
      let historyData = await getHistorySensorData({
        nodeId: nodeId,
        startTime: '2025-01-01 00:00:00',  // 直接查询所有历史数据
        endTime: endTime,
        pageNum: 1,
        pageSize: 100
      })
      
      console.log(`📄 ${nodeId} 数据返回:`, historyData?.list?.length || 0, '条')
      
      if (historyData?.list && historyData.list.length > 0) {
        dataList = historyData.list
        foundNodeId = nodeId
        console.log(`✅ 使用 ${nodeId} 的数据 (${dataList.length}条)`)
        break
      }
    }
    
    if (dataList.length === 0) {
      console.warn('没有历史传感器数据，请等待系统采集数据')
      // 如果没有历史数据，使用最新数据
      const latestData = await getLatestSensorData()
      if (latestData && latestData.length > 0) {
        const times = latestData.map((item: SensorData) => 
          dayjs(item.collectTime).format('HH:mm')
        )
        const nh3 = latestData.map((item: SensorData) => item.nh3Concentration || 0)
        const h2s = latestData.map((item: SensorData) => item.h2sConcentration || 0)
        
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

// 🆕 初始化产奶量趋势图
const initMilkYieldChart = async () => {
  await nextTick()
  
  // 等待DOM完全渲染
  await new Promise(resolve => setTimeout(resolve, 200))
  
  if (!milkYieldChartRef.value) {
    console.warn('产奶量图表容器未找到')
    return
  }
  
  // 检查DOM尺寸
  const width = milkYieldChartRef.value.clientWidth || 0
  const height = milkYieldChartRef.value.clientHeight || 0
  if (width === 0 || height === 0) {
    console.warn('产奶量图表容器尺寸为0')
    return
  }
  
  if (milkYieldChart) {
    try {
      milkYieldChart.dispose()
    } catch (e) {
      console.warn('销毁旧图表失败:', e)
    }
  }
  
  milkYieldChart = echarts.init(milkYieldChartRef.value)
  
  try {
    // 获取最近24小时的历史数据(扩大时间范围)
    const endTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    const startTime = dayjs().subtract(24, 'hour').format('YYYY-MM-DD HH:mm:ss')
    
    // 尝试多个节点查询,直到找到有数据的节点
    const nodeIds = ['NODE_001', 'NODE_002', 'NODE_003']
    let dataList = []
    let foundNodeId = ''
    
    for (const nodeId of nodeIds) {
      console.log(`🔍 尝试查询 ${nodeId} 的产奶量数据`)
      
      let historyData = await getHistorySensorData({
        nodeId: nodeId,
        startTime: '2025-01-01 00:00:00',  // 直接查询所有历史数据
        endTime: endTime,
        pageNum: 1,
        pageSize: 100
      })
      
      console.log(`📄 ${nodeId} 数据返回:`, historyData?.list?.length || 0, '条')
      
      if (historyData?.list && historyData.list.length > 0) {
        dataList = historyData.list
        foundNodeId = nodeId
        console.log(`✅ 使用 ${nodeId} 的数据 (${dataList.length}条)`)
        break
      }
    }
    
    if (dataList.length === 0) {
      const latestData = await getLatestSensorData()
      if (latestData && latestData.length > 0) {
        const times = latestData.map((item: SensorData) => 
          dayjs(item.collectTime).format('HH:mm')
        )
        const milkYields = latestData.map((item: SensorData) => item.milkYield || 0)
        
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: { type: 'cross' },
            backgroundColor: 'rgba(50, 50, 50, 0.9)',
            borderColor: '#333',
            textStyle: { color: '#fff' },
            formatter: (params: any) => {
              const data = params[0]
              return `<div style="padding:8px">
                <div style="font-weight:bold;margin-bottom:4px">${data.name}</div>
                <div>产奶量: ${data.value} kg/天</div>
              </div>`
            }
          },
          legend: {
            data: ['产奶量'],
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
            data: times
          },
          yAxis: {
            type: 'value',
            name: '产奶量(kg/天)',
            axisLabel: { formatter: '{value} kg' }
          },
          series: [
            {
              name: '产奶量',
              type: 'line',
              smooth: true,
              data: milkYields,
              itemStyle: { color: '#67c23a' },
              areaStyle: { 
                color: new (echarts as any).graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
                  { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
                ])
              },
              markLine: {
                data: [{ type: 'average', name: '平均值' }],
                lineStyle: { color: '#e6a23c', type: 'dashed' }
              }
            }
          ]
        }
        milkYieldChart.setOption(option)
      }
      return
    }
    
    const times = dataList.map((item: SensorData) => 
      dayjs(item.collectTime).format('HH:mm')
    )
    const milkYields = dataList.map((item: SensorData) => item.milkYield || 0)
    
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        backgroundColor: 'rgba(50, 50, 50, 0.9)',
        borderColor: '#333',
        textStyle: { color: '#fff' }
      },
      legend: {
        data: ['产奶量'],
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
          interval: Math.floor(times.length / 5)
        }
      },
      yAxis: {
        type: 'value',
        name: '产奶量(kg/天)',
        axisLabel: { formatter: '{value} kg' }
      },
      series: [
        {
          name: '产奶量',
          type: 'line',
          smooth: true,
          data: milkYields,
          itemStyle: { color: '#67c23a' },
          areaStyle: { 
            color: new (echarts as any).graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
            ])
          },
          markLine: {
            data: [{ type: 'average', name: '平均值' }],
            lineStyle: { color: '#e6a23c', type: 'dashed' }
          }
        }
      ]
    }
    
    milkYieldChart.setOption(option)
  } catch (error) {
    console.error('初始化产奶量图表失败:', error)
  }
}

// 🆕 初始化环境评分仪表盘
const initScoreGauge = async () => {
  await nextTick()
  
  // 等待DOM完全渲染
  await new Promise(resolve => setTimeout(resolve, 200))
  
  if (!scoreGaugeRef.value) {
    console.warn('评分仪表盘容器未找到')
    return
  }
  
  // 检查DOM尺寸
  const width = scoreGaugeRef.value.clientWidth || 0
  const height = scoreGaugeRef.value.clientHeight || 0
  if (width === 0 || height === 0) {
    console.warn('评分仪表盘容器尺寸为0')
    return
  }
  
  if (scoreGauge) {
    try {
      scoreGauge.dispose()
    } catch (e) {
      console.warn('销毁旧图表失败:', e)
    }
  }
  
  scoreGauge = echarts.init(scoreGaugeRef.value)
  
  const option = {
    tooltip: {
      formatter: '{a} <br/>{b} : {c}分'
    },
    series: [
      {
        name: '环境评分',
        type: 'gauge',
        min: 0,
        max: 100,
        splitNumber: 10,
        radius: '80%',
        axisLine: {
          lineStyle: {
            width: 30,
            color: [
              [0.5, '#f56c6c'],
              [0.7, '#e6a23c'],
              [0.85, '#409eff'],
              [1, '#67c23a']
            ]
          }
        },
        pointer: {
          itemStyle: {
            color: 'inherit'
          },
          width: 6,
          length: '70%'
        },
        axisTick: {
          distance: -30,
          length: 8,
          lineStyle: {
            color: '#fff',
            width: 2
          }
        },
        splitLine: {
          distance: -30,
          length: 15,
          lineStyle: {
            color: '#fff',
            width: 3
          }
        },
        axisLabel: {
          color: 'inherit',
          distance: 35,
          fontSize: 14,
          fontWeight: 'bold'
        },
        detail: {
          valueAnimation: true,
          formatter: '{value}分',
          fontSize: 28,
          fontWeight: 'bold',
          offsetCenter: [0, '70%'],
          color: 'inherit'
        },
        title: {
          offsetCenter: [0, '-20%'],
          fontSize: 16,
          fontWeight: 'bold',
          color: '#666'
        },
        data: [
          {
            value: stats.value.avgScore,
            name: '综合评分'
          }
        ]
      }
    ]
  }
  
  scoreGauge.setOption(option)
}

// 刷新所有数据
const refreshData = async () => {
  await Promise.all([
    fetchStats(),
    fetchNodeStats(),
    fetchDeviceStats(),
    fetchAlarms(),
    initTempHumiChart(),
    initGasChart(),
    initMilkYieldChart(),
    initScoreGauge()
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
    milkYieldChart?.resize()
    scoreGauge?.resize()
  })
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  tempHumiChart?.dispose()
  gasChart?.dispose()
  milkYieldChart?.dispose()
  scoreGauge?.dispose()
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

/* 🆕 公式对话框样式 */
.formula-content {
  padding: 10px;
}

.formula-box {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin: 15px 0;
  border-left: 4px solid #409eff;
}

.formula-box code {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  font-family: 'Monaco', 'Menlo', 'Courier New', monospace;
  line-height: 1.8;
}

.formula-description {
  color: #606266;
  line-height: 1.8;
  margin: 15px 0;
}

.formula-description p {
  margin: 10px 0;
}

.formula-description ul {
  margin: 10px 0;
  padding-left: 25px;
}

.formula-description li {
  margin: 8px 0;
}

.formula-description strong {
  color: #303133;
  font-weight: 600;
}

.formula-levels {
  margin: 15px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.formula-reason {
  margin: 15px 0;
}

:deep(.el-divider__text) {
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

:deep(.el-alert__title) {
  line-height: 1.8;
}
</style>
