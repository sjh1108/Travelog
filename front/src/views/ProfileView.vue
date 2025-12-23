<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <div v-if="user" class="space-y-6">
          <!-- Profile Header -->
          <div class="flex items-start gap-8">
            <img
              :src="userProfileImage"
              :alt="user.nickname || 'User'"
              class="w-32 h-32 rounded-full object-cover"
            />
            <div class="flex-1">
              <div class="flex items-center gap-4 mb-4">
                <h1 class="text-2xl font-bold">{{ user.nickname || 'Unknown User' }}</h1>
                <button
                  v-if="!isOwnProfile"
                  @click="handleFollow"
                  :disabled="isFollowLoading"
                  :class="[
                    'px-3 py-1 rounded-lg font-semibold transition-all duration-200',
                    isFollowing
                      ? 'bg-gray-200 text-gray-800 border-2 border-gray-300 hover:bg-gray-300'
                      : 'bg-blue-600 text-white border-2 border-blue-600 hover:bg-blue-700',
                    isFollowLoading ? 'opacity-50 cursor-not-allowed' : ''
                  ]"
                >
                  {{ isFollowLoading ? 'Loading...' : (isFollowing ? 'Following' : 'Follow') }}
                </button>
              </div>

              <div class="flex gap-6 mb-4">
                <div>
                  <span class="font-bold">{{ userPosts.length }}</span>
                  <span class="text-foreground/60 ml-1">posts</span>
                </div>
                <button @click="showFollowers = true">
                  <span class="font-bold">{{ user.followerCount || 0 }}</span>
                  <span class="text-foreground/60 ml-1">followers</span>
                </button>
                <button @click="showFollowing = true">
                  <span class="font-bold">{{ user.followingCount || 0 }}</span>
                  <span class="text-foreground/60 ml-1">following</span>
                </button>
              </div>

              <p class="text-foreground/80">
                {{ user.bio || 'No bio' }}
              </p>
            </div>
          </div>

          <!-- Photo Grid -->
          <div>
            <h2 class="text-xl font-bold mb-4">Photos</h2>
            <div v-if="userPosts.length === 0" class="text-center py-12 text-foreground/50">
              No posts yet
            </div>
            <div v-else class="grid grid-cols-3 gap-2">
              <router-link
                v-for="post in userPosts"
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
        </div>

        <div v-else class="text-center py-20">
          <p class="text-foreground/50">User not found</p>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'
import { followAPI, userAPI } from '@/api/api'

const route = useRoute()
const store = useAppStore()

const userId = route.params.id
const isFollowLoading = ref(false)
const userInfo = ref(null)

// 본인 프로필인지 확인
const isOwnProfile = computed(() => {
  if (!store.currentUser) return false
  return userId === store.currentUser.id ||
         userId === store.currentUser.email ||
         userId === String(store.currentUser.id)
})

// 사용자 정보 가져오기
const fetchUserInfo = async () => {
  try {
    // 본인 프로필이면 현재 사용자 정보 사용
    if (isOwnProfile.value) {
      userInfo.value = store.currentUser
      return
    }

    // 다른 사용자 정보 조회
    // console.log('사용자 정보 조회:', userId)
    const data = await userAPI.getUserInfo(userId)
    // console.log('받은 사용자 정보:', data)
    userInfo.value = data
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error)
    // API 실패 시 게시물에서 사용자 정보 추출
    const userPost = store.posts.find(post =>
      post.userId === userId ||
      post.writerEmail === userId ||
      post.user?.id === userId ||
      post.user?.email === userId
    )
    if (userPost?.user) {
      userInfo.value = userPost.user
    }
  }
}

// 게시물에서 사용자 정보 추출 또는 현재 사용자 정보 사용
const user = computed(() => {
  // API에서 가져온 사용자 정보가 있으면 사용
  if (userInfo.value) {
    return userInfo.value
  }

  // userId가 현재 사용자와 같으면 현재 사용자 정보 반환
  if (userId === store.currentUser?.id || userId === store.currentUser?.email) {
    return store.currentUser
  }

  // 게시물에서 해당 사용자의 정보 찾기
  const userPost = store.posts.find(post =>
    post.userId === userId ||
    post.writerEmail === userId ||
    post.user?.id === userId ||
    post.user?.email === userId
  )

  if (userPost?.user) {
    return userPost.user
  }

  // console.warn('사용자 정보를 찾을 수 없습니다:', userId)
  return null
})

// 해당 사용자의 게시물 필터링
const userPosts = computed(() => {
  return store.posts.filter(post =>
    post.userId === userId ||
    post.writerEmail === userId ||
    post.user?.id === userId ||
    post.user?.email === userId
  )
})

const showFollowers = ref(false)
const showFollowing = ref(false)

const isFollowing = computed(() => store.followingUsers.has(userId))

const handleFollow = async () => {
  if (isFollowLoading.value) return

  isFollowLoading.value = true
  try {
    // console.log('팔로우 토글 시작:', userId)
    const response = await followAPI.toggleFollow(userId)
    // console.log('팔로우 토글 응답:', response)

    // 로컬 스토어 상태 업데이트
    store.toggleFollow(userId)

    // 사용자 정보 업데이트 (팔로워 수)
    if (userInfo.value && response.followerCount !== undefined) {
      userInfo.value.followerCount = response.followerCount
    }

    // 본인의 팔로잉 수 업데이트
    if (store.currentUser && response.followingCount !== undefined) {
      const updatedUser = {
        ...store.currentUser,
        followingCount: response.followingCount
      }
      store.setCurrentUser(updatedUser)
      localStorage.setItem('user', JSON.stringify(updatedUser))
    }
  } catch (error) {
    console.error('팔로우 처리 실패:', error)
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.')
      store.showLoginModal = true
    } else {
      alert('팔로우 처리에 실패했습니다.')
    }
  } finally {
    isFollowLoading.value = false
  }
}

// 팔로우 상태 확인
const checkFollowStatus = async () => {
  if (!store.isLoggedIn || isOwnProfile.value) return

  try {
    const response = await followAPI.checkFollowStatus(userId)
    if (response.isFollowing) {
      const newFollowing = new Set(store.followingUsers)
      newFollowing.add(userId)
      store.followingUsers = newFollowing
    }
  } catch (error) {
    console.error('팔로우 상태 확인 실패:', error)
  }
}

// 컴포넌트 마운트 시 사용자 정보와 팔로우 상태 확인
onMounted(async () => {
  await fetchUserInfo()
  await checkFollowStatus()
})

// 이미지 URL 처리
const getImageUrl = getFullImageUrl
const userProfileImage = computed(() => getProfileImageUrl(user.value?.profileImage))
</script>
