import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useRuleStore } from '@/store/rule'

// Mock the API
vi.mock('@/api/rule', () => ({
  getRules: vi.fn(() => Promise.resolve([])),
  createRule: vi.fn((sceneId, data) => Promise.resolve({ id: 1, sceneId, ...data })),
  updateRule: vi.fn((sceneId, id, data) => Promise.resolve({ id, ...data })),
  deleteRule: vi.fn(() => Promise.resolve()),
  addCondition: vi.fn((ruleId, data) => Promise.resolve({ id: 1, ruleId, ...data })),
  deleteCondition: vi.fn(() => Promise.resolve())
}))

describe('RuleStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('initializes with empty state', () => {
    const store = useRuleStore()
    expect(store.rules).toEqual([])
    expect(store.currentRule).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('creates a rule', async () => {
    const store = useRuleStore()
    const data = { name: 'Test Rule' }
    const result = await store.createRule(1, data)

    expect(result.name).toBe('Test Rule')
    expect(result.sceneId).toBe(1)
  })

  it('adds a condition', async () => {
    const store = useRuleStore()
    const data = { fieldName: 'amount', operator: 'GT', value: '1000' }
    const result = await store.addCondition(1, data)

    expect(result.fieldName).toBe('amount')
    expect(result.operator).toBe('GT')
    expect(result.value).toBe('1000')
  })
})