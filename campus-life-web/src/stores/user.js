import { defineStore } from 'pinia'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('campus_token') || '',
    user: JSON.parse(localStorage.getItem('campus_user') || 'null')
  }),
  actions: {
    async login(form) {
      const data = await userApi.login(form)
      this.token = data.token
      this.user = { userId: data.userId, username: data.username, nickname: data.nickname }
      localStorage.setItem('campus_token', data.token)
      localStorage.setItem('campus_user', JSON.stringify(this.user))
    },
    async register(form) {
      await userApi.register(form)
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('campus_token')
      localStorage.removeItem('campus_user')
    }
  }
})
