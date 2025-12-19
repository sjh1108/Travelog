import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { useAppStore } from './stores/app'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// Axios 인터셉터 설정
axios.interceptors.request.use(
  (config) => {
    const store = useAppStore()
    if (store.authToken) {
      config.headers.Authorization = `Bearer ${store.authToken}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 401 에러 시 자동 로그아웃
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const store = useAppStore()
      store.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

app.mount('#app')
