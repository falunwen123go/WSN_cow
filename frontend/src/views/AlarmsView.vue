<template>
  <div class="alarms-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>报警信息</h2>
        <p>查看和处理环境异常报警</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Alarms页面已加载')
})
</script>

<style scoped>
.alarms-page {
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
