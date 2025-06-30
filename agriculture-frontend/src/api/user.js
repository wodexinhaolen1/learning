import axios from 'axios'

const BASE_URL = 'http://localhost:8080'

const api = axios.create({
  baseURL: `${BASE_URL}/api`
})

// 请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const userApi = {
  // 登录
  login(username, password) {
    return api.post('/users/login', { username, password })
  },

  // 注册
  register(userData) {
    return api.post('/users/register', userData)
  },

  // 重置密码
  resetPassword(username, email, newPassword) {
    return api.post('/users/reset-password', {
      username,
      email,
      newPassword
    })
  },

  // 验证用户名和邮箱
  verifyUserEmail(username, email) {
    return api.post('/users/verify-email', {
      username,
      email
    })
  },

  // 检查用户名是否存在
  checkUsername(username) {
    return api.get(`/users/check/${username}`)
  }
} 