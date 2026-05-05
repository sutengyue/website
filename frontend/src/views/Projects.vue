
<template>
  <div class="projects-page" :class="{ 'dark': darkMode }">
    <nav class="navbar" :class="{ 'dark': darkMode }">
      <div class="nav-container">
        <div class="nav-logo" @click="goHome">
          <span class="logo-text">{{ profile?.name || 'Developer' }}</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/about" class="nav-link">关于</a>
          <a href="/projects" class="nav-link active">项目</a>
          <a href="/blog" class="nav-link">博客</a>
          <button class="dark-toggle" @click="toggleDarkMode">
            <Moon v-if="!darkMode" :size="20" />
            <Sun v-else :size="20" />
          </button>
        </div>
      </div>
    </nav>

    <div class="projects-content">
      <div class="container">
        <div class="page-header">
          <h1 class="page-title">项目作品</h1>
          <p class="page-subtitle">这里展示了我参与的一些项目</p>
        </div>

        <div class="projects-grid">
          <div v-for="project in projects" :key="project.id" class="project-card">
            <div class="project-cover">
              <img v-if="project.cover" :src="project.cover" :alt="project.name" />
              <div v-else class="cover-placeholder">
                <Folder :size="64" />
              </div>
            </div>
            <div class="project-info">
              <h3 class="project-name">{{ project.name }}</h3>
              <p class="project-desc">{{ project.description }}</p>
              <div class="project-tags">
                <span v-for="tag in getTags(project.tags)" :key="tag" class="project-tag">{{ tag }}</span>
              </div>
              <div class="project-links">
                <a v-if="project.url" :href="project.url" target="_blank" class="project-link">
                  <ExternalLink :size="16" />
                  <span>演示</span>
                </a>
                <a v-if="project.githubUrl" :href="project.githubUrl" target="_blank" class="project-link">
                  <Github :size="16" />
                  <span>源码</span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <div v-if="projects.length === 0" class="empty-state">
          <Folder :size="64" />
          <p>暂无项目</p>
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
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import { Github, Moon, Sun, Folder, ExternalLink } from 'lucide-vue-next'
import { profileApi, projectApi } from '@/api'

const router = useRouter()
const darkMode = inject('darkMode')
const toggleDarkMode = inject('toggleDarkMode')

const profile = ref(null)
const projects = ref([])

const goHome = () => {
  router.push('/')
}

const getTags = (tags) => {
  return tags ? tags.split(',').map(t => t.trim()) : []
}

onMounted(async () => {
  try {
    const profileRes = await profileApi.get()
    profile.value = profileRes.data
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }

  try {
    const projectsRes = await projectApi.list()
    projects.value = projectsRes.data
  } catch (e) {
    console.error('Failed to fetch projects:', e)
  }
})
</script>

<style lang="scss" scoped>
.projects-page {
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

.projects-content {
  padding-top: 80px;
  background: #f9fafb;
  min-height: calc(100vh - 144px);

  .dark & {
    background: #1f2937;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
  }
}

.page-header {
  text-align: center;
  margin-bottom: 48px;

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

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 24px;
}

.project-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  }

  .dark & {
    background: #374151;
  }

  .project-cover {
    height: 220px;
    overflow: hidden;
    background: #f3f4f6;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;

      .project-card:hover & {
        transform: scale(1.05);
      }
    }

    .cover-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #9ca3af;
      background: #f3f4f6;

      .dark & {
        background: #4b5563;
      }
    }
  }

  .project-info {
    padding: 24px;

    .project-name {
      font-size: 1.25rem;
      font-weight: 600;
      color: #111827;
      margin-bottom: 8px;

      .dark & {
        color: #f9fafb;
      }
    }

    .project-desc {
      color: #6b7280;
      margin-bottom: 12px;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;

      .dark & {
        color: #9ca3af;
      }
    }

    .project-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 16px;

      .project-tag {
        padding: 4px 10px;
        background: #f3f4f6;
        color: #6b7280;
        border-radius: 12px;
        font-size: 0.75rem;

        .dark & {
          background: #4b5563;
          color: #d1d5db;
        }
      }
    }

    .project-links {
      display: flex;
      gap: 12px;

      .project-link {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        background: #667eea;
        color: white;
        border-radius: 8px;
        text-decoration: none;
        font-size: 0.875rem;
        transition: all 0.3s ease;

        &:hover {
          background: #5a6fd6;
          transform: translateY(-2px);
        }
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
  .projects-grid {
    grid-template-columns: 1fr;
  }
}
</style>
