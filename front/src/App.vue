<template>
  <div class="min-h-screen bg-background">
    <Header />

    <main class="max-w-[935px] mx-auto pt-20 pb-20 px-4">
      <Stories />
      <Feed />
    </main>

    <Navigation />
  </div>
</template>

<script setup>
import Header from './components/Header.vue'
import Stories from './components/Stories.vue'
import Feed from './components/Feed.vue'
import Navigation from './components/Navigation.vue'
</script>
