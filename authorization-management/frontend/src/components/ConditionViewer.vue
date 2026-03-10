<template>
  <div class="condition-viewer">
    <div v-if="!conditions || conditions.length === 0" class="empty-text">
      暂无条件
    </div>
    <div v-else class="condition-list">
      <div v-for="(condition, index) in conditions" :key="condition.id || index" class="condition-item">
        <ConditionNode :condition="condition" :is-last="index === conditions.length - 1" />
        <div v-if="!condition.isLast && index < conditions.length - 1" class="logic-operator">
          {{ condition.logicOperator === 'AND' ? '且' : '或' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, defineComponent } from 'vue'

const props = defineProps({
  conditions: {
    type: Array,
    default: () => []
  }
})

const ConditionNode = defineComponent({
  name: 'ConditionNode',
  props: {
    condition: {
      type: Object,
      required: true
    },
    isLast: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    return () => {
      const condition = props.condition

      if (condition.children && condition.children.length > 0) {
        // Nested condition group
        return h('div', { class: 'condition-group' }, [
          h('span', { class: 'group-label' }, condition.logicOperator === 'AND' ? '( 且 )' : '( 或 )'),
          h('div', { class: 'group-content' },
            condition.children.map((child, index) =>
              h('div', { class: 'nested-item' }, [
                h(ConditionNode, { condition: child, isLast: index === condition.children.length - 1 }),
                index < condition.children.length - 1 && condition.logicOperator ?
                  h('span', { class: 'nested-operator' }, condition.logicOperator === 'AND' ? '且' : '或') : null
              ])
            )
          )
        ])
      }

      // Simple condition
      const operatorText = {
        'EQ': '=',
        'NE': '!=',
        'GT': '>',
        'GE': '>=',
        'LT': '<',
        'LE': '<=',
        'LIKE': '包含',
        'IN': '属于',
        'NOT_IN': '不属于'
      }

      return h('div', { class: 'simple-condition' }, [
        h('span', { class: 'field-name' }, condition.fieldName),
        h('span', { class: 'operator' }, operatorText[condition.operator] || condition.operator),
        h('span', { class: 'value' }, condition.value)
      ])
    }
  }
})
</script>

<style scoped>
.condition-viewer {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.empty-text {
  color: #909399;
  font-size: 13px;
}

.condition-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.condition-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.simple-condition {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #ecf5ff;
  border-radius: 4px;
  font-size: 13px;
}

.field-name {
  color: #409eff;
  font-weight: 500;
}

.operator {
  color: #67c23a;
}

.value {
  color: #e6a23c;
}

.logic-operator {
  color: #909399;
  font-size: 12px;
}

.condition-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
}

.group-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.group-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nested-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nested-operator {
  color: #909399;
  font-size: 11px;
}
</style>