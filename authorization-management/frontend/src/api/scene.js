import api from './index'

// Get all scenes by authorization letter ID
export function getScenes(authLetterId) {
  return api.get(`/auth-letters/${authLetterId}/scenes`)
}

// Get scene by ID
export function getScene(authLetterId, id) {
  return api.get(`/auth-letters/${authLetterId}/scenes/${id}`)
}

// Create scene
export function createScene(authLetterId, data) {
  return api.post(`/auth-letters/${authLetterId}/scenes`, data)
}

// Update scene
export function updateScene(authLetterId, id, data) {
  return api.put(`/auth-letters/${authLetterId}/scenes/${id}`, data)
}

// Delete scene
export function deleteScene(authLetterId, id) {
  return api.delete(`/auth-letters/${authLetterId}/scenes/${id}`)
}