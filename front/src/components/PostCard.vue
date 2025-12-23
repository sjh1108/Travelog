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
      <button class="text-foreground/60 hover:text-foreground transition-colors">
        <span class="text-xl">•••</span>
      </button>
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
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { MessageCircle, Share2 } from 'lucide-vue-next'
import { useAppStore } from '@/stores/app'
import LikeButton from './LikeButton.vue'
import { getFullImageUrl, getProfileImageUrl } from '@/utils/imageUtils'

const props = defineProps({
  post: Object,
  onCommentClick: Function
})

const store = useAppStore()

const commentCount = computed(() => {
  const comments = store.getComments(props.post.id)
  return comments.length || props.post.commentCount
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
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
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
</script>
