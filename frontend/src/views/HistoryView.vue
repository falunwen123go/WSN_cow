<template>
  <div class="history-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>历史记录</h2>
        <p>查询历史传感器数据和设备操作日志</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('History页面已加载')
})
</script>

<style scoped>
.history-page {
  min-height: 100%;
}

h2 {
  color: #333;
  margin-bottom: 16px;
}

p {
  color: #666;
  font-size: 14px;
}
</style>
