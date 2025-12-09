<template>
  <Teleport to="body">
    <div
      v-if="isOpen"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click="onClose"
    >
      <div
        class="bg-background rounded-lg max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col"
        @click.stop
      >
        <!-- Header -->
        <div class="flex items-center justify-between p-4 border-b border-border">
          <h2 class="text-lg font-semibold">Comments</h2>
          <button
            @click="onClose"
            class="text-foreground/60 hover:text-foreground transition-colors"
          >
            <X class="h-6 w-6" />
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto p-4">
          <div class="flex items-start gap-3 mb-4">
            <img
              :src="post.user.profileImage || '/placeholder.svg'"
              :alt="post.user.nickname"
              class="w-10 h-10 rounded-full"
            />
            <div>
              <p class="font-semibold">{{ post.user.nickname }}</p>
              <p class="text-sm text-foreground/80">{{ post.caption }}</p>
            </div>
          </div>

          <div class="space-y-4">
            <div
              v-for="comment in comments"
              :key="comment.id"
              class="flex items-start gap-3"
            >
              <img
                :src="comment.user.profileImage || '/placeholder.svg'"
                :alt="comment.user.nickname"
                class="w-8 h-8 rounded-full"
              />
              <div class="flex-1">
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
        </div>

        <!-- Comment Input -->
        <div class="border-t border-border p-4">
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
  </Teleport>
</template>

<script setup>
import { ref, computed } from 'vue'
import { X } from 'lucide-vue-next'
import { useAppStore } from '@/stores/app'

const props = defineProps({
  post: Object,
  isOpen: Boolean,
  onClose: Function
})

const store = useAppStore()
const newComment = ref('')

const comments = computed(() => store.getComments(props.post.id))

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))
  
  if (hours < 24) {
    return `${hours}h ago`
  }
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

const handleAddComment = () => {
  if (!newComment.value.trim() || !store.currentUser) return

  const comment = {
    id: `comment-${Date.now()}`,
    postId: props.post.id,
    userId: store.currentUser.id,
    user: store.currentUser,
    content: newComment.value.trim(),
    createdAt: new Date().toISOString(),
  }

  store.addComment(props.post.id, comment)
  newComment.value = ''
}
</script>
