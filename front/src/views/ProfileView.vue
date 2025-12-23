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
                  :class="[
                    'px-6 py-2 rounded-lg font-semibold transition-colors',
                    isFollowing
                      ? 'bg-secondary/20 text-secondary hover:bg-secondary/30'
                      : 'bg-primary text-primary-foreground hover:opacity-90'
                  ]"
                >
                  {{ isFollowing ? 'Following' : 'Follow' }}
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
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'

const route = useRoute()
const store = useAppStore()

const userId = route.params.id

// 본인 프로필인지 확인
const isOwnProfile = computed(() => {
  if (!store.currentUser) return false
  return userId === store.currentUser.id ||
         userId === store.currentUser.email ||
         userId === String(store.currentUser.id)
})

// 게시물에서 사용자 정보 추출 또는 현재 사용자 정보 사용
const user = computed(() => {
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

  console.warn('사용자 정보를 찾을 수 없습니다:', userId)
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

const handleFollow = () => {
  store.toggleFollow(userId)
}

// 이미지 URL 처리
const getImageUrl = getFullImageUrl
const userProfileImage = computed(() => getProfileImageUrl(user.value?.profileImage))
</script>
