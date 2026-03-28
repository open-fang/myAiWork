import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/AuthLetterList',
    name: 'AuthLetterList',
    component: () => import('@/views/AuthLetterList.vue')
  },
  {
    path: '/AuthLetterDetail',
    name: 'AuthLetterDetail',
    component: () => import('@/views/AuthLetterDetail.vue')
  },
  {
    path: '/RuleParamConfig',
    name: 'RuleParamConfig',
    component: () => import('@/views/RuleParamConfig.vue')
  },
  {
    path: '*',
    redirect: '/AuthLetterList'
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

export default router