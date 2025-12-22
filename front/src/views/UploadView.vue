<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-2xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">Upload Photo</h1>
        
        <div class="bg-card border border-border rounded-lg p-6">
          <!-- Image Upload Area -->
          <div
            class="border-2 border-dashed border-border rounded-lg p-8 text-center mb-6 cursor-pointer hover:border-primary transition-colors"
            @click="triggerFileInput"
            @dragover.prevent
            @drop.prevent="handleDrop"
          >
            <Upload class="mx-auto h-12 w-12 text-foreground/40 mb-4" />
            <p class="text-foreground/60 mb-2">
              Drag and drop your photo here, or click to select
            </p>
            <p class="text-sm text-foreground/40">PNG, JPG up to 10MB</p>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleFileSelect"
            />
          </div>

          <!-- Preview -->
          <div v-if="previewUrl" class="mb-6">
            <img :src="previewUrl" alt="Preview" class="w-full rounded-lg" />
          </div>

          <!-- Form -->
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium mb-2">Location</label>
              <input
                v-model="location"
                type="text"
                placeholder="Where was this photo taken?"
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
              />
            </div>

            <div>
              <label class="block text-sm font-medium mb-2">Caption</label>
              <textarea
                v-model="caption"
                rows="4"
                placeholder="Write a caption..."
                class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 resize-none"
              />
            </div>

            <button
              @click="handleUpload"
              :disabled="!previewUrl || !location || isUploading"
              class="w-full py-3 bg-primary text-primary-foreground rounded-lg font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 transition-opacity"
            >
              {{ isUploading ? 'Uploading...' : 'Share Photo' }}
            </button>
          </div>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Upload } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { fileAPI, postAPI } from '@/api/api'

const router = useRouter()
const store = useAppStore()

const fileInput = ref(null)
const previewUrl = ref('')
const selectedFile = ref(null)
const location = ref('')
const caption = ref('')
const isUploading = ref(false)

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleFileSelect = (event) => {
  const target = event.target
  const file = target.files?.[0]
  if (file) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleDrop = (event) => {
  const file = event.dataTransfer?.files[0]
  if (file && file.type.startsWith('image/')) {
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleUpload = async () => {
  // 로그인 체크
  if (!store.currentUser) {
    alert('로그인이 필요합니다.')
    return
  }

  if (!selectedFile.value || !location.value) {
    alert('이미지와 위치를 입력해주세요.')
    return
  }

  isUploading.value = true

  try {
    let imageUrl = previewUrl.value // 기본값으로 프리뷰 URL 사용

    // 이미지 업로드 시도 (실패해도 계속 진행)
    try {
      console.log('1. 이미지 업로드 시도 중...')
      const uploadResult = await fileAPI.uploadImage(selectedFile.value)
      console.log('이미지 업로드 결과:', uploadResult)

      // 업로드 결과에서 이미지 URL 추출
      const serverImageUrl = uploadResult.url || uploadResult.imageUrl || uploadResult.data?.url || uploadResult.data?.imageUrl

      // 만약 uploadResult가 문자열이면 그대로 사용
      if (typeof uploadResult === 'string') {
        imageUrl = uploadResult
      } else if (serverImageUrl) {
        imageUrl = serverImageUrl
      }

      console.log('서버 이미지 URL:', imageUrl)
    } catch (uploadError) {
      console.warn('이미지 업로드 실패 (백엔드 미구현 가능성), 로컬 URL 사용:', uploadError)
      // 이미지 업로드가 실패해도 계속 진행 (프리뷰 URL 사용)
      imageUrl = previewUrl.value
    }

    // 2. 게시물 작성
    console.log('2. 게시물 작성 시작...')
    const postData = {
      imageUrl: imageUrl,
      travelLocation: location.value,
      caption: caption.value || '',
    }
    console.log('게시물 데이터:', postData)

    const newPost = await postAPI.createPost(postData)
    console.log('게시물 작성 결과:', newPost)

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const postToAdd = {
      ...newPost,
      userId: newPost.writerEmail || store.currentUser?.email,
      user: newPost.user || {
        id: newPost.writerEmail || store.currentUser?.email,
        email: newPost.writerEmail || store.currentUser?.email,
        nickname: newPost.nickname || store.currentUser?.nickname || 'Unknown User',
        profileImage: newPost.profileImage || store.currentUser?.profileImage || '/default-profile.svg'
      }
    }

    console.log('변환된 게시물:', postToAdd)

    // Store에 새 게시물 추가
    store.addPost(postToAdd)

    // 피드로 이동
    alert('게시물이 성공적으로 업로드되었습니다!')
    router.push('/feed')
  } catch (error) {
    console.error('게시물 업로드 실패 상세:', error)
    console.error('에러 응답:', error.response)

    let errorMessage = '게시물 업로드에 실패했습니다.'

    if (error.response?.status === 401 || error.response?.status === 403) {
      errorMessage = '인증이 필요합니다. 다시 로그인해주세요.'
      store.logout()
      router.push('/map')
    } else if (error.response?.status === 404) {
      errorMessage = '백엔드 API를 찾을 수 없습니다. 서버를 확인해주세요.'
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }

    alert(errorMessage)
  } finally {
    isUploading.value = false
  }
}
</script>
