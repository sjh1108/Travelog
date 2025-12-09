<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-2xl mx-auto">
        <h1 class="text-3xl font-bold mb-8">Notifications</h1>

        <div v-if="notifications.length === 0" class="flex items-center justify-center py-20">
          <p class="text-foreground/50">No notifications yet.</p>
        </div>

        <div v-else class="space-y-2">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            :class="[
              'flex items-start gap-4 p-4 rounded-lg transition-colors cursor-pointer',
              notification.isRead
                ? 'bg-background hover:bg-background/50'
                : 'bg-card hover:bg-card/80'
            ]"
            @click="handleNotificationClick(notification)"
          >
            <!-- User Avatar -->
            <router-link
              :to="`/profile/${notification.userId}`"
              @click.stop
            >
              <img
                :src="notification.user.profileImage || '/placeholder.svg'"
                :alt="notification.user.nickname"
                class="w-12 h-12 rounded-full object-cover ring-2 ring-border"
              />
            </router-link>

            <!-- Notification Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-2">
                <div class="flex-1">
                  <p class="text-sm">
                    <router-link
                      :to="`/profile/${notification.userId}`"
                      class="font-bold hover:underline"
                      @click.stop
                    >
                      {{ notification.user.nickname }}
                    </router-link>
                    <span class="text-foreground/80">{{ notification.message }}</span>
                  </p>

                  <!-- Comment text for comment notifications -->
                  <p
                    v-if="notification.type === 'comment' && notification.commentText"
                    class="text-sm text-foreground/60 mt-1"
                  >
                    {{ notification.commentText }}
                  </p>

                  <p class="text-xs text-foreground/50 mt-1">
                    {{ formatDate(notification.createdAt) }}
                  </p>
                </div>

                <!-- Post thumbnail for like/comment notifications -->
                <router-link
                  v-if="notification.post"
                  :to="`/post/${notification.postId}`"
                  @click.stop
                  class="flex-shrink-0"
                >
                  <img
                    :src="notification.post.imageUrl"
                    :alt="notification.post.travelLocation"
                    class="w-12 h-12 object-cover rounded"
                  />
                </router-link>
              </div>
            </div>

            <!-- Unread indicator -->
            <div
              v-if="!notification.isRead"
              class="w-2 h-2 rounded-full bg-primary flex-shrink-0 mt-2"
            />
          </div>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { dummyNotifications } from '@/data/dummy-data'

const router = useRouter()
const notifications = ref([])

onMounted(() => {
  // Sort notifications by date (newest first)
  notifications.value = [...dummyNotifications].sort(
    (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
  )
})

const handleNotificationClick = (notification) => {
  // Mark as read (in a real app, this would update the backend)
  notification.isRead = true

  // Navigate based on notification type
  if (notification.type === 'follow') {
    router.push(`/profile/${notification.userId}`)
  } else if (notification.post) {
    router.push(`/post/${notification.postId}`)
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffInSeconds = Math.floor((now - date) / 1000)

  if (diffInSeconds < 60) {
    return '방금 전'
  } else if (diffInSeconds < 3600) {
    const minutes = Math.floor(diffInSeconds / 60)
    return `${minutes}분 전`
  } else if (diffInSeconds < 86400) {
    const hours = Math.floor(diffInSeconds / 3600)
    return `${hours}시간 전`
  } else if (diffInSeconds < 604800) {
    const days = Math.floor(diffInSeconds / 86400)
    return `${days}일 전`
  } else {
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
  }
}
</script>
