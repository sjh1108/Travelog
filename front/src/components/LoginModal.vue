<template>
  <!-- 모달 배경 (반투명) -->
  <Transition name="fade">
    <div
      v-if="modelValue"
      class="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4"
      @click="handleBackdropClick"
    >
      <!-- 모달 컨텐츠 -->
      <Transition name="scale">
        <div
          v-if="modelValue"
          class="bg-card border border-border rounded-2xl shadow-2xl max-w-md w-full overflow-hidden"
          @click.stop
        >
          <!-- 헤더 -->
          <div class="relative bg-gradient-to-r from-primary/10 to-primary/5 px-8 py-6 border-b border-border">
            <h2 class="text-2xl font-bold text-center">로그인</h2>
            <p class="text-center text-sm text-foreground/60 mt-1">여행의 순간을 공유하세요</p>
            <button
              @click="$emit('update:modelValue', false)"
              class="absolute top-4 right-4 text-foreground/60 hover:text-foreground transition-colors"
            >
              <X class="w-5 h-5" />
            </button>
          </div>

          <!-- 바디 -->
          <div class="px-8 py-6">
            <form @submit.prevent="handleLogin" class="space-y-4">
              <!-- Email -->
              <div>
                <label for="login-email" class="block text-sm font-medium mb-2">
                  이메일
                </label>
                <div class="relative">
                  <Mail class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="login-email"
                    v-model="formData.email"
                    type="email"
                    required
                    placeholder="example@ssafy.com"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
                  />
                </div>
              </div>

              <!-- Password -->
              <div>
                <label for="login-password" class="block text-sm font-medium mb-2">
                  비밀번호
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="login-password"
                    v-model="formData.password"
                    type="password"
                    required
                    placeholder="비밀번호를 입력하세요"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
                  />
                </div>
              </div>

              <!-- Error Message -->
              <div v-if="errorMessage" class="bg-red-500/10 border border-red-500/20 rounded-lg p-3">
                <p class="text-red-500 text-sm">{{ errorMessage }}</p>
              </div>

              <!-- Login Button -->
              <button
                type="submit"
                :disabled="isLoading"
                class="w-full bg-primary text-primary-foreground py-3 rounded-lg hover:bg-primary/90 transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed font-medium shadow-lg shadow-primary/20"
              >
                {{ isLoading ? '로그인 중...' : '로그인' }}
              </button>
            </form>

            <!-- Divider -->
            <div class="relative my-6">
              <div class="absolute inset-0 flex items-center">
                <div class="w-full border-t border-border"></div>
              </div>
              <div class="relative flex justify-center text-sm">
                <span class="px-4 bg-card text-foreground/60">또는</span>
              </div>
            </div>

            <!-- Register Link -->
            <div class="text-center">
              <p class="text-sm text-foreground/60">
                계정이 없으신가요?
                <button
                  @click="switchToRegister"
                  type="button"
                  class="text-primary hover:underline font-medium ml-1"
                >
                  회원가입
                </button>
              </p>
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { X, Mail, Lock } from 'lucide-vue-next'
import axios from 'axios'

const props = defineProps({
  modelValue: Boolean,
})

const emit = defineEmits(['update:modelValue', 'switchToRegister'])

const router = useRouter()
const store = useAppStore()

const formData = ref({
  email: '',
  password: '',
})

const isLoading = ref(false)
const errorMessage = ref('')

// ESC 키로 모달 닫기
const handleKeydown = (e) => {
  if (e.key === 'Escape' && props.modelValue) {
    emit('update:modelValue', false)
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
})

// 모달이 열릴 때 body 스크롤 방지
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    document.body.style.overflow = 'hidden'
  } else {
    document.body.style.overflow = ''
    errorMessage.value = ''
  }
})

const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post('/api/users/login', formData.value)

    store.setAuthToken(response.data.accessToken)
    store.setCurrentUser(response.data.user || {
      email: formData.value.email,
      nickname: response.data.nickname || '여행자',
    })

    emit('update:modelValue', false)
    router.push('/map')
  } catch (error) {
    console.error('로그인 실패:', error)
    errorMessage.value = error.response?.data?.message || '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.'
  } finally {
    isLoading.value = false
  }
}

const handleBackdropClick = () => {
  emit('update:modelValue', false)
}

const switchToRegister = () => {
  emit('update:modelValue', false)
  emit('switchToRegister')
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.3s ease;
}

.scale-enter-from,
.scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
