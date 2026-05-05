
import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

instance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/admin/login'
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  login: (data) => instance.post('/auth/login', data)
}

export const profileApi = {
  get: () => instance.get('/public/profile'),
  getAdmin: () => instance.get('/admin/profile'),
  update: (data) => instance.put('/admin/profile', data)
}

export const blogApi = {
  list: (params) => instance.get('/public/blogs', { params }),
  listAll: () => instance.get('/public/blogs/all'),
  getBySlug: (slug) => instance.get(`/public/blog/${slug}`),
  adminList: (params) => instance.get('/admin/blogs', { params }),
  get: (id) => instance.get(`/admin/blog/${id}`),
  create: (data) => instance.post('/admin/blog', data),
  update: (id, data) => instance.put(`/admin/blog/${id}`, data),
  delete: (id) => instance.delete(`/admin/blog/${id}`)
}

export const projectApi = {
  list: () => instance.get('/public/projects'),
  adminList: (params) => instance.get('/admin/projects', { params }),
  get: (id) => instance.get(`/admin/project/${id}`),
  create: (data) => instance.post('/admin/project', data),
  update: (id, data) => instance.put(`/admin/project/${id}`, data),
  delete: (id) => instance.delete(`/admin/project/${id}`)
}

export const adminApi = {
  list: (params) => instance.get('/admin/list', { params }),
  get: (id) => instance.get(`/admin/${id}`),
  create: (data) => instance.post('/admin', data),
  update: (id, data) => instance.put(`/admin/${id}`, data),
  delete: (id) => instance.delete(`/admin/${id}`)
}

export const logApi = {
  list: (params) => instance.get('/admin/logs', { params })
}

export const fileApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return instance.post('/admin/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
