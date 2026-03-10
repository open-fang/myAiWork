import { defineStore } from 'pinia'
import { getAuthLetters, getAuthLetter, createAuthLetter, updateAuthLetter, deleteAuthLetter, publishAuthLetter } from '@/api/authLetter'

export const useAuthLetterStore = defineStore('authLetter', {
  state: () => ({
    authLetters: [],
    currentAuthLetter: null,
    loading: false
  }),

  actions: {
    async fetchAuthLetters() {
      this.loading = true
      try {
        this.authLetters = await getAuthLetters()
      } finally {
        this.loading = false
      }
    },

    async fetchAuthLetter(id) {
      this.loading = true
      try {
        this.currentAuthLetter = await getAuthLetter(id)
      } finally {
        this.loading = false
      }
    },

    async createAuthLetter(data) {
      return await createAuthLetter(data)
    },

    async updateAuthLetter(id, data) {
      return await updateAuthLetter(id, data)
    },

    async deleteAuthLetter(id) {
      await deleteAuthLetter(id)
      await this.fetchAuthLetters()
    },

    async publishAuthLetter(id) {
      return await publishAuthLetter(id)
    }
  }
})