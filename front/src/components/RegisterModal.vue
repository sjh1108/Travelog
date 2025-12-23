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
          class="bg-card border border-border rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto"
          @click.stop
        >
          <!-- 헤더 -->
          <div class="sticky top-0 bg-gradient-to-r from-primary/10 to-primary/5 px-8 py-6 border-b border-border z-10">
            <h2 class="text-2xl font-bold text-center">회원가입</h2>
            <p class="text-center text-sm text-foreground/60 mt-1">함께 여행의 추억을 나눠요</p>
            <button
              @click="$emit('update:modelValue', false)"
              class="absolute top-4 right-4 text-foreground/60 hover:text-foreground transition-colors"
            >
              <X class="w-5 h-5" />
            </button>
          </div>

          <!-- 바디 -->
          <div class="px-8 py-6">
            <form @submit.prevent="handleRegister" class="space-y-4">
              <!-- Email -->
              <div>
                <label for="register-email" class="block text-sm font-medium mb-2">
                  이메일 *
                </label>
                <div class="relative">
                  <Mail class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="register-email"
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
                <label for="register-password" class="block text-sm font-medium mb-2">
                  비밀번호 *
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="register-password"
                    v-model="formData.password"
                    type="password"
                    required
                    placeholder="비밀번호를 입력하세요"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
                  />
                </div>
              </div>

              <!-- Password Confirm -->
              <div>
                <label for="register-password-confirm" class="block text-sm font-medium mb-2">
                  비밀번호 확인 *
                </label>
                <div class="relative">
                  <Lock class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="register-password-confirm"
                    v-model="passwordConfirm"
                    type="password"
                    required
                    placeholder="비밀번호를 다시 입력하세요"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
                  />
                </div>
              </div>

              <!-- Nickname -->
              <div>
                <label for="register-nickname" class="block text-sm font-medium mb-2">
                  닉네임 *
                </label>
                <div class="relative">
                  <User class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-foreground/40" />
                  <input
                    id="register-nickname"
                    v-model="formData.nickname"
                    type="text"
                    required
                    placeholder="닉네임을 입력하세요"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
                  />
                </div>
              </div>

              <!-- Profile Image Upload -->
              <div>
                <label class="block text-sm font-medium mb-2">
                  프로필 이미지 (선택)
                </label>

                <!-- 이미지 미리보기 (선택된 경우에만 표시) -->
                <div v-if="previewUrl" class="flex justify-center">
                  <div class="relative">
                    <img
                      :src="previewUrl"
                      alt="프로필 미리보기"
                      class="w-24 h-24 rounded-full object-cover border-2 border-primary"
                    />
                    <button
                      type="button"
                      @click="clearProfileImage"
                      class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600 transition-colors"
                    >
                      <X class="w-4 h-4" />
                    </button>
                  </div>
                </div>

                <!-- 드래그앤드롭 영역 (선택되지 않은 경우에만 표시) -->
                <div
                  v-else
                  @dragover.prevent="handleDragOver"
                  @dragleave.prevent="isDragging = false"
                  @drop.prevent="handleDrop"
                  @click="$refs.profileImageInput?.click()"
                  :class="[
                    'relative border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition-all',
                    isDragging ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/50 hover:bg-primary/5'
                  ]"
                >
                  <input
                    ref="profileImageInput"
                    type="file"
                    accept="image/*"
                    @change="handleFileSelect"
                    class="hidden"
                  />
                  <Image class="w-10 h-10 mx-auto mb-2 text-foreground/40" />
                  <p class="text-sm text-foreground/60">
                    클릭하거나 이미지를 드래그하세요
                  </p>
                  <p class="text-xs text-foreground/40 mt-1">
                    JPG, PNG, GIF
                  </p>
                </div>
              </div>

              <!-- Bio -->
              <div>
                <label for="register-bio" class="block text-sm font-medium mb-2">
                  자기소개 (선택)
                </label>
                <div class="relative">
                  <FileText class="absolute left-3 top-3 w-5 h-5 text-foreground/40" />
                  <textarea
                    id="register-bio"
                    v-model="formData.bio"
                    rows="3"
                    placeholder="자기소개를 입력하세요"
                    class="w-full pl-10 pr-4 py-3 bg-background border border-border rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all resize-none"
                  ></textarea>
                </div>
              </div>

              <!-- Error Message -->
              <div v-if="errorMessage" class="bg-red-500/10 border border-red-500/20 rounded-lg p-3">
                <p class="text-red-500 text-sm">{{ errorMessage }}</p>
              </div>

              <!-- Register Button -->
              <button
                type="submit"
                :disabled="isLoading"
                class="w-full bg-primary text-primary-foreground py-3 rounded-lg hover:bg-primary/90 transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed font-medium shadow-lg shadow-primary/20"
              >
                {{ isLoading ? '가입 중...' : '회원가입' }}
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

            <!-- Login Link -->
            <div class="text-center">
              <p class="text-sm text-foreground/60">
                이미 계정이 있으신가요?
                <button
                  @click="switchToLogin"
                  type="button"
                  class="text-primary hover:underline font-medium ml-1"
                >
                  로그인
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
import { X, Mail, Lock, User, Image, FileText } from 'lucide-vue-next'
import axios from 'axios'
import { fileAPI } from '@/api/api'

const props = defineProps({
  modelValue: Boolean,
})

const emit = defineEmits(['update:modelValue', 'switchToLogin'])

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

// 프로필 이미지 업로드 관련
const selectedFile = ref(null)
const previewUrl = ref('')
const isDragging = ref(false)
const profileImageInput = ref(null)

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

// 파일 선택 처리
const handleFileSelect = (event) => {
  const file = event.target.files?.[0]
  if (file && file.type.startsWith('image/')) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

// 드래그 오버 처리
const handleDragOver = (event) => {
  event.preventDefault()
  isDragging.value = true
}

// 드롭 처리
const handleDrop = (event) => {
  event.preventDefault()
  isDragging.value = false

  const file = event.dataTransfer?.files?.[0]
  if (file && file.type.startsWith('image/')) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

// 프로필 이미지 제거
const clearProfileImage = () => {
  selectedFile.value = null
  previewUrl.value = ''
  formData.value.profileImage = ''
  if (profileImageInput.value) {
    profileImageInput.value.value = ''
  }
}

const handleRegister = async () => {
  // 비밀번호 확인
  if (formData.value.password !== passwordConfirm.value) {
    errorMessage.value = '비밀번호가 일치하지 않습니다.'
    return
  }

  isLoading.value = true
  errorMessage.value = ''

  try {
    // 프로필 이미지 업로드 (선택한 경우)
    if (selectedFile.value) {
      try {
        console.log('=== 프로필 이미지 업로드 시작 ===')
        console.log('파일:', selectedFile.value.name)
        const uploadResult = await fileAPI.uploadImage(selectedFile.value)
        console.log('업로드 API 응답:', uploadResult)
        console.log('응답 타입:', typeof uploadResult)

        // 업로드 결과에서 이미지 URL 추출
        const serverImageUrl = uploadResult.url || uploadResult.imageUrl ||
                             uploadResult.data?.url || uploadResult.data?.imageUrl

        if (typeof uploadResult === 'string') {
          formData.value.profileImage = uploadResult
          console.log('✅ 프로필 이미지 설정 완료:', formData.value.profileImage)
        } else if (serverImageUrl) {
          formData.value.profileImage = serverImageUrl
          console.log('✅ 프로필 이미지 설정 완료:', formData.value.profileImage)
        } else {
          console.warn('❌ 이미지 업로드 결과가 불명확합니다:', uploadResult)
          formData.value.profileImage = ''
        }
      } catch (uploadError) {
        console.error('❌ 프로필 이미지 업로드 실패:', uploadError)
        console.error('에러 상세:', uploadError.response?.data)
        formData.value.profileImage = ''
      }
    } else {
      console.log('프로필 이미지 선택 안 함')
    }

    const response = await axios.post('/api/users/join', formData.value)

    // 회원가입 성공 후 자동 로그인
    if (response.data.accessToken) {
      store.setAuthToken(response.data.accessToken)

      // currentUser 설정 (id는 email 사용)
      let user = response.data.user || {
        ...formData.value,
        id: formData.value.email
      }

      // user 객체에 id가 없으면 email을 id로 사용
      if (user && !user.id) {
        user.id = user.email || formData.value.email
      }

      // profileImage가 없으면 기본값 설정
      if (!user.profileImage) {
        user.profileImage = formData.value.profileImage || '/default-profile.svg'
      }

      console.log('회원가입 성공 - 사용자 정보:', user)
      store.setCurrentUser(user)
      emit('update:modelValue', false)
      router.push('/map')
    } else {
      // 토큰이 없으면 로그인 모달로 전환
      emit('update:modelValue', false)
      emit('switchToLogin')
    }
  } catch (error) {
    console.error('회원가입 실패:', error)
    errorMessage.value = error.response?.data?.message || '회원가입에 실패했습니다. 다시 시도해주세요.'
  } finally {
    isLoading.value = false
  }
}

const handleBackdropClick = () => {
  emit('update:modelValue', false)
}

const switchToLogin = () => {
  emit('update:modelValue', false)
  emit('switchToLogin')
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
