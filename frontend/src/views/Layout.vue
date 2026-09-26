<!--
  后台管理布局 Layout
  侧边栏菜单 + 顶部导航栏 + 主体内容区
-->
<template>
  <el-container style="height:100vh">
    <el-aside :width="isCollapse ? '64px' : '220px'" style="background:#304156; transition: width 0.3s">
      <div style="height:60px; display:flex; align-items:center; justify-content:center; color:#fff; font-size:18px; font-weight:bold; border-bottom:1px solid rgba(255,255,255,0.1)">
        {{ isCollapse ? '管' : '管理系统' }}
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        style="border-right:none"
      >
        <template v-for="menu in userStore.menus" :key="menu.id">
          <el-menu-item v-if="!menu.children || menu.children.length === 0" :index="menu.url">
            <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
            <template #title>{{ menu.name }}</template>
          </el-menu-item>
          <el-sub-menu v-else :index="menu.url">
            <template #title>
              <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.url">
              {{ child.name }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff; border-bottom:1px solid #e6e6e6; display:flex; align-items:center; justify-content:space-between; height:60px; padding:0 20px">
        <div>
          <el-button :icon="isCollapse ? 'Expand' : 'Fold'" text @click="isCollapse = !isCollapse" />
          <span style="margin-left:10px; color:#666">欢迎您，{{ userStore.user?.nickname || userStore.user?.username }}</span>
        </div>
        <div>
          <el-button type="danger" text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main style="background:#f0f2f5; padding:20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 布局组件逻辑
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
// 侧边栏是否折叠
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  if (!userStore.user) {
    userStore.initFromSession()
  }
  if (!userStore.user) {
    router.push('/login')
  }
})
</script>
