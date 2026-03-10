import api from './index'

// Get all authorization letters
export function getAuthLetters() {
  return api.get('/auth-letters')
}

// Get authorization letter by ID
export function getAuthLetter(id) {
  return api.get(`/auth-letters/${id}`)
}

// Create authorization letter
export function createAuthLetter(data) {
  return api.post('/auth-letters', data)
}

// Update authorization letter
export function updateAuthLetter(id, data) {
  return api.put(`/auth-letters/${id}`, data)
}

// Delete authorization letter
export function deleteAuthLetter(id) {
  return api.delete(`/auth-letters/${id}`)
}

// Publish authorization letter
export function publishAuthLetter(id) {
  return api.post(`/auth-letters/${id}/publish`)
}

// Match scenes
export function matchScenes(data) {
  return api.post('/auth-letters/match', data)
}