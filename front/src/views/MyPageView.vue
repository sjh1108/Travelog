<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">My Page</h1>

        <div v-if="store.currentUser" class="space-y-8">
          <!-- Profile Section -->
          <div class="bg-card border border-border rounded-lg p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold">Profile</h2>
              <button
                @click="openEditModal"
                class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity font-semibold"
              >
                회원정보 수정
              </button>
            </div>
            <div class="flex items-start gap-6">
              <img
                :src="userProfileImage"
                :alt="store.currentUser.nickname"
                class="w-24 h-24 rounded-full object-cover"
              />
              <div class="flex-1">
                <h3 class="text-lg font-semibold mb-2">{{ store.currentUser.nickname }}</h3>
                <p class="text-foreground/80 mb-4">
                  {{ store.currentUser.bio || (store.currentUser.nickname + '입니다.') }}
                </p>
                <div class="flex gap-6 text-sm mb-4">
                  <div>
                    <span class="font-bold">{{ myPosts.length }}</span>
                    <span class="text-foreground/60 ml-1">posts</span>
                  </div>
                  <div>
                    <span class="font-bold">{{ store.currentUser.followerCount }}</span>
                    <span class="text-foreground/60 ml-1">followers</span>
                  </div>
                  <div>
                    <span class="font-bold">{{ store.currentUser.followingCount }}</span>
                    <span class="text-foreground/60 ml-1">following</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- My Posts -->
          <div class="bg-card border border-border rounded-lg p-6">
            <h2 class="text-xl font-bold mb-4">My Posts</h2>
            <div v-if="myPosts.length === 0" class="text-center py-12 text-foreground/50">
              No posts yet. Start sharing your travel photos!
            </div>
            <div v-else class="grid grid-cols-3 gap-2">
              <router-link
                v-for="post in myPosts"
                :key="post.id"
                :to="`/post/${post.id}`"
                class="aspect-square"
              >
                <img
                  :src="getImageUrl(post.imageUrl)"
                  :alt="post.travelLocation"
                  class="w-full h-full object-cover rounded-lg hover:opacity-90 transition-opacity"
                />
              </router-link>
            </div>
          </div>

          <!-- Travel Stats -->
          <div class="bg-card border border-border rounded-lg p-6">
            <h2 class="text-xl font-bold mb-4">Travel Statistics</h2>
            <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div class="text-center p-4 bg-background rounded-lg">
                <p class="text-3xl font-bold text-primary">{{ myPosts.length }}</p>
                <p class="text-sm text-foreground/60 mt-1">Photos Shared</p>
              </div>
              <div class="text-center p-4 bg-background rounded-lg">
                <p class="text-3xl font-bold text-primary">{{ store.travelLogs.length }}</p>
                <p class="text-sm text-foreground/60 mt-1">Places Visited</p>
              </div>
              <div class="text-center p-4 bg-background rounded-lg">
                <p class="text-3xl font-bold text-primary">{{ totalLikes }}</p>
                <p class="text-sm text-foreground/60 mt-1">Total Likes</p>
              </div>
              <div class="text-center p-4 bg-background rounded-lg">
                <p class="text-3xl font-bold text-primary">{{ totalComments }}</p>
                <p class="text-sm text-foreground/60 mt-1">Total Comments</p>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-20">
          <p class="text-foreground/50">Please log in to view your page</p>
        </div>
      </div>
    </main>
    <Footer />

    <!-- 회원정보 수정 모달 -->
    <div
      v-if="showEditModal"
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      @click="showEditModal = false"
    >
      <div
        class="bg-background rounded-lg max-w-md w-full p-6"
        @click.stop
      >
        <div class="flex items-center justify-between mb-6">
          <h2 class="text-xl font-bold">회원정보 수정</h2>
          <button
            @click="showEditModal = false"
            class="text-foreground/60 hover:text-foreground transition-colors"
          >
            <X class="h-6 w-6" />
          </button>
        </div>

        <!-- 프로필 이미지 -->
        <div class="mb-4">
          <label class="block text-sm font-semibold mb-2">프로필 이미지</label>

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

          <!-- 파일 선택 영역 (선택되지 않은 경우에만 표시) -->
          <div
            v-else
            @click="$refs.profileImageInput?.click()"
            class="relative border-2 border-dashed border-border rounded-lg p-4 text-center cursor-pointer transition-all hover:border-primary/50 hover:bg-primary/5"
          >
            <input
              ref="profileImageInput"
              type="file"
              accept="image/*"
              @change="handleProfileImageChange"
              class="hidden"
            />
            <!-- 현재 프로필 이미지가 있으면 작게 표시 -->
            <div v-if="store.currentUser?.profileImage" class="mb-2 flex justify-center">
              <img
                :src="userProfileImage"
                alt="현재 프로필"
                class="w-16 h-16 rounded-full object-cover opacity-50"
              />
            </div>
            <p class="text-sm text-foreground/60">
              클릭하여 이미지 선택
            </p>
            <p class="text-xs text-foreground/40 mt-1">
              JPG, PNG, GIF
            </p>
          </div>
        </div>

        <!-- 닉네임 -->
        <div class="mb-4">
          <label class="block text-sm font-semibold mb-2">닉네임</label>
          <input
            v-model="editForm.nickname"
            type="text"
            class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20"
            placeholder="닉네임을 입력하세요"
          />
        </div>

        <!-- 소개 -->
        <div class="mb-6">
          <label class="block text-sm font-semibold mb-2">소개</label>
          <textarea
            v-model="editForm.bio"
            rows="3"
            class="w-full px-4 py-2 bg-background border border-border rounded-lg outline-none focus:ring-2 ring-primary/20 resize-none"
            placeholder="자기소개를 입력하세요"
          ></textarea>
        </div>

        <!-- 저장 버튼 -->
        <div class="flex gap-3 mb-4">
          <button
            @click="showEditModal = false"
            class="flex-1 px-4 py-2 bg-background border border-border rounded-lg hover:bg-foreground/5 transition-colors"
          >
            취소
          </button>
          <button
            @click="handleUpdateProfile"
            :disabled="isUpdating"
            class="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ isUpdating ? '저장 중...' : '저장' }}
          </button>
        </div>

        <!-- 회원탈퇴 버튼 (모달 하단) -->
        <div class="pt-4 border-t border-border">
          <button
            @click="handleDeleteButtonClick"
            class="w-full px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
          >
            회원탈퇴
          </button>
        </div>
      </div>
    </div>

    <!-- 회원탈퇴 확인 모달 -->
    <div
      v-if="showDeleteConfirmModal"
      class="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      @click="showDeleteConfirmModal = false"
    >
      <div
        class="bg-background rounded-lg max-w-md w-full p-6"
        @click.stop
      >
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold">회원탈퇴</h2>
          <button
            @click="showDeleteConfirmModal = false"
            class="text-foreground/60 hover:text-foreground transition-colors"
          >
            <X class="h-6 w-6" />
          </button>
        </div>
        <p class="text-foreground/80 mb-6">
          정말로 탈퇴하시겠습니까?<br />
          탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.
        </p>
        <div class="flex gap-3 justify-end">
          <button
            @click="showDeleteConfirmModal = false"
            class="px-4 py-2 bg-background border border-border rounded-lg hover:bg-foreground/5 transition-colors"
          >
            취소
          </button>
          <button
            @click="handleDeleteAccount"
            :disabled="isDeleting"
            class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors disabled:opacity-50"
          >
            {{ isDeleting ? '삭제 중...' : '탈퇴하기' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { X } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { postAPI, userAPI, fileAPI } from '@/api/api'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'
import axios from 'axios'

const store = useAppStore()
const router = useRouter()

// 회원정보 수정 관련 상태
const showEditModal = ref(false)
const isUpdating = ref(false)
const editForm = ref({
  nickname: '',
  bio: '',
  profileImage: null
})
const previewUrl = ref('')
const profileImageInput = ref(null)

// 회원탈퇴 관련 상태
const showDeleteConfirmModal = ref(false)
const isDeleting = ref(false)

// 회원정보 수정 모달 열 때 현재 정보로 초기화
const openEditModal = () => {
  if (store.currentUser) {
    editForm.value = {
      nickname: store.currentUser.nickname || '',
      bio: store.currentUser.bio || '',
      profileImage: null
    }
    previewUrl.value = '' // 미리보기 초기화
    showEditModal.value = true
  }
}

// 프로필 이미지 변경 처리
const handleProfileImageChange = (event) => {
  const file = event.target.files?.[0]
  if (file && file.type.startsWith('image/')) {
    editForm.value.profileImage = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

// 프로필 이미지 제거
const clearProfileImage = () => {
  editForm.value.profileImage = null
  previewUrl.value = ''
  if (profileImageInput.value) {
    profileImageInput.value.value = ''
  }
}

// 회원정보 수정 처리
const handleUpdateProfile = async () => {
  isUpdating.value = true

  try {
    let profileImageUrl = store.currentUser.profileImage

    // 1. 프로필 이미지가 변경된 경우 먼저 업로드
    if (editForm.value.profileImage) {
      console.log('프로필 이미지 업로드 중...')
      profileImageUrl = await fileAPI.uploadImage(editForm.value.profileImage)
      console.log('업로드된 이미지 URL:', profileImageUrl)
    }

    // 2. 회원정보 수정 API 호출
    const updateData = {
      nickname: editForm.value.nickname,
      bio: editForm.value.bio,
      profileImage: profileImageUrl
    }

    console.log('회원정보 수정 요청:', updateData)
    const response = await userAPI.updateProfile(updateData)
    console.log('회원정보 수정 성공:', response)

    // 3. 스토어의 현재 사용자 정보 업데이트
    const updatedUser = {
      ...store.currentUser,
      nickname: response.nickname,
      bio: response.bio,
      profileImage: response.profileImage
    }
    store.setCurrentUser(updatedUser)

    // 4. localStorage도 업데이트
    localStorage.setItem('user', JSON.stringify(updatedUser))

    alert('회원정보가 수정되었습니다.')
    showEditModal.value = false

    // 5. 게시물 목록 새로고침 (프로필 이미지/닉네임 반영)
    await fetchPosts()
  } catch (error) {
    console.error('회원정보 수정 실패:', error)
    if (error.response?.data) {
      alert(error.response.data)
    } else {
      alert('회원정보 수정에 실패했습니다.')
    }
  } finally {
    isUpdating.value = false
  }
}

// 회원탈퇴 버튼 클릭 (수정 모달에서)
const handleDeleteButtonClick = () => {
  showEditModal.value = false
  showDeleteConfirmModal.value = true
}

// 회원탈퇴 처리
const handleDeleteAccount = async () => {
  isDeleting.value = true

  try {
    // 백엔드 API 호출 (회원 삭제)
    await axios.delete('/api/users/me', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })

    // 로컬 스토리지 정리
    localStorage.removeItem('token')
    localStorage.removeItem('user')

    // 스토어 상태 초기화
    store.logout()

    alert('회원탈퇴가 완료되었습니다.')

    // 홈으로 이동
    router.push('/')
  } catch (error) {
    console.error('회원탈퇴 실패:', error)
    alert('회원탈퇴에 실패했습니다. 다시 시도해주세요.')
  } finally {
    isDeleting.value = false
    showDeleteConfirmModal.value = false
  }
}

// 사용자 정보 갱신
const fetchUserInfo = async () => {
  try {
    console.log('MyPage - 사용자 정보 조회 시작...')
    const userData = await userAPI.getMyInfo()
    console.log('MyPage - 받은 사용자 데이터:', userData)

    // 스토어 업데이트
    const updatedUser = {
      ...store.currentUser,
      ...userData
    }
    store.setCurrentUser(updatedUser)
    localStorage.setItem('user', JSON.stringify(updatedUser))
  } catch (error) {
    console.error('MyPage - 사용자 정보 조회 실패:', error)
  }
}

// 게시물 불러오기
const fetchPosts = async () => {
  try {
    console.log('MyPage - 게시물 조회 시작...')
    const data = await postAPI.getPosts()
    console.log('MyPage - 받은 게시물 데이터:', data)

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const postsWithUser = data.map(post => {
      // 이미 user 객체가 있으면 그대로 사용
      if (post.user) {
        return post
      }

      // nickname, profileImage가 있으면 user 객체로 변환
      if (post.nickname || post.profileImage || post.writerEmail) {
        return {
          ...post,
          userId: post.writerEmail,
          user: {
            id: post.writerEmail,
            email: post.writerEmail,
            nickname: post.nickname || 'Unknown User',
            profileImage: post.profileImage || '/default-profile.svg'
          }
        }
      }

      console.warn(`게시물 ${post.id}에 작성자 정보가 없습니다.`)
      return post
    })

    console.log('MyPage - 처리된 게시물:', postsWithUser)
    store.setPosts(postsWithUser)
  } catch (error) {
    console.error('MyPage - 게시물 조회 실패:', error)
    console.error('MyPage - 에러 상세:', error.response)
    store.setPosts([])
  }
}

onMounted(async () => {
  // 페이지 로드 시 사용자 정보와 게시물 불러오기
  await fetchUserInfo()
  await fetchPosts()
})

const myPosts = computed(() => {
  if (!store.currentUser) return []

  const currentUserId = store.currentUser.id || store.currentUser.email

  console.log('MyPage - currentUser:', store.currentUser)
  console.log('MyPage - currentUserId:', currentUserId)
  console.log('MyPage - 전체 게시물:', store.posts)

  const filtered = store.posts.filter(post => {
    const match = post.userId === currentUserId ||
           post.writerEmail === currentUserId ||
           post.userId === store.currentUser.email ||
           post.writerEmail === store.currentUser.email

    if (match) {
      console.log('매칭된 게시물:', post)
    }

    return match
  })

  console.log('MyPage - 내 게시물:', filtered)
  return filtered
})

const totalLikes = computed(() => {
  return myPosts.value.reduce((sum, post) => sum + post.likeCount, 0)
})

const totalComments = computed(() => {
  return myPosts.value.reduce((sum, post) => sum + post.commentCount, 0)
})

// 이미지 URL 처리
const getImageUrl = getFullImageUrl
const userProfileImage = computed(() => getProfileImageUrl(store.currentUser?.profileImage))

// ESC 키로 모달 닫기
const handleEscKey = (event) => {
  if (event.key === 'Escape') {
    if (showDeleteConfirmModal.value) {
      showDeleteConfirmModal.value = false
    } else if (showEditModal.value) {
      showEditModal.value = false
    }
  }
}

// 모달 상태 변경 시 ESC 키 리스너 추가/제거
watch([showEditModal, showDeleteConfirmModal], ([editModal, deleteModal]) => {
  if (editModal || deleteModal) {
    window.addEventListener('keydown', handleEscKey)
  } else {
    window.removeEventListener('keydown', handleEscKey)
  }
})

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener('keydown', handleEscKey)
})
</script>
