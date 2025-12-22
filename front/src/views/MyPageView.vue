<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">My Page</h1>

        <div v-if="store.currentUser" class="space-y-8">
          <!-- Profile Section -->
          <div class="bg-card border border-border rounded-lg p-6">
            <h2 class="text-xl font-bold mb-4">Profile</h2>
            <div class="flex items-start gap-6">
              <img
                :src="userProfileImage"
                :alt="store.currentUser.nickname"
                class="w-24 h-24 rounded-full object-cover"
              />
              <div class="flex-1">
                <h3 class="text-lg font-semibold mb-2">{{ store.currentUser.nickname }}</h3>
                <p class="text-foreground/80 mb-4">{{ store.currentUser.bio }}</p>
                <div class="flex gap-6 text-sm">
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
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { postAPI } from '@/api/api'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'

const store = useAppStore()

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
  // 페이지 로드 시 게시물 불러오기
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
</script>
