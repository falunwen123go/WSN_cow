<template>
  <div class="history-container">
    <!-- 查询区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="节点">
          <el-select v-model="queryForm.nodeId" placeholder="全部节点" clearable style="width: 150px">
            <el-option label="NODE_001" value="NODE_001" />
            <el-option label="NODE_002" value="NODE_002" />
            <el-option label="NODE_003" value="NODE_003" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            :shortcuts="dateShortcuts"
            style="width: 380px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>历史数据</span>
        </div>
      </template>
      <el-table :data="historyData" stripe v-loading="loading">
        <el-table-column label="采集时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.collectTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="nodeId" label="节点ID" width="120" />
        <el-table-column label="温度(℃)" width="100">
          <template #default="{ row }">
            <span :style="{ color: getTempColor(row.temperature) }">
              {{ row.temperature.toFixed(1) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="湿度(%)" width="100">
          <template #default="{ row }">
            <span :style="{ color: getHumiColor(row.humidity) }">
              {{ row.humidity.toFixed(1) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="氨气(ppm)" width="120">
          <template #default="{ row }">
            <span :style="{ color: getNH3Color(row.nh3Concentration) }">
              {{ row.nh3Concentration.toFixed(1) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="硫化氢(ppm)" width="120">
          <template #default="{ row }">
            <span :style="{ color: getH2SColor(row.h2sConcentration) }">
              {{ row.h2sConcentration.toFixed(1) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="产奶量(kg)" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.milkYield" type="success" size="small">
              {{ row.milkYield.toFixed(2) }}
            </el-tag>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column label="THI指数" width="100">
          <template #default="{ row }">
            <el-tag :type="getTHIType(calculateTHI(row.temperature, row.humidity))" size="small">
              {{ calculateTHI(row.temperature, row.humidity).toFixed(1) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="AQI指数" width="100">
          <template #default="{ row }">
            <el-tag :type="getAQIType(calculateAQI(row.nh3Concentration, row.h2sConcentration))" size="small">
              {{ calculateAQI(row.nh3Concentration, row.h2sConcentration).toFixed(3) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="环境评分" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="getEnvironmentScore(row)"
              :color="getScoreColor(getEnvironmentScore(row))"
              :stroke-width="16"
            />
          </template>
        </el-table-column>
        <el-table-column label="数据状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.dataStatus === 0 ? 'success' : 'danger'" size="small">
              {{ row.dataStatus === 0 ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewTrend(row)">
              查看趋势
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchHistoryData"
          @current-change="fetchHistoryData"
        />
      </div>
    </el-card>

    <!-- 趋势图对话框 -->
    <el-dialog 
      v-model="trendDialogVisible" 
      title="数据趋势" 
      width="900px"
      @close="handleDialogClose"
    >
      <div ref="chartRef" style="width: 100%; height: 400px;"></div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getHistorySensorData } from '@/api/sensor'
import type { SensorData } from '@/types'
import dayjs from 'dayjs'
import * as echarts from 'echarts'

const loading = ref(false)
const historyData = ref<SensorData[]>([])
const trendDialogVisible = ref(false)
const chartRef = ref<HTMLDivElement>()
let trendChart: any = null

const queryForm = reactive({
  nodeId: '',
  dateRange: [] as string[]
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const dateShortcuts = [
  {
    text: '最近1小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000)
      return [start, end]
    }
  },
  {
    text: '最近6小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 6)
      return [start, end]
    }
  },
  {
    text: '最近24小时',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24)
      return [start, end]
    }
  },
  {
    text: '最近7天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  }
]

const fetchHistoryData = async () => {
  if (!queryForm.dateRange || queryForm.dateRange.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }
  
  loading.value = true
  try {
    const res = await getHistorySensorData({
      nodeId: queryForm.nodeId,
      startTime: queryForm.dateRange[0],
      endTime: queryForm.dateRange[1],
      pageNum: pagination.page,
      pageSize: pagination.size
    })
    historyData.value = res?.list || []
    pagination.total = res?.total || 0
  } catch (error) {
    ElMessage.error('获取历史数据失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pagination.page = 1
  fetchHistoryData()
}

const handleReset = () => {
  queryForm.nodeId = ''
  // 默认最近24小时
  const end = new Date()
  const start = new Date()
  start.setTime(start.getTime() - 3600 * 1000 * 24)
  queryForm.dateRange = [
    dayjs(start).format('YYYY-MM-DD HH:mm:ss'),
    dayjs(end).format('YYYY-MM-DD HH:mm:ss')
  ]
  pagination.page = 1
  fetchHistoryData()
}

const viewTrend = async (row: SensorData) => {
  console.log('🔍 查看趋势 - 节点:', row.nodeId)
  trendDialogVisible.value = true
  await nextTick()
  
  // 等待对话框完全打开
  await new Promise(resolve => setTimeout(resolve, 300))
  
  if (!chartRef.value) {
    console.warn('⚠️ 图表容器未找到')
    return
  }
  
  // 检查容器尺寸
  const width = chartRef.value.clientWidth || 0
  const height = chartRef.value.clientHeight || 0
  console.log('📏 图表容器尺寸:', { width, height })
  if (width === 0 || height === 0) {
    console.warn('⚠️ 图表容器尺寸为0')
    return
  }
  
  const chart = echarts.init(chartRef.value)
  trendChart = chart
  
  // 获取该节点最近24小时的数据用于趋势展示
  try {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24)
    
    console.log('🔍 趋势图查询参数:', {
      nodeId: row.nodeId,
      startTime: dayjs(start).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(end).format('YYYY-MM-DD HH:mm:ss')
    })
    
    let res = await getHistorySensorData({
      nodeId: row.nodeId,
      startTime: dayjs(start).format('YYYY-MM-DD HH:mm:ss'),
      endTime: dayjs(end).format('YYYY-MM-DD HH:mm:ss'),
      pageNum: 1,
      pageSize: 100
    })
    
    console.log('📄 趋势图API返回:', res)
    console.log('📄 返回数据类型:', typeof res)
    console.log('📄 list字段:', res?.list)
    console.log('📄 数据条数:', res?.list?.length || 0)
    
    let data = res?.list || []
    
    // 如果24小时内无数据,尝试查询所有历史数据
    if (data.length === 0) {
      console.warn('⚠️ 24小时内无数据,尝试查询所有历史数据')
      res = await getHistorySensorData({
        nodeId: row.nodeId,
        startTime: '2025-01-01 00:00:00',
        endTime: dayjs(end).format('YYYY-MM-DD HH:mm:ss'),
        pageNum: 1,
        pageSize: 100
      })
      console.log('📄 全量查询结果:', res)
      data = res?.list || []
    }
    
    if (data.length === 0) {
      console.error('❌ 该节点暂无任何历史数据')
      ElMessage.warning('该节点暂无历史数据')
      chart.dispose()
      return
    }
    
    console.log('✅ 获取到数据条数:', data.length)
    console.log('📄 第一条数据:', data[0])
    
    const times = data.map((d: SensorData) => dayjs(d.collectTime).format('HH:mm'))
    console.log('📊 时间轴数据:', times.slice(0, 5), '...')
    
    // 检查数据中是否有milkYield字段
    const hasMilkYield = data.some((d: SensorData) => d.milkYield !== null && d.milkYield !== undefined)
    console.log('🥛 是否有产奶量数据:', hasMilkYield)
    
    chart.setOption({
      title: {
        text: `${row.nodeId} 数据趋势 (共${data.length}条)`,
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['温度', '湿度', '氨气', '硫化氢', '产奶量'],
        bottom: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '10%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: times
      },
      yAxis: [
        {
          type: 'value',
          name: '温度/湿度',
          position: 'left'
        },
        {
          type: 'value',
          name: '气体浓度(ppm)',
          position: 'right'
        }
      ],
      series: [
        {
          name: '温度',
          type: 'line',
          data: data.map((d: SensorData) => d.temperature),
          smooth: true,
          yAxisIndex: 0
        },
        {
          name: '湿度',
          type: 'line',
          data: data.map((d: SensorData) => d.humidity),
          smooth: true,
          yAxisIndex: 0
        },
        {
          name: '氨气',
          type: 'line',
          data: data.map((d: SensorData) => d.nh3Concentration),
          smooth: true,
          yAxisIndex: 1
        },
        {
          name: '硫化氢',
          type: 'line',
          data: data.map((d: SensorData) => d.h2sConcentration),
          smooth: true,
          yAxisIndex: 1
        },
        {
          name: '产奶量',
          type: 'line',
          data: data.map((d: SensorData) => d.milkYield || 0),
          smooth: true,
          yAxisIndex: 0,
          itemStyle: { color: '#67c23a' },
          lineStyle: { width: 3 }
        }
      ]
    })
  } catch (error) {
    ElMessage.error('获取趋势数据失败')
  }
}

const getTempColor = (value: number) => {
  if (value < 5 || value > 35) return '#F56C6C'
  if (value < 10 || value > 30) return '#E6A23C'
  return '#67C23A'
}

const getHumiColor = (value: number) => {
  if (value < 30 || value > 85) return '#F56C6C'
  if (value < 40 || value > 75) return '#E6A23C'
  return '#67C23A'
}

const getNH3Color = (value: number) => {
  if (value > 40) return '#F56C6C'
  if (value > 25) return '#E6A23C'
  return '#67C23A'
}

const getH2SColor = (value: number) => {
  if (value > 20) return '#F56C6C'
  if (value > 10) return '#E6A23C'
  return '#67C23A'
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 🆕 计算THI
const calculateTHI = (temp: number, humi: number): number => {
  return (1.8 * temp + 32) - ((0.55 - 0.0055 * humi) * (1.8 * temp - 26))
}

// 🆕 计算AQI
const calculateAQI = (nh3: number, h2s: number): number => {
  return 0.6 * (nh3 / 50) + 0.4 * (h2s / 20)
}

// 🆕 获取THI类型
const getTHIType = (thi: number): string => {
  if (thi < 68) return 'success'
  if (thi < 72) return 'warning'
  if (thi < 79) return 'danger'
  return 'danger'
}

// 🆕 获取AQI类型
const getAQIType = (aqi: number): string => {
  if (aqi < 0.3) return 'success'
  if (aqi < 0.6) return 'warning'
  return 'danger'
}

// 🆕 计算环境评分
const getEnvironmentScore = (row: SensorData): number => {
  const thi = calculateTHI(row.temperature, row.humidity)
  const aqi = calculateAQI(row.nh3Concentration, row.h2sConcentration)
  const thiScore = thi < 68 ? 100 : thi < 72 ? 85 : thi < 79 ? 70 : 50
  const aqiScore = aqi < 0.3 ? 100 : aqi < 0.6 ? 80 : 60
  return Math.round(thiScore * 0.6 + aqiScore * 0.4)
}

// 🆕 获取评分颜色
const getScoreColor = (score: number): string => {
  if (score >= 85) return '#67c23a'
  if (score >= 70) return '#e6a23c'
  return '#f56c6c'
}

// 🆕 对话框关闭处理
const handleDialogClose = () => {
  if (trendChart) {
    try {
      trendChart.dispose()
      trendChart = null
    } catch (e) {
      console.warn('销毁图表失败:', e)
    }
  }
}

onMounted(() => {
  // 默认查询最近24小时
  handleReset()
})
</script>

<style scoped>
.history-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}
</style>
