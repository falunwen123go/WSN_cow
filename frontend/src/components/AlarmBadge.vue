<template>
  <el-tag :type="tagType" :size="size" :effect="effect">
    {{ levelText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  level: 1 | 2 | 3 | number // 1-低级, 2-中级, 3-高级
  size?: 'large' | 'default' | 'small'
  effect?: 'dark' | 'light' | 'plain'
}

const props = withDefaults(defineProps<Props>(), {
  size: 'default',
  effect: 'light'
})

const tagType = computed(() => {
  switch (props.level) {
    case 1:
      return 'info'
    case 2:
      return 'warning'
    case 3:
      return 'danger'
    default:
      return 'info'
  }
})

const levelText = computed(() => {
  switch (props.level) {
    case 1:
      return '低级报警'
    case 2:
      return '中级报警'
    case 3:
      return '高级报警'
    default:
      return '未知级别'
  }
})
</script>

<style scoped>
/* 自定义样式可在此添加 */
</style>
