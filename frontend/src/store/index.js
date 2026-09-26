import { defineStore } from 'pinia'
/**
 * Pinia 状态管理（用户模块）
 * 管理登录用户信息、菜单、权限的全局状态
 */
import { login as loginApi, userInfo as userInfoApi, logout as logoutApi } from '../api/login'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    menus: [],
    permissions: []
  }),
  actions: {
    async login(credentials) {
      const res = await loginApi(credentials)
      if (res.code === 200) {
        const data = res.data
        this.user = data.user
        this.menus = data.menus || []
        this.permissions = data.permissions || []
        sessionStorage.setItem('user', JSON.stringify(data.user))
        sessionStorage.setItem('menus', JSON.stringify(data.menus))
        sessionStorage.setItem('permissions', JSON.stringify(data.permissions))
        return true
      }
      return false
    },
    async fetchUserInfo() {
      try {
        const res = await userInfoApi()
        if (res.code === 200) {
          const data = res.data
          this.user = data.user
          this.menus = data.menus || []
          this.permissions = data.permissions || []
          sessionStorage.setItem('user', JSON.stringify(data.user))
          sessionStorage.setItem('menus', JSON.stringify(data.menus))
          sessionStorage.setItem('permissions', JSON.stringify(data.permissions))
        }
      } catch (e) {
        this.logout()
      }
    },
    logout() {
      logoutApi().catch(() => {})
      this.user = null
      this.menus = []
      this.permissions = []
      sessionStorage.clear()
    },
    hasPerm(perm) {
      return this.permissions.includes(perm) || this.permissions.includes('*')
    },
    initFromSession() {
      const user = sessionStorage.getItem('user')
      const menus = sessionStorage.getItem('menus')
      const permissions = sessionStorage.getItem('permissions')
      if (user) this.user = JSON.parse(user)
      if (menus) this.menus = JSON.parse(menus)
      if (permissions) this.permissions = JSON.parse(permissions)
    }
  }
})
