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
              <input type="text" v-model="formData.name" class="form-input" placeholder="请输入授权书名称" maxlength="100" :disabled="isReadonly" />
            </div>
            <div class="form-item">
              <label class="form-label required">授权发布层级</label>
              <div class="multi-select-wrapper" :class="{ disabled: isReadonly }">
                <div class="multi-select-trigger" @click="!isReadonly && toggleDropdown('authPublishLevel')">
                  <span class="selected-tags" v-if="formData.authPublishLevel.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authPublishLevel.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, authPublishLevelOptions) }}
                      <span class="tag-close" v-if="!isReadonly" @click.stop="removeFormItem('authPublishLevel', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authPublishLevel.length > 2">+{{ formData.authPublishLevel.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="!isReadonly && activeDropdown === 'authPublishLevel'">
                  <div class="option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                    <span class="checkbox" :class="{ checked: formData.authPublishLevel.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">授权发布组织</label>
              <div class="tree-select-wrapper" :class="{ disabled: isReadonly }">
                <div class="tree-select-trigger" @click="!isReadonly && toggleDropdown('authPublishOrg')">
                  <span class="selected-tags" v-if="formData.authPublishOrg.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authPublishOrg.slice(0, 2)" :key="index">
                      {{ getOrgName(code) }}
                      <span class="tag-close" v-if="!isReadonly" @click.stop="removeFormItem('authPublishOrg', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authPublishOrg.length > 2">+{{ formData.authPublishOrg.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="tree-dropdown" v-show="!isReadonly && activeDropdown === 'authPublishOrg'">
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
              <div class="multi-select-wrapper" :class="{ disabled: isReadonly }">
                <div class="multi-select-trigger" @click="!isReadonly && toggleDropdown('authTargetLevel')">
                  <span class="selected-tags" v-if="formData.authTargetLevel.length > 0">
                    <span class="tag" v-for="(code, index) in formData.authTargetLevel.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, authTargetLevelOptions) }}
                      <span class="tag-close" v-if="!isReadonly" @click.stop="removeFormItem('authTargetLevel', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.authTargetLevel.length > 2">+{{ formData.authTargetLevel.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="!isReadonly && activeDropdown === 'authTargetLevel'">
                  <div class="option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                    <span class="checkbox" :class="{ checked: formData.authTargetLevel.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">适用区域</label>
              <div class="multi-select-wrapper" :class="{ disabled: isReadonly }">
                <div class="multi-select-trigger" @click="!isReadonly && toggleDropdown('applicableRegion')">
                  <span class="selected-tags" v-if="formData.applicableRegion.length > 0">
                    <span class="tag" v-for="(code, index) in formData.applicableRegion.slice(0, 2)" :key="index">
                      {{ getOptionLabel(code, applicableRegionOptions) }}
                      <span class="tag-close" v-if="!isReadonly" @click.stop="removeFormItem('applicableRegion', code)">×</span>
                    </span>
                    <span class="tag" v-if="formData.applicableRegion.length > 2">+{{ formData.applicableRegion.length - 2 }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="dropdown" v-show="!isReadonly && activeDropdown === 'applicableRegion'">
                  <div class="option" v-for="item in applicableRegionOptions" :key="item.code" @click="toggleMultiSelect('applicableRegion', item.code)">
                    <span class="checkbox" :class="{ checked: formData.applicableRegion.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label required">授权书发布年份</label>
              <input type="number" v-model="formData.publishYear" class="form-input" placeholder="请输入年份" min="2000" max="2100" :disabled="isReadonly" />
            </div>
          </div>
          <div class="form-row single">
            <div class="form-item full-width">
              <label class="form-label required">授权书内容摘要</label>
              <textarea v-model="formData.contentSummary" class="form-textarea" placeholder="请输入授权书内容摘要" maxlength="4000" rows="4" :disabled="isReadonly"></textarea>
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
              <button class="btn btn-primary btn-sm" @click="handleUpload" :disabled="isReadonly">上传</button>
              <button class="btn btn-default btn-sm" @click="handleDownloadAttachment">下载</button>
              <button class="btn btn-danger btn-sm" @click="handleDeleteAttachment" :disabled="isReadonly">删除</button>
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
                    <span class="icon-btn" title="删除" @click="!isReadonly && handleDeleteAttachmentRow(row)" :class="{ disabled: isReadonly }">🗑️</span>
                    <span class="icon-btn" title="下载" @click="handleDownloadRow(row)">📥</span>
                    <span class="icon-btn" title="加密" @click="!isReadonly && handleEncryptRow(row)" :class="{ disabled: isReadonly }">🔒</span>
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
    <div class="section-card" v-show="!isReadonly">
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

    <!-- 日志区块（已发布状态显示） -->
    <div class="section-card log-section" v-if="isReadonly">
      <div class="card-header collapsible" @click="logExpanded = !logExpanded">
        <span class="card-title">操作日志</span>
        <span class="collapse-icon" :class="{ expanded: logExpanded }">▼</span>
      </div>
      <div class="card-body" v-show="logExpanded">
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-index">序号</th>
              <th class="col-action-type">操作类型</th>
              <th class="col-user">操作人</th>
              <th class="col-time">操作时间</th>
              <th class="col-remark">备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(log, index) in operationLogs" :key="log.id">
              <td class="col-index">{{ index + 1 }}</td>
              <td class="col-action-type">{{ log.actionType }}</td>
              <td class="col-user">{{ log.operator }}</td>
              <td class="col-time">{{ log.operatedAt }}</td>
              <td class="col-remark">{{ log.remark || '-' }}</td>
            </tr>
            <tr v-if="operationLogs.length === 0">
              <td colspan="5" style="text-align: center; color: #909399; padding: 20px;">暂无操作日志</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 底部悬浮按钮 -->
    <div class="bottom-actions">
      <template v-if="!isReadonly">
        <button class="btn btn-default" @click="handleSave" :disabled="!canSave">保存</button>
        <button class="btn btn-primary" @click="handleSaveAndPublish" :disabled="!canSaveAndPublish">保存并发布</button>
        <button class="btn btn-success" @click="handlePublish" :disabled="!canPublish">发布</button>
        <button class="btn btn-default" @click="handleCancel">取消</button>
        <button class="btn btn-danger" @click="handleDeleteAuthLetter" :disabled="!canDelete">删除</button>
      </template>
      <template v-else>
        <button class="btn btn-default" @click="handleCancel">返回</button>
        <button class="btn btn-warning" @click="handleExpire">失效</button>
        <button class="btn btn-danger" @click="handleDeleteAuthLetter">删除</button>
      </template>
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
                  <template v-for="(condition, cIndex) in sceneForm.conditions">
                    <!-- 条件组 -->
                    <div v-if="condition.type === 'group'" :key="cIndex" class="condition-group">
                      <div class="group-connector-line"></div>
                      <div class="group-connector-btn" @click="condition.logic = condition.logic === 'AND' ? 'OR' : 'AND'">
                        {{ condition.logic === 'AND' ? '且' : '或' }}
                      </div>
                      <div class="group-content">
                        <div class="group-conditions">
                          <template v-for="(cond, gIndex) in condition.conditions">
                            <div :key="gIndex" class="condition-row with-line">
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
                    <div v-else :key="cIndex" class="condition-row with-line">
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

<script>
// ========== 树节点组件 ==========
const TreeNode = {
  name: 'TreeNode',
  props: {
    node: Object,
    selectedCodes: Array
  },
  data() {
    return {
      expanded: false
    }
  },
  computed: {
    hasChildren() {
      return this.node.children && this.node.children.length > 0
    },
    isChecked() {
      return this.selectedCodes.includes(this.node.code)
    },
    isIndeterminate() {
      if (!this.hasChildren) return false
      const checkChildren = (children) => {
        let hasChecked = false, hasUnchecked = false
        for (const child of children) {
          if (this.selectedCodes.includes(child.code)) hasChecked = true
          else hasUnchecked = true
          if (child.children) {
            const r = checkChildren(child.children)
            hasChecked = hasChecked || r.hasChecked
            hasUnchecked = hasUnchecked || r.hasUnchecked
          }
        }
        return { hasChecked, hasUnchecked }
      }
      const r = checkChildren(this.node.children)
      return r.hasChecked && r.hasUnchecked
    }
  },
  methods: {
    toggleCheck() {
      this.$emit('toggle', this.node)
    },
    toggleExpand() {
      this.expanded = !this.expanded
    }
  },
  template: `
    <div class="tree-node">
      <div class="tree-node-content" :style="{ paddingLeft: (node.level || 0) * 20 + 'px' }">
        <span v-if="hasChildren" class="tree-expand-icon" :class="{ expanded: expanded }" @click="toggleExpand">▶</span>
        <span v-else class="tree-expand-placeholder"></span>
        <span class="tree-checkbox" :class="{ checked: isChecked, indeterminate: isIndeterminate }" @click="toggleCheck"></span>
        <span class="tree-node-label" @click="toggleCheck">{{ node.name }}</span>
      </div>
      <div v-if="hasChildren && expanded" class="tree-children">
        <tree-node
          v-for="child in node.children"
          :key="child.code"
          :node="{ ...child, level: (node.level || 0) + 1 }"
          :selected-codes="selectedCodes"
          @toggle="$emit('toggle', $event)"
        />
      </div>
    </div>
  `
}

// ========== 条件项组件 ==========
const ConditionItem = {
  name: 'ConditionItem',
  props: {
    condition: Object,
    fieldOptions: Array
  },
  data() {
    return {
      operatorOptions: [
        { value: 'EQ', label: '等于' },
        { value: 'NE', label: '不等于' },
        { value: 'GT', label: '大于' },
        { value: 'GE', label: '大于等于' },
        { value: 'LT', label: '小于' },
        { value: 'LE', label: '小于等于' },
        { value: 'IN', label: '包含' },
        { value: 'NOT_IN', label: '不包含' }
      ],
      compareTypeOptions: [
        { value: 'FIELD', label: '对比字段' },
        { value: 'NUMBER', label: '数值' },
        { value: 'TEXT', label: '文本' }
      ],
      unitOptions: ['万', '千万', '亿', '元', '美元', '欧元']
    }
  },
  methods: {
    updateCondition(key, value) {
      this.$emit('update', { ...this.condition, [key]: value })
    },
    removeCondition() {
      this.$emit('remove')
    }
  },
  template: `
    <div class="condition-item">
      <span class="delete-btn" @click="removeCondition">×</span>
      <select class="condition-select field-select" :value="condition.field" @change="updateCondition('field', $event.target.value)">
        <option value="">请选择字段</option>
        <option v-for="f in fieldOptions" :key="f.code" :value="f.code">{{ f.name }}</option>
      </select>
      <select class="condition-select" :value="condition.operator" @change="updateCondition('operator', $event.target.value)">
        <option v-for="o in operatorOptions" :key="o.value" :value="o.value">{{ o.label }}</option>
      </select>
      <select class="condition-select" :value="condition.compareType" @change="updateCondition('compareType', $event.target.value)">
        <option v-for="c in compareTypeOptions" :key="c.value" :value="c.value">{{ c.label }}</option>
      </select>
      <select v-if="condition.compareType === 'FIELD'" class="condition-select" :value="condition.compareField" @change="updateCondition('compareField', $event.target.value)">
        <option value="">请选择</option>
        <option v-for="f in fieldOptions" :key="f.code" :value="f.code">{{ f.name }}</option>
      </select>
      <template v-if="condition.compareType === 'NUMBER'">
        <input type="number" class="condition-input" :value="condition.compareValue" @input="updateCondition('compareValue', $event.target.value)" placeholder="数值" />
        <select class="condition-select unit-select" :value="condition.unit" @change="updateCondition('unit', $event.target.value)">
          <option v-for="u in unitOptions" :key="u" :value="u">{{ u }}</option>
        </select>
      </template>
      <input v-if="condition.compareType === 'TEXT'" type="text" class="condition-input" :value="condition.compareValue" @input="updateCondition('compareValue', $event.target.value)" placeholder="文本" />
    </div>
  `
}

// ========== API 请求方法 ==========
const BASE_URL = '/api'

async function request(url, options = {}) {
  const config = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    }
  }
  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body)
  }
  const response = await fetch(BASE_URL + url, config)
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`)
  }
  return response.json()
}

const api = {
  // 获取授权书详情
  getAuthLetterDetail: (id) => request(`/auth-letters/${id}`),

  // 创建授权书
  createAuthLetter: (data) => request('/auth-letters', { method: 'POST', body: data }),

  // 更新授权书
  updateAuthLetter: (id, data) => request(`/auth-letters/${id}`, { method: 'PUT', body: data }),

  // 删除授权书
  deleteAuthLetter: (id) => request(`/auth-letters/${id}`, { method: 'DELETE' }),

  // 发布授权书
  publishAuthLetter: (id) => request(`/auth-letters/${id}/publish`, { method: 'PUT' }),

  // 获取下拉选项
  getLookupOptions: (code) => request(`/lookup/${code}`),

  // 获取组织树
  getOrgTree: () => request('/lookup/org/tree'),

  // 获取产业树
  getIndustryTree: () => request('/lookup/industry/tree'),

  // 获取业务场景选项
  getBusinessScenarios: () => request('/lookup/business-scenarios'),

  // 获取决策层级选项
  getDecisionLevels: () => request('/lookup/decision-levels'),

  // 获取规则字段选项
  getRuleFields: () => request('/lookup/rule-fields')
}

export default {
  name: 'AuthLetterDetail',
  components: {
    TreeNode,
    ConditionItem
  },
  data() {
    return {
      authLetterId: null,
      isNew: true,
      loading: false,
      activeDropdown: '',
      pageStatus: 'DRAFT',
      logExpanded: false,
      operationLogs: [],
      message: { show: false, type: 'info', text: '' },
      confirmDialog: { show: false, text: '', onConfirm: () => {}, onCancel: () => {} },
      formData: {
        name: '',
        authPublishLevel: [],
        authPublishOrg: [],
        authTargetLevel: [],
        applicableRegion: [],
        publishYear: null,
        contentSummary: ''
      },
      authPublishLevelOptions: [],
      authTargetLevelOptions: [],
      applicableRegionOptions: [],
      orgTreeData: [],
      selectedAttachments: [],
      attachmentSelectAll: false,
      attachmentData: [],
      attachmentPage: { pageNum: 1, pageSize: 10, total: 0 },
      selectedScenes: [],
      sceneSelectAll: false,
      sceneData: [],
      scenePage: { pageNum: 1, pageSize: 10, total: 0 },
      sceneDialogVisible: false,
      editingScene: null,
      sceneForm: {
        name: '',
        industry: [],
        businessScenario: '',
        decisionLevel: '',
        ruleDetail: '',
        conditions: [],
        conditionLogics: []
      },
      industryTreeData: [],
      businessScenarioOptions: [],
      decisionLevelOptions: [],
      fieldOptions: []
    }
  },
  computed: {
    yearOptions() {
      const years = []
      for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) years.push(i)
      return years
    },
    isReadonly() {
      return this.pageStatus === 'PUBLISHED'
    },
    canSave() {
      return this.pageStatus === 'DRAFT'
    },
    canPublish() {
      return this.pageStatus === 'DRAFT'
    },
    canSaveAndPublish() {
      return this.pageStatus === 'DRAFT'
    },
    canDelete() {
      return this.pageStatus === 'DRAFT' || this.pageStatus === 'PUBLISHED'
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
    this.loadInitialData()
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    async loadInitialData() {
      try {
        this.loading = true

        // 从URL获取授权书ID（支持hash路由 #/AuthLetterDetail?id=1）
        let id = null
        const hash = window.location.hash
        const queryIndex = hash.indexOf('?')
        if (queryIndex > -1) {
          const queryString = hash.substring(queryIndex + 1)
          const urlParams = new URLSearchParams(queryString)
          id = urlParams.get('id')
        }

        this.authLetterId = id ? parseInt(id) : null
        this.isNew = !this.authLetterId

        // 如果不是新建模式，必须要有id参数
        if (!this.isNew && !this.authLetterId) {
          this.showMessage('缺少授权书ID参数', 'error')
          setTimeout(() => {
            window.location.href = '#/AuthLetterList'
          }, 1500)
          return
        }

        // 并行加载下拉选项数据
        const [authTargetLevelRes, authPublishLevelRes, applicableRegionRes, orgTreeRes, industryTreeRes, businessScenariosRes, decisionLevelsRes, ruleFieldsRes] = await Promise.all([
          api.getLookupOptions('authTargetLevel'),
          api.getLookupOptions('authPublishLevel'),
          api.getLookupOptions('applicableRegion'),
          api.getOrgTree(),
          api.getIndustryTree(),
          api.getBusinessScenarios(),
          api.getDecisionLevels(),
          api.getRuleFields()
        ])

        // 设置下拉选项
        if (authTargetLevelRes.code === 200) this.authTargetLevelOptions = authTargetLevelRes.data || []
        if (authPublishLevelRes.code === 200) this.authPublishLevelOptions = authPublishLevelRes.data || []
        if (applicableRegionRes.code === 200) this.applicableRegionOptions = applicableRegionRes.data || []
        if (orgTreeRes.code === 200) this.orgTreeData = orgTreeRes.data || []
        if (industryTreeRes.code === 200) this.industryTreeData = industryTreeRes.data || []
        if (businessScenariosRes.code === 200) this.businessScenarioOptions = businessScenariosRes.data || []
        if (decisionLevelsRes.code === 200) this.decisionLevelOptions = decisionLevelsRes.data || []
        if (ruleFieldsRes.code === 200) this.fieldOptions = ruleFieldsRes.data || []

        // 如果是编辑模式，加载授权书详情
        if (!this.isNew && this.authLetterId) {
          await this.loadAuthLetterDetail()
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.showMessage('加载数据失败', 'error')
      } finally {
        this.loading = false
      }
    },

    async loadAuthLetterDetail() {
      try {
        const res = await api.getAuthLetterDetail(this.authLetterId)
        if (res.code === 200 && res.data) {
          const detail = res.data
          this.formData.name = detail.name
          this.formData.authPublishLevel = detail.authPublishLevel || []
          this.formData.authPublishOrg = detail.authPublishOrg || []
          this.formData.authTargetLevel = detail.authTargetLevel || []
          this.formData.applicableRegion = detail.applicableRegion || []
          this.formData.publishYear = detail.publishYear
          this.formData.contentSummary = detail.contentSummary || ''
          this.pageStatus = detail.status

          // 设置场景数据
          if (detail.scenes) {
            this.sceneData = detail.scenes
            this.scenePage.total = detail.scenes.length
          }

          // 模拟操作日志数据（已发布状态）
          if (this.pageStatus === 'PUBLISHED') {
            this.operationLogs = [
              { id: 1, actionType: '发布', operator: detail.publishedBy || 'admin', operatedAt: detail.publishedAt || new Date().toISOString(), remark: '授权书发布' }
            ]
          }
        } else {
          this.showMessage(res.message || '加载授权书详情失败', 'error')
        }
      } catch (error) {
        console.error('加载授权书详情失败:', error)
        this.showMessage('加载授权书详情失败', 'error')
      }
    },

    toggleDropdown(name) {
      this.activeDropdown = this.activeDropdown === name ? '' : name
    },
    toggleMultiSelect(field, value) {
      const i = this.formData[field].indexOf(value)
      if (i > -1) this.formData[field].splice(i, 1)
      else this.formData[field].push(value)
    },
    removeFormItem(field, value) {
      const i = this.formData[field].indexOf(value)
      if (i > -1) this.formData[field].splice(i, 1)
    },
    selectYear(year) {
      this.formData.publishYear = year
      this.activeDropdown = ''
    },
    getOptionLabel(code, options) {
      const item = options.find(o => o.code === code)
      return item ? item.name : code
    },
    getOrgName(code) {
      const find = (nodes) => {
        for (const n of nodes) {
          if (n.code === code) return n.name
          if (n.children) { const f = find(n.children); if (f) return f }
        }
        return null
      }
      return find(this.orgTreeData) || code
    },
    toggleOrgNode(node) {
      const toggle = (n, check) => {
        const i = this.formData.authPublishOrg.indexOf(n.code)
        if (check && i === -1) this.formData.authPublishOrg.push(n.code)
        else if (!check && i > -1) this.formData.authPublishOrg.splice(i, 1)
        if (n.children) n.children.forEach(c => toggle(c, check))
      }
      toggle(node, !this.formData.authPublishOrg.includes(node.code))
    },
    handleAttachmentSelectAll() {
      if (this.attachmentSelectAll) this.selectedAttachments = this.attachmentData.map(r => r.id)
      else this.selectedAttachments = []
    },
    handleUpload() { this.showMessage('上传功能待实现', 'info') },
    handleDownloadAttachment() {
      if (this.selectedAttachments.length === 0) { this.showMessage('请先选择要下载的附件', 'warning'); return }
      this.showMessage('批量下载功能待实现', 'info')
    },
    handleDeleteAttachment() {
      if (this.selectedAttachments.length === 0) { this.showMessage('请先选择要删除的附件', 'warning'); return }
      this.showConfirm(`确定要删除选中的 ${this.selectedAttachments.length} 个附件吗？`).then(ok => {
        if (ok) this.showMessage('删除成功', 'success')
      })
    },
    handleDeleteAttachmentRow(row) {
      this.showConfirm('确定要删除该附件吗？').then(ok => {
        if (ok) this.showMessage('删除成功', 'success')
      })
    },
    handleDownloadRow(row) { this.showMessage(`下载文件: ${row.fileName}`, 'info') },
    handleEncryptRow(row) { this.showMessage(`加密文件: ${row.fileName}`, 'info') },
    handleSceneSelectAll() {
      if (this.sceneSelectAll) this.selectedScenes = this.sceneData.map(r => r.id)
      else this.selectedScenes = []
    },
    handleDeleteScene() {
      if (this.selectedScenes.length === 0) { this.showMessage('请先选择要删除的场景', 'warning'); return }
      this.showConfirm(`确定要删除选中的 ${this.selectedScenes.length} 个场景吗？`).then(ok => {
        if (ok) this.showMessage('删除成功', 'success')
      })
    },
    handleDeleteSceneRow(row) {
      this.showConfirm('确定要删除该场景吗？').then(ok => {
        if (ok) this.showMessage('删除成功', 'success')
      })
    },
    openSceneDialog(scene = null) {
      this.editingScene = scene
      if (scene) {
        this.sceneForm.name = scene.sceneName
        this.sceneForm.industry = scene.industry || []
        this.sceneForm.businessScenario = scene.businessScenarioCode || ''
        this.sceneForm.decisionLevel = scene.decisionLevelCode || ''
        this.sceneForm.ruleDetail = scene.ruleDetail || ''
        this.sceneForm.conditions = scene.conditions || []
        this.sceneForm.conditionLogics = scene.conditionLogics || []
      } else {
        this.sceneForm.name = ''
        this.sceneForm.industry = []
        this.sceneForm.businessScenario = ''
        this.sceneForm.decisionLevel = ''
        this.sceneForm.ruleDetail = ''
        this.sceneForm.conditions = []
        this.sceneForm.conditionLogics = []
      }
      this.sceneDialogVisible = true
    },
    closeSceneDialog() {
      this.sceneDialogVisible = false
      this.editingScene = null
    },
    saveScene() {
      if (!this.sceneForm.name) { this.showMessage('请输入场景名称', 'warning'); return }
      if (this.sceneForm.industry.length === 0) { this.showMessage('请选择产业', 'warning'); return }
      if (!this.sceneForm.businessScenario) { this.showMessage('请选择业务场景', 'warning'); return }
      if (!this.sceneForm.decisionLevel) { this.showMessage('请选择决策层级', 'warning'); return }
      if (!this.sceneForm.ruleDetail) { this.showMessage('请输入具体规则', 'warning'); return }

      this.showMessage(this.editingScene ? '编辑成功' : '添加成功', 'success')
      this.closeSceneDialog()
    },
    getIndustryName(code) {
      const find = (nodes) => {
        for (const n of nodes) {
          if (n.code === code) return n.name
          if (n.children) { const f = find(n.children); if (f) return f }
        }
        return null
      }
      return find(this.industryTreeData) || code
    },
    toggleIndustryNode(node) {
      const toggle = (n, check) => {
        const i = this.sceneForm.industry.indexOf(n.code)
        if (check && i === -1) this.sceneForm.industry.push(n.code)
        else if (!check && i > -1) this.sceneForm.industry.splice(i, 1)
        if (n.children) n.children.forEach(c => toggle(c, check))
      }
      toggle(node, !this.sceneForm.industry.includes(node.code))
    },
    removeSceneFormItem(field, value) {
      const i = this.sceneForm[field].indexOf(value)
      if (i > -1) this.sceneForm[field].splice(i, 1)
    },
    selectBusiness(code) {
      this.sceneForm.businessScenario = code
      this.activeDropdown = ''
    },
    selectDecisionLevel(code) {
      this.sceneForm.decisionLevel = code
      this.activeDropdown = ''
    },
    getBusinessLabel(code) {
      const item = this.businessScenarioOptions.find(o => o.code === code)
      return item ? item.name : code
    },
    getDecisionLevelLabel(code) {
      const item = this.decisionLevelOptions.find(o => o.code === code)
      return item ? item.name : code
    },
    addCondition() {
      this.sceneForm.conditions.push({
        type: 'condition',
        field: '',
        operator: 'EQ',
        compareType: 'NUMBER',
        compareField: '',
        compareValue: '',
        unit: '万'
      })
    },
    removeCondition(index) {
      this.sceneForm.conditions.splice(index, 1)
      if (this.sceneForm.conditionLogics && this.sceneForm.conditionLogics[index - 1]) {
        this.sceneForm.conditionLogics.splice(index - 1, 1)
      }
    },
    updateCondition(index, data) {
      this.$set(this.sceneForm.conditions, index, { ...this.sceneForm.conditions[index], ...data })
    },
    toggleConditionLogic(index) {
      if (!this.sceneForm.conditionLogics) this.$set(this.sceneForm, 'conditionLogics', [])
      if (!this.sceneForm.conditionLogics[index - 1]) this.$set(this.sceneForm.conditionLogics, index - 1, 'AND')
      this.sceneForm.conditionLogics[index - 1] = this.sceneForm.conditionLogics[index - 1] === 'AND' ? 'OR' : 'AND'
    },
    addConditionGroup() {
      this.sceneForm.conditions.push({
        type: 'group',
        logic: 'AND',
        conditions: [],
        conditionLogics: []
      })
    },
    removeConditionGroup(index) {
      this.sceneForm.conditions.splice(index, 1)
    },
    addGroupCondition(group) {
      if (!group.conditions) this.$set(group, 'conditions', [])
      group.conditions.push({
        type: 'condition',
        field: '',
        operator: 'EQ',
        compareType: 'NUMBER',
        compareField: '',
        compareValue: '',
        unit: '万'
      })
    },
    addGroupSubGroup(group) {
      if (!group.conditions) this.$set(group, 'conditions', [])
      group.conditions.push({
        type: 'group',
        logic: 'AND',
        conditions: [],
        conditionLogics: []
      })
    },
    removeGroupCondition(group, index) {
      group.conditions.splice(index, 1)
      if (group.conditionLogics && group.conditionLogics[index - 1]) {
        group.conditionLogics.splice(index - 1, 1)
      }
    },
    updateGroupCondition(group, index, data) {
      this.$set(group.conditions, index, { ...group.conditions[index], ...data })
    },
    toggleGroupConditionLogic(group, index) {
      if (!group.conditionLogics) this.$set(group, 'conditionLogics', [])
      if (!group.conditionLogics[index - 1]) this.$set(group.conditionLogics, index - 1, 'AND')
      group.conditionLogics[index - 1] = group.conditionLogics[index - 1] === 'AND' ? 'OR' : 'AND'
    },

    // ========== 授权书操作方法 ==========
    validateForm() {
      if (!this.formData.name) {
        this.showMessage('请输入授权书名称', 'warning')
        return false
      }
      if (this.formData.authPublishLevel.length === 0) {
        this.showMessage('请选择授权发布层级', 'warning')
        return false
      }
      if (this.formData.authPublishOrg.length === 0) {
        this.showMessage('请选择授权发布组织', 'warning')
        return false
      }
      if (this.formData.authTargetLevel.length === 0) {
        this.showMessage('请选择授权对象层级', 'warning')
        return false
      }
      if (this.formData.applicableRegion.length === 0) {
        this.showMessage('请选择适用区域', 'warning')
        return false
      }
      if (!this.formData.publishYear) {
        this.showMessage('请选择授权书发布年份', 'warning')
        return false
      }
      if (!this.formData.contentSummary) {
        this.showMessage('请输入授权书内容摘要', 'warning')
        return false
      }
      return true
    },

    getSubmitData() {
      return {
        name: this.formData.name,
        authPublishLevel: this.formData.authPublishLevel,
        authPublishOrg: this.formData.authPublishOrg,
        authTargetLevel: this.formData.authTargetLevel,
        applicableRegion: this.formData.applicableRegion,
        publishYear: this.formData.publishYear,
        contentSummary: this.formData.contentSummary,
        scenes: this.sceneData
      }
    },

    async handleSave() {
      if (!this.validateForm()) return

      try {
        this.loading = true
        const data = this.getSubmitData()
        let res

        if (this.isNew) {
          res = await api.createAuthLetter(data)
          if (res.code === 200) {
            this.authLetterId = res.data
            this.isNew = false
            this.showMessage('保存成功', 'success')
            // 更新URL
            window.history.replaceState({}, '', `?id=${this.authLetterId}`)
          } else {
            this.showMessage(res.message || '保存失败', 'error')
          }
        } else {
          res = await api.updateAuthLetter(this.authLetterId, data)
          if (res.code === 200) {
            this.showMessage('保存成功', 'success')
          } else {
            this.showMessage(res.message || '保存失败', 'error')
          }
        }
      } catch (error) {
        console.error('保存失败:', error)
        this.showMessage('保存失败', 'error')
      } finally {
        this.loading = false
      }
    },

    async handleSaveAndPublish() {
      if (!this.validateForm()) return

      try {
        this.loading = true
        const data = this.getSubmitData()
        let res

        if (this.isNew) {
          // 先创建
          res = await api.createAuthLetter(data)
          if (res.code === 200) {
            this.authLetterId = res.data
            this.isNew = false
            // 再发布
            const publishRes = await api.publishAuthLetter(this.authLetterId)
            if (publishRes.code === 200) {
              this.pageStatus = 'PUBLISHED'
              this.showMessage('保存并发布成功', 'success')
              window.history.replaceState({}, '', `?id=${this.authLetterId}`)
            } else {
              this.showMessage(publishRes.message || '发布失败', 'error')
            }
          } else {
            this.showMessage(res.message || '保存失败', 'error')
          }
        } else {
          // 先更新
          res = await api.updateAuthLetter(this.authLetterId, data)
          if (res.code === 200) {
            // 再发布
            const publishRes = await api.publishAuthLetter(this.authLetterId)
            if (publishRes.code === 200) {
              this.pageStatus = 'PUBLISHED'
              this.showMessage('保存并发布成功', 'success')
            } else {
              this.showMessage(publishRes.message || '发布失败', 'error')
            }
          } else {
            this.showMessage(res.message || '保存失败', 'error')
          }
        }
      } catch (error) {
        console.error('保存并发布失败:', error)
        this.showMessage('保存并发布失败', 'error')
      } finally {
        this.loading = false
      }
    },

    async handlePublish() {
      this.showConfirm('确定要发布该授权书吗？').then(async ok => {
        if (ok) {
          try {
            this.loading = true
            const res = await api.publishAuthLetter(this.authLetterId)
            if (res.code === 200) {
              this.pageStatus = 'PUBLISHED'
              this.showMessage('发布成功', 'success')
            } else {
              this.showMessage(res.message || '发布失败', 'error')
            }
          } catch (error) {
            console.error('发布失败:', error)
            this.showMessage('发布失败', 'error')
          } finally {
            this.loading = false
          }
        }
      })
    },

    handleCancel() {
      window.location.href = '#/AuthLetterList'
    },

    handleExpire() {
      this.showConfirm('确定要将该授权书设为失效吗？').then(async ok => {
        if (ok) {
          this.pageStatus = 'EXPIRED'
          this.showMessage('操作成功', 'success')
        }
      })
    },

    handleDeleteAuthLetter() {
      this.showConfirm('确定要删除该授权书吗？').then(async ok => {
        if (ok) {
          try {
            this.loading = true
            const res = await api.deleteAuthLetter(this.authLetterId)
            if (res.code === 200) {
              this.showMessage('删除成功', 'success')
              setTimeout(() => {
                window.location.href = '#/AuthLetterList'
              }, 1000)
            } else {
              this.showMessage(res.message || '删除失败', 'error')
            }
          } catch (error) {
            console.error('删除失败:', error)
            this.showMessage('删除失败', 'error')
          } finally {
            this.loading = false
          }
        }
      })
    },
    showMessage(text, type = 'info') {
      this.message.text = text
      this.message.type = type
      this.message.show = true
      setTimeout(() => { this.message.show = false }, 3000)
    },
    showConfirm(text) {
      return new Promise(resolve => {
        this.confirmDialog.text = text
        this.confirmDialog.show = true
        this.confirmDialog.onConfirm = () => { this.confirmDialog.show = false; resolve(true) }
        this.confirmDialog.onCancel = () => { this.confirmDialog.show = false; resolve(false) }
      })
    },
    handleClickOutside(e) {
      if (!e.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
        this.activeDropdown = ''
      }
    }
  }
}
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

/* 只读/禁用状态 */
.form-input:disabled, .form-textarea:disabled {
  background-color: #f5f7fa;
  cursor: not-allowed;
  color: #606266;
}
.multi-select-wrapper.disabled, .tree-select-wrapper.disabled {
  opacity: 0.7;
}
.multi-select-wrapper.disabled .multi-select-trigger,
.tree-select-wrapper.disabled .tree-select-trigger {
  background-color: #f5f7fa;
  cursor: not-allowed;
}
.icon-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 日志区块 */
.log-section .card-header.collapsible {
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.log-section .card-header.collapsible:hover {
  background-color: #f5f7fa;
}
.collapse-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.2s;
}
.collapse-icon.expanded {
  transform: rotate(180deg);
}
.col-action-type { width: 100px; text-align: center; }
.col-remark { min-width: 200px; }
</style>