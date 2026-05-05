
<template>
  <div class="blog-detail-page" :class="{ 'dark': darkMode }">
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
        <div v-if="blog" class="blog-detail">
          <div class="blog-header">
            <span class="blog-category">{{ blog.category }}</span>
            <h1 class="blog-title">{{ blog.title }}</h1>
            <div class="blog-meta">
              <span class="blog-date">{{ formatDate(blog.createdAt) }}</span>
              <span class="blog-views"><Eye :size="14" /> {{ blog.views }}</span>
            </div>
          </div>

          <div v-if="blog.cover" class="blog-cover">
            <img :src="blog.cover" :alt="blog.title" />
          </div>

          <div class="blog-body" v-html="renderedContent"></div>

          <div class="blog-tags">
            <span v-for="tag in getTags(blog.tags)" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>

        <div v-else class="loading">
          <Loader2 :size="48" class="loader" />
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
import { ref, onMounted, inject, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Moon, Sun, Eye, Loader2 } from 'lucide-vue-next'
import { marked } from 'marked'
import { profileApi, blogApi } from '@/api'

const route = useRoute()
const router = useRouter()
const darkMode = inject('darkMode')
const toggleDarkMode = inject('toggleDarkMode')

const profile = ref(null)
const blog = ref(null)

marked.setOptions({
  gfm: true,
  breaks: true
})

const renderedContent = computed(() => {
  if (!blog.value?.content) return ''
  return marked(blog.value.content)
})

const goHome = () => {
  router.push('/')
}

const getTags = (tags) => {
  return tags ? tags.split(',').map(t => t.trim()) : []
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(async () => {
  try {
    const profileRes = await profileApi.get()
    profile.value = profileRes.data
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }

  const slug = route.params.slug
  try {
    const blogRes = await blogApi.getBySlug(slug)
    blog.value = blogRes.data
  } catch (e) {
    console.error('Failed to fetch blog:', e)
  }
})
</script>

<style lang="scss" scoped>
.blog-detail-page {
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
    max-width: 800px;
    margin: 0 auto;
    padding: 40px 20px;
  }
}

.blog-detail {
  .blog-header {
    text-align: center;
    margin-bottom: 32px;

    .blog-category {
      display: inline-block;
      padding: 6px 16px;
      background: #667eea;
      color: white;
      border-radius: 20px;
      font-size: 0.875rem;
      margin-bottom: 16px;
    }

    .blog-title {
      font-size: 2.5rem;
      font-weight: 700;
      color: #111827;
      margin-bottom: 16px;

      .dark & {
        color: #f9fafb;
      }
    }

    .blog-meta {
      display: flex;
      justify-content: center;
      gap: 24px;
      color: #9ca3af;
      font-size: 0.875rem;

      .blog-views {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .blog-cover {
    margin-bottom: 32px;
    border-radius: 16px;
    overflow: hidden;

    img {
      width: 100%;
      height: auto;
    }
  }

  .blog-body {
    color: #374151;
    line-height: 1.8;
    font-size: 1.125rem;

    .dark & {
      color: #e5e7eb;
    }

    h1, h2, h3, h4, h5, h6 {
      color: #111827;
      margin: 24px 0 16px;
      font-weight: 600;

      .dark & {
        color: #f9fafb;
      }
    }

    h1 { font-size: 1.75rem; }
    h2 { font-size: 1.5rem; }
    h3 { font-size: 1.25rem; }

    p {
      margin-bottom: 16px;
    }

    ul, ol {
      margin-bottom: 16px;
      padding-left: 24px;

      li {
        margin-bottom: 8px;
      }
    }

    blockquote {
      border-left: 4px solid #667eea;
      padding-left: 16px;
      margin: 16px 0;
      color: #6b7280;
      font-style: italic;

      .dark & {
        color: #9ca3af;
      }
    }

    code {
      background: #f3f4f6;
      padding: 2px 6px;
      border-radius: 4px;
      font-family: 'Fira Code', monospace;
      font-size: 0.9em;

      .dark & {
        background: #374151;
      }
    }

    pre {
      background: #1f2937;
      padding: 16px;
      border-radius: 12px;
      overflow-x: auto;
      margin: 16px 0;

      code {
        background: none;
        padding: 0;
        color: #e5e7eb;
      }
    }

    a {
      color: #667eea;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }

    img {
      max-width: 100%;
      border-radius: 8px;
    }
  }

  .blog-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 40px;
    padding-top: 24px;
    border-top: 1px solid #e5e7eb;

    .dark & {
      border-color: #374151;
    }

    .tag {
      padding: 8px 16px;
      background: #f3f4f6;
      color: #6b7280;
      border-radius: 20px;
      font-size: 0.875rem;

      .dark & {
        background: #374151;
        color: #d1d5db;
      }
    }
  }
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 0;

  .loader {
    color: #667eea;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
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
  .blog-header .blog-title {
    font-size: 1.75rem;
  }
}
</style>
