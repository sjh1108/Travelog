<template>
  <nav class="sticky top-0 z-50 border-b border-border bg-background shadow">
    <div class="mx-auto max-w-6xl px-4 py-3">
      <div class="flex items-center justify-between gap-4">
        <!-- Logo -->
        <router-link to="/map" class="flex items-center gap-2">
          <MapPin class="h-7 w-7 text-primary" />
          <span class="text-2xl font-bold text-foreground">Travelog</span>
        </router-link>

        <!-- Right Menu -->
        <div class="flex items-center gap-6">
          <!-- 로그인 상태일 때 -->
          <template v-if="store.isLoggedIn">
            <router-link
              to="/upload"
              class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
              title="사진 업로드"
            >
              <Upload class="h-6 w-6" />
              <span class="hidden sm:inline">업로드</span>
            </router-link>

            <router-link
              to="/feed"
              class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
              title="피드"
            >
              <Home class="h-6 w-6" />
              <span class="hidden sm:inline">피드</span>
            </router-link>

            <!-- 알림 드롭다운 -->
            <div class="relative notification-dropdown">
              <button
                @click="toggleNotifications"
                class="relative text-foreground transition-colors hover:text-primary"
                title="알림"
              >
                <Bell class="h-6 w-6" />
                <span
                  v-if="unreadCount > 0"
                  class="absolute -right-2 -top-2 flex h-5 w-5 items-center justify-center rounded-full bg-red-500 text-white text-xs font-bold"
                >
                  {{ unreadCount > 9 ? '9+' : unreadCount }}
                </span>
              </button>

              <!-- 알림 드롭다운 메뉴 -->
              <div
                v-if="showNotifications"
                class="absolute right-0 mt-2 w-96 bg-background border border-border rounded-lg shadow-lg z-50"
              >
                <!-- 헤더 -->
                <div class="flex items-center justify-between p-4 border-b border-border">
                  <h3 class="font-semibold text-foreground">알림</h3>
                  <button
                    v-if="notifications.length > 0"
                    @click="markAllAsRead"
                    class="text-xs text-primary hover:underline"
                  >
                    모두 읽음
                  </button>
                </div>

                <!-- 알림 목록 -->
                <div v-if="notifications.length === 0" class="p-8 text-center text-foreground/50">
                  알림이 없습니다
                </div>
                <div v-else class="divide-y divide-border max-h-[450px] overflow-y-auto">
                  <div
                    v-for="notification in notifications"
                    :key="notification.id"
                    @click="handleNotificationClick(notification)"
                    :class="[
                      'p-4 hover:bg-muted cursor-pointer transition-colors',
                      !notification.isRead ? 'bg-primary/5' : ''
                    ]"
                  >
                    <div class="flex items-start gap-3">
                      <img
                        :src="getProfileImageUrl(notification.actorProfileImage)"
                        :alt="notification.actorNickname"
                        class="w-10 h-10 rounded-full object-cover"
                      />
                      <div class="flex-1">
                        <p class="text-sm text-foreground">
                          <span class="font-semibold">{{ notification.actorNickname }}</span>
                          {{ getNotificationMessage(notification.type) }}
                        </p>
                        <p class="text-xs text-foreground/50 mt-1">
                          {{ formatNotificationTime(notification.createdAt) }}
                        </p>
                      </div>
                      <div
                        v-if="!notification.isRead"
                        class="w-2 h-2 rounded-full bg-primary mt-2"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <router-link
              to="/mypage"
              class="flex items-center gap-2 text-foreground transition-colors hover:opacity-80"
              title="마이페이지"
            >
              <img
                :src="userProfileImage"
                :alt="store.currentUser?.nickname || '사용자'"
                class="h-8 w-8 rounded-full object-cover border-2 border-border hover:border-primary transition-colors"
              />
              <span class="hidden sm:inline hover:text-primary transition-colors">프로필</span>
            </router-link>

            <button
              @click="handleLogout"
              class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
              title="로그아웃"
            >
              <LogOut class="h-6 w-6" />
              <span class="hidden sm:inline">로그아웃</span>
            </button>
          </template>

          <!-- 로그아웃 상태일 때 -->
          <template v-else>
            <router-link
              to="/feed"
              class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
              title="피드"
            >
              <Home class="h-6 w-6" />
              <span class="hidden sm:inline">피드</span>
            </router-link>

            <button
              @click="store.showLoginModal = true"
              class="flex items-center gap-2 text-foreground transition-colors hover:text-primary"
              title="로그인"
            >
              <LogIn class="h-6 w-6" />
              <span class="hidden sm:inline">로그인</span>
            </button>

            <button
              @click="showRegisterModal = true"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              회원가입
            </button>
          </template>
        </div>
      </div>
    </div>

    <!-- 로그인 모달 -->
    <LoginModal
      v-model="store.showLoginModal"
      @switchToRegister="switchToRegister"
    />

    <!-- 회원가입 모달 -->
    <RegisterModal
      v-model="showRegisterModal"
      @switchToLogin="switchToLogin"
    />
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Bell, MapPin, Upload, Home, LogOut, LogIn } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { getProfileImageUrl } from '@/utils/imageUtils'
import { notificationAPI } from '@/api/api'
import LoginModal from './LoginModal.vue'
import RegisterModal from './RegisterModal.vue'

const router = useRouter()
const store = useAppStore()

const showRegisterModal = ref(false)
const showNotifications = ref(false)
const notifications = ref([])
const unreadCount = ref(0)

// 프로필 이미지
const userProfileImage = computed(() =>
  getProfileImageUrl(store.currentUser?.profileImage)
)

// 알림 조회
const fetchNotifications = async () => {
  if (!store.isLoggedIn) return

  try {
    const data = await notificationAPI.getNotifications()
    notifications.value = data
  } catch (error) {
    console.error('알림 조회 실패:', error)
  }
}

// 읽지 않은 알림 개수 조회
const fetchUnreadCount = async () => {
  if (!store.isLoggedIn) return

  try {
//     console.log('알림 개수 조회 시작...')
    const data = await notificationAPI.getUnreadCount()
//     console.log('알림 개수 조회 응답:', data)
    unreadCount.value = data.count || 0
//     console.log('읽지 않은 알림 개수:', unreadCount.value)
  } catch (error) {
    console.error('읽지 않은 알림 개수 조회 실패:', error)
    console.error('에러 상세:', error.response)
  }
}

// 알림 드롭다운 토글
const toggleNotifications = async () => {
  showNotifications.value = !showNotifications.value
  if (showNotifications.value) {
    await fetchNotifications()
  }
}

// 알림 클릭 처리
const handleNotificationClick = async (notification) => {
//   console.log('알림 클릭:', notification)
//   console.log('type:', notification.type)
//   console.log('postId:', notification.postId)

  // 읽지 않은 알림이면 읽음 처리 (실패해도 계속 진행)
  if (!notification.isRead) {
    try {
      await notificationAPI.markAsRead(notification.id)
      notification.isRead = true
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('알림 읽음 처리 실패 (무시하고 계속):', error)
    }
  }

  // 알림 드롭다운 닫기
  showNotifications.value = false

  // 알림 타입에 따라 다른 페이지로 이동
  try {
    if (notification.type === 'follow') {
      // 팔로우 알림: 팔로우한 사용자의 프로필로 이동
      // actorEmail을 사용자 ID로 사용 (백엔드에서 이메일로 사용자를 식별)
//       console.log('프로필로 이동:', `/profile/${notification.actorEmail}`)
      await router.push(`/profile/${notification.actorEmail}`)
    } else if (notification.postId) {
      // 좋아요/댓글 알림: 게시물로 이동
//       console.log('게시물로 이동:', `/post/${notification.postId}`)
      await router.push(`/post/${notification.postId}`)
    } else {
      console.warn('이동할 경로를 찾을 수 없습니다:', notification)
    }
  } catch (error) {
    console.error('라우터 이동 실패:', error)
  }
}

// 전체 읽음 처리
const markAllAsRead = async () => {
  try {
    await notificationAPI.markAllAsRead()
    notifications.value.forEach(n => n.isRead = true)
    unreadCount.value = 0
  } catch (error) {
    console.error('전체 읽음 처리 실패:', error)
  }
}

// 알림 메시지 생성
const getNotificationMessage = (type) => {
  switch (type) {
    case 'like':
      return '님이 게시물을 좋아합니다'
    case 'comment':
      return '님이 댓글을 남겼습니다'
    case 'follow':
      return '님이 팔로우했습니다'
    default:
      return '님이 반응했습니다'
  }
}

// 시간 포맷팅
const formatNotificationTime = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) return '방금 전'
  if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}분 전`
  if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}시간 전`
  if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}일 전`

  return date.toLocaleDateString('ko-KR', { month: 'long', day: 'numeric' })
}

const handleLogout = () => {
  store.logout()
  router.push('/')
}

const switchToRegister = () => {
  store.showLoginModal = false
  showRegisterModal.value = true
}

const switchToLogin = () => {
  showRegisterModal.value = false
  store.showLoginModal = true
}

// 로그인 상태 변경 시 알림 개수 조회
watch(() => store.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    fetchUnreadCount()
    // 30초마다 알림 개수 업데이트
    const interval = setInterval(() => {
      if (store.isLoggedIn) {
        fetchUnreadCount()
      } else {
        clearInterval(interval)
      }
    }, 30000)
  } else {
    notifications.value = []
    unreadCount.value = 0
  }
})

// 컴포넌트 마운트 시 알림 개수 조회 및 외부 클릭 이벤트 등록
onMounted(() => {
  if (store.isLoggedIn) {
    fetchUnreadCount()
  }

  // 외부 클릭 시 드롭다운 닫기
  const handleClickOutside = (event) => {
    // Bell 아이콘이나 드롭다운 내부 클릭이 아닌 경우에만 닫기
    if (!event.target.closest('.notification-dropdown') && showNotifications.value) {
      showNotifications.value = false
    }
  }

  document.addEventListener('click', handleClickOutside)
})
</script>
