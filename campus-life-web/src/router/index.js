import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/activities' },
    { path: '/login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', component: () => import('@/views/RegisterView.vue') },
    { path: '/activities', component: () => import('@/views/ActivityListView.vue') },
    { path: '/activities/:id', component: () => import('@/views/ActivityDetailView.vue') },
    { path: '/mine', component: () => import('@/views/MySignupView.vue') },
    { path: '/forum', component: () => import('@/views/ForumListView.vue') },
    { path: '/forum/ask', component: () => import('@/views/AskQuestionView.vue') },
    { path: '/forum/:id', component: () => import('@/views/QuestionDetailView.vue') },
    { path: '/profile', component: () => import('@/views/ProfileView.vue') }
  ]
})

router.beforeEach((to) => {
  const token = localStorage.getItem('campus_token')
  const needLogin = ['/mine', '/profile', '/forum/ask']
  if (needLogin.some((p) => to.path.startsWith(p)) && !token) {
    return '/login'
  }
  if ((to.path === '/login' || to.path === '/register') && token) {
    return '/activities'
  }
})

export default router
