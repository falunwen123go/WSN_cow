<template>
  <div class="alarms-container">
    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="告警类型">
          <el-select v-model="filterForm.alarmType" placeholder="全部" clearable style="width: 150px">
            <el-option label="温度" value="TEMP" />
            <el-option label="湿度" value="HUMI" />
            <el-option label="氨气" value="NH3" />
            <el-option label="硫化氢" value="H2S" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警级别">
          <el-select v-model="filterForm.alarmLevel" placeholder="全部" clearable style="width: 150px">
            <el-option label="一般" :value="1" />
            <el-option label="严重" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="filterForm.handleStatus" placeholder="全部" clearable style="width: 150px">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点ID">
          <el-input v-model="filterForm.nodeId" placeholder="输入节点ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#409EFF"><Bell /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总告警数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#F56C6C"><WarningFilled /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.unhandled }}</div>
              <div class="stat-label">未处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#67C23A"><CircleCheck /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.handled }}</div>
              <div class="stat-label">已处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <el-icon :size="32" color="#E6A23C"><Clock /></el-icon>
            <div class="stat-content">
              <div class="stat-value">{{ stats.urgent }}</div>
              <div class="stat-label">紧急告警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 告警列表 -->
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>告警列表</span>
          <el-button type="primary" size="small" @click="fetchAlarmList">刷新</el-button>
        </div>
      </template>
      <el-table :data="alarmList" stripe v-loading="loading">
        <el-table-column prop="nodeId" label="节点ID" width="120" />
        <el-table-column label="告警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAlarmTypeColor(row.alarmType)">
              {{ formatAlarmType(row.alarmType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="告警级别" width="100">
          <template #default="{ row }">
            <AlarmBadge :level="row.alarmLevel" />
          </template>
        </el-table-column>
        <el-table-column prop="currentValue" label="当前值" width="100" />
        <el-table-column prop="threshold" label="阈值" width="100" />
        <el-table-column label="告警时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.alarmTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.handleStatus === 1 ? 'success' : 'danger'">
              {{ row.handleStatus === 1 ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理时间" width="180">
          <template #default="{ row }">
            {{ row.handleTime ? formatDate(row.handleTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button v-if="row.handleStatus === 0" type="primary" size="small" @click="handleAlarmClick(row)">
              处理
            </el-button>
            <el-button type="info" size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAlarmList"
          @current-change="fetchAlarmList"
        />
      </div>
    </el-card>

    <!-- 处理告警对话框 -->
    <el-dialog v-model="handleDialogVisible" title="处理告警" width="500px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="节点ID">
          <el-input v-model="currentAlarm.nodeId" disabled />
        </el-form-item>
        <el-form-item label="告警类型">
          <el-input :value="formatAlarmType(currentAlarm.alarmType)" disabled />
        </el-form-item>
        <el-form-item label="告警级别">
          <AlarmBadge :level="currentAlarm.alarmLevel" />
        </el-form-item>
        <el-form-item label="处理备注" required>
          <el-input
            v-model="handleForm.handleRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入处理措施和备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitLoading">确认处理</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="告警详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="节点ID">{{ currentAlarm.nodeId }}</el-descriptions-item>
        <el-descriptions-item label="告警类型">{{ formatAlarmType(currentAlarm.alarmType) }}</el-descriptions-item>
        <el-descriptions-item label="告警级别">
          <AlarmBadge :level="currentAlarm.alarmLevel" />
        </el-descriptions-item>
        <el-descriptions-item label="当前值">{{ currentAlarm.currentValue }}</el-descriptions-item>
        <el-descriptions-item label="阈值">{{ currentAlarm.threshold }}</el-descriptions-item>
        <el-descriptions-item label="告警时间">{{ formatDate(currentAlarm.alarmTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="currentAlarm.handleStatus === 1 ? 'success' : 'danger'">
            {{ currentAlarm.handleStatus === 1 ? '已处理' : '未处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">
          {{ currentAlarm.handleTime ? formatDate(currentAlarm.handleTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">
          {{ currentAlarm.handleRemark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, WarningFilled, CircleCheck, Clock } from '@element-plus/icons-vue'
import { getAlarmList, handleAlarm as handleAlarmApi } from '@/api/alarm'
import AlarmBadge from '@/components/AlarmBadge.vue'
import type { AlarmInfo } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const alarmList = ref<AlarmInfo[]>([])
const handleDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentAlarm = ref<AlarmInfo>({} as AlarmInfo)

const filterForm = reactive({
  alarmType: '',
  alarmLevel: undefined as number | undefined,
  handleStatus: undefined as number | undefined,
  nodeId: ''
})

const handleForm = reactive({
  handleRemark: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const stats = reactive({
  total: 0,
  unhandled: 0,
  handled: 0,
  urgent: 0
})

const fetchAlarmList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.page,
      pageSize: pagination.size,
      ...filterForm
    }
    const res = await getAlarmList(params)
    alarmList.value = res.list || []
    pagination.total = res.total || 0
    
    // 获取全部数据用于统计（不分页）
    const allParams = {
      pageNum: 1,
      pageSize: 9999,
      alarmType: '',
      alarmLevel: undefined,
      handleStatus: undefined,
      nodeId: ''
    }
    const allRes = await getAlarmList(allParams)
    const allAlarms = allRes.list || []
    
    // 计算统计数据（基于全部数据）
    stats.total = allAlarms.length
    stats.unhandled = allAlarms.filter(a => a.handleStatus === 0).length
    stats.handled = allAlarms.filter(a => a.handleStatus === 1).length
    stats.urgent = allAlarms.filter(a => a.alarmLevel === 3).length
  } catch (error) {
    ElMessage.error('获取告警列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchAlarmList()
}

const handleReset = () => {
  filterForm.alarmType = ''
  filterForm.alarmLevel = undefined
  filterForm.handleStatus = undefined
  filterForm.nodeId = ''
  pagination.page = 1
  fetchAlarmList()
}

const handleAlarmClick = (row: AlarmInfo) => {
  currentAlarm.value = row
  handleForm.handleRemark = ''
  handleDialogVisible.value = true
}

const viewDetail = (row: AlarmInfo) => {
  currentAlarm.value = row
  detailDialogVisible.value = true
}

const submitHandle = async () => {
  if (!handleForm.handleRemark.trim()) {
    ElMessage.warning('请输入处理备注')
    return
  }
  
  submitLoading.value = true
  try {
    await handleAlarmApi(currentAlarm.value.id!, handleForm.handleRemark)
    ElMessage.success('告警处理成功')
    handleDialogVisible.value = false
    fetchAlarmList()
  } catch (error) {
    ElMessage.error('处理告警失败')
  } finally {
    submitLoading.value = false
  }
}

const formatAlarmType = (type: string) => {
  const map: Record<string, string> = {
    TEMP: '温度',
    HUMI: '湿度',
    NH3: '氨气',
    H2S: '硫化氢'
  }
  return map[type] || type
}

const getAlarmTypeColor = (type: string) => {
  const map: Record<string, string> = {
    TEMP: 'warning',
    HUMI: 'info',
    NH3: 'danger',
    H2S: 'danger'
  }
  return map[type] || ''
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  fetchAlarmList()
})
</script>

<style scoped>
.alarms-container {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
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
</style>
