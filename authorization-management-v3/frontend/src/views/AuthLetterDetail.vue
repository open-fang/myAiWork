<template>
  <div class="auth-letter-detail-page">
    <!-- 基本信息区 -->
    <div class="info-card">
      <div class="card-header">
        <span class="card-title">基本信息</span>
      </div>
      <div class="card-body">
        <div class="info-form">
          <div class="form-row">
            <div class="form-item">
              <label class="form-label required">授权书名称</label>
              <input type="text" v-model="formData.name" class="form-input" placeholder="请输入授权书名称" maxlength="100" />
            </div>
            <div class="form-item">
              <label class="form-label required">授权发布层级</label>
              <div class="multi-select-wrapper">
                <div class="multi-select-trigger" @click="toggleDropdown('authPublishLevel')">
                  <span class="selected-tags" v-if="formData.authPublishLevel.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authPublishLevel.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, authPublishLevelOptions) }}
                      <span class="tag-close" @click.stop="removeFormItem('authPublishLevel', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authPublishLevel.length > 2">+{{ formData.authPublishLevel.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="activeDropdown === 'authPublishLevel'">
                  <div class="option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                    <span class="checkbox" :class="{ checked: formData.authPublishLevel.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">授权发布组织</label>
              <div class="tree-select-wrapper">
                <div class="tree-select-trigger" @click="toggleDropdown('authPublishOrg')">
                  <span class="selected-tags" v-if="formData.authPublishOrg.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authPublishOrg.slice(0, 2)" :key="index">
                      {{ getOrgName(code) }}
                      <span class="tag-close" @click.stop="removeFormItem('authPublishOrg', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authPublishOrg.length > 2">+{{ formData.authPublishOrg.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="tree-dropdown" v-show="activeDropdown === 'authPublishOrg'">
                  <tree-node
                    v-for="node in orgTreeData"
                    :key="node.code"
                    :node="node"
                    :selected-codes="formData.authPublishOrg"
                    @toggle="toggleOrgNode"
                  />
                </div>
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label required">授权对象层级</label>
              <div class="multi-select-wrapper">
                <div class="multi-select-trigger" @click="toggleDropdown('authTargetLevel')">
                  <span class="selected-tags" v-if="formData.authTargetLevel.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authTargetLevel.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, authTargetLevelOptions) }}
                      <span class="tag-close" @click.stop="removeFormItem('authTargetLevel', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authTargetLevel.length > 2">+{{ formData.authTargetLevel.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="activeDropdown === 'authTargetLevel'">
                  <div class="option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                    <span class="checkbox" :class="{ checked: formData.authTargetLevel.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">适用区域</label>
              <div class="multi-select-wrapper">
                <div class="multi-select-trigger" @click="toggleDropdown('applicableRegion')">
                  <span class="selected-tags" v-if="formData.applicableRegion.length > 0">
                    <span class="tag" v-for="(code, index) in formData.applicableRegion.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, applicableRegionOptions) }}
                      <span class="tag-close" @click.stop="removeFormItem('applicableRegion', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.applicableRegion.length > 2">+{{ formData.applicableRegion.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="activeDropdown === 'applicableRegion'">
                  <div class="option" v-for="item in applicableRegionOptions" :key="item.code" @click="toggleMultiSelect('applicableRegion', item.code)">
                    <span class="checkbox" :class="{ checked: formData.applicableRegion.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">授权书发布年份</label>
              <div class="year-select-wrapper">
                <div class="year-select-trigger" @click="toggleDropdown('publishYear')">
                  <span>{{ formData.publishYear || '请选择年份' }}</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown year-dropdown" v-show="activeDropdown === 'publishYear'">
                  <div class="option" v-for="year in yearOptions" :key="year" @click="selectYear(year)">{{ year }}</div>
                </div>
              </div>
            </div>
          </div>
          <div class="form-row single">
            <div class="form-item full-width">
              <label class="form-label required">授权书内容摘要</label>
              <textarea v-model="formData.contentSummary" class="form-textarea" placeholder="请输入授权书内容摘要" maxlength="4000" rows="4"></textarea>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="section-card">
      <div class="card-body">
        <div class="section-header">
          <span class="section-label">附件</span>
          <div class="section-content">
            <div class="action-bar">
              <button class="btn btn-primary btn-sm" @click="handleUpload">上传</button>
              <button class="btn btn-default btn-sm" @click="handleDownloadAttachment">下载</button>
              <button class="btn btn-danger btn-sm" @click="handleDeleteAttachment">删除</button>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th class="col-checkbox"><input type="checkbox" v-model="attachmentSelectAll" @change="handleAttachmentSelectAll" /></th>
                  <th class="col-index">序号</th>
                  <th class="col-action">操作</th>
                  <th class="col-filename">文档名称</th>
                  <th class="col-type">文档类型</th>
                  <th class="col-user">创建人</th>
                  <th class="col-time">创建时间</th>
                  <th class="col-user">更新人</th>
                  <th class="col-time">更新时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in attachmentData" :key="row.id">
                  <td class="col-checkbox"><input type="checkbox" v-model="selectedAttachments" :value="row.id" /></td>
                  <td class="col-index">{{ (attachmentPage.pageNum - 1) * attachmentPage.pageSize + index + 1 }}</td>
                  <td class="col-action">
                    <span class="icon-btn" title="删除" @click="handleDeleteAttachmentRow(row)">🗑️</span>
                    <span class="icon-btn" title="下载" @click="handleDownloadRow(row)">📥</span>
                    <span class="icon-btn" title="加密" @click="handleEncryptRow(row)">🔒</span>
                  </td>
                  <td class="col-filename"><a class="link" @click="handleDownloadRow(row)">{{ row.fileName }}</a></td>
                  <td class="col-type">{{ row.docTypeName }}</td>
                  <td class="col-user">{{ row.createdBy }}</td>
                  <td class="col-time">{{ row.createdAt }}</td>
                  <td class="col-user">{{ row.updatedBy || '-' }}</td>
                  <td class="col-time">{{ row.updatedAt || '-' }}</td>
                </tr>
              </tbody>
            </table>
            <div class="pagination">
              <span class="total">共 {{ attachmentPage.total }} 条</span>
              <select class="page-size" v-model="attachmentPage.pageSize">
                <option :value="10">10条/页</option>
                <option :value="20">20条/页</option>
                <option :value="50">50条/页</option>
              </select>
              <button class="page-btn" :disabled="attachmentPage.pageNum === 1" @click="attachmentPage.pageNum--">‹</button>
              <span class="page-num">{{ attachmentPage.pageNum }}</span>
              <button class="page-btn" :disabled="attachmentPage.pageNum >= Math.ceil(attachmentPage.total / attachmentPage.pageSize)" @click="attachmentPage.pageNum++">›</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="section-card">
      <div class="card-body">
        <div class="section-header">
          <span class="section-label">授权规则</span>
          <div class="section-content">
            <div class="action-bar">
              <button class="btn btn-primary btn-sm" @click="openSceneDialog()">添加场景</button>
              <button class="btn btn-danger btn-sm" @click="handleDeleteScene">删除</button>
            </div>
            <table class="data-table">
              <thead>
                <tr>
                  <th class="col-checkbox"><input type="checkbox" v-model="sceneSelectAll" @change="handleSceneSelectAll" /></th>
                  <th class="col-index">序号</th>
                  <th class="col-action">操作</th>
                  <th class="col-scene">场景</th>
                  <th class="col-industry">产业</th>
                  <th class="col-business">业务场景</th>
                  <th class="col-rule">具体规则</th>
                  <th class="col-level">决策层级</th>
                  <th class="col-user">创建人</th>
                  <th class="col-time">创建时间</th>
                  <th class="col-user">更新人</th>
                  <th class="col-time">更新时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in sceneData" :key="row.id">
                  <td class="col-checkbox"><input type="checkbox" v-model="selectedScenes" :value="row.id" /></td>
                  <td class="col-index">{{ (scenePage.pageNum - 1) * scenePage.pageSize + index + 1 }}</td>
                  <td class="col-action">
                    <span class="icon-btn" title="编辑" @click="openSceneDialog(row)">✏️</span>
                    <span class="icon-btn" title="删除" @click="handleDeleteSceneRow(row)">🗑️</span>
                  </td>
                  <td class="col-scene">{{ row.sceneName }}</td>
                  <td class="col-industry">{{ row.industryText }}</td>
                  <td class="col-business">{{ row.businessScenario }}</td>
                  <td class="col-rule">{{ row.ruleDetail }}</td>
                  <td class="col-level">{{ row.decisionLevel }}</td>
                  <td class="col-user">{{ row.createdBy }}</td>
                  <td class="col-time">{{ row.createdAt }}</td>
                  <td class="col-user">{{ row.updatedBy || '-' }}</td>
                  <td class="col-time">{{ row.updatedAt || '-' }}</td>
                </tr>
              </tbody>
            </table>
            <div class="pagination">
              <span class="total">共 {{ scenePage.total }} 条</span>
              <select class="page-size" v-model="scenePage.pageSize">
                <option :value="10">10条/页</option>
                <option :value="20">20条/页</option>
                <option :value="50">50条/页</option>
              </select>
              <button class="page-btn" :disabled="scenePage.pageNum === 1" @click="scenePage.pageNum--">‹</button>
              <span class="page-num">{{ scenePage.pageNum }}</span>
              <button class="page-btn" :disabled="scenePage.pageNum >= Math.ceil(scenePage.total / scenePage.pageSize)" @click="scenePage.pageNum++">›</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部悬浮按钮 -->
    <div class="bottom-actions">
      <button class="btn btn-default" @click="handleSave" :disabled="!canSave">保存</button>
      <button class="btn btn-primary" @click="handleSaveAndPublish" :disabled="!canSaveAndPublish">保存并发布</button>
      <button class="btn btn-success" @click="handlePublish" :disabled="!canPublish">发布</button>
      <button class="btn btn-default" @click="handleCancel">取消</button>
      <button class="btn btn-danger" @click="handleDeleteAuthLetter" :disabled="!canDelete">删除</button>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal-overlay" v-if="sceneDialogVisible" @click.self="closeSceneDialog">
      <div class="modal-dialog scene-modal">
        <div class="modal-header">
          <span class="modal-title">{{ editingScene ? '编辑场景' : '添加场景' }}</span>
          <span class="modal-close" @click="closeSceneDialog">×</span>
        </div>
        <div class="modal-body">
          <div class="scene-form">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label required">场景名称</label>
                <input type="text" v-model="sceneForm.name" class="form-input" placeholder="请输入场景名称" />
              </div>
              <div class="form-item">
                <label class="form-label required">产业</label>
                <div class="tree-select-wrapper">
                  <div class="tree-select-trigger" @click="toggleDropdown('sceneIndustry')">
                    <span class="selected-tags" v-if="sceneForm.industry.length > 0">
                      <span class="tag" v-for="(code, index) in sceneForm.industry.slice(0, 2)" :key="index">
                        {{ getIndustryName(code) }}
                        <span class="tag-close" @click.stop="removeSceneFormItem('industry', code)">×</span>
                      </span>
                      <span class="tag" v-if="sceneForm.industry.length > 2">+{{ sceneForm.industry.length - 2 }}</span>
                    </span>
                    <span class="placeholder" v-else>请选择</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="tree-dropdown" v-show="activeDropdown === 'sceneIndustry'">
                    <tree-node
                      v-for="node in industryTreeData"
                      :key="node.code"
                      :node="node"
                      :selected-codes="sceneForm.industry"
                      @toggle="toggleIndustryNode"
                    />
                  </div>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label required">业务场景</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleDropdown('sceneBusiness')">
                    <span>{{ sceneForm.businessScenario ? getBusinessLabel(sceneForm.businessScenario) : '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="dropdown" v-show="activeDropdown === 'sceneBusiness'">
                    <div class="option" v-for="item in businessScenarioOptions" :key="item.code" @click="selectBusiness(item.code)">
                      {{ item.name }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-row">
              <div class="form-item"></div>
              <div class="form-item">
                <label class="form-label required">决策层级</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleDropdown('sceneDecisionLevel')">
                    <span>{{ sceneForm.decisionLevel ? getDecisionLevelLabel(sceneForm.decisionLevel) : '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="dropdown" v-show="activeDropdown === 'sceneDecisionLevel'">
                    <div class="option" v-for="item in decisionLevelOptions" :key="item.code" @click="selectDecisionLevel(item.code)">
                      {{ item.name }}
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label required">具体规则</label>
                <textarea v-model="sceneForm.ruleDetail" class="form-textarea" placeholder="请输入具体规则" maxlength="1000" rows="3"></textarea>
              </div>
            </div>

            <!-- 规则配置 -->
            <div class="rule-config-section">
              <div class="rule-config-header">
                <span class="rule-config-title">规则配置</span>
              </div>
              <div class="rule-config-body">
                <!-- 条件列表 -->
                <div class="condition-container">
                  <template v-for="(condition, cIndex) in sceneForm.conditions" :key="cIndex">
                    <!-- 条件组 -->
                    <div v-if="condition.type === 'group'" class="condition-group">
                      <div class="group-connector-line"></div>
                      <div class="group-connector-btn" @click="condition.logic = condition.logic === 'AND' ? 'OR' : 'AND'">
                        {{ condition.logic === 'AND' ? '且' : '或' }}
                      </div>
                      <div class="group-content">
                        <div class="group-conditions">
                          <template v-for="(cond, gIndex) in condition.conditions" :key="gIndex">
                            <div class="condition-row with-line">
                              <div class="condition-line" v-if="gIndex > 0"></div>
                              <div class="condition-connector" v-if="gIndex > 0" @click="toggleGroupConditionLogic(condition, gIndex)">
                                {{ condition.conditionLogics && condition.conditionLogics[gIndex - 1] === 'OR' ? '或' : '且' }}
                              </div>
                              <condition-item
                                :condition="cond"
                                :field-options="fieldOptions"
                                @remove="removeGroupCondition(condition, gIndex)"
                                @update="updateGroupCondition(condition, gIndex, $event)"
                              />
                            </div>
                          </template>
                        </div>
                        <div class="group-actions">
                          <span class="text-btn" @click="addGroupCondition(condition)">+ 新增条件</span>
                          <span class="text-btn" @click="addGroupSubGroup(condition)">+ 新增子条件组</span>
                          <span class="text-btn danger" @click="removeConditionGroup(cIndex)">移除条件组</span>
                        </div>
                      </div>
                    </div>
                    <!-- 普通条件 -->
                    <div v-else class="condition-row with-line">
                      <div class="condition-line" v-if="cIndex > 0"></div>
                      <div class="condition-connector" v-if="cIndex > 0" @click="toggleConditionLogic(cIndex)">
                        {{ sceneForm.conditionLogics && sceneForm.conditionLogics[cIndex - 1] === 'OR' ? '或' : '且' }}
                      </div>
                      <condition-item
                        :condition="condition"
                        :field-options="fieldOptions"
                        @remove="removeCondition(cIndex)"
                        @update="updateCondition(cIndex, $event)"
                      />
                    </div>
                  </template>
                </div>
                <div class="condition-actions">
                  <span class="text-btn" @click="addCondition">+ 新增条件</span>
                  <span class="text-btn" @click="addConditionGroup">+ 新增子条件组</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeSceneDialog">取消</button>
          <button class="btn btn-primary" @click="saveScene">确定</button>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div class="message-box" v-if="message.show" :class="'message-' + message.type">
      {{ message.text }}
    </div>

    <!-- 确认对话框 -->
    <div class="modal-overlay" v-if="confirmDialog.show" @click.self="confirmDialog.onCancel">
      <div class="modal-dialog confirm-modal">
        <div class="modal-body">{{ confirmDialog.text }}</div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="confirmDialog.onCancel">取消</button>
          <button class="btn btn-primary" @click="confirmDialog.onConfirm">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, defineComponent, h } from 'vue'

// ========== 树节点组件 ==========
const TreeNode = defineComponent({
  name: 'TreeNode',
  props: {
    node: Object,
    selectedCodes: Array
  },
  emits: ['toggle'],
  setup(props, { emit }) {
    const expanded = ref(false)
    const hasChildren = computed(() => props.node.children && props.node.children.length > 0)
    const isChecked = computed(() => props.selectedCodes.includes(props.node.code))
    const isIndeterminate = computed(() => {
      if (!hasChildren.value) return false
      const checkChildren = (children) => {
        let hasChecked = false, hasUnchecked = false
        for (const child of children) {
          if (props.selectedCodes.includes(child.code)) hasChecked = true
          else hasUnchecked = true
          if (child.children) {
            const r = checkChildren(child.children)
            hasChecked = hasChecked || r.hasChecked
            hasUnchecked = hasUnchecked || r.hasUnchecked
          }
        }
        return { hasChecked, hasUnchecked }
      }
      const r = checkChildren(props.node.children)
      return r.hasChecked && r.hasUnchecked
    })

    return () => h('div', { class: 'tree-node' }, [
      h('div', {
        class: 'tree-node-content',
        style: { paddingLeft: (props.node.level || 0) * 20 + 'px' }
      }, [
        hasChildren.value ? h('span', {
          class: 'tree-expand-icon' + (expanded.value ? ' expanded' : ''),
          onClick: () => expanded.value = !expanded.value
        }, '▶') : h('span', { class: 'tree-expand-placeholder' }),
        h('span', {
          class: 'tree-checkbox' + (isChecked.value ? ' checked' : '') + (isIndeterminate.value ? ' indeterminate' : ''),
          onClick: () => emit('toggle', props.node)
        }),
        h('span', { class: 'tree-node-label', onClick: () => emit('toggle', props.node) }, props.node.name)
      ]),
      hasChildren.value && expanded.value ? h('div', { class: 'tree-children' },
        props.node.children.map(child =>
          h(TreeNode, {
            key: child.code,
            node: { ...child, level: (props.node.level || 0) + 1 },
            selectedCodes: props.selectedCodes,
            onToggle: (n) => emit('toggle', n)
          })
        )
      ) : null
    ])
  }
})

// ========== 条件项组件 ==========
const ConditionItem = defineComponent({
  name: 'ConditionItem',
  props: {
    condition: Object,
    fieldOptions: Array
  },
  emits: ['remove', 'update'],
  setup(props, { emit }) {
    const operatorOptions = [
      { value: 'EQ', label: '等于' },
      { value: 'NE', label: '不等于' },
      { value: 'GT', label: '大于' },
      { value: 'GE', label: '大于等于' },
      { value: 'LT', label: '小于' },
      { value: 'LE', label: '小于等于' },
      { value: 'IN', label: '包含' },
      { value: 'NOT_IN', label: '不包含' }
    ]
    const compareTypeOptions = [
      { value: 'FIELD', label: '对比字段' },
      { value: 'NUMBER', label: '数值' },
      { value: 'TEXT', label: '文本' }
    ]
    const unitOptions = ['万', '千万', '亿', '元', '美元', '欧元']

    const updateCondition = (key, value) => {
      emit('update', { ...props.condition, [key]: value })
    }

    return () => h('div', { class: 'condition-item' }, [
      h('span', { class: 'delete-btn', onClick: () => emit('remove') }, '×'),
      h('select', {
        class: 'condition-select field-select',
        value: props.condition.field,
        onChange: (e) => updateCondition('field', e.target.value)
      }, [
        h('option', { value: '' }, '请选择字段'),
        ...props.fieldOptions.map(f => h('option', { value: f.code }, f.name))
      ]),
      h('select', {
        class: 'condition-select',
        value: props.condition.operator,
        onChange: (e) => updateCondition('operator', e.target.value)
      }, operatorOptions.map(o => h('option', { value: o.value }, o.label))),
      h('select', {
        class: 'condition-select',
        value: props.condition.compareType,
        onChange: (e) => updateCondition('compareType', e.target.value)
      }, compareTypeOptions.map(c => h('option', { value: c.value }, c.label))),
      props.condition.compareType === 'FIELD' ? h('select', {
        class: 'condition-select',
        value: props.condition.compareField,
        onChange: (e) => updateCondition('compareField', e.target.value)
      }, [
        h('option', { value: '' }, '请选择'),
        ...props.fieldOptions.map(f => h('option', { value: f.code }, f.name))
      ]) : null,
      props.condition.compareType === 'NUMBER' ? [
        h('input', {
          type: 'number',
          class: 'condition-input',
          value: props.condition.compareValue,
          onInput: (e) => updateCondition('compareValue', e.target.value),
          placeholder: '数值'
        }),
        h('select', {
          class: 'condition-select unit-select',
          value: props.condition.unit,
          onChange: (e) => updateCondition('unit', e.target.value)
        }, unitOptions.map(u => h('option', { value: u }, u)))
      ] : null,
      props.condition.compareType === 'TEXT' ? h('input', {
        type: 'text',
        class: 'condition-input',
        value: props.condition.compareValue,
        onInput: (e) => updateCondition('compareValue', e.target.value),
        placeholder: '文本'
      }) : null
    ])
  }
})

// ========== 页面数据 ==========
const activeDropdown = ref('')
const pageStatus = ref('DRAFT')

const message = reactive({ show: false, type: 'info', text: '' })
const confirmDialog = reactive({ show: false, text: '', onConfirm: () => {}, onCancel: () => {} })

// 基本信息表单
const formData = reactive({
  name: '',
  authPublishLevel: [],
  authPublishOrg: [],
  authTargetLevel: [],
  applicableRegion: [],
  publishYear: null,
  contentSummary: ''
})

// 下拉选项
const authPublishLevelOptions = ref([
  { code: 'ORGANIZATION', name: '机关' },
  { code: 'REGIONAL_DEPT', name: '地区部' },
  { code: 'REPRESENTATIVE_OFFICE', name: '代表处' },
  { code: 'OFFICE', name: '办事处' }
])
const authTargetLevelOptions = ref([...authPublishLevelOptions.value])
const applicableRegionOptions = ref([
  { code: 'EAST', name: '华东' }, { code: 'NORTH', name: '华北' },
  { code: 'SOUTH', name: '华南' }, { code: 'WEST', name: '西部' }, { code: 'CENTRAL', name: '华中' }
])

const orgTreeData = ref([
  { code: 'ORG001', name: '总部', level: 0, children: [
    { code: 'ORG002', name: '华东区', level: 1, children: [
      { code: 'ORG003', name: '上海办事处', level: 2 },
      { code: 'ORG004', name: '杭州办事处', level: 2 }
    ]},
    { code: 'ORG005', name: '华北区', level: 1, children: [
      { code: 'ORG006', name: '北京办事处', level: 2 }
    ]}
  ]}
])

const yearOptions = computed(() => {
  const years = []
  for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) years.push(i)
  return years
})

// 附件数据
const selectedAttachments = ref([])
const attachmentSelectAll = ref(false)
const attachmentData = ref([
  { id: 1, fileName: '授权书原件.pdf', docTypeName: '原件', createdBy: 'admin', createdAt: '2024-03-10 10:30:00', updatedBy: '', updatedAt: '' },
  { id: 2, fileName: '授权书附件.docx', docTypeName: '附件', createdBy: 'admin', createdAt: '2024-03-10 11:00:00', updatedBy: '-', updatedAt: '-' }
])
const attachmentPage = reactive({ pageNum: 1, pageSize: 10, total: 2 })

// 授权规则数据
const selectedScenes = ref([])
const sceneSelectAll = ref(false)
const sceneData = ref([
  { id: 1, sceneName: '销售场景', industryText: 'ICT、消费者', businessScenario: '设备销售', ruleDetail: '单笔金额不超过500万的销售订单可自主决策', decisionLevel: '地区部', createdBy: 'admin', createdAt: '2024-03-10 10:30:00', updatedBy: '-', updatedAt: '-' }
])
const scenePage = reactive({ pageNum: 1, pageSize: 10, total: 1 })

// 场景弹窗
const sceneDialogVisible = ref(false)
const editingScene = ref(null)
const sceneForm = reactive({
  name: '',
  industry: [],
  businessScenario: '',
  decisionLevel: '',
  ruleDetail: '',
  conditions: [],
  conditionLogics: []
})

// 场景相关选项
const industryTreeData = ref([
  { code: 'IND001', name: 'ICT', level: 0, children: [
    { code: 'IND001_1', name: '运营商', level: 1 },
    { code: 'IND001_2', name: '企业', level: 1 }
  ]},
  { code: 'IND002', name: '消费者', level: 0, children: [
    { code: 'IND002_1', name: '手机', level: 1 },
    { code: 'IND002_2', name: 'PC', level: 1 }
  ]},
  { code: 'IND003', name: '云计算', level: 0 }
])
const businessScenarioOptions = ref([
  { code: 'BS001', name: '设备销售' }, { code: 'BS002', name: '软件销售' },
  { code: 'BS003', name: '服务销售' }, { code: 'BS004', name: '物料采购' }
])
const decisionLevelOptions = ref([
  { code: 'ORGANIZATION', name: '机关' },
  { code: 'REGIONAL_DEPT', name: '地区部' },
  { code: 'REPRESENTATIVE_OFFICE', name: '代表处' },
  { code: 'OFFICE', name: '办事处' }
])
const fieldOptions = ref([
  { code: 'amount', name: '金额' }, { code: 'quantity', name: '数量' },
  { code: 'customer_level', name: '客户等级' }, { code: 'product_type', name: '产品类型' }
])

// 按钮状态
const canSave = computed(() => pageStatus.value === 'DRAFT')
const canPublish = computed(() => pageStatus.value === 'DRAFT')
const canSaveAndPublish = computed(() => pageStatus.value === 'DRAFT')
const canDelete = computed(() => pageStatus.value === 'DRAFT')

// ========== 工具方法 ==========
function toggleDropdown(name) {
  activeDropdown.value = activeDropdown.value === name ? '' : name
}

function toggleMultiSelect(field, value) {
  const i = formData[field].indexOf(value)
  if (i > -1) formData[field].splice(i, 1)
  else formData[field].push(value)
}

function removeFormItem(field, value) {
  const i = formData[field].indexOf(value)
  if (i > -1) formData[field].splice(i, 1)
}

function selectYear(year) {
  formData.publishYear = year
  activeDropdown.value = ''
}

function getOptionLabel(code, options) {
  const item = options.find(o => o.code === code)
  return item ? item.name : code
}

function getOrgName(code) {
  const find = (nodes) => {
    for (const n of nodes) {
      if (n.code === code) return n.name
      if (n.children) { const f = find(n.children); if (f) return f }
    }
    return null
  }
  return find(orgTreeData.value) || code
}

function toggleOrgNode(node) {
  const toggle = (n, check) => {
    const i = formData.authPublishOrg.indexOf(n.code)
    if (check && i === -1) formData.authPublishOrg.push(n.code)
    else if (!check && i > -1) formData.authPublishOrg.splice(i, 1)
    if (n.children) n.children.forEach(c => toggle(c, check))
  }
  toggle(node, !formData.authPublishOrg.includes(node.code))
}

// ========== 附件操作 ==========
function handleAttachmentSelectAll() {
  if (attachmentSelectAll.value) selectedAttachments.value = attachmentData.value.map(r => r.id)
  else selectedAttachments.value = []
}

function handleUpload() { showMessage('上传功能待实现', 'info') }

function handleDownloadAttachment() {
  if (selectedAttachments.value.length === 0) { showMessage('请先选择要下载的附件', 'warning'); return }
  showMessage('批量下载功能待实现', 'info')
}

function handleDeleteAttachment() {
  if (selectedAttachments.value.length === 0) { showMessage('请先选择要删除的附件', 'warning'); return }
  showConfirm(`确定要删除选中的 ${selectedAttachments.value.length} 个附件吗？`).then(ok => {
    if (ok) showMessage('删除成功', 'success')
  })
}

function handleDeleteAttachmentRow(row) {
  showConfirm('确定要删除该附件吗？').then(ok => {
    if (ok) showMessage('删除成功', 'success')
  })
}

function handleDownloadRow(row) { showMessage(`下载文件: ${row.fileName}`, 'info') }
function handleEncryptRow(row) { showMessage(`加密文件: ${row.fileName}`, 'info') }

// ========== 授权规则操作 ==========
function handleSceneSelectAll() {
  if (sceneSelectAll.value) selectedScenes.value = sceneData.value.map(r => r.id)
  else selectedScenes.value = []
}

function handleDeleteScene() {
  if (selectedScenes.value.length === 0) { showMessage('请先选择要删除的场景', 'warning'); return }
  showConfirm(`确定要删除选中的 ${selectedScenes.value.length} 个场景吗？`).then(ok => {
    if (ok) showMessage('删除成功', 'success')
  })
}

function handleDeleteSceneRow(row) {
  showConfirm('确定要删除该场景吗？').then(ok => {
    if (ok) showMessage('删除成功', 'success')
  })
}

// ========== 场景弹窗 ==========
function openSceneDialog(scene = null) {
  editingScene.value = scene
  if (scene) {
    sceneForm.name = scene.sceneName
    sceneForm.industry = scene.industry || []
    sceneForm.businessScenario = scene.businessScenarioCode || ''
    sceneForm.decisionLevel = scene.decisionLevelCode || ''
    sceneForm.ruleDetail = scene.ruleDetail || ''
    sceneForm.conditions = scene.conditions || []
    sceneForm.conditionLogics = scene.conditionLogics || []
  } else {
    sceneForm.name = ''
    sceneForm.industry = []
    sceneForm.businessScenario = ''
    sceneForm.decisionLevel = ''
    sceneForm.ruleDetail = ''
    sceneForm.conditions = []
    sceneForm.conditionLogics = []
  }
  sceneDialogVisible.value = true
}

function closeSceneDialog() {
  sceneDialogVisible.value = false
  editingScene.value = null
}

function saveScene() {
  if (!sceneForm.name) { showMessage('请输入场景名称', 'warning'); return }
  if (sceneForm.industry.length === 0) { showMessage('请选择产业', 'warning'); return }
  if (!sceneForm.businessScenario) { showMessage('请选择业务场景', 'warning'); return }
  if (!sceneForm.decisionLevel) { showMessage('请选择决策层级', 'warning'); return }
  if (!sceneForm.ruleDetail) { showMessage('请输入具体规则', 'warning'); return }

  showMessage(editingScene.value ? '编辑成功' : '添加成功', 'success')
  closeSceneDialog()
}

function getIndustryName(code) {
  const find = (nodes) => {
    for (const n of nodes) {
      if (n.code === code) return n.name
      if (n.children) { const f = find(n.children); if (f) return f }
    }
    return null
  }
  return find(industryTreeData.value) || code
}

function toggleIndustryNode(node) {
  const toggle = (n, check) => {
    const i = sceneForm.industry.indexOf(n.code)
    if (check && i === -1) sceneForm.industry.push(n.code)
    else if (!check && i > -1) sceneForm.industry.splice(i, 1)
    if (n.children) n.children.forEach(c => toggle(c, check))
  }
  toggle(node, !sceneForm.industry.includes(node.code))
}

function removeSceneFormItem(field, value) {
  const i = sceneForm[field].indexOf(value)
  if (i > -1) sceneForm[field].splice(i, 1)
}

function selectBusiness(code) {
  sceneForm.businessScenario = code
  activeDropdown.value = ''
}

function selectDecisionLevel(code) {
  sceneForm.decisionLevel = code
  activeDropdown.value = ''
}

function getBusinessLabel(code) {
  const item = businessScenarioOptions.value.find(o => o.code === code)
  return item ? item.name : code
}

function getDecisionLevelLabel(code) {
  const item = decisionLevelOptions.value.find(o => o.code === code)
  return item ? item.name : code
}

// ========== 条件配置 ==========
function addCondition() {
  sceneForm.conditions.push({
    type: 'condition',
    field: '',
    operator: 'EQ',
    compareType: 'NUMBER',
    compareField: '',
    compareValue: '',
    unit: '万'
  })
}

function removeCondition(index) {
  sceneForm.conditions.splice(index, 1)
  if (sceneForm.conditionLogics && sceneForm.conditionLogics[index - 1]) {
    sceneForm.conditionLogics.splice(index - 1, 1)
  }
}

function updateCondition(index, data) {
  sceneForm.conditions[index] = { ...sceneForm.conditions[index], ...data }
}

function toggleConditionLogic(index) {
  if (!sceneForm.conditionLogics) sceneForm.conditionLogics = []
  if (!sceneForm.conditionLogics[index - 1]) sceneForm.conditionLogics[index - 1] = 'AND'
  sceneForm.conditionLogics[index - 1] = sceneForm.conditionLogics[index - 1] === 'AND' ? 'OR' : 'AND'
}

function addConditionGroup() {
  sceneForm.conditions.push({
    type: 'group',
    logic: 'AND',
    conditions: [],
    conditionLogics: []
  })
}

function removeConditionGroup(index) {
  sceneForm.conditions.splice(index, 1)
}

function addGroupCondition(group) {
  if (!group.conditions) group.conditions = []
  group.conditions.push({
    type: 'condition',
    field: '',
    operator: 'EQ',
    compareType: 'NUMBER',
    compareField: '',
    compareValue: '',
    unit: '万'
  })
}

function addGroupSubGroup(group) {
  if (!group.conditions) group.conditions = []
  group.conditions.push({
    type: 'group',
    logic: 'AND',
    conditions: [],
    conditionLogics: []
  })
}

function removeGroupCondition(group, index) {
  group.conditions.splice(index, 1)
  if (group.conditionLogics && group.conditionLogics[index - 1]) {
    group.conditionLogics.splice(index - 1, 1)
  }
}

function updateGroupCondition(group, index, data) {
  group.conditions[index] = { ...group.conditions[index], ...data }
}

function toggleGroupConditionLogic(group, index) {
  if (!group.conditionLogics) group.conditionLogics = []
  if (!group.conditionLogics[index - 1]) group.conditionLogics[index - 1] = 'AND'
  group.conditionLogics[index - 1] = group.conditionLogics[index - 1] === 'AND' ? 'OR' : 'AND'
}

// ========== 底部按钮 ==========
function handleSave() { showMessage('保存成功', 'success') }
function handleSaveAndPublish() { showMessage('保存并发布成功', 'success'); pageStatus.value = 'PUBLISHED' }
function handlePublish() {
  showConfirm('确定要发布该授权书吗？').then(ok => {
    if (ok) { showMessage('发布成功', 'success'); pageStatus.value = 'PUBLISHED' }
  })
}
function handleCancel() { showMessage('返回列表页', 'info') }
function handleDeleteAuthLetter() {
  showConfirm('确定要删除该授权书吗？').then(ok => {
    if (ok) showMessage('删除成功', 'success')
  })
}

// ========== 消息和确认框 ==========
function showMessage(text, type = 'info') {
  message.text = text
  message.type = type
  message.show = true
  setTimeout(() => { message.show = false }, 3000)
}

function showConfirm(text) {
  return new Promise(resolve => {
    confirmDialog.text = text
    confirmDialog.show = true
    confirmDialog.onConfirm = () => { confirmDialog.show = false; resolve(true) }
    confirmDialog.onCancel = () => { confirmDialog.show = false; resolve(false) }
  })
}

// ========== 生命周期 ==========
function handleClickOutside(e) {
  if (!e.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
    activeDropdown.value = ''
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
* { box-sizing: border-box; }

.auth-letter-detail-page {
  padding: 20px;
  padding-bottom: 80px;
  background-color: #f5f7fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  color: #333;
}

/* 卡片 */
.info-card, .section-card {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.card-body {
  padding: 20px;
}

/* 表单 */
.info-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.form-row.single { grid-template-columns: 1fr; }
.form-item { display: flex; align-items: flex-start; }
.form-item.full-width { display: block; }
.form-item.full-width .form-label { display: block; width: 100%; text-align: left; margin-bottom: 8px; }
.form-item.full-width .form-textarea { width: 100%; }

.form-label {
  width: 100px;
  text-align: right;
  color: #666;
  flex-shrink: 0;
  margin-right: 12px;
  line-height: 32px;
}

.form-label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.form-input, .form-textarea {
  flex: 1;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
  font-size: 14px;
}

.form-input { height: 32px; }
.form-textarea { padding: 8px 12px; resize: vertical; }

.form-input:focus, .form-textarea:focus { border-color: #409eff; }

/* 下拉选择 */
.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper { flex: 1; position: relative; }
.multi-select-trigger, .tree-select-trigger, .select-trigger, .year-select-trigger {
  height: 32px;
  padding: 0 30px 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  position: relative;
}
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.placeholder { color: #c0c4cc; }
.selected-tags { display: flex; flex-wrap: wrap; gap: 4px; overflow: hidden; }
.tag { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; display: flex; align-items: center; gap: 4px; }
.tag-close { cursor: pointer; color: #909399; }
.tag-close:hover { color: #409eff; }

.dropdown, .tree-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 100;
  max-height: 250px;
  overflow-y: auto;
  margin-top: 4px;
}

.option { padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px; }
.option:hover { background: #f5f7fa; }

.checkbox {
  width: 14px; height: 14px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  display: inline-block;
  position: relative;
}
.checkbox.checked { background: #409eff; border-color: #409eff; }
.checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }

/* 树形选择 */
.tree-node-content { display: flex; align-items: center; padding: 6px 8px; cursor: pointer; }
.tree-node-content:hover { background: #f5f7fa; }
.tree-expand-icon { width: 16px; font-size: 10px; color: #c0c4cc; transition: transform 0.2s; }
.tree-expand-icon.expanded { transform: rotate(90deg); }
.tree-expand-placeholder { width: 16px; }
.tree-checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; margin-right: 8px; position: relative; }
.tree-checkbox.checked { background: #409eff; border-color: #409eff; }
.tree-checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.tree-checkbox.indeterminate { background: #409eff; border-color: #409eff; }
.tree-checkbox.indeterminate::after { content: '-'; color: #fff; font-size: 12px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.tree-node-label { font-size: 14px; }

/* 区块 */
.section-header { display: flex; }
.section-label { width: 80px; font-weight: 600; color: #606266; padding-top: 8px; flex-shrink: 0; }
.section-content { flex: 1; }
.action-bar { display: flex; gap: 10px; margin-bottom: 12px; }

/* 按钮 */
.btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}
.btn:hover { opacity: 0.9; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-sm { padding: 6px 12px; font-size: 13px; }
.btn-primary { background: #409eff; border-color: #409eff; color: #fff; }
.btn-success { background: #67c23a; border-color: #67c23a; color: #fff; }
.btn-warning { background: #e6a23c; border-color: #e6a23c; color: #fff; }
.btn-danger { background: #f56c6c; border-color: #f56c6c; color: #fff; }
.btn-default { background: #fff; color: #191919; border-color: #dcdfe6; }

/* 表格 */
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 10px 8px; text-align: left; border-bottom: 1px solid #ebeef5; }
.data-table th { background: #f5f7fa; font-weight: 600; color: #909399; font-size: 13px; }
.data-table tbody tr:hover { background: #f5f7fa; }

.col-checkbox { width: 40px; text-align: center; }
.col-index { width: 50px; text-align: center; }
.col-action { width: 100px; text-align: center; white-space: nowrap; }
.col-filename { min-width: 180px; }
.col-type { width: 100px; }
.col-user { width: 80px; text-align: center; }
.col-time { width: 150px; text-align: center; }
.col-scene { min-width: 100px; }
.col-industry { min-width: 100px; }
.col-business { min-width: 100px; }
.col-rule { min-width: 150px; }
.col-level { width: 80px; }

.icon-btn { cursor: pointer; margin: 0 4px; font-size: 14px; }
.icon-btn:hover { opacity: 0.7; }

.link { color: #409eff; cursor: pointer; }
.link:hover { text-decoration: underline; }

/* 分页 */
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 8px; margin-top: 12px; font-size: 13px; color: #606266; }
.page-size { height: 28px; padding: 0 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.page-btn { width: 28px; height: 28px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; cursor: pointer; }
.page-btn:disabled { color: #c0c4cc; cursor: not-allowed; }
.page-num { padding: 0 8px; }

/* 底部按钮 */
.bottom-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 16px 20px;
  display: flex;
  justify-content: center;
  gap: 16px;
  box-shadow: 0 -2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 100;
}

/* 弹窗 */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-dialog { background: #fff; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2); }
.scene-modal { width: 900px; max-height: 90vh; overflow: hidden; display: flex; flex-direction: column; }
.confirm-modal { min-width: 400px; }

.modal-header { padding: 16px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
.modal-title { font-weight: 600; font-size: 16px; }
.modal-close { cursor: pointer; font-size: 20px; color: #909399; }
.modal-close:hover { color: #409eff; }
.modal-body { padding: 20px; overflow-y: auto; flex: 1; }
.modal-footer { padding: 16px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; }

/* 场景表单 */
.scene-form { display: flex; flex-direction: column; gap: 16px; }

/* 规则配置 */
.rule-config-section { border: 1px solid #e4e7ed; border-radius: 4px; margin-top: 16px; }
.rule-config-header { padding: 12px 16px; border-bottom: 1px solid #e4e7ed; background: #f5f7fa; }
.rule-config-title { font-weight: 600; }
.rule-config-body { padding: 16px; }

.condition-container { margin-bottom: 12px; }

/* 条件行 - 带连接线 */
.condition-row.with-line {
  display: flex;
  align-items: flex-start;
  position: relative;
  margin-bottom: 0;
}

/* 连接线 */
.condition-line {
  position: absolute;
  left: 16px;
  top: 0;
  width: 1px;
  height: 100%;
  background: #dcdfe6;
}

.condition-line::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  width: 24px;
  height: 1px;
  background: #dcdfe6;
}

.condition-connector {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 28px;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  color: #606266;
  flex-shrink: 0;
  margin-right: 8px;
}

.condition-connector:hover {
  background: #f5f7fa;
}

.condition-connector.active {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

.condition-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  position: relative;
  flex: 1;
  margin-bottom: 0;
}

.delete-btn {
  position: absolute;
  right: -8px;
  top: -8px;
  width: 18px;
  height: 18px;
  background: #c0c4cc;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
}
.delete-btn:hover { background: #909399; }

.condition-select, .condition-input {
  height: 28px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 13px;
  outline: none;
}
.condition-select { min-width: 100px; }
.field-select { min-width: 120px; }
.unit-select { min-width: 60px; }
.condition-input { width: 100px; }

/* 条件组 */
.condition-group {
  display: flex;
  position: relative;
  margin-bottom: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #fff;
}

/* 条件组左侧连接线 */
.group-connector-line {
  position: absolute;
  left: 24px;
  top: 0;
  width: 1px;
  height: 100%;
  background: #dcdfe6;
}

.group-connector-btn {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  cursor: pointer;
  font-size: 12px;
  color: #606266;
  border-radius: 4px 0 0 4px;
}

.group-connector-btn:hover {
  background: #f5f7fa;
}

.group-connector-btn.active {
  background: #409eff;
  color: #fff;
}

.group-content { flex: 1; padding: 12px; padding-left: 16px; }
.group-conditions { margin-bottom: 8px; }
.group-actions { display: flex; gap: 16px; margin-top: 8px; }

.condition-actions { display: flex; gap: 16px; margin-top: 12px; }

.text-btn { color: #409eff; cursor: pointer; font-size: 13px; }
.text-btn:hover { text-decoration: underline; }
.text-btn.danger { color: #f56c6c; }

/* 消息提示 */
.message-box {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 4px;
  z-index: 2000;
  animation: fadeIn 0.3s;
}
@keyframes fadeIn { from { opacity: 0; transform: translateX(-50%) translateY(-10px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }
.message-info { background: #edf2fc; color: #909399; border: 1px solid #dcdfe6; }
.message-success { background: #f0f9eb; color: #67c23a; border: 1px solid #e1f3d8; }
.message-warning { background: #fdf6ec; color: #e6a23c; border: 1px solid #faecd8; }
.message-error { background: #fef0f0; color: #f56c6c; border: 1px solid #fde2e2; }
</style>