
<template>
  <div class="blog-page" :class="{ 'dark': darkMode }">
    <nav class="navbar" :class="{ 'dark': darkMode }">
      <div class="nav-container">
        <div class="nav-logo" @click="goHome">
          <span class="logo-text">{{ profile?.name || 'Developer' }}</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/about" class="nav-link">关于</a>
          <a href="/projects" class="nav-link">项目</a>
          <a href="/blog" class="nav-link active">博客</a>
          <button class="dark-toggle" @click="toggleDarkMode">
            <Moon v-if="!darkMode" :size="20" />
            <Sun v-else :size="20" />
          </button>
        </div>
      </div>
    </nav>

    <div class="blog-content">
      <div class="container">
        <div class="page-header">
          <h1 class="page-title">博客</h1>
          <p class="page-subtitle">分享技术心得和生活感悟</p>
        </div>

        <div class="search-bar">
          <input 
            type="text" 
            v-model="keyword" 
            placeholder="搜索文章..." 
            class="search-input"
            @keyup.enter="search"
          />
          <button class="search-btn" @click="search">
            <Search :size="18" />
          </button>
        </div>

        <div class="blog-list">
          <div v-for="blog in blogs" :key="blog.id" class="blog-card" @click="goToDetail(blog.slug)">
            <div class="blog-cover">
              <img v-if="blog.cover" :src="blog.cover" :alt="blog.title" />
            </div>
            <div class="blog-info">
              <div class="blog-category">{{ blog.category }}</div>
              <h3 class="blog-title">{{ blog.title }}</h3>
              <p class="blog-summary">{{ blog.summary }}</p>
              <div class="blog-tags">
                <span v-for="tag in getTags(blog.tags)" :key="tag" class="blog-tag">{{ tag }}</span>
              </div>
              <div class="blog-meta">
                <span class="blog-date">{{ formatDate(blog.createdAt) }}</span>
                <span class="blog-views"><Eye :size="14" /> {{ blog.views }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="blogs.length === 0" class="empty-state">
          <FileText :size="64" />
          <p>暂无博客文章</p>
        </div>

        <div v-if="total > pageSize" class="pagination">
          <button 
            class="page-btn" 
            :disabled="currentPage === 1"
            @click="prevPage"
          >
            <ChevronLeft :size="20" />
          </button>
          <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
          <button 
            class="page-btn" 
            :disabled="currentPage >= totalPages"
            @click="nextPage"
          >
            <ChevronRight :size="20" />
          </button>
        </div>
      </div>
    </div>

    <footer class="footer" :class="{ 'dark': darkMode }">
      <div class="container">
        <p>&copy; {{ new Date().getFullYear() }} {{ profile?.name || 'Developer' }}. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { Moon, Sun, Search, Eye, FileText, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { profileApi, blogApi } from '@/api'

const router = useRouter()
const darkMode = inject('darkMode')
const toggleDarkMode = inject('toggleDarkMode')

const profile = ref(null)
const blogs = ref([])
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const goHome = () => {
  router.push('/')
}

const goToDetail = (slug) => {
  router.push(`/blog/${slug}`)
}

const getTags = (tags) => {
  return tags ? tags.split(',').map(t => t.trim()).slice(0, 3) : []
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchBlogs = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
    const res = await blogApi.list(params)
    blogs.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error('Failed to fetch blogs:', e)
  }
}

const search = () => {
  currentPage.value = 1
  fetchBlogs()
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchBlogs()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchBlogs()
  }
}

onMounted(async () => {
  try {
    const profileRes = await profileApi.get()
    profile.value = profileRes.data
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }

  await fetchBlogs()
})
</script>

<style lang="scss" scoped>
.blog-page {
  min-height: 100vh;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #eee;
  transition: all 0.3s ease;

  &.dark {
    background: rgba(17, 24, 39, 0.95);
    border-bottom-color: #374151;
  }

  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 64px;
  }

  .nav-logo {
    cursor: pointer;

    .logo-text {
      font-size: 1.5rem;
      font-weight: 700;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 24px;

    .nav-link {
      color: #374151;
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s ease;

      &.active {
        color: #667eea;
      }

      &:hover {
        color: #667eea;
      }
    }

    .dark & .nav-link {
      color: #e5e7eb;

      &.active {
        color: #a78bfa;
      }

      &:hover {
        color: #a78bfa;
      }
    }

    .dark-toggle {
      background: none;
      border: none;
      cursor: pointer;
      color: #374151;
      padding: 8px;
      border-radius: 50%;
      transition: all 0.3s ease;

      &:hover {
        background: #f3f4f6;
      }

      .dark & {
        color: #e5e7eb;

        &:hover {
          background: #374151;
        }
      }
    }
  }
}

.blog-content {
  padding-top: 80px;
  background: white;
  min-height: calc(100vh - 144px);

  .dark & {
    background: #111827;
  }

  .container {
    max-width: 900px;
    margin: 0 auto;
    padding: 40px 20px;
  }
}

.page-header {
  text-align: center;
  margin-bottom: 32px;

  .page-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: #111827;
    margin-bottom: 8px;

    .dark & {
      color: #f9fafb;
    }
  }

  .page-subtitle {
    color: #6b7280;

    .dark & {
      color: #9ca3af;
    }
  }
}

.search-bar {
  display: flex;
  max-width: 400px;
  margin: 0 auto 32px;
  border-radius: 30px;
  overflow: hidden;
  border: 1px solid #e5e7eb;

  .dark & {
    border-color: #374151;
  }

  .search-input {
    flex: 1;
    padding: 12px 20px;
    border: none;
    outline: none;
    font-size: 1rem;
    background: white;

    .dark & {
      background: #1f2937;
      color: #f9fafb;
    }

    &::placeholder {
      color: #9ca3af;
    }
  }

  .search-btn {
    padding: 12px 20px;
    background: #667eea;
    border: none;
    color: white;
    cursor: pointer;
    transition: background 0.3s ease;

    &:hover {
      background: #5a6fd6;
    }
  }
}

.blog-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.blog-card {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: #f9fafb;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    background: #f3f4f6;
    transform: translateX(8px);
  }

  .dark & {
    background: #1f2937;

    &:hover {
      background: #374151;
    }
  }

  .blog-cover {
    flex-shrink: 0;
    width: 200px;
    height: 120px;
    border-radius: 12px;
    overflow: hidden;
    background: #e5e7eb;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .blog-info {
    flex: 1;
    display: flex;
    flex-direction: column;

    .blog-category {
      display: inline-block;
      padding: 4px 12px;
      background: #667eea;
      color: white;
      border-radius: 12px;
      font-size: 0.75rem;
      align-self: flex-start;
      margin-bottom: 8px;
    }

    .blog-title {
      font-size: 1.25rem;
      font-weight: 600;
      color: #111827;
      margin-bottom: 8px;

      .dark & {
        color: #f9fafb;
      }
    }

    .blog-summary {
      color: #6b7280;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;

      .dark & {
        color: #9ca3af;
      }
    }

    .blog-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 12px;

      .blog-tag {
        padding: 3px 8px;
        background: #e5e7eb;
        color: #6b7280;
        border-radius: 8px;
        font-size: 0.7rem;

        .dark & {
          background: #374151;
          color: #d1d5db;
        }
      }
    }

    .blog-meta {
      display: flex;
      gap: 16px;
      color: #9ca3af;
      font-size: 0.875rem;
      margin-top: auto;

      .blog-views {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  color: #9ca3af;

  p {
    margin-top: 16px;
    font-size: 1.125rem;
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;

  .page-btn {
    padding: 10px 16px;
    background: #f3f4f6;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    color: #374151;
    transition: all 0.3s ease;

    &:hover:not(:disabled) {
      background: #e5e7eb;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .dark & {
      background: #374151;
      color: #e5e7eb;

      &:hover:not(:disabled) {
        background: #4b5563;
      }
    }
  }

  .page-info {
    color: #6b7280;
    font-size: 0.875rem;

    .dark & {
      color: #9ca3af;
    }
  }
}

.footer {
  padding: 40px 20px;
  background: #111827;
  text-align: center;

  p {
    color: #9ca3af;
  }

  &.dark {
    background: #0f172a;
  }
}

@media (max-width: 768px) {
  .blog-card {
    flex-direction: column;

    .blog-cover {
      width: 100%;
    }
  }
}
</style>
