<template>
  <el-popover
    v-model="visible"
    placement="bottom-start"
    trigger="click"
    width="400"
    :disabled="disabled"
  >
    <div class="tree-select-content">
      <el-tree
        ref="tree"
        :data="treeData"
        :props="treeProps"
        show-checkbox
        check-strictly
        node-key="code"
        :default-checked-keys="checkedKeys"
        @check="handleCheck"
      />
    </div>
    <el-input
      slot="reference"
      v-model="displayText"
      :placeholder="placeholder"
      :disabled="disabled"
      readonly
      clearable
      @clear="handleClear"
    >
      <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
    </el-input>
  </el-popover>
</template>

<script>
export default {
  name: 'TreeMultiSelect',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    treeData: {
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
    maxLevel: {
      type: Number,
      default: 4
    }
  },
  data() {
    return {
      visible: false,
      checkedKeys: [],
      treeProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  computed: {
    displayText() {
      if (this.checkedKeys.length === 0) {
        return ''
      }
      const names = this.getCheckedNames(this.treeData, this.checkedKeys)
      if (names.length > 3) {
        return names.slice(0, 3).join(', ') + '...'
      }
      return names.join(', ')
    }
  },
  watch: {
    value: {
      handler(val) {
        this.checkedKeys = val || []
      },
      immediate: true
    }
  },
  methods: {
    getCheckedNames(data, codes) {
      const names = []
      const traverse = (nodes) => {
        for (const node of nodes) {
          if (codes.includes(node.code)) {
            names.push(node.name)
          }
          if (node.children && node.children.length > 0) {
            traverse(node.children)
          }
        }
      }
      traverse(data)
      return names
    },
    handleCheck(data, { checkedKeys }) {
      this.checkedKeys = checkedKeys
      this.$emit('input', checkedKeys)
      this.$emit('change', checkedKeys)
    },
    handleClear() {
      this.checkedKeys = []
      this.$refs.tree.setCheckedKeys([])
      this.$emit('input', [])
      this.$emit('change', [])
    }
  }
}
</script>

<style scoped>
.tree-select-content {
  max-height: 300px;
  overflow-y: auto;
}
</style>