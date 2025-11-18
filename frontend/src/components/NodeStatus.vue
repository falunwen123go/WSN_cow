<template>
  <div class="node-status">
    <span :class="['status-dot', statusClass]"></span>
    <span :class="['status-text', statusClass]">{{ statusText }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: 'online' | 'offline' | number // 'online', 'offline' 或 1(在线), 0(离线)
  showText?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showText: true
})

const isOnline = computed(() => {
  if (typeof props.status === 'string') {
    return props.status === 'online'
  }
  return props.status === 1
})

const statusClass = computed(() => {
  return isOnline.value ? 'status-online' : 'status-offline'
})

const statusText = computed(() => {
  return isOnline.value ? '在线' : '离线'
})
</script>

<style scoped>
.node-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  position: relative;
}

.status-dot::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.status-online {
  background-color: #67c23a;
}

.status-online::before {
  background-color: #67c23a;
}

.status-offline {
  background-color: #909399;
}

.status-offline::before {
  display: none;
}

.status-text {
  font-size: 12px;
  font-weight: 500;
}

.status-text.status-online {
  color: #67c23a;
}

.status-text.status-offline {
  color: #909399;
}

@keyframes pulse {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.5);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(2);
  }
}
</style>
