<template>
  <div class="nodes-page">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态筛选">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="在线" :value="1" />
            <el-option label="离线" :value="0" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点名称">
          <el-input v-model="searchForm.keyword" placeholder="输入节点名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="handleAdd">添加节点</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 网络拓扑图 -->
    <el-card shadow="hover" class="topology-card">
      <template #header>
        <div class="card-header">
          <span>网络拓扑图</span>
          <div>
            <el-tag type="success" style="margin-right: 10px">
              <el-icon><Connection /></el-icon>
              星型拓扑
            </el-tag>
            <el-tag type="info">{{ tableData.length }} 个节点</el-tag>
          </div>
        </div>
      </template>
      <div ref="topologyChart" class="topology-chart"></div>
    </el-card>

    <!-- 节点列表 -->
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span>节点列表</span>
          <el-tag type="info">总计: {{ tableData.length }} 个节点</el-tag>
        </div>
      </template>

      <el-table :data="filteredData" stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="nodeId" label="节点ID" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.nodeId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nodeName" label="节点名称" width="150" />
        <el-table-column prop="location" label="安装位置" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="电池电量" width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="row.batteryLevel"
              :color="getBatteryColor(row.batteryLevel)"
              :stroke-width="12"
            />
          </template>
        </el-table-column>
        <el-table-column label="信号强度" width="120">
          <template #default="{ row }">
            <el-tag :type="getSignalType(row.signalStrength)" size="small">
              {{ row.signalStrength }} dBm
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastCommTime" label="最后通信时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastCommTime) }}
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" :icon="View" @click="handleView(row)">
                详情
              </el-button>
              <el-button type="warning" size="small" :icon="Edit" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(row)">
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 节点详情对话框 -->
    <el-dialog v-model="detailVisible" title="节点详情" width="600px">
      <el-descriptions :column="2" border v-if="currentNode">
        <el-descriptions-item label="节点ID">
          <el-tag>{{ currentNode.nodeId }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="节点名称">
          {{ currentNode.nodeName }}
        </el-descriptions-item>
        <el-descriptions-item label="安装位置">
          {{ currentNode.location }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <NodeStatus :status="currentNode.status" />
        </el-descriptions-item>
        <el-descriptions-item label="电池电量">
          <el-progress
            :percentage="currentNode.batteryLevel"
            :color="getBatteryColor(currentNode.batteryLevel)"
          />
        </el-descriptions-item>
        <el-descriptions-item label="信号强度">
          <el-tag :type="getSignalType(currentNode.signalStrength)">
            {{ currentNode.signalStrength }} dBm
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="最后通信时间" :span="2">
          {{ formatDateTime(currentNode.lastCommTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="最后在线时间" :span="2">
          {{ formatDateTime(currentNode.lastOnlineTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="安装日期">
          {{ formatDate(currentNode.installDate) }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(currentNode.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentNode.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="节点ID" prop="nodeId">
          <el-input
            v-model="formData.nodeId"
            :disabled="isEdit"
            placeholder="请输入节点ID"
          />
        </el-form-item>
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="formData.nodeName" placeholder="请输入节点名称" />
        </el-form-item>
        <el-form-item label="安装位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="formData.status" style="width: 100%">
            <el-option label="离线" :value="0" />
            <el-option label="在线" :value="1" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="电池电量" prop="batteryLevel">
          <el-slider v-model="formData.batteryLevel" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="信号强度" prop="signalStrength">
          <el-input-number
            v-model="formData.signalStrength"
            :min="-100"
            :max="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Edit, Delete, Connection } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import NodeStatus from '@/components/NodeStatus.vue'
import { getAllNodes, addNode, updateNode, deleteNode } from '@/api/node'
import { getLatestSensorData } from '@/api/sensor'
import type { NodeInfo, SensorData } from '@/types'

// 拓扑图DOM引用
const topologyChart = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 节点最新传感器数据映射
const nodeLatestDataMap = new Map<string, SensorData>()

// 计算THI (温湿度指数)
const calculateTHI = (temp: number, humi: number): number => {
  return (1.8 * temp + 32) - ((0.55 - 0.0055 * humi) * (1.8 * temp - 26))
}

// 计算AQI (空气质量指数)
const calculateAQI = (nh3: number, h2s: number): number => {
  return 0.6 * (nh3 / 50) + 0.4 * (h2s / 20)
}

// 获取THI级别
const getTHILevel = (thi: number): string => {
  if (thi < 68) return '(舒适)'
  if (thi < 72) return '(轻度应激)'
  if (thi < 79) return '(中度应激)'
  return '(严重应激)'
}

// 获取AQI级别
const getAQILevel = (aqi: number): string => {
  if (aqi < 0.3) return '(优)'
  if (aqi < 0.6) return '(良)'
  return '(差)'
}

// 搜索表单
const searchForm = ref({
  status: undefined as number | undefined,
  keyword: ''
})

// 表格数据
const tableData = ref<NodeInfo[]>([])
const loading = ref(false)

// 过滤后的数据
const filteredData = computed(() => {
  let data = tableData.value
  
  // 状态筛选
  if (searchForm.value.status !== undefined) {
    data = data.filter(item => item.status === searchForm.value.status)
  }
  
  // 关键字搜索
  if (searchForm.value.keyword) {
    const keyword = searchForm.value.keyword.toLowerCase()
    data = data.filter(item =>
      item.nodeName.toLowerCase().includes(keyword) ||
      item.nodeId.toLowerCase().includes(keyword)
    )
  }
  
  return data
})

// 详情对话框
const detailVisible = ref(false)
const currentNode = ref<NodeInfo | null>(null)

// 添加/编辑对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitting = ref(false)

const formData = ref({
  nodeId: '',
  nodeName: '',
  location: '',
  status: 1,
  batteryLevel: 100,
  signalStrength: -50,
  remark: ''
})

const formRules: FormRules = {
  nodeId: [{ required: true, message: '请输入节点ID', trigger: 'blur' }],
  nodeName: [{ required: true, message: '请输入节点名称', trigger: 'blur' }],
  location: [{ required: true, message: '请输入安装位置', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 获取节点列表
const fetchNodes = async () => {
  loading.value = true
  try {
    const data = await getAllNodes()
    tableData.value = data
    
    // 获取最新传感器数据
    await fetchLatestSensorData()
    
    // 更新拓扑图
    await nextTick()
    initTopologyChart()
  } catch (error) {
    ElMessage.error('获取节点列表失败')
  } finally {
    loading.value = false
  }
}

// 获取最新传感器数据
const fetchLatestSensorData = async () => {
  try {
    const data = await getLatestSensorData()
    nodeLatestDataMap.clear()
    data.forEach((item: SensorData) => {
      nodeLatestDataMap.set(item.nodeId, item)
    })
  } catch (error) {
    console.error('获取传感器数据失败:', error)
  }
}

// 初始化拓扑图
const initTopologyChart = () => {
  if (!topologyChart.value) return
  
  // 如果已存在图表实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  // 创建新图表实例
  chartInstance = echarts.init(topologyChart.value)
  
  // 构建图数据
  const nodes: any[] = []
  const links: any[] = []
  
  // 添加协调器节点（中心节点）- 使用渐变色
  nodes.push({
    id: 'coordinator',
    name: 'ZigBee协调器',
    symbol: 'diamond',
    symbolSize: 100,
    x: 400,
    y: 250,
    itemStyle: {
      color: new (echarts as any).graphic.LinearGradient(0, 0, 1, 1, [
        { offset: 0, color: '#4facfe' },
        { offset: 1, color: '#00f2fe' }
      ]),
      borderColor: '#fff',
      borderWidth: 4,
      shadowBlur: 25,
      shadowColor: 'rgba(79, 172, 254, 0.6)'
    },
    label: {
      show: true,
      fontSize: 16,
      fontWeight: 'bold',
      color: '#fff',
      backgroundColor: 'rgba(0, 0, 0, 0.7)',
      padding: [6, 12],
      borderRadius: 4,
      textShadowColor: '#000',
      textShadowBlur: 4,
      textShadowOffsetX: 1,
      textShadowOffsetY: 1
    }
  })
  
  // 添加传感器节点（以圆形排列）
  const nodeCount = tableData.value.length
  const radius = 200
  const centerX = 400
  const centerY = 250
  
  tableData.value.forEach((node, index) => {
    const angle = (2 * Math.PI * index) / nodeCount - Math.PI / 2
    const x = centerX + radius * Math.cos(angle)
    const y = centerY + radius * Math.sin(angle)
    
    // 根据状态设置颜色渐变
    let gradientColors = { start: '#909399', end: '#606266' }
    if (node.status === 1) {
      gradientColors = { start: '#85ce61', end: '#5cb85c' }
    } else if (node.status === 2) {
      gradientColors = { start: '#f78989', end: '#f56c6c' }
    }
    
    // 根据电池电量调整大小
    const sizeMultiplier = 0.6 + (node.batteryLevel / 200)
    const symbolSize = 80 * sizeMultiplier
    
    nodes.push({
      id: node.nodeId,
      name: `${node.nodeName}\n${node.location || ''}`,
      symbol: 'circle',
      symbolSize: symbolSize,
      x: x,
      y: y,
      itemStyle: {
        color: new (echarts as any).graphic.RadialGradient(0.5, 0.5, 1, [
          { offset: 0, color: gradientColors.start },
          { offset: 0.7, color: gradientColors.start },
          { offset: 1, color: gradientColors.end }
        ]),
        borderColor: '#fff',
        borderWidth: 3,
        shadowBlur: 18,
        shadowColor: node.status === 1 ? 'rgba(103, 194, 58, 0.5)' : 'rgba(144, 147, 153, 0.4)'
      },
      label: {
        show: true,
        fontSize: 13,
        fontWeight: 'bold',
        color: '#fff',
        backgroundColor: 'rgba(0, 0, 0, 0.65)',
        padding: [5, 10],
        borderRadius: 3,
        textShadowColor: '#000',
        textShadowBlur: 3,
        textShadowOffsetX: 1,
        textShadowOffsetY: 1,
        lineHeight: 18
      },
      tooltip: {
        formatter: () => {
          // 计算THI和AQI (使用最新传感器数据)
          const latestData = nodeLatestDataMap.get(node.nodeId)
          const thi = latestData ? calculateTHI(latestData.temperature, latestData.humidity) : null
          const aqi = latestData ? calculateAQI(latestData.nh3Concentration, latestData.h2sConcentration) : null
          const milkYield = latestData?.milkYield
          
          return `
            <div style="padding: 10px 12px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.3);">
              <div style="color: #fff; font-weight: bold; font-size: 14px; margin-bottom: 8px; border-bottom: 1px solid rgba(255,255,255,0.3); padding-bottom: 6px;">
                ${node.nodeName}
              </div>
              <div style="color: #fff; font-size: 12px; line-height: 22px;">
                <div>📍 位置: ${node.location || '-'}</div>
                <div>${node.status === 1 ? '🟢' : node.status === 2 ? '🔴' : '⚪'} 状态: ${node.status === 1 ? '在线' : node.status === 2 ? '故障' : '离线'}</div>
                <div>🔋 电池: ${node.batteryLevel}%</div>
                <div>📶 信号: ${node.signalStrength} dBm</div>
                ${milkYield ? `<div style="border-top: 1px solid rgba(255,255,255,0.2); margin-top: 6px; padding-top: 6px;">🥛 产奶量: ${milkYield.toFixed(2)} kg/天</div>` : ''}
                ${thi ? `<div>🌡️ THI指数: ${thi.toFixed(1)} ${getTHILevel(thi)}</div>` : ''}
                ${aqi ? `<div>💨 AQI指数: ${aqi.toFixed(3)} ${getAQILevel(aqi)}</div>` : ''}
              </div>
            </div>
          `
        }
      },
      nodeData: node
    })
    
    // 添加连接线
    links.push({
      source: 'coordinator',
      target: node.nodeId,
      lineStyle: {
        color: node.status === 1 ? 
          new (echarts as any).graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: 'rgba(79, 172, 254, 0.8)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.8)' }
          ]) : 'rgba(220, 223, 230, 0.6)',
        width: node.status === 1 ? 3 : 2,
        type: node.status === 1 ? 'solid' : 'dashed',
        shadowBlur: node.status === 1 ? 10 : 0,
        shadowColor: node.status === 1 ? 'rgba(103, 194, 58, 0.3)' : 'transparent'
      },
      // 脉冲动画效果
      effect: node.status === 1 ? {
        show: true,
        period: 3,
        trailLength: 0.2,
        symbol: 'circle',
        symbolSize: 4,
        color: '#67C23A'
      } : undefined
    })
  })
  
  // 配置图表选项
  const option = {
    title: {
      text: '星型ZigBee网络拓扑',
      left: 20,
      top: 15,
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold',
        color: '#303133',
        backgroundColor: 'rgba(255, 255, 255, 0.9)',
        padding: [8, 12],
        borderRadius: 4
      }
    },
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(50, 50, 50, 0.9)',
      borderColor: '#333',
      borderWidth: 1
    },
    legend: {
      show: false
    },
    animation: true,
    animationDuration: 1500,
    animationEasing: 'elasticOut',
    series: [{
      type: 'graph',
      layout: 'none',
      roam: true,
      scaleLimit: { min: 0.5, max: 3 },
      label: { show: true },
      edgeSymbol: ['none', 'arrow'],
      edgeSymbolSize: [0, 8],
      data: nodes,
      links: links,
      lineStyle: {
        opacity: 0.9,
        curveness: 0
      },
      emphasis: {
        focus: 'adjacency',
        scale: true,
        lineStyle: { width: 5 },
        itemStyle: {
          shadowBlur: 30,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  chartInstance.setOption(option)
  
  // 添加点击事件
  chartInstance.on('click', (params: any) => {
    if (params.dataType === 'node' && params.data.nodeData) {
      handleView(params.data.nodeData)
    }
  })
  
  // 窗口大小改变时重新调整图表
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

// 查询
const handleSearch = () => {
  // 过滤已经在computed中处理
}

// 重置
const handleReset = () => {
  searchForm.value = {
    status: undefined,
    keyword: ''
  }
}

// 添加节点
const handleAdd = () => {
  dialogTitle.value = '添加节点'
  isEdit.value = false
  dialogVisible.value = true
}

// 查看详情
const handleView = (row: NodeInfo) => {
  currentNode.value = row
  detailVisible.value = true
}

// 编辑节点
const handleEdit = (row: NodeInfo) => {
  dialogTitle.value = '编辑节点'
  isEdit.value = true
  currentNode.value = row
  formData.value = {
    nodeId: row.nodeId,
    nodeName: row.nodeName,
    location: row.location || '',
    status: row.status,
    batteryLevel: row.batteryLevel || 100,
    signalStrength: row.signalStrength || -50,
    remark: row.remark || ''
  }
  dialogVisible.value = true
}

// 删除节点
const handleDelete = async (row: NodeInfo) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除节点 "${row.nodeName}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteNode(row.id)
    ElMessage.success('删除成功')
    await fetchNodes()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateNode(currentNode.value.id, formData.value)
        ElMessage.success('更新成功')
      } else {
        await addNode(formData.value)
        ElMessage.success('添加成功')
      }
      
      dialogVisible.value = false
      await fetchNodes()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formData.value = {
    nodeId: '',
    nodeName: '',
    location: '',
    status: 1,
    batteryLevel: 100,
    signalStrength: -50,
    remark: ''
  }
}

// 电池颜色
const getBatteryColor = (level: number) => {
  if (level > 60) return '#67c23a'
  if (level > 30) return '#e6a23c'
  return '#f56c6c'
}

// 信号强度类型
const getSignalType = (strength: number) => {
  if (strength > -50) return 'success'
  if (strength > -70) return 'warning'
  return 'danger'
}

// 格式化日期时间
const formatDateTime = (date: string | Date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化日期
const formatDate = (date: string | Date) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD')
}

onMounted(() => {
  fetchNodes()
})

// 组件卸载时清理图表实例
onUnmounted(() => {
  if (chartInstance) {
    window.removeEventListener('resize', () => {
      chartInstance?.resize()
    })
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.nodes-page {
  min-height: 100%;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.topology-card {
  margin-bottom: 20px;
}

.topology-chart {
  width: 100%;
  height: 550px;
}
</style>
