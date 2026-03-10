import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useAuthLetterStore } from '@/store/authLetter'

// Mock the API
vi.mock('@/api/authLetter', () => ({
  getAuthLetters: vi.fn(() => Promise.resolve([])),
  getAuthLetter: vi.fn((id) => Promise.resolve({ id, name: 'Test Letter' })),
  createAuthLetter: vi.fn((data) => Promise.resolve({ id: 1, ...data })),
  updateAuthLetter: vi.fn((id, data) => Promise.resolve({ id, ...data })),
  deleteAuthLetter: vi.fn(() => Promise.resolve()),
  publishAuthLetter: vi.fn((id) => Promise.resolve({ id, status: 'PUBLISHED' }))
}))

describe('AuthLetterStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useAuthLetterStore()
    expect(store.authLetters).toEqual([])
    expect(store.currentAuthLetter).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('creates an auth letter', async () => {
    const store = useAuthLetterStore()
    const data = { name: 'Test Auth Letter' }
    const result = await store.createAuthLetter(data)

    expect(result.name).toBe('Test Auth Letter')
    expect(result.id).toBe(1)
  })

  it('fetches auth letters', async () => {
    const store = useAuthLetterStore()
    await store.fetchAuthLetters()

    expect(store.authLetters).toEqual([])
    expect(store.loading).toBe(false)
  })

  it('publishes an auth letter', async () => {
    const store = useAuthLetterStore()
    const result = await store.publishAuthLetter(1)

    expect(result.status).toBe('PUBLISHED')
  })
})