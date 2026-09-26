<!--
  根组件 App.vue
  仅包含 router-view，页面由路由控制
-->
<template>
  <router-view />
</template>

<script setup>
// 应用启动时从 sessionStorage 恢复用户状态
import { onMounted } from 'vue'
import { useUserStore } from './store'

const userStore = useUserStore()

onMounted(() => {
  userStore.initFromSession()
})
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app { height: 100%; }
</style>
