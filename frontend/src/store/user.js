import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, setUserInfo, getUserInfo } from '@/utils/auth'
import { login, logout, getUserInfo as fetchUserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getUserInfo() || null
  }),

  actions: {
    /**
     * 登录
     */
    async login(loginData) {
      const res = await login(loginData)
      this.token = res.data.token
      this.userInfo = res.data.userInfo
      setToken(this.token)
      setUserInfo(this.userInfo)
      return res
    },

    /**
     * 登出
     */
    async logout() {
      try {
        await logout()
      } catch (e) {
        // 忽略登出接口错误
      }
      this.token = ''
      this.userInfo = null
      removeToken()
    },

    /**
     * 刷新用户信息
     */
    async refreshUserInfo() {
      const res = await fetchUserInfo()
      this.userInfo = res.data
      setUserInfo(this.userInfo)
    }
  }
})