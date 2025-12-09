<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-2xl mx-auto">
        <div v-if="store.posts.length === 0" class="flex items-center justify-center py-20">
          <p class="text-foreground/50">No posts yet. Start following users!</p>
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
      :is-open="!!selectedPostId"
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
import { dummyPosts, dummyUsers } from '@/data/dummy-data'

const store = useAppStore()
const selectedPostId = ref(null)
const isLoading = ref(false)
const observerTarget = ref(null)
let observer = null

// Initialize demo data
onMounted(() => {
  if (store.posts.length === 0) {
    store.setPosts(dummyPosts)
    store.setCurrentUser(dummyUsers['user-1'])
  }

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

const handleLoadMore = () => {
  isLoading.value = true
  // Simulate API call delay
  setTimeout(() => {
    const newPosts = dummyPosts.map((post, index) => ({
      ...post,
      id: `post-${Date.now()}-${index}`,
    }))
    store.setPosts([...store.posts, ...newPosts])
    isLoading.value = false
  }, 600)
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
