import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useSceneStore } from '@/store/scene'

// Mock the API
vi.mock('@/api/scene', () => ({
  getScenes: vi.fn(() => Promise.resolve([])),
  createScene: vi.fn((authLetterId, data) => Promise.resolve({ id: 1, authLetterId, ...data })),
  updateScene: vi.fn((authLetterId, id, data) => Promise.resolve({ id, ...data })),
  deleteScene: vi.fn(() => Promise.resolve())
}))

describe('SceneStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useSceneStore()
    expect(store.scenes).toEqual([])
    expect(store.currentScene).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('creates a scene', async () => {
    const store = useSceneStore()
    const data = { name: 'Test Scene', decisionLevel: 1 }
    const result = await store.createScene(1, data)

    expect(result.name).toBe('Test Scene')
    expect(result.decisionLevel).toBe(1)
    expect(result.authLetterId).toBe(1)
  })
})