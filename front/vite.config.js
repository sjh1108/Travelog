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
      '/api/users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/api/tour': {
        target: 'https://apis.data.go.kr',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/tour/, '/B551011/KorService2'),
        secure: false,
      },
    },
  },
})
