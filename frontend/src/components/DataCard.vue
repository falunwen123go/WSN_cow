<template>
  <el-card class="data-card" shadow="hover">
    <div class="card-header">
      <el-icon class="card-icon" :style="{ color: iconColor }">
        <component :is="icon" />
      </el-icon>
      <span class="card-title">{{ title }}</span>
    </div>
    <div class="card-content">
      <div class="card-value">
        <span class="value-number">{{ value }}</span>
        <span class="value-unit">{{ unit }}</span>
      </div>
      <div v-if="trend !== undefined" class="card-trend">
        <el-icon :class="['trend-icon', trendClass]">
          <component :is="trendIcon" />
        </el-icon>
        <span :class="['trend-text', trendClass]">{{ Math.abs(trend) }}%</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowUp, ArrowDown, Minus } from '@element-plus/icons-vue'

interface Props {
  title: string
  value: string | number
  unit?: string
  icon?: any
  iconColor?: string
  trend?: number // 正数上升,负数下降,0持平
}

const props = withDefaults(defineProps<Props>(), {
  unit: '',
  iconColor: '#409eff',
  trend: undefined
})

const trendIcon = computed(() => {
  if (props.trend === undefined) return null
  if (props.trend > 0) return ArrowUp
  if (props.trend < 0) return ArrowDown
  return Minus
})

const trendClass = computed(() => {
  if (props.trend === undefined) return ''
  if (props.trend > 0) return 'trend-up'
  if (props.trend < 0) return 'trend-down'
  return 'trend-stable'
})
</script>

<style scoped>
.data-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 24px;
}

.card-title {
  font-size: 14px;
  color: #606266;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.card-value {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.value-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.value-unit {
  font-size: 14px;
  color: #909399;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-icon {
  font-size: 16px;
}

.trend-text {
  font-size: 12px;
  font-weight: 500;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.trend-stable {
  color: #909399;
}
</style>
