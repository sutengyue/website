
<template>
  <div class="projects-page">
    <div class="page-header">
      <div class="header-left">
        <h3>项目管理</h3>
        <p>管理您的项目作品</p>
      </div>
      <button class="add-btn" @click="openAddModal">
        <Plus :size="18" />
        <span>添加项目</span>
      </button>
    </div>

    <div class="search-bar">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索项目名称..." 
        class="search-input"
        @keyup.enter="fetchProjects"
      />
      <button class="search-btn" @click="fetchProjects">
        <Search :size="16" />
      </button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>封面</th>
            <th>项目名称</th>
            <th>标签</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="project in projects" :key="project.id">
            <td>
              <img v-if="project.cover" :src="project.cover" class="cover-img" />
              <div v-else class="cover-placeholder">
                <Image :size="24" />
              </div>
            </td>
            <td>{{ project.name }}</td>
            <td>
              <span v-for="tag in getTags(project.tags)" :key="tag" class="tag">{{ tag }}</span>
            </td>
            <td>
              <span class="status" :class="project.status === 1 ? 'active' : 'inactive'">
                {{ project.status === 1 ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>{{ formatDate(project.createdAt) }}</td>
            <td>
              <button class="action-btn edit" @click="openEditModal(project)">
                <Pencil :size="16" />
              </button>
              <button class="action-btn delete" @click="handleDelete(project.id)">
                <Trash2 :size="16" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="projects.length === 0" class="empty-state">
        <Folder :size="48" />
        <p>暂无项目</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h4>{{ isEdit ? '编辑项目' : '添加项目' }}</h4>
          <button class="close-btn" @click="closeModal">
            <X :size="18" />
          </button>
        </div>

        <form class="modal-form" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>封面</label>
            <div class="cover-upload">
              <img v-if="form.cover" :src="form.cover" class="cover-preview" />
              <div v-else class="cover-placeholder">
                <Image :size="32" />
              </div>
              <input type="file" accept="image/*" class="cover-input" @change="handleCoverUpload" />
              <label class="upload-btn" for="cover">
                <Upload :size="16" />
                <span>上传封面</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label>项目名称 *</label>
            <input v-model="form.name" type="text" placeholder="请输入项目名称" class="form-input" />
          </div>

          <div class="form-group">
            <label>项目描述</label>
            <textarea v-model="form.description" rows="3" placeholder="请输入项目描述" class="form-textarea"></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>项目链接</label>
              <input v-model="form.url" type="url" placeholder="请输入项目链接" class="form-input" />
            </div>
            <div class="form-group">
              <label>Github链接</label>
              <input v-model="form.githubUrl" type="url" placeholder="请输入Github链接" class="form-input" />
            </div>
          </div>

          <div class="form-group">
            <label>标签</label>
            <input v-model="form.tags" type="text" placeholder="请输入标签，用逗号分隔" class="form-input" />
          </div>

          <div class="form-group">
            <label>排序</label>
            <input v-model.number="form.sortOrder" type="number" placeholder="排序序号" class="form-input" />
          </div>

          <div class="form-group">
            <label>状态</label>
            <select v-model.number="form.status" class="form-select">
              <option :value="1">已发布</option>
              <option :value="0">草稿</option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="closeModal">取消</button>
            <button type="submit" class="submit-btn" :disabled="loading">
              <Loader2 v-if="loading" :size="16" />
              <span>{{ loading ? '保存中...' : '保存' }}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Search, Image, Pencil, Trash2, X, Upload, Folder, Loader2 } from 'lucide-vue-next'
import { projectApi, fileApi } from '@/api'

const projects = ref([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const form = ref({
  id: null,
  name: '',
  description: '',
  cover: '',
  url: '',
  githubUrl: '',
  tags: '',
  sortOrder: 0,
  status: 1
})

const getTags = (tags) => {
  return tags ? tags.split(',').map(t => t.trim()).slice(0, 3) : []
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchProjects = async () => {
  try {
    const res = await projectApi.adminList({ keyword: keyword.value })
    projects.value = res.data.records
  } catch (e) {
    console.error('Failed to fetch projects:', e)
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    description: '',
    cover: '',
    url: '',
    githubUrl: '',
    tags: '',
    sortOrder: 0,
    status: 1
  }
  showModal.value = true
}

const openEditModal = (project) => {
  isEdit.value = true
  form.value = { ...project }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleCoverUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  loading.value = true

  try {
    const res = await fileApi.upload(file)
    form.value.cover = res.data.url
  } catch (e) {
    alert('上传失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!form.value.name) {
    alert('请输入项目名称')
    return
  }

  loading.value = true

  try {
    if (isEdit.value) {
      await projectApi.update(form.value.id, form.value)
    } else {
      await projectApi.create(form.value)
    }
    closeModal()
    await fetchProjects()
    alert('保存成功')
  } catch (e) {
    alert('保存失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这个项目吗？')) return

  try {
    await projectApi.delete(id)
    await fetchProjects()
    alert('删除成功')
  } catch (e) {
    alert('删除失败：' + (e.response?.data?.message || e.message))
  }
}

onMounted(async () => {
  await fetchProjects()
})
</script>

<style lang="scss" scoped>
.projects-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .header-left {
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

    .add-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      border: none;
      border-radius: 10px;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
      }
    }
  }

  .search-bar {
    display: flex;
    max-width: 400px;
    margin-bottom: 20px;
    border-radius: 10px;
    overflow: hidden;
    border: 2px solid #e5e7eb;

    .search-input {
      flex: 1;
      padding: 12px 16px;
      border: none;
      outline: none;
      font-size: 0.875rem;

      &::placeholder {
        color: #9ca3af;
      }
    }

    .search-btn {
      padding: 12px 20px;
      background: #f3f4f6;
      border: none;
      cursor: pointer;
      color: #6b7280;
      transition: all 0.3s ease;

      &:hover {
        background: #e5e7eb;
      }
    }
  }

  .table-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    overflow-x: auto;

    .data-table {
      width: 100%;
      border-collapse: collapse;

      thead tr {
        border-bottom: 2px solid #e5e7eb;

        th {
          text-align: left;
          padding: 12px 16px;
          font-weight: 600;
          color: #374151;
          font-size: 0.875rem;
        }
      }

      tbody tr {
        border-bottom: 1px solid #f3f4f6;

        &:hover {
          background: #f9fafb;
        }

        td {
          padding: 16px;
          vertical-align: middle;

          .cover-img {
            width: 60px;
            height: 60px;
            border-radius: 8px;
            object-fit: cover;
          }

          .cover-placeholder {
            width: 60px;
            height: 60px;
            border-radius: 8px;
            background: #f3f4f6;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #9ca3af;
          }

          .tag {
            display: inline-block;
            padding: 4px 8px;
            background: #f3f4f6;
            color: #6b7280;
            border-radius: 6px;
            font-size: 0.75rem;
            margin-right: 4px;
          }

          .status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 500;

            &.active {
              background: #dcfce7;
              color: #16a34a;
            }

            &.inactive {
              background: #fef3c7;
              color: #d97706;
            }
          }

          .action-btn {
            padding: 8px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-right: 8px;

            &.edit {
              background: #dbeafe;
              color: #2563eb;

              &:hover {
                background: #bfdbfe;
              }
            }

            &.delete {
              background: #fee2e2;
              color: #dc2626;

              &:hover {
                background: #fecaca;
              }
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
        margin-top: 12px;
      }
    }
  }

  .modal-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;

    .modal-content {
      background: white;
      border-radius: 16px;
      width: 90%;
      max-width: 500px;
      max-height: 90vh;
      overflow-y: auto;

      .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        border-bottom: 1px solid #e5e7eb;

        h4 {
          font-size: 1.125rem;
          font-weight: 600;
          color: #111827;
        }

        .close-btn {
          padding: 6px;
          background: #f3f4f6;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          color: #6b7280;
          transition: all 0.3s ease;

          &:hover {
            background: #e5e7eb;
          }
        }
      }

      .modal-form {
        padding: 24px;

        .form-row {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 16px;
        }

        .form-group {
          margin-bottom: 16px;

          label {
            display: block;
            font-weight: 500;
            color: #374151;
            font-size: 0.875rem;
            margin-bottom: 8px;
          }

          .form-input, .form-select {
            width: 100%;
            padding: 10px 14px;
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            font-size: 0.875rem;
            outline: none;
            transition: all 0.3s ease;

            &:focus {
              border-color: #667eea;
            }
          }

          .form-textarea {
            width: 100%;
            padding: 10px 14px;
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            font-size: 0.875rem;
            outline: none;
            resize: vertical;
          }

          .cover-upload {
            position: relative;
            display: inline-block;

            .cover-preview {
              width: 150px;
              height: 100px;
              border-radius: 8px;
              object-fit: cover;
            }

            .cover-placeholder {
              width: 150px;
              height: 100px;
              border-radius: 8px;
              background: #f3f4f6;
              display: flex;
              align-items: center;
              justify-content: center;
              color: #9ca3af;
              border: 2px dashed #d1d5db;
            }

            .cover-input {
              display: none;
            }

            .upload-btn {
              position: absolute;
              bottom: 8px;
              right: 8px;
              display: flex;
              align-items: center;
              gap: 4px;
              padding: 4px 8px;
              background: #667eea;
              color: white;
              border-radius: 8px;
              font-size: 0.7rem;
              cursor: pointer;
            }
          }
        }

        .form-actions {
          display: flex;
          justify-content: flex-end;
          gap: 12px;
          margin-top: 20px;

          .cancel-btn {
            padding: 10px 20px;
            background: #f3f4f6;
            color: #374151;
            border: none;
            border-radius: 8px;
            font-size: 0.875rem;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover {
              background: #e5e7eb;
            }
          }

          .submit-btn {
            padding: 10px 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 0.875rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 8px;

            &:hover:not(:disabled) {
              transform: translateY(-2px);
            }

            &:disabled {
              opacity: 0.7;
              cursor: not-allowed;
            }
          }
        }
      }
    }
  }
}
</style>
