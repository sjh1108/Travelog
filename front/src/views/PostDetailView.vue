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
                :src="fullImageUrl"
                :alt="post.travelLocation"
                class="w-full object-cover"
                style="max-height: 600px; min-height: 400px;"
              />
            </div>

            <!-- Content -->
            <div class="flex flex-col" style="max-height: 600px;">
              <!-- Header -->
              <div class="flex items-center gap-3 p-4 border-b border-border">
                <router-link :to="profileLink">
                  <img
                    :src="userProfileImage"
                    :alt="post.user?.nickname || 'User'"
                    class="w-10 h-10 rounded-full object-cover"
                  />
                </router-link>
                <div>
                  <router-link :to="profileLink">
                    <h3 class="font-semibold hover:text-primary transition-colors">
                      {{ post.user?.nickname || 'Unknown User' }}
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
                    :src="userProfileImage"
                    :alt="post.user?.nickname || 'User'"
                    class="w-8 h-8 rounded-full"
                  />
                  <div>
                    <p class="text-sm">
                      <span class="font-semibold">{{ post.user?.nickname || 'Unknown User' }}</span>
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
                    :src="getImageUrl(comment.user?.profileImage)"
                    :alt="comment.user?.nickname || 'User'"
                    class="w-8 h-8 rounded-full"
                  />
                  <div class="flex-1">
                    <div class="flex items-start justify-between gap-2">
                      <div class="flex-1">
                        <p class="text-sm">
                          <span class="font-semibold">{{ comment.user?.nickname || 'Unknown User' }}</span>
                          <span class="ml-2 text-foreground/80">{{ comment.content }}</span>
                        </p>
                        <p class="text-xs text-foreground/50 mt-1">
                          {{ formatDate(comment.createdAt) }}
                        </p>
                      </div>
                      <button
                        v-if="canDeleteComment(comment)"
                        @click="handleDeleteComment(comment.id)"
                        class="text-red-500/60 hover:text-red-500 text-xs px-2 py-1 rounded hover:bg-red-500/10 transition-all flex-shrink-0"
                      >
                        Delete
                      </button>
                    </div>
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

    <!-- 삭제 확인 모달 -->
    <ConfirmModal
      :is-open="showDeleteConfirm"
      title="댓글 삭제"
      message="정말로 이 댓글을 삭제하시겠습니까? 이 작업은 취소할 수 없습니다."
      confirm-text="삭제"
      cancel-text="취소"
      :danger="true"
      :on-confirm="confirmDelete"
      :on-cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { MessageCircle } from 'lucide-vue-next'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import LikeButton from '@/components/LikeButton.vue'
import ConfirmModal from '@/components/ConfirmModal.vue'
import { useAppStore } from '@/stores/app'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'
import { commentAPI, postAPI } from '@/api/api'

const route = useRoute()
const store = useAppStore()

const postId = route.params.id
const newComment = ref('')
const localComments = ref([]) // 로컬에 댓글 저장
const showDeleteConfirm = ref(false)
const commentToDelete = ref(null)

// 게시물 불러오기
const fetchPosts = async () => {
  try {
    const data = await postAPI.getPosts()

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const postsWithUser = data.map(post => {
      if (post.user) {
        return post
      }

      const email = post.writerEmail || post.userEmail
      if (post.nickname || post.profileImage || email) {
        return {
          ...post,
          userId: email,
          user: {
            id: email,
            email: email,
            nickname: post.nickname || 'Unknown User',
            profileImage: post.profileImage || '/default-profile.svg'
          }
        }
      }

      return post
    })

    store.setPosts(postsWithUser)
  } catch (error) {
    console.error('게시물 조회 실패:', error)
    store.setPosts([])
  }
}

// ID 타입 변환 (문자열 또는 숫자 모두 지원)
const post = computed(() => {
  return store.posts.find((p) => {
    return p.id === postId || p.id === Number(postId) || String(p.id) === postId
  })
})
const comments = computed(() => localComments.value)

// 프로필 링크 계산 (현재 사용자면 /mypage, 아니면 /profile/:id)
const profileLink = computed(() => {
  if (!post.value) return '/feed'

  const postUserId = post.value.userId || post.value.writerEmail || post.value.user?.id || post.value.user?.email
  const currentUserId = store.currentUser?.id || store.currentUser?.email

  // postUserId가 없으면 기본값 반환
  if (!postUserId) {
    return '/feed'
  }

  // 현재 로그인된 사용자와 게시물 작성자가 같으면 /mypage로 이동
  if (currentUserId && (postUserId === currentUserId ||
      postUserId === store.currentUser?.email ||
      postUserId === store.currentUser?.id)) {
    return '/mypage'
  }

  // 다른 사용자면 프로필 페이지로 이동
  return `/profile/${postUserId}`
})

// 이미지 URL 처리
const fullImageUrl = computed(() => post.value ? getFullImageUrl(post.value.imageUrl) : '/placeholder.svg')
const userProfileImage = computed(() => post.value ? getProfileImageUrl(post.value.user?.profileImage) : '/default-profile.svg')

// 템플릿에서 사용할 수 있도록 노출 (댓글 작성자 프로필 이미지용)
const getImageUrl = getProfileImageUrl

onMounted(async () => {
  // Load posts first, then comments
  await fetchPosts()
  await fetchComments()
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 댓글 목록 다시 불러오기
const fetchComments = async () => {
  if (!post.value?.id) return

  try {
    const data = await commentAPI.getComments(post.value.id)

    // 백엔드 응답을 프론트엔드 형식으로 변환
    const commentsWithUser = data.map(comment => {
      if (comment.user) return comment

      const email = comment.writerEmail || comment.userEmail
      if (comment.nickname || comment.profileImage || email) {
        return {
          ...comment,
          userId: email || comment.userId,
          user: {
            id: email || comment.userId,
            email: email || comment.userId,
            nickname: comment.nickname || 'Unknown User',
            profileImage: comment.profileImage || '/default-profile.svg'
          }
        }
      }

      return comment
    })

    // 로컬 ref에 직접 저장
    localComments.value = commentsWithUser

    // store에도 댓글 저장
    store.setComments(post.value.id, commentsWithUser)
  } catch (error) {
    // 403 에러는 인증 문제이므로 인터셉터에서 처리됨
    if (error.response?.status !== 403) {
      console.error('댓글 조회 실패:', error)
    }
  }
}

const handleAddComment = async () => {
  if (!newComment.value.trim() || !store.currentUser || !post.value) return

  try {
    const commentData = {
      postId: post.value.id,
      content: newComment.value.trim(),
    }

    await commentAPI.createComment(commentData)

    // 댓글 목록 다시 불러오기
    await fetchComments()

    newComment.value = ''
  } catch (error) {
    console.error('댓글 작성 실패:', error)
  }
}

// 댓글 삭제 권한 확인 (본인이 작성한 댓글만 삭제 가능)
const canDeleteComment = (comment) => {
  if (!store.currentUser) return false

  const currentUserId = store.currentUser.id || store.currentUser.email
  const commentUserId = comment.user?.id || comment.user?.email || comment.userId || comment.writerEmail || comment.userEmail

  return currentUserId === commentUserId
}

// 댓글 삭제 확인 모달 열기
const handleDeleteComment = (commentId) => {
  commentToDelete.value = commentId
  showDeleteConfirm.value = true
}

// 댓글 삭제 확인
const confirmDelete = async () => {
  if (!commentToDelete.value) return

  try {
    await commentAPI.deleteComment(commentToDelete.value)

    // 댓글 목록 다시 불러오기
    await fetchComments()
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
  } finally {
    showDeleteConfirm.value = false
    commentToDelete.value = null
  }
}

// 댓글 삭제 취소
const cancelDelete = () => {
  showDeleteConfirm.value = false
  commentToDelete.value = null
}
</script>
