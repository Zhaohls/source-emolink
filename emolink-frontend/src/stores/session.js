import { defineStore } from 'pinia'
import { getUserInfo } from '@/api/user'
import { resolveMediaUrl } from '@/utils/media'

const USER_STORAGE_KEY = 'emolink_user'

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch (error) {
    localStorage.removeItem(USER_STORAGE_KEY)
    return null
  }
}

const getDisplayName = (user) => {
  if (!user) return '当前旅人'
  return user.nickname || user.username || '当前旅人'
}

export const useSessionStore = defineStore('session', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: readStoredUser(),
    role: localStorage.getItem('role') || 'user',
  }),

  getters: {
    isAuthenticated: (state) => Boolean(state.token),
    isAdmin: (state) => state.role === 'admin',
    displayName: (state) => getDisplayName(state.user),
    avatarText: (state) => getDisplayName(state.user).slice(0, 1),
    avatarUrl: (state) => resolveMediaUrl(state.user?.avatar),
  },

  actions: {
    setToken(token) {
      this.token = token || ''
      if (this.token) {
        localStorage.setItem('token', this.token)
      } else {
        localStorage.removeItem('token')
      }
    },

    setUser(user) {
      this.user = user || null
      if (this.user) {
        localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(this.user))
      } else {
        localStorage.removeItem(USER_STORAGE_KEY)
      }
    },

    setRole(role) {
      this.role = role || 'user'
      if (this.role) {
        localStorage.setItem('role', this.role)
      } else {
        localStorage.removeItem('role')
      }
    },

    setSession(payload) {
      this.setToken(payload?.token || '')
      this.setUser(payload || null)
      this.setRole(payload?.role || 'user')
    },

    clearSession() {
      this.setToken('')
      this.setUser(null)
      this.setRole('user')
    },

    async fetchUser(force = false) {
      if (!this.token) {
        this.clearSession()
        return null
      }
      if (this.user && !force) {
        return this.user
      }

      const { data } = await getUserInfo()
      this.setUser(data)
      this.setRole(data?.role || this.role || 'user')
      return data
    },
  },
})
