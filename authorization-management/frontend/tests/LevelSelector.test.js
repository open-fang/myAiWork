import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import LevelSelector from '@/components/LevelSelector.vue'

describe('LevelSelector', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('renders correctly', () => {
    const wrapper = mount(LevelSelector)
    expect(wrapper.find('.el-select').exists()).toBe(true)
  })

  it('displays all levels', async () => {
    const wrapper = mount(LevelSelector)
    const options = wrapper.findAll('.el-option')
    expect(options.length).toBe(4)
  })

  it('emits update:modelValue when selection changes', async () => {
    const wrapper = mount(LevelSelector)
    await wrapper.vm.handleChange('ORGANIZATION')
    expect(wrapper.emitted('update:modelValue')).toBeTruthy()
    expect(wrapper.emitted('update:modelValue')[0]).toEqual(['ORGANIZATION'])
  })
})