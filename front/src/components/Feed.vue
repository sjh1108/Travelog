<template>
  <section class="space-y-8">
    <PostCard
      v-for="post in posts"
      :key="post.id"
      :post="post"
    />
  </section>
</template>

<script setup>
import { ref } from 'vue'
import PostCard from './PostCard.vue'

const posts = ref([
  {
    id: 1,
    author: {
      name: 'John Doe',
      avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&h=100&fit=crop'
    },
    image: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&h=600&fit=crop',
    caption: 'Beautiful sunset at the beach 🌅',
    likes: 1234,
    comments: 45,
    timestamp: '2 hours ago'
  },
  {
    id: 2,
    author: {
      name: 'Jane Smith',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop'
    },
    image: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&h=600&fit=crop',
    caption: 'Morning coffee ☕',
    likes: 856,
    comments: 23,
    timestamp: '4 hours ago'
  }
])
</script>
