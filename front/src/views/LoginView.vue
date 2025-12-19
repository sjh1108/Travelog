<template>
  <div class="min-h-screen bg-background flex items-center justify-center px-4">
    <div class="max-w-md w-full">
      <div class="bg-card border border-border rounded-lg p-8 shadow-lg">
        <h1 class="text-3xl font-bold text-center mb-8">Travelog</h1>

        <form @submit.prevent="handleLogin">
          <!-- Email -->
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium mb-2">
              이메일
            </label>
            <input
              id="email"
              v-model="formData.email"
              type="email"
              required
              placeholder="example@ssafy.com"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Password -->
          <div class="mb-6">
            <label for="password" class="block text-sm font-medium mb-2">
              비밀번호
            </label>
            <input
              id="password"
              v-model="formData.password"
              type="password"
              required
              placeholder="비밀번호를 입력하세요"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="mb-4 text-red-500 text-sm text-center">
            {{ errorMessage }}
          </div>

          <!-- Login Button -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-primary text-primary-foreground py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50"
          >
            {{ isLoading ? '로그인 중...' : '로그인' }}
          </button>
        </form>

        <!-- Register Link -->
        <div class="mt-6 text-center text-sm">
          <span class="text-foreground/60">계정이 없으신가요?</span>
          <router-link
            to="/register"
            class="text-primary hover:underline ml-2 font-medium"
          >
            회원가입
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import axios from 'axios'

const router = useRouter()
const store = useAppStore()

const formData = ref({
  email: '',
  password: '',
})

const isLoading = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post('/api/users/login', formData.value)

    // 토큰과 사용자 정보 저장
    store.setAuthToken(response.data.accessToken)
    store.setCurrentUser(response.data.user || {
      email: formData.value.email,
      nickname: response.data.nickname || '여행자',
    })

    // 피드 페이지로 이동
    router.push('/')
  } catch (error) {
    console.error('로그인 실패:', error)
    errorMessage.value = error.response?.data?.message || '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.'
  } finally {
    isLoading.value = false
  }
}
</script>
