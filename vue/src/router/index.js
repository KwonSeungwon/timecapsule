
import { createRouter, createWebHistory } from 'vue-router'
import Title from '@/components/Title'
import Login from '@/components/Login'
import Join from '@/components/Join'
import Target from "@/components/Target"
import MyCapsule from '@/components/myCapsule'
import SelectCapsule from '@/components/SelectCapsule'

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
  },
  {
    path: '/target',
    name: 'cTarget',
    component: Target
  },
  {
    path: '/myCapsule',
    name: 'myCapsule',
    component: MyCapsule
  },
  {
    path: '/selectCapsule',
    name: 'selectCapsule',
    component: SelectCapsule
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
