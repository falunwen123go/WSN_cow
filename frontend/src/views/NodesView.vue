<template>
  <div class="nodes-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <h2>节点管理</h2>
        <p>查看和管理所有ZigBee节点信息</p>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('Nodes页面已加载')
})
</script>

<style scoped>
.nodes-page {
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
