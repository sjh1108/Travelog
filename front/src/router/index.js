import { createRouter, createWebHistory } from 'vue-router'
import { useAppStore } from '@/stores/app'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/map'
    },
    {
      path: '/feed',
      name: 'feed',
      component: () => import('@/views/FeedView.vue')
    },
    {
      path: '/upload',
      name: 'upload',
      component: () => import('@/views/UploadView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/map',
      name: 'map',
      component: () => import('@/views/MapView.vue')
    },
    {
      path: '/profile/:id',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue')
    },
    {
      path: '/post/:id',
      name: 'post',
      component: () => import('@/views/PostDetailView.vue')
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('@/views/MyPageView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('@/views/NotificationsView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const store = useAppStore()
  const isLoggedIn = store.isLoggedIn

  // 로그인이 필요한 페이지
  if (to.meta.requiresAuth && !isLoggedIn) {
    // 로그인 모달을 열도록 메인 페이지로 리다이렉트
    next('/')
    // 페이지 로드 후 모달을 여는 것은 Navigation 컴포넌트에서 처리
  }
  else {
    next()
  }
})

export default router
