
<template>
  <div class="admin-page">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="sidebar-title">管理后台</h1>
      </div>

      <nav class="sidebar-nav">
        <a 
          v-for="item in menuItems" 
          :key="item.path"
          :href="item.path"
          class="nav-item"
          :class="{ active: currentPath === item.path }"
        >
          <component :is="item.icon" :size="20" />
          <span>{{ item.label }}</span>
        </a>
      </nav>

      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">
          <LogOut :size="18" />
          <span>退出登录</span>
        </button>
      </div>
    </aside>

    <main class="main-content">
      <header class="main-header">
        <div class="header-title">
          <h2>{{ currentTitle }}</h2>
        </div>
        <div class="header-user">
          <span>管理员</span>
        </div>
      </header>

      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Folder, FileText, Shield, FileLog, LogOut } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()

const menuItems = [
  { path: '/admin/profile', label: '个人资料', icon: User },
  { path: '/admin/projects', label: '项目管理', icon: Folder },
  { path: '/admin/blogs', label: '博客管理', icon: FileText },
  { path: '/admin/admins', label: '管理员管理', icon: Shield },
  { path: '/admin/logs', label: '操作日志', icon: FileLog }
]

const currentPath = computed(() => route.path)

const currentTitle = computed(() => {
  const item = menuItems.find(i => i.path === route.path)
  return item?.label || '管理后台'
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('admin')
  router.push('/admin/login')
}
</script>

<style lang="scss" scoped>
.admin-page {
  display: flex;
  min-height: 100vh;
  background: #f3f4f6;
}

.sidebar {
  width: 250px;
  background: #1f2937;
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
}

.sidebar-header {
  padding: 24px;
  border-bottom: 1px solid #374151;

  .sidebar-title {
    font-size: 1.25rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.sidebar-nav {
  flex: 1;
  padding: 16px;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    color: #9ca3af;
    text-decoration: none;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      background: #374151;
      color: #f9fafb;
    }

    &.active {
      background: #667eea;
      color: white;
    }
  }
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #374151;

  .logout-btn {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;
    padding: 12px 16px;
    background: #dc2626;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: #b91c1c;
    }
  }
}

.main-content {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 20px 32px;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-title {
    h2 {
      font-size: 1.25rem;
      font-weight: 600;
      color: #111827;
    }
  }

  .header-user {
    padding: 8px 16px;
    background: #f3f4f6;
    border-radius: 20px;
    font-size: 0.875rem;
    color: #374151;
  }
}

.content-wrapper {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    position: relative;
    height: auto;
  }

  .sidebar-nav {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .nav-item {
    flex: 1;
    min-width: 120px;
    justify-content: center;
  }

  .main-content {
    margin-left: 0;
  }
}
</style>
