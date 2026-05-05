
<template>
  <div class="home-page">
    <nav class="navbar" :class="{ 'dark': darkMode }">
      <div class="nav-container">
        <div class="nav-logo" @click="goHome">
          <span class="logo-text">{{ profile?.name || 'Developer' }}</span>
        </div>
        <div class="nav-links">
          <a href="#home" class="nav-link">首页</a>
          <a href="#about" class="nav-link">关于</a>
          <a href="#projects" class="nav-link">项目</a>
          <a href="#blog" class="nav-link">博客</a>
          <button class="dark-toggle" @click="toggleDarkMode">
            <Moon v-if="!darkMode" :size="20" />
            <Sun v-else :size="20" />
          </button>
        </div>
      </div>
    </nav>

    <section id="home" class="hero" :class="{ 'dark': darkMode }">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">{{ profile?.title || 'Full Stack Developer' }}</h1>
          <p class="hero-bio">{{ profile?.bio || '热爱技术，专注于创造有价值的产品' }}</p>
          <div class="hero-links">
            <a v-if="profile?.github" :href="profile.github" target="_blank" class="social-link">
              <Github :size="24" />
            </a>
            <a v-if="profile?.linkedin" :href="profile.linkedin" target="_blank" class="social-link">
              <Linkedin :size="24" />
            </a>
            <a v-if="profile?.website" :href="profile.website" target="_blank" class="social-link">
              <Globe :size="24" />
            </a>
          </div>
        </div>
        <div class="hero-image">
          <div class="avatar-wrapper">
            <img v-if="profile?.avatar" :src="profile.avatar" :alt="profile.name" class="avatar" />
            <div v-else class="avatar-placeholder">
              <User :size="64" />
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="about" class="about" :class="{ 'dark': darkMode }">
      <div class="container">
        <h2 class="section-title">关于我</h2>
        <div class="about-content">
          <div class="about-item">
            <h3 class="about-subtitle">技能</h3>
            <div class="skills">
              <span v-for="skill in skillsList" :key="skill" class="skill-tag">{{ skill }}</span>
            </div>
          </div>
          <div class="about-item">
            <h3 class="about-subtitle">教育背景</h3>
            <p>{{ profile?.education || '暂无教育背景信息' }}</p>
          </div>
          <div class="about-item">
            <h3 class="about-subtitle">工作经历</h3>
            <p>{{ profile?.experience || '暂无工作经历信息' }}</p>
          </div>
        </div>
      </div>
    </section>

    <section id="projects" class="projects" :class="{ 'dark': darkMode }">
      <div class="container">
        <h2 class="section-title">项目作品</h2>
        <div class="projects-grid">
          <div v-for="project in projects" :key="project.id" class="project-card" @click="goToProject(project)">
            <div class="project-cover">
              <img v-if="project.cover" :src="project.cover" :alt="project.name" />
              <div v-else class="cover-placeholder">
                <Folder :size="48" />
              </div>
            </div>
            <div class="project-info">
              <h3 class="project-name">{{ project.name }}</h3>
              <p class="project-desc">{{ project.description }}</p>
              <div class="project-tags">
                <span v-for="tag in projectTags(project.tags)" :key="tag" class="project-tag">{{ tag }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="view-more">
          <a href="/projects" class="btn">查看更多项目</a>
        </div>
      </div>
    </section>

    <section id="blog" class="blog-preview" :class="{ 'dark': darkMode }">
      <div class="container">
        <h2 class="section-title">最新博客</h2>
        <div class="blog-list">
          <div v-for="blog in recentBlogs" :key="blog.id" class="blog-card" @click="goToBlog(blog.slug)">
            <div class="blog-cover">
              <img v-if="blog.cover" :src="blog.cover" :alt="blog.title" />
            </div>
            <div class="blog-info">
              <span class="blog-category">{{ blog.category }}</span>
              <h3 class="blog-title">{{ blog.title }}</h3>
              <p class="blog-summary">{{ blog.summary }}</p>
              <div class="blog-meta">
                <span class="blog-date">{{ formatDate(blog.createdAt) }}</span>
                <span class="blog-views"><Eye :size="14" /> {{ blog.views }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="view-more">
          <a href="/blog" class="btn">查看更多博客</a>
        </div>
      </div>
    </section>

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
import { Github, Linkedin, Globe, Moon, Sun, User, Folder, Eye } from 'lucide-vue-next'
import { profileApi, projectApi, blogApi } from '@/api'

const router = useRouter()
const darkMode = inject('darkMode')
const toggleDarkMode = inject('toggleDarkMode')

const profile = ref(null)
const projects = ref([])
const recentBlogs = ref([])

const skillsList = ref([])

const goHome = () => {
  router.push('/')
}

const goToProject = (project) => {
  router.push('/projects')
}

const goToBlog = (slug) => {
  router.push(`/blog/${slug}`)
}

const projectTags = (tags) => {
  return tags ? tags.split(',') : []
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
    if (profile.value.skills) {
      skillsList.value = profile.value.skills.split(',').map(s => s.trim())
    }
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }

  try {
    const projectsRes = await projectApi.list()
    projects.value = projectsRes.data.slice(0, 3)
  } catch (e) {
    console.error('Failed to fetch projects:', e)
  }

  try {
    const blogsRes = await blogApi.listAll()
    recentBlogs.value = blogsRes.data.slice(0, 3)
  } catch (e) {
    console.error('Failed to fetch blogs:', e)
  }
})
</script>

<style lang="scss" scoped>
.home-page {
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

      &:hover {
        color: #667eea;
      }
    }

    .dark & .nav-link {
      color: #e5e7eb;

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

.hero {
  padding: 120px 20px 80px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  transition: all 0.3s ease;

  &.dark {
    background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
  }

  .hero-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 40px;
    flex-wrap: wrap;
  }

  .hero-text {
    flex: 1;
    min-width: 300px;

    .hero-title {
      font-size: 3rem;
      font-weight: 800;
      color: #111827;
      margin-bottom: 16px;
      animation: fadeInUp 0.6s ease;

      .dark & {
        color: #f9fafb;
      }
    }

    .hero-bio {
      font-size: 1.25rem;
      color: #6b7280;
      margin-bottom: 24px;
      animation: fadeInUp 0.6s ease 0.2s both;

      .dark & {
        color: #9ca3af;
      }
    }

    .hero-links {
      display: flex;
      gap: 16px;
      animation: fadeInUp 0.6s ease 0.4s both;

      .social-link {
        color: #6b7280;
        padding: 12px;
        border-radius: 50%;
        background: white;
        transition: all 0.3s ease;

        &:hover {
          color: #667eea;
          transform: translateY(-4px);
          box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
        }

        .dark & {
          background: #374151;
          color: #9ca3af;
        }
      }
    }
  }

  .hero-image {
    flex-shrink: 0;

    .avatar-wrapper {
      animation: fadeInRight 0.6s ease 0.3s both;
    }

    .avatar {
      width: 200px;
      height: 200px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid white;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    }

    .avatar-placeholder {
      width: 200px;
      height: 200px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      border: 4px solid white;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    }
  }
}

.about {
  padding: 80px 20px;
  background: white;
  transition: all 0.3s ease;

  &.dark {
    background: #111827;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    font-size: 2.5rem;
    font-weight: 700;
    text-align: center;
    margin-bottom: 40px;
    color: #111827;

    .dark & {
      color: #f9fafb;
    }
  }

  .about-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 32px;
  }

  .about-item {
    padding: 24px;
    background: #f9fafb;
    border-radius: 16px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }

    .dark & {
      background: #1f2937;
    }

    .about-subtitle {
      font-size: 1.25rem;
      font-weight: 600;
      color: #374151;
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 8px;

      .dark & {
        color: #f9fafb;
      }
    }

    p {
      color: #6b7280;
      line-height: 1.8;

      .dark & {
        color: #9ca3af;
      }
    }

    .skills {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .skill-tag {
        padding: 6px 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 20px;
        font-size: 0.875rem;
      }
    }
  }
}

.projects {
  padding: 80px 20px;
  background: #f9fafb;
  transition: all 0.3s ease;

  &.dark {
    background: #1f2937;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    font-size: 2.5rem;
    font-weight: 700;
    text-align: center;
    margin-bottom: 40px;
    color: #111827;

    .dark & {
      color: #f9fafb;
    }
  }

  .projects-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;
  }

  .project-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
    }

    .dark & {
      background: #374151;
    }

    .project-cover {
      height: 200px;
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
      padding: 20px;

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
    }
  }

  .view-more {
    text-align: center;
    margin-top: 32px;

    .btn {
      display: inline-block;
      padding: 12px 32px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 30px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

.blog-preview {
  padding: 80px 20px;
  background: white;
  transition: all 0.3s ease;

  &.dark {
    background: #111827;
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    font-size: 2.5rem;
    font-weight: 700;
    text-align: center;
    margin-bottom: 40px;
    color: #111827;

    .dark & {
      color: #f9fafb;
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
      justify-content: space-between;

      .blog-category {
        display: inline-block;
        padding: 4px 12px;
        background: #667eea;
        color: white;
        border-radius: 12px;
        font-size: 0.75rem;
        align-self: flex-start;
      }

      .blog-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #111827;
        margin: 8px 0;

        .dark & {
          color: #f9fafb;
        }
      }

      .blog-summary {
        color: #6b7280;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;

        .dark & {
          color: #9ca3af;
        }
      }

      .blog-meta {
        display: flex;
        gap: 16px;
        color: #9ca3af;
        font-size: 0.875rem;
        margin-top: 8px;

        .blog-views {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .view-more {
    text-align: center;
    margin-top: 32px;

    .btn {
      display: inline-block;
      padding: 12px 32px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 30px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
      }
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .hero .hero-title {
    font-size: 2rem;
  }

  .hero .hero-content {
    flex-direction: column;
    text-align: center;
  }

  .hero .hero-links {
    justify-content: center;
  }

  .blog-card {
    flex-direction: column;

    .blog-cover {
      width: 100%;
    }
  }
}
</style>
