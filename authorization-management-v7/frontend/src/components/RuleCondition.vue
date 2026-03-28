<template>
  <div class="rule-condition">
    <!-- 条件列表 -->
    <div class="conditions-container">
      <template v-for="(condition, index) in conditions">
        <!-- 逻辑连接词 -->
        <span
          v-if="index > 0"
          :key="'logic-' + index"
          class="logic-link"
          @click="toggleLogic(condition)"
        >
          [{{ condition.logicType === 'AND' ? '且' : '或' }}]
        </span>

        <!-- 条件组 -->
        <div
          v-if="condition.isGroup"
          :key="'group-' + index"
          class="condition-group"
        >
          <div class="group-header">
            <span class="group-logic" @click="toggleGroupLogic(condition)">
              [{{ condition.groupLogicType === 'AND' ? '且' : '或' }}]
            </span>
          </div>
          <RuleCondition
            :conditions="condition.children"
            :rule-param-options="ruleParamOptions"
            :operator-options="operatorOptions"
            :compare-type-options="compareTypeOptions"
            :compare-unit-options="compareUnitOptions"
            :level="level + 1"
            @update="updateChildConditions(condition, $event)"
          />
          <div class="group-actions">
            <span class="action-link" @click="addChildCondition(condition)">[+新增条件]</span>
            <span class="action-link" @click="addChildConditionGroup(condition)">[+新增子条件组]</span>
            <span class="action-link delete" @click="removeConditionGroup(index)">[移除条件组]</span>
          </div>
        </div>

        <!-- 单条件 -->
        <div
          v-else
          :key="'condition-' + index"
          class="condition-row"
        >
          <el-select
            v-model="condition.fieldCode"
            placeholder="规则字段"
            size="small"
            @change="handleFieldChange(condition)"
          >
            <el-option
              v-for="item in ruleParamOptions"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
          <el-select
            v-model="condition.operator"
            placeholder="运算符"
            size="small"
          >
            <el-option
              v-for="item in operatorOptions"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
          <el-select
            v-model="condition.compareType"
            placeholder="对比类型"
            size="small"
            @change="handleCompareTypeChange(condition)"
          >
            <el-option
              v-for="item in compareTypeOptions"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>

          <!-- 对比字段 -->
          <el-select
            v-if="condition.compareType === 'FIELD'"
            v-model="condition.compareFieldCode"
            placeholder="对比字段"
            size="small"
          >
            <el-option
              v-for="item in ruleParamOptions"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>

          <!-- 数值 -->
          <template v-if="condition.compareType === 'NUMBER'">
            <el-input
              v-model="condition.compareValue"
              placeholder="数值"
              size="small"
              style="width: 120px"
            />
            <el-select
              v-model="condition.compareUnit"
              placeholder="单位"
              size="small"
            >
              <el-option
                v-for="item in compareUnitOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </template>

          <!-- 文本 -->
          <el-input
            v-if="condition.compareType === 'TEXT'"
            v-model="condition.compareValue"
            placeholder="文本"
            size="small"
            style="width: 200px"
          />

          <!-- 比例 -->
          <el-input
            v-if="condition.compareType === 'RATIO'"
            v-model="condition.compareValue"
            placeholder="比例"
            size="small"
            style="width: 120px"
          />

          <!-- 删除按钮 -->
          <el-button
            type="text"
            size="small"
            class="delete-btn"
            @click="removeCondition(index)"
          >
            <i class="el-icon-delete" />
          </el-button>
        </div>
      </template>
    </div>

    <!-- 新增按钮 -->
    <div class="actions-container" v-if="level === 0">
      <span class="action-link" @click="addCondition">[+新增条件]</span>
      <span class="action-link" @click="addConditionGroup">[+新增子条件组]</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RuleCondition',
  props: {
    conditions: {
      type: Array,
      default: () => []
    },
    ruleParamOptions: {
      type: Array,
      default: () => []
    },
    operatorOptions: {
      type: Array,
      default: () => []
    },
    compareTypeOptions: {
      type: Array,
      default: () => []
    },
    compareUnitOptions: {
      type: Array,
      default: () => []
    },
    level: {
      type: Number,
      default: 0
    }
  },
  methods: {
    addCondition() {
      const newCondition = {
        fieldCode: '',
        operator: '',
        compareType: '',
        compareFieldCode: '',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false,
        sortOrder: this.conditions.length + 1
      }
      this.conditions.push(newCondition)
      this.emitUpdate()
    },
    addConditionGroup() {
      const newGroup = {
        logicType: 'AND',
        isGroup: true,
        groupLogicType: 'AND',
        sortOrder: this.conditions.length + 1,
        children: []
      }
      this.conditions.push(newGroup)
      this.emitUpdate()
    },
    addChildCondition(group) {
      const newCondition = {
        fieldCode: '',
        operator: '',
        compareType: '',
        compareFieldCode: '',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false,
        sortOrder: group.children.length + 1
      }
      group.children.push(newCondition)
      this.emitUpdate()
    },
    addChildConditionGroup(group) {
      const newGroup = {
        logicType: 'AND',
        isGroup: true,
        groupLogicType: 'AND',
        sortOrder: group.children.length + 1,
        children: []
      }
      group.children.push(newGroup)
      this.emitUpdate()
    },
    removeCondition(index) {
      this.conditions.splice(index, 1)
      this.emitUpdate()
    },
    removeConditionGroup(index) {
      this.conditions.splice(index, 1)
      this.emitUpdate()
    },
    toggleLogic(condition) {
      condition.logicType = condition.logicType === 'AND' ? 'OR' : 'AND'
      this.emitUpdate()
    },
    toggleGroupLogic(condition) {
      condition.groupLogicType = condition.groupLogicType === 'AND' ? 'OR' : 'AND'
      this.emitUpdate()
    },
    handleFieldChange(condition) {
      const field = this.ruleParamOptions.find(item => item.code === condition.fieldCode)
      if (field) {
        condition.fieldName = field.name
      }
    },
    handleCompareTypeChange(condition) {
      condition.compareFieldCode = ''
      condition.compareValue = ''
      condition.compareUnit = ''
    },
    updateChildConditions(condition, childConditions) {
      condition.children = childConditions
      this.emitUpdate()
    },
    emitUpdate() {
      this.$emit('update', this.conditions)
    }
  }
}
</script>

<style scoped>
.rule-condition {
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.conditions-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.condition-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background-color: #fff;
  border-radius: 4px;
}

.condition-group {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  margin-left: 20px;
}

.group-header {
  margin-bottom: 8px;
}

.logic-link,
.group-logic,
.action-link {
  color: #409EFF;
  cursor: pointer;
  font-size: 12px;
  padding: 4px 8px;
}

.logic-link:hover,
.group-logic:hover,
.action-link:hover {
  text-decoration: underline;
}

.action-link.delete {
  color: #F56C6C;
}

.delete-btn {
  color: #909399;
}

.delete-btn:hover {
  color: #F56C6C;
}

.actions-container {
  margin-top: 10px;
}
</style>