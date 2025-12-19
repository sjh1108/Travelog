<template>
  <div class="min-h-screen bg-background flex items-center justify-center px-4 py-8">
    <div class="max-w-md w-full">
      <div class="bg-card border border-border rounded-lg p-8 shadow-lg">
        <h1 class="text-3xl font-bold text-center mb-8">회원가입</h1>

        <form @submit.prevent="handleRegister">
          <!-- Email -->
          <div class="mb-4">
            <label for="email" class="block text-sm font-medium mb-2">
              이메일 *
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
          <div class="mb-4">
            <label for="password" class="block text-sm font-medium mb-2">
              비밀번호 *
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

          <!-- Password Confirm -->
          <div class="mb-4">
            <label for="passwordConfirm" class="block text-sm font-medium mb-2">
              비밀번호 확인 *
            </label>
            <input
              id="passwordConfirm"
              v-model="passwordConfirm"
              type="password"
              required
              placeholder="비밀번호를 다시 입력하세요"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Nickname -->
          <div class="mb-4">
            <label for="nickname" class="block text-sm font-medium mb-2">
              닉네임 *
            </label>
            <input
              id="nickname"
              v-model="formData.nickname"
              type="text"
              required
              placeholder="닉네임을 입력하세요"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Profile Image URL -->
          <div class="mb-4">
            <label for="profileImage" class="block text-sm font-medium mb-2">
              프로필 이미지 URL (선택)
            </label>
            <input
              id="profileImage"
              v-model="formData.profileImage"
              type="url"
              placeholder="http://example.com/profile.jpg"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <!-- Bio -->
          <div class="mb-6">
            <label for="bio" class="block text-sm font-medium mb-2">
              자기소개 (선택)
            </label>
            <textarea
              id="bio"
              v-model="formData.bio"
              rows="3"
              placeholder="자기소개를 입력하세요"
              class="w-full px-4 py-2 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary resize-none"
            ></textarea>
          </div>

          <!-- Error Message -->
          <div v-if="errorMessage" class="mb-4 text-red-500 text-sm text-center">
            {{ errorMessage }}
          </div>

          <!-- Register Button -->
          <button
            type="submit"
            :disabled="isLoading"
            class="w-full bg-primary text-primary-foreground py-2 rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50"
          >
            {{ isLoading ? '가입 중...' : '회원가입' }}
          </button>
        </form>

        <!-- Login Link -->
        <div class="mt-6 text-center text-sm">
          <span class="text-foreground/60">이미 계정이 있으신가요?</span>
          <router-link
            to="/login"
            class="text-primary hover:underline ml-2 font-medium"
          >
            로그인
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
  nickname: '',
  profileImage: '',
  bio: '',
})

const passwordConfirm = ref('')
const isLoading = ref(false)
const errorMessage = ref('')

const handleRegister = async () => {
  // 비밀번호 확인
  if (formData.value.password !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post('/api/users/join', formData.value)

    // 회원가입 성공 후 자동 로그인
    if (response.data.accessToken) {
      store.setAuthToken(response.data.accessToken)
      store.setCurrentUser(response.data.user || formData.value)
      router.push('/')
    } else {
      // 토큰이 없으면 로그인 페이지로 이동
      router.push('/login')
    }
  } catch (error) {
    console.error('회원가입 실패:', error)
    errorMessage.value = error.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해주세요.'
  } finally {
    isLoading.value = false
  }
}
</script>
