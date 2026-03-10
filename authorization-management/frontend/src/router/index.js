import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/auth-letters'
  },
  {
    path: '/auth-letters',
    name: 'AuthLetterList',
    component: () => import('@/views/AuthLetter/AuthLetterList.vue'),
    meta: { title: '授权书列表' }
  },
  {
    path: '/auth-letters/create',
    name: 'AuthLetterCreate',
    component: () => import('@/views/AuthLetter/AuthLetterEdit.vue'),
    meta: { title: '创建授权书' }
  },
  {
    path: '/auth-letters/:id/edit',
    name: 'AuthLetterEdit',
    component: () => import('@/views/AuthLetter/AuthLetterEdit.vue'),
    meta: { title: '编辑授权书' }
  },
  {
    path: '/auth-letters/:id',
    name: 'AuthLetterDetail',
    component: () => import('@/views/AuthLetter/AuthLetterDetail.vue'),
    meta: { title: '授权书详情' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '授权书管理系统'
  next()
})

export default router