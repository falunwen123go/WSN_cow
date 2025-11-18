import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue'),
          meta: { title: '数据监控' }
        },
        {
          path: 'nodes',
          name: 'nodes',
          component: () => import('@/views/NodesView.vue'),
          meta: { title: '节点管理' }
        },
        {
          path: 'alarms',
          name: 'alarms',
          component: () => import('@/views/AlarmsView.vue'),
          meta: { title: '报警信息' }
        },
        {
          path: 'devices',
          name: 'devices',
          component: () => import('@/views/DevicesView.vue'),
          meta: { title: '设备控制' }
        },
        {
          path: 'history',
          name: 'history',
          component: () => import('@/views/HistoryView.vue'),
          meta: { title: '历史记录' }
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/SettingsView.vue'),
          meta: { title: '系统配置' }
        }
      ]
    }
  ],
})

export default router
