<template>
  <div class="settings-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>系统配置</h2>
        <p>配置报警阈值、数据采集间隔等参数</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Settings页面已加载')
})
</script>

<style scoped>
.settings-page {
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
