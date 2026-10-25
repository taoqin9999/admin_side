/**
 * 应用入口文件
 * 初始化 Vue 应用，注册 Element Plus、路由、Pinia、图标等
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import axios from 'axios'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用中文 locale（日期选择器、分页等组件文案显示为中文）
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.use(createPinia())

// 全局 axios
app.config.globalProperties.$axios = axios

app.mount('#app')
