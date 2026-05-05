
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue')
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import('@/views/Projects.vue')
  },
  {
    path: '/blog',
    name: 'Blog',
    component: () => import('@/views/Blog.vue')
  },
  {
    path: '/blog/:slug',
    name: 'BlogDetail',
    component: () => import('@/views/BlogDetail.vue')
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Admin.vue'),
    redirect: '/admin/profile',
    children: [
      { path: 'profile', name: 'AdminProfile', component: () => import('@/views/admin/Profile.vue') },
      { path: 'projects', name: 'AdminProjects', component: () => import('@/views/admin/Projects.vue') },
      { path: 'blogs', name: 'AdminBlogs', component: () => import('@/views/admin/Blogs.vue') },
      { path: 'admins', name: 'AdminAdmins', component: () => import('@/views/admin/Admins.vue') },
      { path: 'logs', name: 'AdminLogs', component: () => import('@/views/admin/Logs.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAdminRoute = to.path.startsWith('/admin')
  const token = localStorage.getItem('token')
  
  if (isAdminRoute && to.path !== '/admin/login') {
    if (!token) {
      next('/admin/login')
      return
    }
  }
  
  if (to.path === '/admin/login' && token) {
    next('/admin')
    return
  }
  
  next()
})

export default router
