import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/attendance',
    name: 'Attendance',
    component: () => import('../views/Attendance.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/records',
    name: 'Records',
    component: () => import('../views/Records.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/leave',
    name: 'Leave',
    component: () => import('../views/Leave.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/approval',
    name: 'Approval',
    component: () => import('../views/Approval.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要认证但没有token，重定向到登录页
    next('/login')
  } else if (to.path === '/login' && token) {
    // 已登录用户访问登录页，重定向到仪表板
    next('/dashboard')
  } else {
    next()
  }
})

export default router