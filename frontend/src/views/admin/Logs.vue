<template>
  <div class="logs-page">
    <div class="page-header">
      <h3>操作日志</h3>
      <p>查看系统操作记录</p>
    </div>

    <div class="search-bar">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索操作描述..." 
        class="search-input"
        @keyup.enter="fetchLogs"
      />
      <button class="search-btn" @click="fetchLogs">
        <Search :size="16" />
      </button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>用户名</th>
            <th>操作类型</th>
            <th>操作描述</th>
            <th>IP地址</th>
            <th>操作时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs" :key="log.id">
            <td>{{ log.username }}</td>
            <td>
              <span class="type" :class="getTypeClass(log.operationType)">
                {{ getTypeLabel(log.operationType) }}
              </span>
            </td>
            <td class="desc-cell">{{ log.description }}</td>
            <td>{{ log.ipAddress || '-' }}</td>
            <td>{{ formatDate(log.createdAt) }}</td>
          </tr>
        </tbody>
      </table>

      <div v-if="logs.length === 0" class="empty-state">
        <FileLog :size="48" />
        <p>暂无操作日志</p>
      </div>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, FileLog, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { logApi } from '@/api'

const logs = ref([])
const keyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const getTypeLabel = (type) => {
  const types = {
    CREATE: '创建',
    UPDATE: '更新',
    DELETE: '删除',
    LOGIN: '登录',
    LOGOUT: '登出'
  }
  return types[type] || type
}

const getTypeClass = (type) => {
  const classes = {
    CREATE: 'create',
    UPDATE: 'update',
    DELETE: 'delete',
    LOGIN: 'login',
    LOGOUT: 'logout'
  }
  return classes[type] || 'default'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchLogs = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    }
    const res = await logApi.list(params)
    logs.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error('Failed to fetch logs:', e)
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchLogs()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchLogs()
  }
}

onMounted(async () => {
  await fetchLogs()
})
</script>

<style lang="scss" scoped>
.logs-page {
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
          padding: 14px 16px;
          vertical-align: middle;

          &.desc-cell {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .type {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 500;

            &.create {
              background: #dcfce7;
              color: #16a34a;
            }

            &.update {
              background: #dbeafe;
              color: #2563eb;
            }

            &.delete {
              background: #fee2e2;
              color: #dc2626;
            }

            &.login {
              background: #fef3c7;
              color: #d97706;
            }

            &.logout {
              background: #e0e7ff;
              color: #6366f1;
            }

            &.default {
              background: #f3f4f6;
              color: #6b7280;
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

  .pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-top: 24px;

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
    }

    .page-info {
      color: #6b7280;
      font-size: 0.875rem;
    }
  }
}
</style>