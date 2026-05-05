
<template>
  <div class="profile-page">
    <div class="page-header">
      <h3>个人资料设置</h3>
      <p>管理您的个人信息展示</p>
    </div>

    <div class="form-card">
      <form class="profile-form" @submit.prevent="handleSubmit">
        <div class="form-row">
          <div class="form-group">
            <label>头像</label>
            <div class="avatar-upload">
              <img v-if="form.avatar" :src="form.avatar" class="preview-avatar" />
              <div v-else class="avatar-placeholder">
                <User :size="48" />
              </div>
              <input type="file" accept="image/*" class="avatar-input" @change="handleAvatarUpload" />
              <label class="upload-btn" for="avatar">
                <Upload :size="16" />
                <span>上传</span>
              </label>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>姓名 *</label>
            <input v-model="form.name" type="text" placeholder="请输入姓名" class="form-input" />
          </div>
          <div class="form-group">
            <label>职位 *</label>
            <input v-model="form.title" type="text" placeholder="请输入职位" class="form-input" />
          </div>
        </div>

        <div class="form-group">
          <label>简介 *</label>
          <textarea v-model="form.bio" rows="3" placeholder="请输入个人简介" class="form-textarea"></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" placeholder="请输入邮箱" class="form-input" />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="form.phone" type="tel" placeholder="请输入电话" class="form-input" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>所在地</label>
            <input v-model="form.location" type="text" placeholder="请输入所在地" class="form-input" />
          </div>
          <div class="form-group">
            <label>个人网站</label>
            <input v-model="form.website" type="url" placeholder="请输入个人网站地址" class="form-input" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Github</label>
            <input v-model="form.github" type="url" placeholder="请输入Github地址" class="form-input" />
          </div>
          <div class="form-group">
            <label>LinkedIn</label>
            <input v-model="form.linkedin" type="url" placeholder="请输入LinkedIn地址" class="form-input" />
          </div>
        </div>

        <div class="form-group">
          <label>技能</label>
          <input v-model="form.skills" type="text" placeholder="请输入技能，用逗号分隔" class="form-input" />
        </div>

        <div class="form-group">
          <label>教育背景</label>
          <textarea v-model="form.education" rows="3" placeholder="请输入教育背景" class="form-textarea"></textarea>
        </div>

        <div class="form-group">
          <label>工作经历</label>
          <textarea v-model="form.experience" rows="3" placeholder="请输入工作经历" class="form-textarea"></textarea>
        </div>

        <div class="form-actions">
          <button type="submit" class="submit-btn" :disabled="loading">
            <Loader2 v-if="loading" :size="16" />
            <span>{{ loading ? '保存中...' : '保存更改' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Upload, Loader2 } from 'lucide-vue-next'
import { profileApi, fileApi } from '@/api'

const form = ref({
  name: '',
  avatar: '',
  title: '',
  bio: '',
  email: '',
  phone: '',
  location: '',
  github: '',
  linkedin: '',
  website: '',
  skills: '',
  education: '',
  experience: ''
})

const loading = ref(false)

const handleSubmit = async () => {
  if (!form.value.name || !form.value.title || !form.value.bio) {
    alert('请填写必填字段')
    return
  }

  loading.value = true

  try {
    await profileApi.update(form.value)
    alert('保存成功')
  } catch (e) {
    alert('保存失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  loading.value = true

  try {
    const res = await fileApi.upload(file)
    form.value.avatar = res.data.url
  } catch (e) {
    alert('上传失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const res = await profileApi.getAdmin()
    Object.assign(form.value, res.data)
  } catch (e) {
    console.error('Failed to fetch profile:', e)
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
  .page-header {
    margin-bottom: 24px;

    h3 {
      font-size: 1.25rem;
      font-weight: 600;
      color: #111827;
      margin-bottom: 4px;
    }

    p {
      color: #6b7280;
      font-size: 0.875rem;
    }
  }

  .form-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .profile-form {
    .form-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
      }
    }

    .form-group {
      margin-bottom: 20px;

      label {
        display: block;
        font-weight: 500;
        color: #374151;
        font-size: 0.875rem;
        margin-bottom: 8px;
      }

      .form-input {
        width: 100%;
        padding: 12px 16px;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 1rem;
        outline: none;
        transition: all 0.3s ease;

        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        &::placeholder {
          color: #9ca3af;
        }
      }

      .form-textarea {
        width: 100%;
        padding: 12px 16px;
        border: 2px solid #e5e7eb;
        border-radius: 10px;
        font-size: 1rem;
        outline: none;
        resize: vertical;
        transition: all 0.3s ease;

        &:focus {
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        &::placeholder {
          color: #9ca3af;
        }
      }

      .avatar-upload {
        position: relative;
        display: inline-block;

        .preview-avatar {
          width: 120px;
          height: 120px;
          border-radius: 50%;
          object-fit: cover;
          border: 3px solid #667eea;
        }

        .avatar-placeholder {
          width: 120px;
          height: 120px;
          border-radius: 50%;
          background: #f3f4f6;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #9ca3af;
          border: 3px dashed #d1d5db;
        }

        .avatar-input {
          display: none;
        }

        .upload-btn {
          position: absolute;
          bottom: 0;
          right: 0;
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 6px 12px;
          background: #667eea;
          color: white;
          border-radius: 12px;
          font-size: 0.75rem;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background: #5a6fd6;
          }
        }
      }
    }

    .form-actions {
      display: flex;
      gap: 12px;
      margin-top: 24px;

      .submit-btn {
        padding: 12px 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border: none;
        border-radius: 10px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 8px;

        &:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }

        &:disabled {
          opacity: 0.7;
          cursor: not-allowed;
        }
      }
    }
  }
}
</style>
