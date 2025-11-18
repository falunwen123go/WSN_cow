<template>
  <div class="device-switch">
    <div class="switch-info">
      <span class="device-name">{{ deviceName }}</span>
      <el-tag :type="statusTagType" size="small">{{ statusText }}</el-tag>
      <el-tag v-if="showMode" :type="modeTagType" size="small">{{ modeText }}</el-tag>
    </div>
    <el-switch
      v-model="switchValue"
      :loading="loading"
      :disabled="disabled || mode === 1"
      active-text="开启"
      inactive-text="关闭"
      @change="handleSwitch"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'

interface Props {
  deviceId: string                  // ⚠️ String类型
  deviceName: string
  status: number // 1-开启, 0-关闭
  mode?: number // 1-自动, 2-手动 (对应后端autoMode: 0-手动, 1-自动)
  showMode?: boolean
  disabled?: boolean
}

interface Emits {
  (e: 'control', deviceId: string, action: 'START' | 'STOP'): void
}

const props = withDefaults(defineProps<Props>(), {
  mode: 2,
  showMode: false,
  disabled: false
})

const emit = defineEmits<Emits>()

const switchValue = ref(false)
const loading = ref(false)

watch(
  () => props.status,
  (newStatus) => {
    switchValue.value = newStatus === 1
  },
  { immediate: true }
)

const statusTagType = computed(() => {
  return props.status === 1 ? 'success' : 'info'
})

const statusText = computed(() => {
  return props.status === 1 ? '运行中' : '已停止'
})

const modeTagType = computed(() => {
  return props.mode === 1 ? 'warning' : 'primary'
})

const modeText = computed(() => {
  return props.mode === 1 ? '自动模式' : '手动模式'
})

const handleSwitch = async (value: boolean) => {
  if (props.mode === 1) {
    ElMessage.warning('设备处于自动模式,无法手动控制')
    switchValue.value = !value
    return
  }

  try {
    loading.value = true
    const action = value ? 'START' : 'STOP'
    emit('control', props.deviceId, action)
  } catch (error) {
    console.error('设备控制失败:', error)
    switchValue.value = !value
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.device-switch {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
  transition: all 0.3s;
}

.device-switch:hover {
  border-color: #c0c4cc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.switch-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.device-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
</style>
