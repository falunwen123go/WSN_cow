<template>
  <div class="settings-container">
    <el-tabs v-model="activeTab">
      <!-- 告警阈值配置 -->
      <el-tab-pane label="告警阈值" name="thresholds">
        <el-card shadow="never" v-loading="loading">
          <el-form :model="thresholdForm" label-width="150px">
            <el-divider content-position="left">温度阈值</el-divider>
            <el-form-item label="告警下限(℃)">
              <el-input-number v-model="thresholdForm.tempMin" :min="-20" :max="50" :precision="1" />
            </el-form-item>
            <el-form-item label="告警上限(℃)">
              <el-input-number v-model="thresholdForm.tempMax" :min="-20" :max="50" :precision="1" />
            </el-form-item>
            <el-form-item label="严重告警上限(℃)">
              <el-input-number v-model="thresholdForm.tempCritical" :min="-20" :max="50" :precision="1" />
            </el-form-item>

            <el-divider content-position="left">湿度阈值</el-divider>
            <el-form-item label="告警下限(%)">
              <el-input-number v-model="thresholdForm.humiMin" :min="0" :max="100" :precision="1" />
            </el-form-item>
            <el-form-item label="告警上限(%)">
              <el-input-number v-model="thresholdForm.humiMax" :min="0" :max="100" :precision="1" />
            </el-form-item>

            <el-divider content-position="left">氨气阈值</el-divider>
            <el-form-item label="告警阈值(ppm)">
              <el-input-number v-model="thresholdForm.nh3Warning" :min="0" :max="100" :precision="1" />
            </el-form-item>
            <el-form-item label="严重告警阈值(ppm)">
              <el-input-number v-model="thresholdForm.nh3Critical" :min="0" :max="100" :precision="1" />
            </el-form-item>

            <el-divider content-position="left">硫化氢阈值</el-divider>
            <el-form-item label="告警阈值(ppm)">
              <el-input-number v-model="thresholdForm.h2sWarning" :min="0" :max="50" :precision="1" />
            </el-form-item>
            <el-form-item label="严重告警阈值(ppm)">
              <el-input-number v-model="thresholdForm.h2sCritical" :min="0" :max="50" :precision="1" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveThresholds" :loading="saveLoading">
                保存配置
              </el-button>
              <el-button @click="fetchConfigs">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 系统参数配置 -->
      <el-tab-pane label="系统参数" name="system">
        <el-card shadow="never" v-loading="loading">
          <el-form :model="systemForm" label-width="180px">
            <el-form-item label="数据采集间隔(秒)">
              <el-input-number v-model="systemForm.dataInterval" :min="10" :max="300" :step="10" />
              <span style="margin-left: 10px; color: #909399;">建议: 30秒</span>
            </el-form-item>
            <el-form-item label="节点离线超时(秒)">
              <el-input-number v-model="systemForm.nodeTimeout" :min="60" :max="3600" :step="60" />
              <span style="margin-left: 10px; color: #909399;">建议: 300秒</span>
            </el-form-item>
            <el-form-item label="告警去重间隔(秒)">
              <el-input-number v-model="systemForm.alarmInterval" :min="60" :max="3600" :step="60" />
              <span style="margin-left: 10px; color: #909399;">建议: 600秒</span>
            </el-form-item>
            <el-form-item label="Socket服务端口">
              <el-input v-model="systemForm.socketPort" disabled />
              <span style="margin-left: 10px; color: #E6A23C;">需重启服务生效</span>
            </el-form-item>
            <el-form-item label="自动控制启用">
              <el-switch
                v-model="systemForm.autoControl"
                active-text="启用"
                inactive-text="禁用"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveSystemParams" :loading="saveLoading">
                保存配置
              </el-button>
              <el-button @click="fetchConfigs">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 关于系统 -->
      <el-tab-pane label="关于" name="about">
        <el-card shadow="never">
          <el-descriptions title="系统信息" :column="2" border>
            <el-descriptions-item label="系统名称">奶牛养殖环境监测系统</el-descriptions-item>
            <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端框架">Spring Boot 2.7.18</el-descriptions-item>
            <el-descriptions-item label="前端框架">Vue 3 + TypeScript</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0+</el-descriptions-item>
            <el-descriptions-item label="通信协议">ZigBee</el-descriptions-item>
            <el-descriptions-item label="开发团队" :span="2">WSN团队</el-descriptions-item>
            <el-descriptions-item label="技术支持" :span="2">
              <el-link type="primary" href="#">技术文档</el-link>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigList, updateConfig } from '@/api/config'

const loading = ref(false)
const saveLoading = ref(false)
const activeTab = ref('thresholds')

const thresholdForm = reactive({
  tempMin: 5,
  tempMax: 35,
  tempCritical: 40,
  humiMin: 30,
  humiMax: 85,
  nh3Warning: 25,
  nh3Critical: 40,
  h2sWarning: 10,
  h2sCritical: 20
})

const systemForm = reactive({
  dataInterval: 30,
  nodeTimeout: 300,
  alarmInterval: 600,
  socketPort: '8888',
  autoControl: true
})

const fetchConfigs = async () => {
  loading.value = true
  try {
    const res = await getConfigList()
    const configs = res.data || []
    
    // 解析配置到表单
    configs.forEach((config: any) => {
      const key = config.configKey
      const value = config.configValue
      
      if (key === 'alarm.temp.min') thresholdForm.tempMin = parseFloat(value)
      else if (key === 'alarm.temp.max') thresholdForm.tempMax = parseFloat(value)
      else if (key === 'alarm.temp.critical') thresholdForm.tempCritical = parseFloat(value)
      else if (key === 'alarm.humi.min') thresholdForm.humiMin = parseFloat(value)
      else if (key === 'alarm.humi.max') thresholdForm.humiMax = parseFloat(value)
      else if (key === 'alarm.nh3.warning') thresholdForm.nh3Warning = parseFloat(value)
      else if (key === 'alarm.nh3.critical') thresholdForm.nh3Critical = parseFloat(value)
      else if (key === 'alarm.h2s.warning') thresholdForm.h2sWarning = parseFloat(value)
      else if (key === 'alarm.h2s.critical') thresholdForm.h2sCritical = parseFloat(value)
      else if (key === 'data.collect.interval') systemForm.dataInterval = parseInt(value)
      else if (key === 'node.offline.timeout') systemForm.nodeTimeout = parseInt(value)
      else if (key === 'alarm.deduplicate.interval') systemForm.alarmInterval = parseInt(value)
      else if (key === 'serial.port') systemForm.socketPort = value
      else if (key === 'device.auto.control.enabled') systemForm.autoControl = value === 'true'
    })
  } catch (error) {
    ElMessage.error('获取配置失败')
  } finally {
    loading.value = false
  }
}

const saveThresholds = async () => {
  saveLoading.value = true
  try {
    const updates = [
      { key: 'alarm.temp.min', value: thresholdForm.tempMin.toString() },
      { key: 'alarm.temp.max', value: thresholdForm.tempMax.toString() },
      { key: 'alarm.temp.critical', value: thresholdForm.tempCritical.toString() },
      { key: 'alarm.humi.min', value: thresholdForm.humiMin.toString() },
      { key: 'alarm.humi.max', value: thresholdForm.humiMax.toString() },
      { key: 'alarm.nh3.warning', value: thresholdForm.nh3Warning.toString() },
      { key: 'alarm.nh3.critical', value: thresholdForm.nh3Critical.toString() },
      { key: 'alarm.h2s.warning', value: thresholdForm.h2sWarning.toString() },
      { key: 'alarm.h2s.critical', value: thresholdForm.h2sCritical.toString() }
    ]
    
    for (const update of updates) {
      await updateConfig(update.key, update.value)
    }
    
    ElMessage.success('告警阈值保存成功')
  } catch (error) {
    ElMessage.error('保存配置失败')
  } finally {
    saveLoading.value = false
  }
}

const saveSystemParams = async () => {
  saveLoading.value = true
  try {
    const updates = [
      { key: 'data.collect.interval', value: systemForm.dataInterval.toString() },
      { key: 'node.offline.timeout', value: systemForm.nodeTimeout.toString() },
      { key: 'alarm.deduplicate.interval', value: systemForm.alarmInterval.toString() },
      { key: 'device.auto.control.enabled', value: systemForm.autoControl.toString() }
    ]
    
    for (const update of updates) {
      await updateConfig(update.key, update.value)
    }
    
    ElMessage.success('系统参数保存成功')
  } catch (error) {
    ElMessage.error('保存配置失败')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  fetchConfigs()
})
</script>

<style scoped>
.settings-container {
  padding: 20px;
}
</style>
