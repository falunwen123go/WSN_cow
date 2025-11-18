<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>数据监控大屏</h2>
        <p>实时展示牛舍环境数据、节点状态、报警信息</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Dashboard页面已加载')
})
</script>

<style scoped>
.dashboard-page {
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
