<template>
  <div class="bg-background border border-border rounded-lg shadow overflow-hidden mb-4 hover:shadow-lg transition-shadow">
    <!-- Header -->
    <div class="flex items-center justify-between p-4 border-b border-border">
      <div class="flex items-center gap-3">
        <router-link :to="profileLink">
          <img
            :src="userProfileImage"
            :alt="post.user?.nickname || 'User'"
            class="rounded-full object-cover w-10 h-10 cursor-pointer hover:opacity-80 transition-opacity"
          />
        </router-link>
        <div class="flex flex-col">
          <router-link :to="profileLink">
            <h3 class="font-semibold text-foreground hover:text-primary transition-colors cursor-pointer">
              {{ post.user?.nickname || 'Unknown User' }}
            </h3>
          </router-link>
          <p class="text-xs text-foreground/50">{{ post.travelLocation }}</p>
        </div>
      </div>
      <div class="relative" v-if="canDeletePost">
        <button
          @click="showDeleteMenu = !showDeleteMenu"
          class="text-foreground/60 hover:text-foreground transition-colors"
        >
          <span class="text-xl">•••</span>
        </button>
        <div
          v-if="showDeleteMenu"
          class="absolute right-0 mt-2 bg-background border border-border rounded-lg shadow-lg py-1 z-10 min-w-[120px]"
        >
          <button
            @click="handleDeleteClick"
            class="w-full px-4 py-2 text-left text-sm text-red-500 hover:bg-red-500/10 transition-colors"
          >
            Delete
          </button>
        </div>
      </div>
    </div>

    <!-- Image -->
    <router-link :to="`/post/${post.id}`">
      <div class="relative w-full cursor-pointer">
        <img
          :src="fullImageUrl"
          :alt="post.travelLocation"
          class="w-full aspect-square object-cover hover:opacity-95 transition-opacity"
        />
      </div>
    </router-link>

    <!-- Actions -->
    <div class="flex items-center gap-4 p-4 border-b border-border">
      <!-- Like Button Component -->
      <LikeButton
        :post-id="post.id"
        :initial-like-count="post.likeCount"
        :initial-is-liked="post.isLiked"
      />

      <button
        @click.stop="handleCommentButtonClick"
        class="flex items-center gap-1 text-foreground/60 hover:text-primary transition-colors group"
        title="Comment"
      >
        <MessageCircle class="h-6 w-6 group-hover:scale-110 transition-transform" />
        <span class="text-sm">{{ commentCount }}</span>
      </button>

      <button class="flex items-center gap-1 text-foreground/60 hover:text-accent transition-colors group ml-auto">
        <Share2 class="h-6 w-6 group-hover:scale-110 transition-transform" />
      </button>
    </div>

    <!-- Caption -->
    <div class="px-4 py-3">
      <p class="text-sm text-foreground">
        <span class="font-semibold">{{ post.user?.nickname || 'Unknown User' }}</span>
        &nbsp;
        <span class="text-foreground/80"> {{ post.caption }}</span>
      </p>
    </div>

    <!-- Comments Preview -->
    <div class="px-4 pb-4">
      <button
        @click.stop="handleViewCommentsClick"
        class="text-xs text-foreground/50 hover:text-foreground/70 transition-colors"
      >
        View all {{ commentCount }} comments
      </button>
    </div>

    <!-- Timestamp -->
    <div class="px-4 pb-4 border-t border-border pt-3">
      <router-link
        :to="`/post/${post.id}`"
        class="text-xs text-foreground/40 hover:text-foreground/60 transition-colors"
      >
        {{ formatDate(post.createdAt) }}
      </router-link>
    </div>

    <!-- 삭제 확인 모달 -->
    <ConfirmModal
      :is-open="showDeleteConfirm"
      title="게시물 삭제"
      message="정말로 이 게시물을 삭제하시겠습니까?"
      confirm-text="삭제"
      cancel-text="취소"
      :danger="true"
      :on-confirm="confirmDelete"
      :on-cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { MessageCircle, Share2 } from 'lucide-vue-next'
import { useAppStore } from '@/stores/app'
import LikeButton from './LikeButton.vue'
import ConfirmModal from './ConfirmModal.vue'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'
import { postAPI } from '@/api/api'

const props = defineProps({
  post: Object,
  onCommentClick: Function,
  onDelete: Function
})

const store = useAppStore()
const showDeleteMenu = ref(false)
const showDeleteConfirm = ref(false)

const commentCount = computed(() => {
  // 항상 백엔드에서 받은 commentCount 사용 (캐시된 댓글 수가 아닌 실제 DB 값)
  return props.post.commentCount || 0
})

// 프로필 링크 계산 (현재 사용자면 /mypage, 아니면 /profile/:id)
const profileLink = computed(() => {
  const postUserId = props.post.userId || props.post.writerEmail || props.post.user?.id || props.post.user?.email
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

// 전체 이미지 URL 계산
const fullImageUrl = computed(() => getFullImageUrl(props.post.imageUrl))
const userProfileImage = computed(() => getProfileImageUrl(props.post.user?.profileImage))

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  })
}

const handleCommentButtonClick = () => {
  if (props.onCommentClick) {
    props.onCommentClick(props.post.id)
  }
}

const handleViewCommentsClick = () => {
  if (props.onCommentClick) {
    props.onCommentClick(props.post.id)
  }
}

// 게시물 삭제 권한 확인 (본인이 작성한 게시물만 삭제 가능)
const canDeletePost = computed(() => {
  if (!store.currentUser) return false

  // 현재 사용자의 이메일 (우선순위: email > id)
  const currentUserEmail = store.currentUser.email || store.currentUser.id

  // 게시물 작성자의 이메일
  const postUserEmail =
    props.post.writerEmail ||
    props.post.userId ||
    props.post.user?.email ||
    props.post.user?.id

  return currentUserEmail === postUserEmail
})

// 삭제 확인 모달 열기
const handleDeleteClick = () => {
  showDeleteMenu.value = false
  showDeleteConfirm.value = true
}

// 게시물 삭제 확인
const confirmDelete = async () => {
  try {
    await postAPI.deletePost(props.post.id)

    showDeleteConfirm.value = false

    // 부모 컴포넌트에 삭제 알림 (성공 여부 전달)
    if (props.onDelete) {
      props.onDelete(props.post.id, true)
    }
  } catch (error) {
    console.error('게시물 삭제 실패:', error)
    showDeleteConfirm.value = false
  }
}

// 삭제 취소
const cancelDelete = () => {
  showDeleteConfirm.value = false
}
</script>
