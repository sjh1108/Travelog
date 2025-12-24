import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    proxy: {
      // 공공데이터 관광지 API (더 구체적인 패턴이 먼저 와야 함)
      '/api/tour': {
        target: 'https://apis.data.go.kr',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/tour/, '/B551011/KorService2'),
        secure: false,
      },
      // 백엔드 API 프록시 (나머지 모든 /api/* 요청)
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
