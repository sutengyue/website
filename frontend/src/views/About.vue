
<template>
  <div class="about-page" :class="{ 'dark': darkMode }">
    <nav class="navbar" :class="{ 'dark': darkMode }">
      <div class="nav-container">
        <div class="nav-logo" @click="goHome">
          <span class="logo-text">{{ profile?.name || 'Developer' }}</span>
        </div>
        <div class="nav-links">
          <a href="/" class="nav-link">首页</a>
          <a href="/about" class="nav-link active">关于</a>
          <a href="/projects" class="nav-link">项目</a>
          <a href="/blog" class="nav-link">博客</a>
          <button class="dark-toggle" @click="toggleDarkMode">
            <Moon v-if="!darkMode" :size="20" />
            <Sun v-else :size="20" />
          </button>
        </div>
      </div>
    </nav>

    <div class="about-content">
      <div class="container">
        <div class="profile-section">
          <div class="profile-image">
            <img v-if="profile?.avatar" :src="profile.avatar" :alt="profile.name" class="avatar" />
            <div v-else class="avatar-placeholder">
              <User :size="80" />
            </div>
          </div>
          <div class="profile-info">
            <h1 class="profile-name">{{ profile?.name || 'Developer' }}</h1>
            <p class="profile-title">{{ profile?.title || 'Full Stack Developer' }}</p>
            <p class="profile-bio">{{ profile?.bio || '' }}</p>
            <div class="profile-contact">
              <div class="contact-item">
                <Mail :size="16" />
                <span>{{ profile?.email || '' }}</span>
              </div>
              <div class="contact-item">
                <MapPin :size="16" />
                <span>{{ profile?.location || '' }}</span>
              </div>
            </div>
            <div class="social-links">
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
        </div>

        <div class="skills-section">
          <h2 class="section-title">技能</h2>
          <div class="skills-grid">
            <span v-for="skill in skillsList" :key="skill" class="skill-card">{{ skill }}</span>
          </div>
        </div>

        <div class="education-section">
          <h2 class="section-title">教育背景</h2>
          <div class="timeline">
            <div v-if="profile?.education" class="timeline-item">
              <div class="timeline-content">
                <p>{{ profile.education }}</p>
              </div>
            </div>
            <div v-else class="empty-state">
              <BookOpen :size="48" />
              <p>暂无教育背景信息</p>
            </div>
          </div>
        </div>

        <div class="experience-section">
          <h2 class="section-title">工作经历</h2>
          <div class="timeline">
            <div v-if="profile?.experience" class="timeline-item">
              <div class="timeline-content">
                <p>{{ profile.experience }}</p>
              </div>
            </div>
            <div v-else class="empty-state">
              <Briefcase :size="48" />
              <p>暂无工作经历信息</p>
            </div>
          </div>
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
import { Github, Linkedin, Globe, Moon, Sun, User, Mail, MapPin, BookOpen, Briefcase } from 'lucide-vue-next'
import { profileApi } from '@/api'

const router = useRouter()
const darkMode = inject('darkMode')
const toggleDarkMode = inject('toggleDarkMode')

const profile = ref(null)
const skillsList = ref([])

const goHome = () => {
  router.push('/')
}

onMounted(async () => {
  try {
    const res = await profileApi.get()
    profile.value = res.data
    if (profile.value.skills) {
      skillsList.value = profile.value.skills.split(',').map(s => s.trim())
    }
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }
})
</script>

<style lang="scss" scoped>
.about-page {
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

.about-content {
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

.profile-section {
  display: flex;
  gap: 40px;
  padding: 40px;
  background: #f9fafb;
  border-radius: 20px;
  margin-bottom: 40px;

  .dark & {
    background: #1f2937;
  }

  @media (max-width: 768px) {
    flex-direction: column;
    text-align: center;
  }

  .profile-image {
    flex-shrink: 0;

    .avatar {
      width: 200px;
      height: 200px;
      border-radius: 50%;
      object-fit: cover;
      border: 4px solid #667eea;
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
      border: 4px solid #667eea;
    }
  }

  .profile-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .profile-name {
      font-size: 2rem;
      font-weight: 700;
      color: #111827;
      margin-bottom: 8px;

      .dark & {
        color: #f9fafb;
      }
    }

    .profile-title {
      font-size: 1.25rem;
      color: #667eea;
      margin-bottom: 16px;
    }

    .profile-bio {
      color: #6b7280;
      line-height: 1.8;
      margin-bottom: 20px;

      .dark & {
        color: #9ca3af;
      }
    }

    .profile-contact {
      display: flex;
      gap: 24px;
      margin-bottom: 20px;

      .contact-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #6b7280;

        .dark & {
          color: #9ca3af;
        }
      }
    }

    .social-links {
      display: flex;
      gap: 12px;

      .social-link {
        color: #6b7280;
        padding: 10px;
        border-radius: 50%;
        background: white;
        transition: all 0.3s ease;

        &:hover {
          color: #667eea;
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        .dark & {
          background: #374151;
          color: #9ca3af;
        }
      }
    }
  }
}

.skills-section {
  margin-bottom: 40px;

  .section-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #111827;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 2px solid #667eea;

    .dark & {
      color: #f9fafb;
    }
  }

  .skills-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .skill-card {
      padding: 10px 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border-radius: 25px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

.education-section, .experience-section {
  margin-bottom: 40px;

  .section-title {
    font-size: 1.5rem;
    font-weight: 600;
    color: #111827;
    margin-bottom: 24px;
    padding-bottom: 12px;
    border-bottom: 2px solid #667eea;

    .dark & {
      color: #f9fafb;
    }
  }

  .timeline {
    position: relative;

    &::before {
      content: '';
      position: absolute;
      left: 19px;
      top: 0;
      bottom: 0;
      width: 2px;
      background: #e5e7eb;

      .dark & {
        background: #374151;
      }
    }

    .timeline-item {
      position: relative;
      padding-left: 60px;
      padding-bottom: 30px;

      &::before {
        content: '';
        position: absolute;
        left: 10px;
        top: 5px;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background: #667eea;
        border: 4px solid white;

        .dark & {
          border-color: #1f2937;
        }
      }

      .timeline-content {
        padding: 20px;
        background: #f9fafb;
        border-radius: 12px;

        .dark & {
          background: #1f2937;
        }

        p {
          color: #6b7280;
          line-height: 1.8;

          .dark & {
            color: #9ca3af;
          }
        }
      }
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px;
      color: #9ca3af;

      p {
        margin-top: 12px;
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
</style>
