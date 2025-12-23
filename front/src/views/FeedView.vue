<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-2xl mx-auto">
        <div v-if="store.posts.length === 0" class="flex flex-col items-center justify-center py-20 gap-4">
          <p class="text-foreground/50">
            {{ store.currentUser ? 'No posts yet. Start following users!' : 'Please log in to view posts.' }}
          </p>
          <button
            v-if="!store.currentUser"
            @click="store.showLoginModal = true"
            class="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-opacity font-semibold"
          >
            Log In
          </button>
        </div>
        <div v-else class="space-y-4">
          <PostCard
            v-for="post in store.posts"
            :key="post.id"
            :post="post"
            :on-comment-click="handleCommentClick"
          />

          <!-- Infinite scroll trigger -->
          <div ref="observerTarget" class="py-8 flex justify-center">
            <div v-if="isLoading" class="flex flex-col items-center gap-2">
              <div class="h-8 w-8 animate-spin rounded-full border-4 border-border border-t-primary" />
              <p class="text-sm text-foreground/50">Loading more posts...</p>
            </div>
          </div>
        </div>
      </div>
    </main>
    <Footer />

    <!-- Comment Modal -->
    <CommentModal
      v-if="selectedPost"
      :post="selectedPost"
      :is-open="!!selectedPostId && !!selectedPost"
      :on-close="() => setSelectedPostId(null)"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import PostCard from '@/components/PostCard.vue'
import CommentModal from '@/components/CommentModal.vue'
import { useAppStore } from '@/stores/app'
import { postAPI } from '@/api/api'

const store = useAppStore()
const selectedPostId = ref(null)
const isLoading = ref(false)
const observerTarget = ref(null)
let observer = null

// Fetch posts from API
const fetchPosts = async () => {
  try {
    isLoading.value = true
    console.log('게시물 조회 시작...')
    const data = await postAPI.getPosts()
    console.log('받은 게시물 데이터:', data)

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const postsWithUser = data.map(post => {
      console.log('게시물 원본 데이터:', post)
      console.log('isLiked:', post.isLiked, 'likeCount:', post.likeCount)

      // 이미 user 객체가 있으면 그대로 사용
      if (post.user) {
        return post
      }

      // nickname, profileImage가 있으면 user 객체로 변환
      const email = post.writerEmail || post.userEmail
      if (post.nickname || post.profileImage || email) {
        return {
          ...post,
          userId: email, // userId가 없으면 writerEmail/userEmail 사용
          user: {
            id: email,
            email: email,
            nickname: post.nickname || 'Unknown User',
            profileImage: post.profileImage || '/default-profile.svg'
          }
        }
      }

      console.warn(`게시물 ${post.id}에 작성자 정보가 없습니다.`)
      return post
    })

    console.log('처리된 게시물:', postsWithUser)
    store.setPosts(postsWithUser)
  } catch (error) {
    console.error('게시물 조회 실패:', error)
    console.error('에러 상세:', error.response)

    // 403 에러 (인증 필요) 또는 401 에러 (로그인 필요) 시 빈 배열 설정
    if (error.response?.status === 403 || error.response?.status === 401) {
      console.log('로그인이 필요합니다. 빈 피드를 표시합니다.')
      store.setPosts([])

      // 로그인하지 않은 상태임을 사용자에게 알림
      if (!store.currentUser) {
        console.log('로그인하면 게시물을 볼 수 있습니다.')
      }
    } else {
      // 다른 에러의 경우 빈 배열 설정
      store.setPosts([])
    }
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  // Load posts from API
  await fetchPosts()

  // Setup Intersection Observer for infinite scroll
  observer = new IntersectionObserver(
    (entries) => {
      if (entries[0].isIntersecting && !isLoading.value) {
        handleLoadMore()
      }
    },
    { threshold: 0.1 }
  )

  if (observerTarget.value) {
    observer.observe(observerTarget.value)
  }
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})

const handleLoadMore = async () => {
  // 무한 스크롤 기능은 백엔드에서 페이지네이션을 지원할 때 구현
  // 현재는 전체 게시물만 불러옴
  console.log('무한 스크롤 - 추가 게시물 로드는 백엔드 페이지네이션 지원 필요')
}

const handleCommentClick = (postId) => {
  selectedPostId.value = postId
}

const setSelectedPostId = (postId) => {
  selectedPostId.value = postId
}

const selectedPost = computed(() => {
  return store.posts.find((p) => p.id === selectedPostId.value)
})
</script>
