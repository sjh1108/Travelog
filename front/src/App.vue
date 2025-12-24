<template>
  <RouterView />
  <ChatBot />
</template>

<script setup>
import { RouterView } from 'vue-router'
import ChatBot from '@/components/ChatBot.vue'
</script>

<style>
@import '@/assets/styles.css';
</style>
