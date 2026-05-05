
<template>
  <div class="blogs-page">
    <div class="page-header">
      <div class="header-left">
        <h3>博客管理</h3>
        <p>管理您的博客文章</p>
      </div>
      <button class="add-btn" @click="openAddModal">
        <Plus :size="18" />
        <span>添加文章</span>
      </button>
    </div>

    <div class="search-bar">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索文章标题..." 
        class="search-input"
        @keyup.enter="fetchBlogs"
      />
      <button class="search-btn" @click="fetchBlogs">
        <Search :size="16" />
      </button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>标题</th>
            <th>分类</th>
            <th>标签</th>
            <th>浏览量</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="blog in blogs" :key="blog.id">
            <td class="title-cell">{{ blog.title }}</td>
            <td>{{ blog.category }}</td>
            <td>
              <span v-for="tag in getTags(blog.tags)" :key="tag" class="tag">{{ tag }}</span>
            </td>
            <td>{{ blog.views }}</td>
            <td>
              <span class="status" :class="blog.status === 1 ? 'active' : 'inactive'">
                {{ blog.status === 1 ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>{{ formatDate(blog.createdAt) }}</td>
            <td>
              <button class="action-btn edit" @click="openEditModal(blog)">
                <Pencil :size="16" />
              </button>
              <button class="action-btn delete" @click="handleDelete(blog.id)">
                <Trash2 :size="16" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="blogs.length === 0" class="empty-state">
        <FileText :size="48" />
        <p>暂无博客文章</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content blog-modal" @click.stop>
        <div class="modal-header">
          <h4>{{ isEdit ? '编辑文章' : '添加文章' }}</h4>
          <button class="close-btn" @click="closeModal">
            <X :size="18" />
          </button>
        </div>

        <form class="modal-form" @submit.prevent="handleSubmit">
          <div class="form-row">
            <div class="form-group">
              <label>标题 *</label>
              <input v-model="form.title" type="text" placeholder="请输入文章标题" class="form-input" />
            </div>
            <div class="form-group">
              <label>Slug</label>
              <input v-model="form.slug" type="text" placeholder="URL别名" class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>分类</label>
              <input v-model="form.category" type="text" placeholder="请输入分类" class="form-input" />
            </div>
            <div class="form-group">
              <label>标签</label>
              <input v-model="form.tags" type="text" placeholder="用逗号分隔" class="form-input" />
            </div>
          </div>

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
            <label>摘要</label>
            <textarea v-model="form.summary" rows="2" placeholder="请输入文章摘要" class="form-textarea"></textarea>
          </div>

          <div class="form-group">
            <label>内容 *</label>
            <div class="editor-container">
              <div class="editor-tabs">
                <button 
                  class="tab-btn" 
                  :class="{ active: showPreview }"
                  @click="showPreview = !showPreview"
                >
                  {{ showPreview ? '编辑' : '预览' }}
                </button>
              </div>
              <div v-if="!showPreview" class="editor-wrapper">
                <textarea 
                  v-model="form.content" 
                  rows="15" 
                  placeholder="请输入文章内容（支持Markdown）" 
                  class="form-textarea editor"
                ></textarea>
              </div>
              <div v-else class="preview-wrapper" v-html="renderedContent"></div>
            </div>
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
import { ref, computed, onMounted } from 'vue'
import { Plus, Search, Image, Pencil, Trash2, X, Upload, FileText, Loader2 } from 'lucide-vue-next'
import { marked } from 'marked'
import { blogApi, fileApi } from '@/api'

const blogs = ref([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const showPreview = ref(false)

const form = ref({
  id: null,
  title: '',
  slug: '',
  content: '',
  summary: '',
  cover: '',
  category: '',
  tags: '',
  status: 1
})

marked.setOptions({
  gfm: true,
  breaks: true
})

const renderedContent = computed(() => {
  if (!form.value.content) return ''
  return marked(form.value.content)
})

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
    const res = await blogApi.adminList({ keyword: keyword.value })
    blogs.value = res.data.records
  } catch (e) {
    console.error('Failed to fetch blogs:', e)
  }
}

const openAddModal = () => {
  isEdit.value = false
  showPreview.value = false
  form.value = {
    id: null,
    title: '',
    slug: '',
    content: '',
    summary: '',
    cover: '',
    category: '',
    tags: '',
    status: 1
  }
  showModal.value = true
}

const openEditModal = (blog) => {
  isEdit.value = true
  showPreview.value = false
  form.value = { ...blog }
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
  if (!form.value.title || !form.value.content) {
    alert('请填写标题和内容')
    return
  }

  loading.value = true

  try {
    if (isEdit.value) {
      await blogApi.update(form.value.id, form.value)
    } else {
      await blogApi.create(form.value)
    }
    closeModal()
    await fetchBlogs()
    alert('保存成功')
  } catch (e) {
    alert('保存失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这篇文章吗？')) return

  try {
    await blogApi.delete(id)
    await fetchBlogs()
    alert('删除成功')
  } catch (e) {
    alert('删除失败：' + (e.response?.data?.message || e.message))
  }
}

onMounted(async () => {
  await fetchBlogs()
})
</script>

<style lang="scss" scoped>
.blogs-page {
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

          &.title-cell {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
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
    overflow-y: auto;

    .modal-content {
      background: white;
      border-radius: 16px;
      width: 90%;
      max-width: 800px;
      max-height: 90vh;
      overflow-y: auto;

      &.blog-modal {
        max-width: 900px;
      }

      .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px;
        border-bottom: 1px solid #e5e7eb;
        position: sticky;
        top: 0;
        background: white;

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

            &.editor {
              font-family: 'Fira Code', monospace;
              font-size: 0.875rem;
              line-height: 1.6;
              min-height: 300px;
            }
          }

          .cover-upload {
            position: relative;
            display: inline-block;

            .cover-preview {
              width: 200px;
              height: 120px;
              border-radius: 8px;
              object-fit: cover;
            }

            .cover-placeholder {
              width: 200px;
              height: 120px;
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

          .editor-container {
            border: 2px solid #e5e7eb;
            border-radius: 8px;
            overflow: hidden;

            .editor-tabs {
              display: flex;
              border-bottom: 1px solid #e5e7eb;

              .tab-btn {
                padding: 10px 20px;
                border: none;
                background: #f9fafb;
                color: #6b7280;
                cursor: pointer;
                font-size: 0.875rem;
                transition: all 0.3s ease;

                &.active {
                  background: white;
                  color: #667eea;
                  border-bottom: 2px solid #667eea;
                }
              }
            }

            .editor-wrapper {
              padding: 16px;
            }

            .preview-wrapper {
              padding: 16px;
              min-height: 300px;
              background: #f9fafb;
              color: #374151;
              line-height: 1.8;

              h1, h2, h3 { margin: 16px 0 8px; }
              p { margin-bottom: 12px; }
              code { background: #e5e7eb; padding: 2px 6px; border-radius: 4px; }
              pre { background: #1f2937; color: #e5e7eb; padding: 16px; border-radius: 8px; overflow-x: auto; }
              blockquote { border-left: 4px solid #667eea; padding-left: 12px; color: #6b7280; }
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
