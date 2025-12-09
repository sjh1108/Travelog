<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <div v-if="post" class="bg-card border border-border rounded-lg overflow-hidden">
          <div class="grid md:grid-cols-2 gap-0">
            <!-- Image -->
            <div class="relative">
              <img
                :src="post.imageUrl || '/placeholder.svg'"
                :alt="post.travelLocation"
                class="w-full h-full object-cover"
              />
            </div>

            <!-- Content -->
            <div class="flex flex-col">
              <!-- Header -->
              <div class="flex items-center gap-3 p-4 border-b border-border">
                <router-link :to="`/profile/${post.userId}`">
                  <img
                    :src="post.user.profileImage || '/placeholder.svg'"
                    :alt="post.user.nickname"
                    class="w-10 h-10 rounded-full object-cover"
                  />
                </router-link>
                <div>
                  <router-link :to="`/profile/${post.userId}`">
                    <h3 class="font-semibold hover:text-primary transition-colors">
                      {{ post.user.nickname }}
                    </h3>
                  </router-link>
                  <p class="text-xs text-foreground/50">{{ post.travelLocation }}</p>
                </div>
              </div>

              <!-- Comments Section -->
              <div class="flex-1 overflow-y-auto p-4 space-y-4">
                <!-- Caption -->
                <div class="flex gap-3">
                  <img
                    :src="post.user.profileImage || '/placeholder.svg'"
                    :alt="post.user.nickname"
                    class="w-8 h-8 rounded-full"
                  />
                  <div>
                    <p class="text-sm">
                      <span class="font-semibold">{{ post.user.nickname }}</span>
                      <span class="ml-2 text-foreground/80">{{ post.caption }}</span>
                    </p>
                    <p class="text-xs text-foreground/50 mt-1">
                      {{ formatDate(post.createdAt) }}
                    </p>
                  </div>
                </div>

                <!-- Comments -->
                <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="flex gap-3"
                >
                  <img
                    :src="comment.user.profileImage || '/placeholder.svg'"
                    :alt="comment.user.nickname"
                    class="w-8 h-8 rounded-full"
                  />
                  <div>
                    <p class="text-sm">
                      <span class="font-semibold">{{ comment.user.nickname }}</span>
                      <span class="ml-2 text-foreground/80">{{ comment.content }}</span>
                    </p>
                    <p class="text-xs text-foreground/50 mt-1">
                      {{ formatDate(comment.createdAt) }}
                    </p>
                  </div>
                </div>
              </div>

              <!-- Actions -->
              <div class="border-t border-border p-4 space-y-3">
                <div class="flex items-center gap-4">
                  <LikeButton :post-id="post.id" :initial-like-count="post.likeCount" />
                  <button class="flex items-center gap-1 text-foreground/60 hover:text-primary transition-colors">
                    <MessageCircle class="h-6 w-6" />
                    <span class="text-sm">{{ comments.length }}</span>
                  </button>
                </div>

                <!-- Comment Input -->
                <div class="flex gap-2">
                  <input
                    v-model="newComment"
                    type="text"
                    placeholder="Add a comment..."
                    class="flex-1 px-4 py-2 bg-background border border-border rounded-full outline-none focus:ring-2 ring-primary/20"
                    @keyup.enter="handleAddComment"
                  />
                  <button
                    @click="handleAddComment"
                    :disabled="!newComment.trim()"
                    class="px-4 py-2 bg-primary text-primary-foreground rounded-full font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 transition-opacity"
                  >
                    Post
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-20">
          <p class="text-foreground/50">Post not found</p>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { MessageCircle } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import LikeButton from '@/components/LikeButton.vue'
import { useAppStore } from '@/stores/app'
import { dummyComments } from '@/data/dummy-data'

const route = useRoute()
const store = useAppStore()

const postId = route.params.id
const newComment = ref('')

const post = computed(() => store.posts.find((p) => p.id === postId))
const comments = computed(() => store.getComments(postId))

onMounted(() => {
  // Load dummy comments if available
  if (dummyComments[postId]) {
    dummyComments[postId].forEach(comment => {
      if (!comments.value.find(c => c.id === comment.id)) {
        store.addComment(postId, comment)
      }
    })
  }
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

const handleAddComment = () => {
  if (!newComment.value.trim() || !store.currentUser || !post.value) return

  const comment = {
    id: `comment-${Date.now()}`,
    postId: post.value.id,
    userId: store.currentUser.id,
    user: store.currentUser,
    content: newComment.value.trim(),
    createdAt: new Date().toISOString(),
  }

  store.addComment(post.value.id, comment)
  newComment.value = ''
}
</script>
