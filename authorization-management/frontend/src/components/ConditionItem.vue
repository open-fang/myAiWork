<template>
  <div class="condition-item" :style="{ marginLeft: depth * 24 + 'px' }">
    <div class="condition-row">
      <!-- Field Name -->
      <el-input
        v-model="localCondition.fieldName"
        placeholder="字段名"
        style="width: 120px;"
        size="small"
        @input="handleChange"
      />

      <!-- Operator -->
      <el-select
        v-model="localCondition.operator"
        placeholder="操作符"
        style="width: 100px;"
        size="small"
        @change="handleChange"
      >
        <el-option label="等于" value="EQ" />
        <el-option label="不等于" value="NE" />
        <el-option label="大于" value="GT" />
        <el-option label="大于等于" value="GE" />
        <el-option label="小于" value="LT" />
        <el-option label="小于等于" value="LE" />
        <el-option label="包含" value="LIKE" />
        <el-option label="属于" value="IN" />
      </el-select>

      <!-- Value -->
      <el-input
        v-model="localCondition.value"
        placeholder="值"
        style="width: 120px;"
        size="small"
        @input="handleChange"
      />

      <!-- Actions -->
      <el-button-group size="small">
        <el-button @click="addChild" :disabled="!isGroup">
          <el-icon><Plus /></el-icon>
        </el-button>
        <el-button type="danger" @click="remove">
          <el-icon><Delete /></el-icon>
        </el-button>
      </el-button-group>
    </div>

    <!-- Nested Children -->
    <div v-if="localCondition.children && localCondition.children.length > 0" class="children-container">
      <div class="children-logic">
        <el-select v-model="localCondition.logicOperator" size="small" style="width: 80px;">
          <el-option label="且" value="AND" />
          <el-option label="或" value="OR" />
        </el-select>
      </div>
      <ConditionItem
        v-for="(child, idx) in localCondition.children"
        :key="idx"
        :condition="child"
        :depth="depth + 1"
        :index="idx"
        @update="updateChild(idx, $event)"
        @remove="removeChild(idx)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  condition: {
    type: Object,
    required: true
  },
  depth: {
    type: Number,
    default: 0
  },
  index: {
    type: Number,
    default: 0
  },
  isLast: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update', 'remove', 'add-child'])

const localCondition = ref({ ...props.condition })

const isGroup = computed(() => {
  return localCondition.value.children && localCondition.value.children.length > 0
})

watch(() => props.condition, (val) => {
  localCondition.value = { ...val }
}, { deep: true })

function handleChange() {
  emit('update', localCondition.value)
}

function addChild() {
  emit('add-child', {
    fieldName: '',
    operator: 'EQ',
    value: '',
    children: []
  })
}

function remove() {
  emit('remove')
}

function updateChild(index, updated) {
  localCondition.value.children[index] = updated
  handleChange()
}

function removeChild(index) {
  localCondition.value.children.splice(index, 1)
  handleChange()
}
</script>

<style scoped>
.condition-item {
  padding: 8px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.condition-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.children-container {
  margin-top: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.children-logic {
  margin-bottom: 8px;
}
</style>