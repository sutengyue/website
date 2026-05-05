<template>
  <div class="admins-page">
    <div class="page-header">
      <div class="header-left">
        <h3>管理员管理</h3>
        <p>管理系统管理员账号</p>
      </div>
      <button class="add-btn" @click="openAddModal">
        <Plus :size="18" />
        <span>添加管理员</span>
      </button>
    </div>

    <div class="search-bar">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索用户名..." 
        class="search-input"
        @keyup.enter="fetchAdmins"
      />
      <button class="search-btn" @click="fetchAdmins">
        <Search :size="16" />
      </button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>邮箱</th>
            <th>角色</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in admins" :key="admin.id">
            <td>{{ admin.username }}</td>
            <td>{{ admin.email || '-' }}</td>
            <td>{{ admin.role === 'ADMIN' ? '超级管理员' : '管理员' }}</td>
            <td>
              <span class="status" :class="admin.status === 1 ? 'active' : 'inactive'">
                {{ admin.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(admin.createdAt) }}</td>
            <td>
              <button class="action-btn edit" @click="openEditModal(admin)">
                <Pencil :size="16" />
              </button>
              <button 
                class="action-btn delete" 
                @click="handleDelete(admin.id)"
                :disabled="admin.role === 'ADMIN'"
              >
                <Trash2 :size="16" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="admins.length === 0" class="empty-state">
        <Shield :size="48" />
        <p>暂无管理员</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h4>{{ isEdit ? '编辑管理员' : '添加管理员' }}</h4>
          <button class="close-btn" @click="closeModal">
            <X :size="18" />
          </button>
        </div>

        <form class="modal-form" @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>用户名 *</label>
            <input v-model="form.username" type="text" placeholder="请输入用户名" class="form-input" />
          </div>

          <div class="form-group">
            <label>邮箱</label>
            <input v-model="form.email" type="email" placeholder="请输入邮箱" class="form-input" />
          </div>

          <div class="form-group" v-if="!isEdit">
            <label>密码 *</label>
            <input v-model="form.password" type="password" placeholder="请输入密码" class="form-input" />
          </div>

          <div class="form-group">
            <label>角色</label>
            <select v-model="form.role" class="form-select">
              <option value="ADMIN">超级管理员</option>
              <option value="USER">管理员</option>
            </select>
          </div>

          <div class="form-group">
            <label>状态</label>
            <select v-model.number="form.status" class="form-select">
              <option :value="1">启用</option>
              <option :value="0">禁用</option>
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
import { Plus, Search, Pencil, Trash2, X, Shield, Loader2 } from 'lucide-vue-next'
import { adminApi } from '@/api'

const admins = ref([])
const keyword = ref('')
const showModal = ref(false)
const isEdit = ref(false)
const loading = ref(false)

const form = ref({
  id: null,
  username: '',
  password: '',
  email: '',
  role: 'USER',
  status: 1
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const fetchAdmins = async () => {
  try {
    const res = await adminApi.list({ keyword: keyword.value })
    admins.value = res.data.records
  } catch (e) {
    console.error('Failed to fetch admins:', e)
  }
}

const openAddModal = () => {
  isEdit.value = false
  form.value = {
    id: null,
    username: '',
    password: '',
    email: '',
    role: 'USER',
    status: 1
  }
  showModal.value = true
}

const openEditModal = (admin) => {
  isEdit.value = true
  form.value = { 
    ...admin,
    password: ''
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

const handleSubmit = async () => {
  if (!form.value.username) {
    alert('请输入用户名')
    return
  }
  
  if (!isEdit.value && !form.value.password) {
    alert('请输入密码')
    return
  }

  loading.value = true

  try {
    if (isEdit.value) {
      const data = { ...form.value }
      delete data.password
      await adminApi.update(form.value.id, data)
    } else {
      await adminApi.create(form.value)
    }
    closeModal()
    await fetchAdmins()
    alert('保存成功')
  } catch (e) {
    alert('保存失败：' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  if (!confirm('确定要删除这个管理员吗？')) return

  try {
    await adminApi.delete(id)
    await fetchAdmins()
    alert('删除成功')
  } catch (e) {
    alert('删除失败：' + (e.response?.data?.message || e.message))
  }
}

onMounted(async () => {
  await fetchAdmins()
})
</script>

<style lang="scss" scoped>
.admins-page {
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
              background: #fee2e2;
              color: #dc2626;
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

              &:hover:not(:disabled) {
                background: #fecaca;
              }

              &:disabled {
                opacity: 0.5;
                cursor: not-allowed;
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
      max-width: 450px;

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