<template>
  <el-select v-model="selectedValue" :placeholder="placeholder" :disabled="disabled" @change="handleChange">
    <el-option
      v-for="level in levels"
      :key="level.value"
      :label="level.label"
      :value="level.value"
    />
  </el-select>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: null
  },
  placeholder: {
    type: String,
    default: '请选择层级'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const levels = [
  { value: 'ORGANIZATION', label: '机关' },
  { value: 'REGIONAL_DEPT', label: '地区部' },
  { value: 'REPRESENTATIVE_OFFICE', label: '代表处' },
  { value: 'OFFICE', label: '办事处' }
]

const selectedValue = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  selectedValue.value = val
})

function handleChange(val) {
  emit('update:modelValue', val)
}
</script>