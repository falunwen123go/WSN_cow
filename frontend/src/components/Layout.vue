<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <el-icon class="header-icon"><Monitor /></el-icon>
        <span class="header-title">牛舍环境监测系统</span>
      </div>
      <div class="header-right">
        <el-badge :value="unhandledAlarms" :max="99" class="alarm-badge">
          <el-button :icon="Bell" circle />
        </el-badge>
        <el-dropdown class="user-dropdown">
          <span class="user-info">
            <el-avatar :size="32" :icon="UserFilled" />
            <span class="username">管理员</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="User">个人中心</el-dropdown-item>
              <el-dropdown-item :icon="Setting">系统设置</el-dropdown-item>
              <el-dropdown-item divided :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="layout-main">
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="layout-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><DataLine /></el-icon>
            <template #title>数据监控</template>
          </el-menu-item>
          <el-menu-item index="/nodes">
            <el-icon><Connection /></el-icon>
            <template #title>节点管理</template>
          </el-menu-item>
          <el-menu-item index="/alarms">
            <el-icon><Bell /></el-icon>
            <template #title>报警信息</template>
          </el-menu-item>
          <el-menu-item index="/devices">
            <el-icon><Setting /></el-icon>
            <template #title>设备控制</template>
          </el-menu-item>
          <el-menu-item index="/history">
            <el-icon><Clock /></el-icon>
            <template #title>历史记录</template>
          </el-menu-item>
          <el-menu-item index="/settings">
            <el-icon><Tools /></el-icon>
            <template #title>系统配置</template>
          </el-menu-item>
        </el-menu>
        <div class="collapse-button">
          <el-button
            :icon="isCollapse ? Expand : Fold"
            circle
            @click="toggleCollapse"
          />
        </div>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="layout-content">
        <router-view v-slot="{ Component, route }">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import {
  Monitor,
  Bell,
  UserFilled,
  User,
  Setting,
  SwitchButton,
  DataLine,
  Connection,
  Clock,
  Tools,
  Expand,
  Fold
} from '@element-plus/icons-vue'

const route = useRoute()
const isCollapse = ref(false)
const unhandledAlarms = ref(0)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 获取未处理报警数量
const fetchUnhandledAlarms = async () => {
  // 后续实现
  unhandledAlarms.value = 5
}

onMounted(() => {
  fetchUnhandledAlarms()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 28px;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.alarm-badge {
  cursor: pointer;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username {
  font-size: 14px;
}

.layout-main {
  height: calc(100vh - 60px);
}

.layout-aside {
  background: #2c3e50;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  position: relative;
}

.layout-menu {
  border: none;
  background: transparent;
  flex: 1;
}

.layout-menu .el-menu-item {
  color: #ecf0f1;
}

.layout-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

.layout-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #fff;
}

.collapse-button {
  padding: 16px;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.layout-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
