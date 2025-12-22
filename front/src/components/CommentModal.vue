<template>
  <Transition name="modal">
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
              :src="getImageUrl(post.user?.profileImage)"
              :alt="post.user?.nickname || 'User'"
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
              class="flex items-start gap-3 group"
            >
              <img
                :src="getImageUrl(comment.user?.profileImage)"
                :alt="comment.user?.nickname || 'User'"
                class="w-8 h-8 rounded-full"
              />
              <div class="flex-1">
                <div class="flex items-start justify-between">
                  <p class="text-sm flex-1">
                    <span class="font-semibold">{{ comment.user.nickname }}</span>
                    <span class="ml-2 text-foreground/80">{{ comment.content }}</span>
                  </p>
                  <button
                    v-if="canDeleteComment(comment)"
                    @click="handleDeleteComment(comment.id)"
                    class="text-red-500/60 hover:text-red-500 text-xs ml-2 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    Delete
                  </button>
                </div>
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
              @click="() => { console.log('버튼 클릭됨!'); handleAddComment(); }"
              :disabled="!newComment.trim()"
              class="px-4 py-2 bg-primary text-primary-foreground rounded-full font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:opacity-90 transition-opacity"
            >
              Post
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { X } from 'lucide-vue-next'
import { useAppStore } from '@/stores/app'
import { commentAPI } from '@/api/api'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'

const props = defineProps({
  post: Object,
  isOpen: Boolean,
  onClose: Function
})

const store = useAppStore()
const newComment = ref('')
const comments = ref([])
const isLoading = ref(false)

// 이미지 URL 처리 함수를 템플릿에서 사용 가능하도록 노출 (프로필 이미지용)
const getImageUrl = getProfileImageUrl

// 댓글 불러오기
const fetchComments = async () => {
  if (!props.post?.id) return

  try {
    isLoading.value = true
    const data = await commentAPI.getComments(props.post.id)

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const commentsWithUser = data.map(comment => {
      // 이미 user 객체가 있으면 그대로 사용
      if (comment.user) {
        return comment
      }

      // nickname, profileImage, writerEmail이 있으면 user 객체로 변환
      if (comment.nickname || comment.profileImage || comment.writerEmail) {
        return {
          ...comment,
          userId: comment.writerEmail || comment.userId,
          user: {
            id: comment.writerEmail || comment.userId,
            email: comment.writerEmail || comment.userId,
            nickname: comment.nickname || 'Unknown User',
            profileImage: comment.profileImage || '/default-profile.svg'
          }
        }
      }

      return comment
    })

    comments.value = commentsWithUser
  } catch (error) {
    console.error('댓글 조회 실패:', error)
    console.error('에러 상세:', error.response)
    comments.value = []
  } finally {
    isLoading.value = false
  }
}

// 모달이 열릴 때 댓글 불러오기
watch(() => props.isOpen, (isOpen) => {
  if (isOpen) {
    fetchComments()
  }
})

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

const handleAddComment = async () => {
  if (!newComment.value.trim() || !store.currentUser) {
    console.log('댓글 작성 불가:', { hasComment: !!newComment.value.trim(), hasUser: !!store.currentUser })
    return
  }

  try {
    const commentData = {
      postId: props.post.id,
      content: newComment.value.trim(),
    }

    console.log('댓글 작성 요청 데이터:', commentData)
    const newCommentData = await commentAPI.createComment(commentData)
    console.log('댓글 작성 응답:', newCommentData)

    // 댓글 목록 다시 불러오기
    await fetchComments()

    newComment.value = ''
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    console.error('에러 응답:', error.response)
  }
}

// 댓글 삭제 권한 확인 (본인이 작성한 댓글만 삭제 가능)
const canDeleteComment = (comment) => {
  if (!store.currentUser) return false

  const currentUserId = store.currentUser.id || store.currentUser.email
  const commentUserId = comment.user?.id || comment.user?.email || comment.userId || comment.writerEmail

  return currentUserId === commentUserId
}

// 댓글 삭제
const handleDeleteComment = async (commentId) => {
  if (!confirm('Are you sure you want to delete this comment?')) return

  try {
    await commentAPI.deleteComment(commentId)

    // 로컬 댓글 목록에서 제거
    comments.value = comments.value.filter(c => c.id !== commentId)
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
  }
}
</script>

<style scoped>
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
</style>
