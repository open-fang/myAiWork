<template>
  <div class="auth-letter-detail-page">
    <!-- 基本信息区 -->
    <div class="info-card">
      <div class="card-title">基本信息</div>
      <div class="info-form">
        <div class="form-row">
          <div class="form-item">
            <label class="form-label required">授权书名称</label>
            <input type="text" v-model="formData.name" class="form-input" :disabled="!isEditable" placeholder="请输入授权书名称" />
          </div>
          <div class="form-item">
            <label class="form-label required">授权发布层级</label>
            <div class="multi-select-wrapper">
              <div class="multi-select-trigger" @click="toggleSelect('authPublishLevel')" :class="{ disabled: !isEditable }">
                <span class="selected-tags" v-if="formData.authPublishLevel.length > 0">
                  <span class="tag" v-for="(item, index) in getSelectedLabels(formData.authPublishLevel, authPublishLevelOptions)" :key="index">{{ item }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="multi-select-dropdown" v-show="activeDropdown === 'authPublishLevel' && isEditable">
                <div class="select-option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                  <span class="checkbox" :class="{ checked: formData.authPublishLevel.includes(item.code) }"></span>
                  <span>{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label required">授权发布组织</label>
            <div class="tree-select-wrapper">
              <div class="tree-select-trigger" @click="toggleSelect('authPublishOrg')" :class="{ disabled: !isEditable }">
                <span class="selected-tags" v-if="formData.authPublishOrg.length > 0">
                  <span class="tag" v-for="(item, index) in formData.authPublishOrg.slice(0, 2)" :key="index">{{ getOrgName(item) }}</span>
                  <span class="tag" v-if="formData.authPublishOrg.length > 2">+{{ formData.authPublishOrg.length - 2 }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-show="activeDropdown === 'authPublishOrg' && isEditable">
                <tree-node v-for="node in authPublishOrgTree" :key="node.code" :node="node" :selected-codes="formData.authPublishOrg" @toggle="toggleOrgTreeNode" />
              </div>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label class="form-label required">授权对象层级</label>
            <div class="multi-select-wrapper">
              <div class="multi-select-trigger" @click="toggleSelect('authTargetLevel')" :class="{ disabled: !isEditable }">
                <span class="selected-tags" v-if="formData.authTargetLevel.length > 0">
                  <span class="tag" v-for="(item, index) in getSelectedLabels(formData.authTargetLevel, authTargetLevelOptions)" :key="index">{{ item }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="multi-select-dropdown" v-show="activeDropdown === 'authTargetLevel' && isEditable">
                <div class="select-option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                  <span class="checkbox" :class="{ checked: formData.authTargetLevel.includes(item.code) }"></span>
                  <span>{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label required">适用区域</label>
            <div class="tree-select-wrapper">
              <div class="tree-select-trigger" @click="toggleSelect('applicableRegion')" :class="{ disabled: !isEditable }">
                <span class="selected-tags" v-if="formData.applicableRegion.length > 0">
                  <span class="tag" v-for="(item, index) in formData.applicableRegion.slice(0, 2)" :key="index">{{ getRegionName(item) }}</span>
                  <span class="tag" v-if="formData.applicableRegion.length > 2">+{{ formData.applicableRegion.length - 2 }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-show="activeDropdown === 'applicableRegion' && isEditable">
                <tree-node v-for="node in applicableRegionTree" :key="node.code" :node="node" :selected-codes="formData.applicableRegion" @toggle="toggleRegionTreeNode" />
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label required">授权书发布年份</label>
            <div class="year-select-wrapper">
              <div class="year-select-trigger" @click="toggleSelect('publishYear')" :class="{ disabled: !isEditable }">
                <span>{{ formData.publishYear || '请选择年份' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="year-select-dropdown" v-show="activeDropdown === 'publishYear' && isEditable">
                <div class="year-option" v-for="year in yearOptions" :key="year" @click="selectYear(year)">{{ year }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="form-row single">
          <div class="form-item full">
            <label class="form-label required">授权书内容摘要</label>
            <textarea v-model="formData.summary" class="form-textarea" :disabled="!isEditable" placeholder="请输入授权书内容摘要" maxlength="4000" rows="4"></textarea>
            <span class="char-count">{{ (formData.summary || '').length }}/4000</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="info-card" v-if="isEditable || attachmentList.length > 0">
      <div class="card-title">附件</div>
      <div class="section-content">
        <div class="action-row" v-if="isEditable">
          <button class="btn btn-default btn-sm" @click="handleUpload">上传</button>
          <button class="btn btn-default btn-sm" @click="handleDownloadAttachment">下载</button>
          <button class="btn btn-default btn-sm" @click="handleDeleteAttachment">删除</button>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="attachmentSelectAll" @change="handleAttachmentSelectAll" /></th>
              <th class="col-index">序号</th>
              <th class="col-action">操作</th>
              <th class="col-name">文档名称</th>
              <th class="col-type">文档类型</th>
              <th class="col-user">创建人</th>
              <th class="col-time">创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="attachmentList.length === 0">
              <td :colspan="isEditable ? 7 : 6" style="text-align: center; padding: 20px; color: #909399;">暂无附件</td>
            </tr>
            <tr v-else v-for="(row, index) in attachmentList" :key="row.id">
              <td class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectedAttachments" :value="row.id" /></td>
              <td class="col-index">{{ (attachmentPageNum - 1) * attachmentPageSize + index + 1 }}</td>
              <td class="col-action">
                <span class="icon-btn" @click="handleDownloadSingle(row)" title="下载">📥</span>
                <span class="icon-btn disabled" title="加密(暂不可用)">🔒</span>
              </td>
              <td class="col-name"><a class="link" @click="handleDownloadSingle(row)">{{ row.docName }}</a></td>
              <td class="col-type">{{ getDocTypeText(row.docType) }}</td>
              <td class="col-user">{{ row.createdBy || '-' }}</td>
              <td class="col-time">{{ formatTime(row.createdTime) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination" v-if="attachmentTotal > 0">
          <span>共 {{ attachmentTotal }} 条</span>
          <select v-model="attachmentPageSize" @change="loadAttachmentList">
            <option :value="10">10条/页</option>
            <option :value="30">30条/页</option>
            <option :value="50">50条/页</option>
          </select>
          <button :disabled="attachmentPageNum === 1" @click="attachmentPageNum--; loadAttachmentList()">上一页</button>
          <span>第 {{ attachmentPageNum }} 页</span>
          <button :disabled="attachmentPageNum >= attachmentTotalPages" @click="attachmentPageNum++; loadAttachmentList()">下一页</button>
        </div>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="info-card">
      <div class="card-title">授权规则</div>
      <div class="section-content">
        <div class="action-row" v-if="isEditable">
          <button class="btn btn-primary btn-sm" @click="handleAddScene">添加场景</button>
          <button class="btn btn-default btn-sm" @click="handleDeleteScene">删除</button>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="sceneSelectAll" @change="handleSceneSelectAll" /></th>
              <th class="col-index">序号</th>
              <th class="col-action" v-if="isEditable">操作</th>
              <th class="col-name">场景</th>
              <th class="col-name">产业</th>
              <th class="col-name">业务场景</th>
              <th class="col-name">具体规则</th>
              <th class="col-name">决策层级</th>
              <th class="col-user">创建人</th>
              <th class="col-time">创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sceneList.length === 0">
              <td :colspan="isEditable ? 10 : 9" style="text-align: center; padding: 20px; color: #909399;">暂无授权规则</td>
            </tr>
            <tr v-else v-for="(row, index) in sceneList" :key="row.id">
              <td class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectedScenes" :value="row.id" /></td>
              <td class="col-index">{{ (scenePageNum - 1) * scenePageSize + index + 1 }}</td>
              <td class="col-action" v-if="isEditable">
                <span class="icon-btn" @click="handleEditScene(row)" title="编辑">✏️</span>
                <span class="icon-btn" @click="handleDeleteSingleScene(row.id)" title="删除">🗑️</span>
              </td>
              <td class="col-name">{{ row.sceneName }}</td>
              <td class="col-name">{{ getIndustryText(row.industry) }}</td>
              <td class="col-name">{{ getBusinessSceneText(row.businessScene) }}</td>
              <td class="col-name">{{ row.specificRule || '-' }}</td>
              <td class="col-name">{{ getDecisionLevelText(row.decisionLevel) }}</td>
              <td class="col-user">{{ row.createdBy || '-' }}</td>
              <td class="col-time">{{ formatTime(row.createdTime) }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination" v-if="sceneTotal > 0">
          <span>共 {{ sceneTotal }} 条</span>
          <select v-model="scenePageSize" @change="loadSceneList">
            <option :value="10">10条/页</option>
            <option :value="30">30条/页</option>
            <option :value="50">50条/页</option>
          </select>
          <button :disabled="scenePageNum === 1" @click="scenePageNum--; loadSceneList()">上一页</button>
          <span>第 {{ scenePageNum }} 页</span>
          <button :disabled="scenePageNum >= sceneTotalPages" @click="scenePageNum++; loadSceneList()">下一页</button>
        </div>
      </div>
    </div>

    <!-- 日志区块（已发布状态显示） -->
    <div class="info-card" v-if="showLogSection">
      <div class="card-title collapsible" @click="logCollapsed = !logCollapsed">
        日志
        <span class="expand-icon" :class="{ expanded: !logCollapsed }">▼</span>
      </div>
      <div class="section-content" v-show="!logCollapsed">
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-index">序号</th>
              <th class="col-name">操作</th>
              <th class="col-user">操作人</th>
              <th class="col-time">操作时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="logList.length === 0">
              <td colspan="4" style="text-align: center; padding: 20px; color: #909399;">暂无日志</td>
            </tr>
            <tr v-else v-for="(row, index) in logList" :key="row.id">
              <td class="col-index">{{ (logPageNum - 1) * logPageSize + index + 1 }}</td>
              <td class="col-name">{{ getOperationText(row.operation) }}</td>
              <td class="col-user">{{ row.operator || '-' }}</td>
              <td class="col-time">{{ formatTime(row.operationTime) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="footer-buttons">
      <template v-if="isEditable">
        <button class="btn btn-primary" @click="handleSave">保存</button>
        <button class="btn btn-primary" @click="handleSaveAndPublish" v-if="!isNew">保存并发布</button>
        <button class="btn btn-primary" @click="handlePublish" v-if="!isNew">发布</button>
        <button class="btn btn-default" @click="handleCancel">取消</button>
        <button class="btn btn-danger" @click="handleDelete" v-if="!isNew">删除</button>
      </template>
      <template v-else>
        <button class="btn btn-default" @click="handleBack">返回</button>
        <button class="btn btn-primary" @click="handleDeactivate" v-if="formData.status === 'PUBLISHED'">失效</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal" v-if="showSceneModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingSceneId ? '编辑场景' : '添加场景' }}</h3>
          <span class="close" @click="closeSceneModal">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item required">
              <label class="form-label">场景名称</label>
              <input type="text" v-model="sceneForm.sceneName" class="form-input" placeholder="请输入场景名称" />
            </div>
            <div class="form-item required">
              <label class="form-label">产业</label>
              <div class="multi-select-wrapper">
                <div class="multi-select-trigger" @click="toggleSelect('sceneIndustry')">
                  <span class="selected-tags" v-if="sceneForm.industry.length > 0">
                    <span class="tag" v-for="(item, index) in getSelectedLabels(sceneForm.industry, industryOptions)" :key="index">{{ item }}</span>
                  </span>
                  <span class="placeholder" v-else>请选择</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="multi-select-dropdown" v-show="activeDropdown === 'sceneIndustry'">
                  <div class="select-option" v-for="item in industryOptions" :key="item.code" @click="toggleSceneIndustry(item.code)">
                    <span class="checkbox" :class="{ checked: sceneForm.industry.includes(item.code) }"></span>
                    <span>{{ item.name }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item required">
              <label class="form-label">业务场景</label>
              <div class="select-wrapper">
                <div class="select-trigger" @click="toggleSelect('sceneBusinessScene')">
                  <span>{{ getBusinessSceneText(sceneForm.businessScene) || '请选择' }}</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="select-dropdown" v-show="activeDropdown === 'sceneBusinessScene'">
                  <div class="select-option" @click="selectSceneBusinessScene('')">请选择</div>
                  <div class="select-option" v-for="item in businessSceneOptions" :key="item.code" @click="selectSceneBusinessScene(item.code)">
                    {{ item.name }}
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="form-label">决策层级</label>
              <div class="select-wrapper">
                <div class="select-trigger" @click="toggleSelect('sceneDecisionLevel')">
                  <span>{{ getDecisionLevelText(sceneForm.decisionLevel) || '请选择' }}</span>
                  <span class="arrow">▼</span>
                </div>
                <div class="select-dropdown" v-show="activeDropdown === 'sceneDecisionLevel'">
                  <div class="select-option" @click="selectSceneDecisionLevel('')">请选择</div>
                  <div class="select-option" v-for="item in decisionLevelOptions" :key="item.code" @click="selectSceneDecisionLevel(item.code)">
                    {{ item.name }}
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item full-width">
              <label class="form-label">具体规则</label>
              <textarea v-model="sceneForm.specificRule" class="form-textarea" maxlength="1000" placeholder="请输入具体规则" rows="3"></textarea>
            </div>
          </div>

          <!-- 规则配置 -->
          <div class="rule-config">
            <div class="rule-config-header">
              <span>规则配置</span>
            </div>
            <div class="rule-config-body">
              <div v-if="sceneForm.ruleConditions.length === 0" class="empty-rule">暂无规则条件</div>
              <div v-else>
                <div v-for="(cond, index) in sceneForm.ruleConditions" :key="index" class="condition-item">
                  <!-- 单个条件 -->
                  <div v-if="cond.type === 'condition' || !cond.type" class="condition-row">
                    <select v-model="cond.fieldId" @change="onFieldChange(cond)">
                      <option value="">请选择规则字段</option>
                      <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                        {{ field.name }}
                      </option>
                    </select>
                    <template v-if="cond.fieldId">
                      <select v-model="cond.operator">
                        <option value=">">></option>
                        <option value="<"><</option>
                        <option value="=">=</option>
                        <option value=">=">>=</option>
                        <option value="<="><=</option>
                        <option value="!=">!=</option>
                      </select>
                      <select v-model="cond.compareType">
                        <option value="FIELD">对比字段</option>
                        <option value="NUMBER">数值</option>
                        <option value="TEXT">文本</option>
                      </select>
                      <template v-if="cond.compareType === 'FIELD'">
                        <select v-model="cond.compareFieldId">
                          <option value="">请选择字段</option>
                          <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                            {{ field.name }}
                          </option>
                        </select>
                      </template>
                      <template v-else-if="cond.compareType === 'NUMBER'">
                        <input type="number" v-model="cond.compareValue" placeholder="请输入数值" />
                        <select v-model="cond.unit">
                          <option value="">单位</option>
                          <option v-for="item in measureUnitOptions" :key="item.code" :value="item.code">
                            {{ item.name }}
                          </option>
                        </select>
                      </template>
                      <template v-else>
                        <input type="text" v-model="cond.compareValue" placeholder="请输入文本" />
                      </template>
                    </template>
                    <span class="remove-btn" @click="removeCondition(index)">✕</span>
                    <div class="condition-logic" v-if="index < sceneForm.ruleConditions.length - 1">
                      <span class="logic-btn" @click="toggleLogic(index)">
                        {{ cond.logic || '且' }}
                      </span>
                    </div>
                  </div>
                  <!-- 条件组 -->
                  <div v-else-if="cond.type === 'group'" class="condition-group">
                    <div class="condition-group-header">
                      <span class="logic-toggle" @click="toggleGroupLogic(index)">{{ cond.logic }}</span>
                      <span class="remove-btn" @click="removeCondition(index)">✕ 删除组</span>
                    </div>
                    <div class="condition-group-body">
                      <div v-if="cond.conditions.length === 0" class="empty-group">暂无条件</div>
                      <div v-for="(subCond, subIndex) in cond.conditions" :key="subIndex" class="condition-row">
                        <select v-model="subCond.fieldId" @change="onFieldChange(subCond)">
                          <option value="">请选择规则字段</option>
                          <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                            {{ field.name }}
                          </option>
                        </select>
                        <template v-if="subCond.fieldId">
                          <select v-model="subCond.operator">
                            <option value=">">></option>
                            <option value="<"><</option>
                            <option value="=">=</option>
                            <option value=">=">>=</option>
                            <option value="<="><=</option>
                            <option value="!=">!=</option>
                          </select>
                          <select v-model="subCond.compareType">
                            <option value="FIELD">对比字段</option>
                            <option value="NUMBER">数值</option>
                            <option value="TEXT">文本</option>
                          </select>
                          <template v-if="subCond.compareType === 'FIELD'">
                            <select v-model="subCond.compareFieldId">
                              <option value="">请选择字段</option>
                              <option v-for="field in ruleParamList" :key="field.id" :value="field.id">
                                {{ field.name }}
                              </option>
                            </select>
                          </template>
                          <template v-else-if="subCond.compareType === 'NUMBER'">
                            <input type="number" v-model="subCond.compareValue" placeholder="请输入数值" />
                            <select v-model="subCond.unit">
                              <option value="">单位</option>
                              <option v-for="item in measureUnitOptions" :key="item.code" :value="item.code">
                                {{ item.name }}
                              </option>
                            </select>
                          </template>
                          <template v-else>
                            <input type="text" v-model="subCond.compareValue" placeholder="请输入文本" />
                          </template>
                        </template>
                        <span class="remove-btn" @click="removeSubCondition(index, subIndex)">✕</span>
                        <div class="condition-logic" v-if="subIndex < cond.conditions.length - 1">
                          <span class="logic-btn" @click="toggleSubLogic(index, subIndex)">
                            {{ subCond.logic || '且' }}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div class="condition-group-footer">
                      <span class="text-btn" @click="addSubCondition(index)">+ 添加条件</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="rule-config-actions">
                <span class="text-btn" @click="addCondition">+ 新增条件</span>
                <span class="text-btn" @click="addConditionGroup">+ 新增子条件组</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveScene">确定</button>
          <button class="btn btn-default" @click="closeSceneModal">取消</button>
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
    hasChildren() { return this.node.children && this.node.children.length > 0 },
    isChecked() { return this.selectedCodes.includes(this.node.code) }
  },
  methods: {
    toggleCheck() { this.$emit('toggle', this.node) },
    toggleExpand() { this.expanded = !this.expanded }
  },
  template: `
    <div class="tree-node">
      <div class="tree-node-content" :style="{ paddingLeft: (node.level || 0) * 20 + 'px' }">
        <span v-if="hasChildren" class="tree-expand-icon" :class="{ expanded: expanded }" @click="toggleExpand">▶</span>
        <span v-else class="tree-expand-placeholder"></span>
        <span class="tree-checkbox" :class="{ checked: isChecked }" @click="toggleCheck"></span>
        <span class="tree-node-label" @click="toggleCheck">{{ node.name }}</span>
      </div>
      <div v-if="hasChildren && expanded" class="tree-children">
        <tree-node v-for="child in node.children" :key="child.code" :node="{ ...child, level: (node.level || 0) + 1 }" :selected-codes="selectedCodes" @toggle="$emit('toggle', $event)" />
      </div>
    </div>
  `
}

// ========== API 请求方法 ==========
import axios from 'axios';

const BASE_URL = '/api';
const axiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
});
axiosInstance.interceptors.response.use(
  response => response.data,
  error => { console.error('请求错误:', error); return Promise.reject(error); }
);
const request = {
  get: (url, params) => axiosInstance.get(url, { params }),
  post: (url, data) => axiosInstance.post(url, data),
  put: (url, data) => axiosInstance.put(url, data),
  delete: (url, params) => axiosInstance.delete(url, { params })
};

export default {
  name: 'AuthLetterDetail',
  components: { TreeNode },
  data() {
    return {
      letterId: null,
      isNew: true,
      activeDropdown: '',
      formData: {
        name: '',
        authTargetLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        summary: '',
        status: 'DRAFT'
      },

      // 下拉列表数据
      authTargetLevelOptions: [],
      authPublishLevelOptions: [],
      authPublishOrgTree: [],
      applicableRegionOptions: [],
      applicableRegionTree: [],
      industryOptions: [],
      businessSceneOptions: [],
      decisionLevelOptions: [],
      docTypeOptions: [],
      measureUnitOptions: [],
      ruleParamList: [],

      // 附件
      attachmentList: [],
      selectedAttachments: [],
      attachmentSelectAll: false,
      attachmentPageNum: 1,
      attachmentPageSize: 10,
      attachmentTotal: 0,

      // 场景
      sceneList: [],
      selectedScenes: [],
      sceneSelectAll: false,
      scenePageNum: 1,
      scenePageSize: 10,
      sceneTotal: 0,

      // 场景弹窗
      showSceneModal: false,
      editingSceneId: null,
      sceneForm: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        ruleConditions: []
      },

      // 日志
      logList: [],
      logCollapsed: true,
      logPageNum: 1,
      logPageSize: 10,
      logTotal: 0
    };
  },
  computed: {
    isEditable() {
      return this.formData.status === 'DRAFT';
    },
    showLogSection() {
      return this.formData.status === 'PUBLISHED' || this.formData.status === 'INVALID';
    },
    yearOptions() {
      const years = [];
      for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) years.push(i);
      return years;
    },
    attachmentTotalPages() {
      return Math.ceil(this.attachmentTotal / this.attachmentPageSize) || 1;
    },
    sceneTotalPages() {
      return Math.ceil(this.sceneTotal / this.scenePageSize) || 1;
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
    const params = new URLSearchParams(window.location.hash.split('?')[1]);
    this.letterId = params.get('id');
    this.isNew = !this.letterId;
    this.loadLookupData();
    if (!this.isNew) {
      this.loadDetail();
      this.loadAttachmentList();
      this.loadSceneList();
    }
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside);
  },
  methods: {
    async loadLookupData() {
      try {
        const [
          targetLevel,
          publishLevel,
          publishOrg,
          region,
          industry,
          businessScene,
          decisionLevel,
          docType,
          measureUnit,
          ruleParams
        ] = await Promise.all([
          request.get('/lookup/AUTH_TARGET_LEVEL'),
          request.get('/lookup/AUTH_PUBLISH_LEVEL'),
          request.get('/lookup/AUTH_PUBLISH_ORG'),
          request.get('/lookup/APPLICABLE_REGION'),
          request.get('/lookup/INDUSTRY'),
          request.get('/lookup/BUSINESS_SCENE'),
          request.get('/lookup/DECISION_LEVEL'),
          request.get('/lookup/DOC_TYPE'),
          request.get('/lookup/MEASURE_UNIT'),
          request.get('/rule-param/allActive')
        ]);
        // 扁平列表（使用 transformLookupData 转换字段名）
        this.authTargetLevelOptions = targetLevel.code === 200 ? this.transformLookupData(targetLevel.data) : [];
        this.authPublishLevelOptions = publishLevel.code === 200 ? this.transformLookupData(publishLevel.data) : [];
        this.industryOptions = industry.code === 200 ? this.transformLookupData(industry.data) : [];
        this.businessSceneOptions = businessScene.code === 200 ? this.transformLookupData(businessScene.data) : [];
        this.decisionLevelOptions = decisionLevel.code === 200 ? this.transformLookupData(decisionLevel.data) : [];
        this.docTypeOptions = docType.code === 200 ? this.transformLookupData(docType.data) : [];
        this.measureUnitOptions = measureUnit.code === 200 ? this.transformLookupData(measureUnit.data) : [];
        // 树形结构
        this.authPublishOrgTree = publishOrg.code === 200 ? this.transformLookupData(publishOrg.data) : [];
        this.applicableRegionOptions = region.code === 200 ? this.transformLookupData(region.data) : [];
        this.applicableRegionTree = this.applicableRegionOptions;
        // 规则参数
        this.ruleParamList = ruleParams.code === 200 ? (ruleParams.data || []) : [];
      } catch (error) {
        console.error('加载下拉数据失败:', error);
      }
    },

    transformLookupData(data) {
      if (!data || !Array.isArray(data)) return [];
      return data.map(item => ({
        code: item.value,
        name: item.label,
        children: item.children && item.children.length > 0 ? this.transformLookupData(item.children) : []
      }));
    },

    async loadDetail() {
      try {
        const res = await request.get(`/authorization/detail?id=${this.letterId}`);
        if (res.code === 200 && res.data) {
          this.formData = {
            name: res.data.name || '',
            authTargetLevel: res.data.authTargetLevel || [],
            applicableRegion: res.data.applicableRegion || [],
            authPublishLevel: res.data.authPublishLevel || [],
            authPublishOrg: res.data.authPublishOrg || [],
            publishYear: res.data.publishYear,
            summary: res.data.summary || '',
            status: res.data.status || 'DRAFT'
          };
          if (this.showLogSection) {
            this.loadLogList();
          }
        }
      } catch (error) {
        console.error('加载详情失败:', error);
      }
    },

    async loadAttachmentList() {
      try {
        const res = await request.get(`/attachment/list?letterId=${this.letterId}&pageNum=${this.attachmentPageNum}&pageSize=${this.attachmentPageSize}`);
        if (res.code === 200) {
          this.attachmentList = res.data.list || [];
          this.attachmentTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载附件失败:', error);
      }
    },

    async loadSceneList() {
      try {
        const res = await request.get(`/scene/list?letterId=${this.letterId}&pageNum=${this.scenePageNum}&pageSize=${this.scenePageSize}`);
        if (res.code === 200) {
          this.sceneList = res.data.list || [];
          this.sceneTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载场景失败:', error);
      }
    },

    async loadLogList() {
      try {
        const res = await request.post('/log/list', {
          letterId: this.letterId,
          pageNum: this.logPageNum,
          pageSize: this.logPageSize
        });
        if (res.code === 200) {
          this.logList = res.data.list || [];
          this.logTotal = res.data.total || 0;
        }
      } catch (error) {
        console.error('加载日志失败:', error);
      }
    },

    // 下拉选择
    toggleSelect(name) {
      this.activeDropdown = this.activeDropdown === name ? '' : name;
    },
    toggleMultiSelect(field, value) {
      const index = this.formData[field].indexOf(value);
      if (index > -1) this.formData[field].splice(index, 1);
      else this.formData[field].push(value);
    },
    selectYear(year) {
      this.formData.publishYear = year;
      this.activeDropdown = '';
    },
    getSelectedLabels(codes, options) {
      return codes.map(code => {
        const item = options.find(o => o.code === code);
        return item ? item.name : code;
      });
    },
    getOrgName(code) {
      const findNode = (nodes, targetCode) => {
        for (const node of nodes) {
          if (node.code === targetCode) return node.name;
          if (node.children && node.children.length > 0) {
            const found = findNode(node.children, targetCode);
            if (found) return found;
          }
        }
        return null;
      };
      return findNode(this.authPublishOrgTree, code) || code;
    },
    getRegionName(code) {
      const findNode = (nodes, targetCode) => {
        for (const node of nodes) {
          if (node.code === targetCode) return node.name;
          if (node.children && node.children.length > 0) {
            const found = findNode(node.children, targetCode);
            if (found) return found;
          }
        }
        return null;
      };
      return findNode(this.applicableRegionOptions, code) || code;
    },
    toggleOrgTreeNode(node) {
      const index = this.formData.authPublishOrg.indexOf(node.code);
      const allCodes = this.getAllNodeCodes(node);
      if (index > -1) {
        // 取消选中：移除当前节点和所有子节点
        allCodes.forEach(code => {
          const idx = this.formData.authPublishOrg.indexOf(code);
          if (idx > -1) this.formData.authPublishOrg.splice(idx, 1);
        });
      } else {
        // 选中：添加当前节点和所有子节点
        allCodes.forEach(code => {
          if (!this.formData.authPublishOrg.includes(code)) {
            this.formData.authPublishOrg.push(code);
          }
        });
      }
    },
    toggleRegionTreeNode(node) {
      const index = this.formData.applicableRegion.indexOf(node.code);
      const allCodes = this.getAllNodeCodes(node);
      if (index > -1) {
        // 取消选中：移除当前节点和所有子节点
        allCodes.forEach(code => {
          const idx = this.formData.applicableRegion.indexOf(code);
          if (idx > -1) this.formData.applicableRegion.splice(idx, 1);
        });
      } else {
        // 选中：添加当前节点和所有子节点
        allCodes.forEach(code => {
          if (!this.formData.applicableRegion.includes(code)) {
            this.formData.applicableRegion.push(code);
          }
        });
      }
    },
    getAllNodeCodes(node) {
      let codes = [node.code];
      if (node.children && node.children.length > 0) {
        node.children.forEach(child => {
          codes = codes.concat(this.getAllNodeCodes(child));
        });
      }
      return codes;
    },

    // 附件操作
    handleAttachmentSelectAll() {
      this.selectedAttachments = this.attachmentSelectAll ? this.attachmentList.map(item => item.id) : [];
    },
    handleUpload() {
      alert('上传功能暂未实现');
    },
    handleDownloadAttachment() {
      if (this.selectedAttachments.length === 0) {
        alert('请选择要下载的附件');
        return;
      }
      alert('下载功能暂未实现');
    },
    handleDownloadSingle(item) {
      alert('下载功能暂未实现: ' + item.docName);
    },
    async handleDeleteAttachment() {
      if (this.selectedAttachments.length === 0) {
        alert('请选择要删除的附件');
        return;
      }
      if (!confirm('确定要删除选中的附件吗？')) return;
      try {
        await request.post('/attachment/deleteBatch', this.selectedAttachments);
        alert('删除成功');
        this.loadAttachmentList();
      } catch (error) {
        alert('删除失败');
      }
    },

    // 场景操作
    handleSceneSelectAll() {
      this.selectedScenes = this.sceneSelectAll ? this.sceneList.map(item => item.id) : [];
    },
    handleAddScene() {
      this.editingSceneId = null;
      this.sceneForm = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        ruleConditions: []
      };
      this.showSceneModal = true;
    },
    handleEditScene(item) {
      this.editingSceneId = item.id;
      this.sceneForm = {
        sceneName: item.sceneName,
        industry: item.industry || [],
        businessScene: item.businessScene,
        decisionLevel: item.decisionLevel,
        specificRule: item.specificRule,
        ruleConditions: item.ruleConfig ? this.parseRuleConfig(item.ruleConfig) : []
      };
      this.showSceneModal = true;
    },
    parseRuleConfig(config) {
      if (!config) return [];
      try {
        const parsed = typeof config === 'string' ? JSON.parse(config) : config;
        return this.parseConditions(parsed);
      } catch (e) {
        return [];
      }
    },
    parseConditions(group) {
      if (!group || !group.conditions) return [];
      return group.conditions.map(cond => {
        if (cond.type === 'group') {
          return {
            type: 'group',
            logic: cond.logic || 'AND',
            conditions: this.parseConditions(cond)
          };
        } else {
          return {
            type: 'condition',
            fieldId: cond.fieldId,
            fieldName: cond.fieldName,
            operator: cond.operator,
            compareType: cond.compareType,
            compareValue: cond.compareValue,
            unit: cond.unit,
            compareFieldId: cond.compareFieldId,
            logic: 'AND'
          };
        }
      });
    },
    closeSceneModal() {
      this.showSceneModal = false;
    },
    async saveScene() {
      if (!this.sceneForm.sceneName) {
        alert('请输入场景名称');
        return;
      }
      try {
        const data = {
          id: this.editingSceneId,
          letterId: this.letterId,
          ...this.sceneForm,
          ruleConfig: this.buildRuleConfig()
        };
        if (this.editingSceneId) {
          await request.put('/scene/update', data);
        } else {
          await request.post('/scene/create', data);
        }
        alert('保存成功');
        this.closeSceneModal();
        this.loadSceneList();
      } catch (error) {
        alert('保存失败');
      }
    },
    buildRuleConfig() {
      if (!this.sceneForm.ruleConditions || this.sceneForm.ruleConditions.length === 0) {
        return null;
      }
      return {
        logic: 'AND',
        conditions: this.sceneForm.ruleConditions.map(cond => {
          if (cond.type === 'group') {
            return {
              type: 'group',
              logic: cond.logic || 'AND',
              conditions: cond.conditions.map(subCond => ({
                type: 'condition',
                fieldId: subCond.fieldId,
                fieldName: this.getFieldName(subCond.fieldId),
                operator: subCond.operator,
                compareType: subCond.compareType,
                compareValue: subCond.compareValue,
                unit: subCond.unit,
                compareFieldId: subCond.compareFieldId
              }))
            };
          } else {
            return {
              type: 'condition',
              fieldId: cond.fieldId,
              fieldName: this.getFieldName(cond.fieldId),
              operator: cond.operator,
              compareType: cond.compareType,
              compareValue: cond.compareValue,
              unit: cond.unit,
              compareFieldId: cond.compareFieldId
            };
          }
        })
      };
    },
    getFieldName(fieldId) {
      const field = this.ruleParamList.find(f => f.id === fieldId);
      return field ? field.name : '';
    },
    async handleDeleteScene() {
      if (this.selectedScenes.length === 0) {
        alert('请选择要删除的场景');
        return;
      }
      if (!confirm('确定要删除选中的场景吗？')) return;
      try {
        await request.post('/scene/deleteBatch', this.selectedScenes);
        alert('删除成功');
        this.loadSceneList();
      } catch (error) {
        alert('删除失败');
      }
    },
    async handleDeleteSingleScene(id) {
      if (!confirm('确定要删除该场景吗？')) return;
      try {
        await request.delete(`/scene/delete?id=${id}`);
        alert('删除成功');
        this.loadSceneList();
      } catch (error) {
        alert('删除失败');
      }
    },

    // 场景弹窗下拉
    toggleSceneIndustry(code) {
      const index = this.sceneForm.industry.indexOf(code);
      if (index > -1) this.sceneForm.industry.splice(index, 1);
      else this.sceneForm.industry.push(code);
    },
    selectSceneBusinessScene(code) {
      this.sceneForm.businessScene = code;
      this.activeDropdown = '';
    },
    selectSceneDecisionLevel(code) {
      this.sceneForm.decisionLevel = code;
      this.activeDropdown = '';
    },

    // 规则配置
    addCondition() {
      this.sceneForm.ruleConditions.push({
        type: 'condition',
        fieldId: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        unit: '',
        compareFieldId: '',
        logic: 'AND'
      });
    },
    addConditionGroup() {
      this.sceneForm.ruleConditions.push({
        type: 'group',
        logic: 'AND',
        conditions: []
      });
    },
    addSubCondition(groupIndex) {
      this.sceneForm.ruleConditions[groupIndex].conditions.push({
        type: 'condition',
        fieldId: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        unit: '',
        compareFieldId: '',
        logic: 'AND'
      });
    },
    removeCondition(index) {
      this.sceneForm.ruleConditions.splice(index, 1);
    },
    removeSubCondition(groupIndex, subIndex) {
      this.sceneForm.ruleConditions[groupIndex].conditions.splice(subIndex, 1);
    },
    onFieldChange(condition) {
      const field = this.ruleParamList.find(f => f.id === condition.fieldId);
      if (field) {
        condition.compareType = field.dataType === 'NUMBER' ? 'NUMBER' : 'TEXT';
      }
    },
    toggleLogic(index) {
      const condition = this.sceneForm.ruleConditions[index];
      condition.logic = condition.logic === 'AND' ? 'OR' : 'AND';
    },
    toggleGroupLogic(groupIndex) {
      const group = this.sceneForm.ruleConditions[groupIndex];
      group.logic = group.logic === 'AND' ? 'OR' : 'AND';
    },
    toggleSubLogic(groupIndex, subIndex) {
      const subCond = this.sceneForm.ruleConditions[groupIndex].conditions[subIndex];
      subCond.logic = subCond.logic === 'AND' ? 'OR' : 'AND';
    },

    // 工具方法
    getDocTypeText(type) {
      const item = this.docTypeOptions.find(d => d.code === type);
      return item ? item.name : type;
    },
    getIndustryText(codes) {
      if (!codes || codes.length === 0) return '-';
      return codes.map(c => {
        const item = this.industryOptions.find(o => o.code === c);
        return item ? item.name : c;
      }).join('、');
    },
    getBusinessSceneText(code) {
      const item = this.businessSceneOptions.find(d => d.code === code);
      return item ? item.name : (code || '-');
    },
    getDecisionLevelText(code) {
      const item = this.decisionLevelOptions.find(d => d.code === code);
      return item ? item.name : (code || '-');
    },
    getOperationText(operation) {
      const map = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'PUBLISH': '发布',
        'DEACTIVATE': '失效',
        'DELETE': '删除'
      };
      return map[operation] || operation;
    },
    formatTime(time) {
      if (!time) return '-';
      return time.replace('T', ' ').substring(0, 19);
    },

    handleClickOutside(event) {
      if (!event.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
        this.activeDropdown = '';
      }
    },

    // 保存相关
    async handleSave() {
      if (!this.validateForm()) return;
      try {
        if (this.isNew) {
          const res = await request.post('/authorization/create', this.formData);
          if (res.code === 200) {
            this.letterId = res.data;
            this.isNew = false;
            alert('保存成功');
          }
        } else {
          await request.put('/authorization/update', { ...this.formData, id: this.letterId });
          alert('保存成功');
        }
      } catch (error) {
        alert('保存失败');
      }
    },
    async handleSaveAndPublish() {
      await this.handleSave();
      if (this.letterId) {
        await this.handlePublish();
      }
    },
    async handlePublish() {
      if (!this.letterId) {
        alert('请先保存');
        return;
      }
      try {
        await request.post(`/authorization/publish?id=${this.letterId}`);
        alert('发布成功');
        this.loadDetail();
      } catch (error) {
        alert('发布失败');
      }
    },
    handleCancel() {
      window.location.href = '#/AuthLetterList';
    },
    handleBack() {
      window.location.href = '#/AuthLetterList';
    },
    async handleDeactivate() {
      if (!confirm('确定要失效该授权书吗？')) return;
      try {
        await request.post(`/authorization/deactivate?id=${this.letterId}`);
        alert('操作成功');
        this.loadDetail();
      } catch (error) {
        alert('操作失败');
      }
    },
    async handleDelete() {
      if (!confirm('确定要删除该授权书吗？')) return;
      try {
        await request.delete(`/authorization/delete?id=${this.letterId}`);
        alert('删除成功');
        window.location.href = '#/AuthLetterList';
      } catch (error) {
        alert('删除失败');
      }
    },
    validateForm() {
      if (!this.formData.name) {
        alert('请输入授权书名称');
        return false;
      }
      if (!this.formData.authPublishLevel || this.formData.authPublishLevel.length === 0) {
        alert('请选择授权发布层级');
        return false;
      }
      if (!this.formData.authPublishOrg || this.formData.authPublishOrg.length === 0) {
        alert('请选择授权发布组织');
        return false;
      }
      if (!this.formData.authTargetLevel || this.formData.authTargetLevel.length === 0) {
        alert('请选择授权对象层级');
        return false;
      }
      if (!this.formData.applicableRegion || this.formData.applicableRegion.length === 0) {
        alert('请选择适用区域');
        return false;
      }
      if (!this.formData.publishYear) {
        alert('请选择授权书发布年份');
        return false;
      }
      return true;
    }
  }
};
</script>

<style scoped>
* { box-sizing: border-box; }
.auth-letter-detail-page { padding: 20px; background: #f5f7fa; min-height: 100vh; padding-bottom: 80px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 14px; color: #333; }
.info-card { background: #fff; border-radius: 4px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1); }
.card-title { padding: 12px 20px; border-bottom: 1px solid #e8e8e8; font-weight: 600; font-size: 15px; }
.card-title.collapsible { cursor: pointer; }
.expand-icon { float: right; transition: transform 0.2s; }
.expand-icon.expanded { transform: rotate(180deg); }
.section-content { padding: 16px 20px; }
.info-form { padding: 20px; }
.form-row { display: flex; flex-wrap: wrap; margin-bottom: 15px; }
.form-row.single { margin-bottom: 0; }
.form-item { display: flex; align-items: center; margin-right: 20px; margin-bottom: 10px; }
.form-item.full { width: 100%; margin-right: 0; }
.form-item.full-width { flex: 1; min-width: 300px; }
.form-label { width: 100px; text-align: right; margin-right: 12px; color: #666; flex-shrink: 0; }
.form-label.required::before { content: '*'; color: #f00; margin-right: 4px; }
.form-input { width: 220px; height: 32px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; outline: none; transition: border-color 0.2s; }
.form-input:focus { border-color: #409eff; }
.form-input:disabled { background: #f5f5f5; cursor: not-allowed; }
.form-textarea { flex: 1; min-width: 300px; height: 80px; padding: 8px 12px; border: 1px solid #dcdfe6; border-radius: 4px; resize: vertical; outline: none; }
.form-textarea:focus { border-color: #409eff; }
.form-textarea:disabled { background: #f5f5f5; cursor: not-allowed; }
.char-count { position: absolute; right: 140px; bottom: 8px; color: #999; font-size: 12px; }

/* 下拉选择 */
.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper { position: relative; width: 220px; }
.multi-select-trigger, .tree-select-trigger, .select-trigger, .year-select-trigger { height: 32px; padding: 0 30px 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; position: relative; overflow: hidden; }
.multi-select-trigger.disabled, .tree-select-trigger.disabled, .select-trigger.disabled, .year-select-trigger.disabled { background: #f5f5f5; cursor: not-allowed; }
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.placeholder { color: #c0c4cc; }
.selected-tags { display: flex; flex-wrap: wrap; gap: 4px; overflow: hidden; }
.tag { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; white-space: nowrap; }
.multi-select-dropdown, .tree-select-dropdown, .select-dropdown, .year-select-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #fff; border: 1px solid #dcdfe6; border-radius: 4px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); z-index: 100; max-height: 250px; overflow-y: auto; margin-top: 4px; }
.select-option, .year-option { padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px; }
.select-option:hover, .year-option:hover { background: #f5f7fa; }
.checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; display: inline-block; position: relative; flex-shrink: 0; }
.checkbox.checked { background: #409eff; border-color: #409eff; }
.checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }

/* 树形选择 */
.tree-node-content { display: flex; align-items: center; padding: 6px 8px; cursor: pointer; }
.tree-node-content:hover { background: #f5f7fa; }
.tree-expand-icon { width: 16px; font-size: 10px; color: #c0c4cc; transition: transform 0.2s; flex-shrink: 0; }
.tree-expand-icon.expanded { transform: rotate(90deg); }
.tree-expand-placeholder { width: 16px; flex-shrink: 0; }
.tree-checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; margin-right: 8px; position: relative; flex-shrink: 0; }
.tree-checkbox.checked { background: #409eff; border-color: #409eff; }
.tree-checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.tree-node-label { font-size: 14px; }

/* 按钮 */
.btn { padding: 6px 16px; border-radius: 4px; border: 1px solid #dcdfe6; background: #fff; cursor: pointer; font-size: 14px; margin-right: 10px; transition: all 0.2s; }
.btn:hover { opacity: 0.9; }
.btn-primary { background: #409eff; border-color: #409eff; color: #fff; }
.btn-default { background: #fff; color: #333; }
.btn-danger { background: #f56c6c; border-color: #f56c6c; color: #fff; }
.btn-sm { padding: 4px 12px; font-size: 13px; }
.btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* 表格 */
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 10px 8px; text-align: center; border-bottom: 1px solid #e8e8e8; }
.data-table th { background: #fafafa; font-weight: 600; color: #333; }
.data-table tbody tr:hover { background: #f5f5f5; }
.col-checkbox { width: 40px; }
.col-index { width: 50px; }
.col-action { width: 80px; }
.col-name { text-align: left; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.col-type { width: 100px; }
.col-user { width: 80px; }
.col-time { width: 150px; }
.icon-btn { cursor: pointer; margin-right: 8px; font-size: 14px; }
.icon-btn.disabled { color: #ccc; cursor: not-allowed; }
.link { color: #409eff; cursor: pointer; }
.link:hover { text-decoration: underline; }

/* 分页 */
.pagination { display: flex; align-items: center; justify-content: flex-end; margin-top: 16px; gap: 10px; font-size: 13px; color: #606266; }
.pagination select { height: 28px; padding: 0 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.pagination button { padding: 4px 12px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination button:disabled { color: #c0c4cc; cursor: not-allowed; }

/* 操作行 */
.action-row { padding: 10px 0; border-bottom: 1px solid #e8e8e8; margin-bottom: 10px; }

/* 底部按钮 */
.footer-buttons { position: fixed; bottom: 0; left: 0; right: 0; padding: 15px 20px; background: #fff; border-top: 1px solid #e8e8e8; text-align: center; z-index: 100; box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05); }

/* 弹窗 */
.modal { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { width: 800px; max-height: 90vh; background: #fff; border-radius: 8px; overflow: hidden; display: flex; flex-direction: column; }
.modal-header { padding: 15px 20px; border-bottom: 1px solid #e8e8e8; display: flex; align-items: center; justify-content: space-between; }
.modal-header h3 { margin: 0; font-size: 16px; }
.close { font-size: 24px; cursor: pointer; color: #999; }
.close:hover { color: #333; }
.modal-body { padding: 20px; overflow-y: auto; max-height: calc(90vh - 120px); }
.modal-footer { padding: 15px 20px; border-top: 1px solid #e8e8e8; text-align: right; }

/* 规则配置 */
.rule-config { margin-top: 20px; border: 1px solid #e8e8e8; border-radius: 4px; }
.rule-config-header { padding: 10px 15px; background: #fafafa; border-bottom: 1px solid #e8e8e8; font-weight: 600; }
.rule-config-body { padding: 15px; }
.rule-config-actions { margin-top: 10px; }
.text-btn { color: #409eff; cursor: pointer; margin-right: 20px; }
.text-btn:hover { text-decoration: underline; }
.condition-row { display: flex; align-items: center; margin-bottom: 10px; flex-wrap: wrap; gap: 8px; }
.condition-row select, .condition-row input { height: 32px; padding: 0 10px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 13px; min-width: 100px; }
.condition-row input { width: 80px; min-width: auto; }
.remove-btn { color: #999; cursor: pointer; margin-left: 10px; }
.remove-btn:hover { color: #f56c6c; }
.condition-logic { margin-bottom: 10px; padding-left: 20px; }
.logic-btn { display: inline-block; padding: 4px 12px; background: #f0f0f0; border-radius: 4px; cursor: pointer; font-size: 12px; }
.logic-btn:hover { background: #e0e0e0; }
.condition-item { margin-bottom: 12px; }
.condition-group { border: 1px solid #e4e7ed; border-radius: 4px; padding: 12px; background: #fafafa; margin-bottom: 12px; }
.condition-group-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.logic-toggle { background: #409eff; color: #fff; padding: 2px 8px; border-radius: 3px; cursor: pointer; font-size: 12px; }
.logic-toggle:hover { opacity: 0.8; }
.condition-group-body { padding-left: 20px; }
.condition-group-footer { padding-top: 8px; border-top: 1px dashed #e4e7ed; margin-top: 8px; }
.empty-rule { color: #909399; text-align: center; padding: 20px; }
.empty-group { color: #909399; text-align: center; padding: 10px; font-size: 13px; }
</style>