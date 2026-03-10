import { defineStore } from 'pinia'
import { getRules, createRule, updateRule, deleteRule, addCondition, deleteCondition, addNestedCondition } from '@/api/rule'

export const useRuleStore = defineStore('rule', {
  state: () => ({
    rules: [],
    currentRule: null,
    loading: false
  }),

  actions: {
    async fetchRules(sceneId) {
      this.loading = true
      try {
        this.rules = await getRules(sceneId)
      } finally {
        this.loading = false
      }
    },

    async createRule(sceneId, data) {
      return await createRule(sceneId, data)
    },

    async updateRule(sceneId, id, data) {
      return await updateRule(sceneId, id, data)
    },

    async deleteRule(sceneId, id) {
      await deleteRule(sceneId, id)
      await this.fetchRules(sceneId)
    },

    async addCondition(ruleId, data) {
      return await addCondition(ruleId, data)
    },

    async addNestedCondition(parentConditionId, data) {
      return await addNestedCondition(parentConditionId, data)
    },

    async deleteCondition(conditionId) {
      await deleteCondition(conditionId)
    }
  }
})