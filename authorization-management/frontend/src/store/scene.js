import { defineStore } from 'pinia'
import { getScenes, createScene, updateScene, deleteScene } from '@/api/scene'

export const useSceneStore = defineStore('scene', {
  state: () => ({
    scenes: [],
    currentScene: null,
    loading: false
  }),

  actions: {
    async fetchScenes(authLetterId) {
      this.loading = true
      try {
        this.scenes = await getScenes(authLetterId)
      } finally {
        this.loading = false
      }
    },

    async createScene(authLetterId, data) {
      return await createScene(authLetterId, data)
    },

    async updateScene(authLetterId, id, data) {
      return await updateScene(authLetterId, id, data)
    },

    async deleteScene(authLetterId, id) {
      await deleteScene(authLetterId, id)
      await this.fetchScenes(authLetterId)
    }
  }
})