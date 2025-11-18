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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import NodeStatus from '@/components/NodeStatus.vue'
import { getAllNodes, addNode, updateNode, deleteNode } from '@/api/node'
import type { NodeInfo } from '@/types'

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
  } catch (error) {
    ElMessage.error('获取节点列表失败')
  } finally {
    loading.value = false
  }
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
</style>
