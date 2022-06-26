import { createRouter, createWebHistory } from 'vue-router'
import Title from '@/components/Title'
import Login from '@/components/Login'
import Join from '@/components/Join'

const routes = [
  {
    path: '/',
    name: 'cTitle',
    component: Title
  },
  {
    path: '/login',
    name: 'cLogin',
    component: Login
  },
  {
    path: '/join',
    name: 'cJoin',
    component: Join
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router