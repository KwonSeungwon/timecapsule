import { createRouter, createWebHistory } from 'vue-router'
import Title from '@/components/Title'
import Login from '@/components/Login'
import LoginTest from '@/components/LoginTest'
import Join from '@/components/Join'
import Target from '@/components/Target'
import MyCapsule from '@/components/myCapsule'
import SelectCapsule from '@/components/SelectCapsule'
import SelectLetterPaper from '@/components/SelectLetterPaper'
import Tutorial from '@/components/Tutorial'
import WriteLetter from '@/components/WriteLetter'


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
    path: '/loginTest',
    name: 'cLoginTest',
    component: LoginTest
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
    path: '/mypage',
    name: 'myCapsule',
    component: MyCapsule
  },
  {
    path: '/join/capsule',
    name: 'selectCapsule',
    component: SelectCapsule
  },
  {
    path: '/target/letter',
    name: 'selectLetterPaper',
    component: SelectLetterPaper
  },
  {
    path: '/tutorial',
    name: 'tutorial',
    component: Tutorial
  },
  {
    path: '/target/letter/write',
    name: 'writeLetter',
    component: WriteLetter
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
