<template>
  <div class="min-h-screen bg-background flex flex-col">
    <Navigation />
    <main class="flex-1 py-8 px-4">
      <div class="max-w-4xl mx-auto">
        <div v-if="user" class="space-y-6">
          <!-- Profile Header -->
          <div class="flex items-start gap-8">
            <img
              :src="user.profileImage || '/placeholder.svg'"
              :alt="user.nickname"
              class="w-32 h-32 rounded-full object-cover"
            />
            <div class="flex-1">
              <div class="flex items-center gap-4 mb-4">
                <h1 class="text-2xl font-bold">{{ user.nickname }}</h1>
                <button
                  v-if="user.id !== store.currentUser?.id"
                  @click="handleFollow"
                  :class="[
                    'px-6 py-2 rounded-lg font-semibold transition-colors',
                    isFollowing
                      ? 'bg-secondary/20 text-secondary hover:bg-secondary/30'
                      : 'bg-primary text-primary-foreground hover:opacity-90'
                  ]"
                >
                  {{ isFollowing ? 'Following' : 'Follow' }}
                </button>
              </div>
              
              <div class="flex gap-6 mb-4">
                <div>
                  <span class="font-bold">{{ userPosts.length }}</span>
                  <span class="text-foreground/60 ml-1">posts</span>
                </div>
                <button @click="showFollowers = true">
                  <span class="font-bold">{{ user.followerCount }}</span>
                  <span class="text-foreground/60 ml-1">followers</span>
                </button>
                <button @click="showFollowing = true">
                  <span class="font-bold">{{ user.followingCount }}</span>
                  <span class="text-foreground/60 ml-1">following</span>
                </button>
              </div>
              
              <p class="text-foreground/80">
                {{ user.bio }}
              </p>
            </div>
          </div>

          <!-- Photo Grid -->
          <div>
            <h2 class="text-xl font-bold mb-4">Photos</h2>
            <div v-if="userPosts.length === 0" class="text-center py-12 text-foreground/50">
              No posts yet
            </div>
            <div v-else class="grid grid-cols-3 gap-2">
              <router-link
                v-for="post in userPosts"
                :key="post.id"
                :to="`/post/${post.id}`"
                class="aspect-square"
              >
                <img
                  :src="post.imageUrl"
                  :alt="post.travelLocation"
                  class="w-full h-full object-cover rounded-lg hover:opacity-90 transition-opacity"
                />
              </router-link>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-20">
          <p class="text-foreground/50">User not found</p>
        </div>
      </div>
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import Navigation from '@/components/Navigation.vue'
import Footer from '@/components/Footer.vue'
import { useAppStore } from '@/stores/app'
import { dummyUsers, userPostsMap } from '@/data/dummy-data'

const route = useRoute()
const store = useAppStore()

const userId = route.params.id
const user = computed(() => dummyUsers[userId])
const userPosts = computed(() => userPostsMap[userId] || [])

const showFollowers = ref(false)
const showFollowing = ref(false)

const isFollowing = computed(() => store.followingUsers.has(userId))

const handleFollow = () => {
  store.toggleFollow(userId)
}
</script>
