<template>
  <section class="mb-8 flex gap-4 overflow-x-auto pb-4">
    <div v-for="story in stories" :key="story.id" class="flex flex-col items-center gap-2 cursor-pointer flex-shrink-0">
      <div class="w-14 h-14 rounded-full border-2 border-pink-500 p-0.5 hover:border-pink-600 transition-colors">
        <img :src="story.avatar" :alt="story.name" class="w-full h-full rounded-full object-cover" />
      </div>
      <p class="text-xs font-medium truncate w-14 text-center">{{ story.name }}</p>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const stories = ref([
  {
    id: 1,
    name: 'User 1',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop'
  },
  {
    id: 2,
    name: 'User 2',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop'
  },
  {
    id: 3,
    name: 'User 3',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&h=100&fit=crop'
  }
])
</script>
