import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  // 预构建 zh-cn locale，避免经内网穿透访问时现场编译导致 504
  optimizeDeps: {
    include: ['element-plus/dist/locale/zh-cn.mjs']
  },
  server: {
    port: 3000,
    // 放行 cpolar 内网穿透域名（本地访问 localhost 也保留），否则经隧道访问会被 vite 安全校验拦截
    allowedHosts: ['.cpolar.top', '.cpolar.cn', 'localhost'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes, req, res) => {
            // 后端返回重定向时，将 Location 中的后端地址重写为前端地址
            // 避免浏览器直接访问后端地址导致 CORS 问题
            if (proxyRes.headers.location) {
              proxyRes.headers.location = proxyRes.headers.location.replace(
                'http://localhost:8080',
                'http://localhost:3000'
              )
            }
          })
        }
      }
    }
  }
})