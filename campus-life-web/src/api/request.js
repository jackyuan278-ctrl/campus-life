import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('campus_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端 401 走业务码（HTTP 200 + code=401），要和 HTTP 401 同等处理
    if (res.code === 401) {
      ElMessage.warning(res.msg || '请先登录')
      localStorage.removeItem('campus_token')
      window.location.href = '/login'
      return Promise.reject(new Error(res.msg))
    }
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res.data
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.warning('请先登录')
      localStorage.removeItem('campus_token')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
