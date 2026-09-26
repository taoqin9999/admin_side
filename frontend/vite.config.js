import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
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