<template>
  <div class="devices-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>设备控制</h2>
        <p>远程控制通风机、照明等设备</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Devices页面已加载')
})
</script>

<style scoped>
.devices-page {
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
