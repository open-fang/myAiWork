import api from './index'

// Get all rules by scene ID
export function getRules(sceneId) {
  return api.get(`/scenes/${sceneId}/rules`)
}

// Get rule by ID
export function getRule(sceneId, id) {
  return api.get(`/scenes/${sceneId}/rules/${id}`)
}

// Create rule
export function createRule(sceneId, data) {
  return api.post(`/scenes/${sceneId}/rules`, data)
}

// Update rule
export function updateRule(sceneId, id, data) {
  return api.put(`/scenes/${sceneId}/rules/${id}`, data)
}

// Delete rule
export function deleteRule(sceneId, id) {
  return api.delete(`/scenes/${sceneId}/rules/${id}`)
}

// Add condition to rule
export function addCondition(ruleId, data) {
  return api.post(`/scenes/0/rules/${ruleId}/conditions`, data)
}

// Add nested condition
export function addNestedCondition(parentConditionId, data) {
  return api.post(`/scenes/0/rules/conditions/${parentConditionId}/children`, data)
}

// Delete condition
export function deleteCondition(conditionId) {
  return api.delete(`/scenes/0/rules/conditions/${conditionId}`)
}