import { createRouter, createWebHistory } from 'vue-router'
import Title from '@/components/Title'
import MyCapsule from '@/components/myCapsule'
import FindCapsule from '@/components/FindCapsule'
import Tutorial from '@/components/Tutorial'
import WriteLetter from '@/components/WriteLetter'


const routes = [
  {
    path: '/',
    name: 'cTitle',
    component: Title
  },
  {
    path: '/write',
    name: 'writeLetter',
    component: WriteLetter
  },
  {
    path: '/find',
    name: 'findCapsule',
    component: FindCapsule
  },
  {
    path: '/my-capsule',
    name: 'myCapsule',
    component: MyCapsule
  },
  {
    path: '/tutorial',
    name: 'tutorial',
    component: Tutorial
  },
  {
    path : "/:catchAll(.*)",
    name : "title",
    component : Title
  },
]


const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
