<template>
  <el-select
    v-model="selectedValues"
    multiple
    collapse-tags
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    @change="handleChange"
  >
    <el-option
      v-for="item in options"
      :key="item.code"
      :label="item.name"
      :value="item.code"
    />
  </el-select>
</template>

<script>
export default {
  name: 'FlatMultiSelect',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    options: {
      type: Array,
      default: () => []
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    disabled: {
      type: Boolean,
      default: false
    },
    clearable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      selectedValues: []
    }
  },
  watch: {
    value: {
      handler(val) {
        this.selectedValues = val || []
      },
      immediate: true
    }
  },
  methods: {
    handleChange(val) {
      this.$emit('input', val)
      this.$emit('change', val)
    }
  }
}
</script>