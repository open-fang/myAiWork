<template>
  <div class="condition-builder">
    <div class="builder-header">
      <span class="title">条件构建器</span>
      <el-button size="small" @click="addRootCondition">
        <el-icon><Plus /></el-icon>
        添加条件
      </el-button>
    </div>

    <div class="condition-tree">
      <div v-for="(condition, index) in localConditions" :key="index" class="condition-node">
        <ConditionItem
          :condition="condition"
          :depth="0"
          :index="index"
          :is-last="index === localConditions.length - 1"
          @update="updateCondition(index, $event)"
          @remove="removeCondition(index)"
          @add-child="addChildCondition(index, $event)"
        />
        <div v-if="index < localConditions.length - 1 && condition.logicOperator" class="logic-divider">
          <el-select v-model="condition.logicOperator" size="small" style="width: 80px;">
            <el-option label="且 (AND)" value="AND" />
            <el-option label="或 (OR)" value="OR" />
          </el-select>
        </div>
      </div>
    </div>

    <el-empty v-if="localConditions.length === 0" description="暂无条件，请点击上方按钮添加" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import ConditionItem from './ConditionItem.vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const localConditions = ref([...props.modelValue])

watch(() => props.modelValue, (val) => {
  localConditions.value = [...val]
}, { deep: true })

watch(localConditions, (val) => {
  emit('update:modelValue', val)
}, { deep: true })

function addRootCondition() {
  localConditions.value.push({
    fieldName: '',
    operator: 'EQ',
    value: '',
    logicOperator: localConditions.value.length > 0 ? 'AND' : null,
    children: []
  })
}

function updateCondition(index, updated) {
  localConditions.value[index] = updated
}

function removeCondition(index) {
  localConditions.value.splice(index, 1)
}

function addChildCondition(parentIndex, childCondition) {
  if (!localConditions.value[parentIndex].children) {
    localConditions.value[parentIndex].children = []
  }
  localConditions.value[parentIndex].children.push(childCondition)
}
</script>

<style scoped>
.condition-builder {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  padding: 16px;
  background-color: #fafafa;
}

.builder-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.title {
  font-weight: 600;
  font-size: 14px;
}

.condition-tree {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.condition-node {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.logic-divider {
  display: flex;
  justify-content: center;
  padding: 4px 0;
}
</style>