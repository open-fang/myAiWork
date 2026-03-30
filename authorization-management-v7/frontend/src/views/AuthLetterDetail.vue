<template>
  <div class="auth-letter-detail">
    <h2 class="page-title">{{ isNew ? '新建授权书' : '授权书详情' }}</h2>

    <!-- 基本信息区域 -->
    <el-card class="info-card">
      <div slot="header">基本信息</div>
      <el-form ref="baseForm" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授权书名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入授权书名称"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权发布层级" prop="authPublishLevel">
              <!-- FlatMultiSelect 内联 -->
              <el-select
                v-model="formData.authPublishLevel"
                multiple
                collapse-tags
                placeholder="请选择"
                :disabled="!editable"
                clearable
              >
                <el-option
                  v-for="item in authPublishLevelOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授权发布组织" prop="authPublishOrg">
              <!-- TreeMultiSelect 内联 -->
              <el-popover
                v-model="treeSelectVisible.authPublishOrg"
                placement="bottom-start"
                trigger="click"
                width="400"
                :disabled="!editable"
              >
                <div class="tree-select-content">
                  <el-tree
                    ref="authPublishOrgTree"
                    :data="authPublishOrgOptions"
                    :props="{ children: 'children', label: 'name' }"
                    show-checkbox
                    check-strictly
                    node-key="code"
                    :default-checked-keys="formData.authPublishOrg"
                    @check="handleTreeCheck('authPublishOrg', $event)"
                  />
                </div>
                <el-input
                  slot="reference"
                  :value="getTreeDisplayText('authPublishOrg', authPublishOrgOptions, formData.authPublishOrg)"
                  placeholder="请选择"
                  :disabled="!editable"
                  readonly
                  clearable
                  @clear="handleTreeClear('authPublishOrg')"
                >
                  <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权对象层级" prop="authObjectLevel">
              <el-select
                v-model="formData.authObjectLevel"
                multiple
                collapse-tags
                placeholder="请选择"
                :disabled="!editable"
                clearable
              >
                <el-option
                  v-for="item in authObjectLevelOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适用区域" prop="applicableRegion">
              <el-popover
                v-model="treeSelectVisible.applicableRegion"
                placement="bottom-start"
                trigger="click"
                width="400"
                :disabled="!editable"
              >
                <div class="tree-select-content">
                  <el-tree
                    ref="applicableRegionTree"
                    :data="applicableRegionOptions"
                    :props="{ children: 'children', label: 'name' }"
                    show-checkbox
                    check-strictly
                    node-key="code"
                    :default-checked-keys="formData.applicableRegion"
                    @check="handleTreeCheck('applicableRegion', $event)"
                  />
                </div>
                <el-input
                  slot="reference"
                  :value="getTreeDisplayText('applicableRegion', applicableRegionOptions, formData.applicableRegion)"
                  placeholder="请选择"
                  :disabled="!editable"
                  readonly
                  clearable
                  @clear="handleTreeClear('applicableRegion')"
                >
                  <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权书发布年份" prop="publishYear">
              <el-date-picker
                v-model="formData.publishYear"
                type="year"
                placeholder="请选择"
                value-format="yyyy"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="授权书内容摘要" prop="summary">
              <el-input
                v-model="formData.summary"
                type="textarea"
                :rows="4"
                placeholder="请输入授权书内容摘要"
                maxlength="4000"
                show-word-limit
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 附件区域 - AttachmentUpload 内联 -->
    <el-card class="attachment-card">
      <div slot="header">附件</div>
      <template v-if="authLetterId">
        <!-- 功能按钮 -->
        <div class="attachment-buttons">
          <el-button
            type="primary"
            size="small"
            :disabled="!editable"
            @click="handleUpload"
          >
            上传
          </el-button>
          <el-button
            size="small"
            :disabled="attachmentSelectedRows.length === 0"
            @click="handleDownloadAttachments"
          >
            下载
          </el-button>
          <el-button
            size="small"
            :disabled="!editable || attachmentSelectedRows.length === 0"
            @click="handleDeleteAttachments"
          >
            删除
          </el-button>
        </div>
        <!-- 附件表格 -->
        <el-table
          ref="attachmentTable"
          :data="attachmentList"
          border
          style="width: 100%"
          @selection-change="handleAttachmentSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column label="序号" width="60">
            <template slot-scope="scope">
              {{ (attachmentPagination.currentPage - 1) * attachmentPagination.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="handleDownloadAttachmentRow(scope.row)">下载</el-button>
              <el-button type="text" size="small" :disabled="!editable" @click="handleDeleteAttachmentRow(scope.row)">删除</el-button>
            </template>
          </el-table-column>
          <el-table-column label="文档名称" prop="docName">
            <template slot-scope="scope">
              <el-link type="primary" @click="handleDownloadAttachmentRow(scope.row)">{{ scope.row.docName }}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="文档类型" prop="docTypeName" width="120" />
          <el-table-column label="创建人" prop="createdBy" width="100" />
          <el-table-column label="创建时间" prop="createdTime" width="160" />
        </el-table>
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="attachmentPagination.total"
            :page-size="attachmentPagination.pageSize"
            :current-page="attachmentPagination.currentPage"
            :page-sizes="[10, 30, 50]"
            @size-change="handleAttachmentPageSizeChange"
            @current-change="handleAttachmentPageChange"
          />
        </div>
        <!-- 上传弹窗 -->
        <el-dialog
          title="上传附件"
          :visible.sync="uploadDialogVisible"
          width="400px"
          append-to-body
        >
          <el-form ref="uploadForm" :model="uploadForm" :rules="uploadRules" label-width="100px">
            <el-form-item label="文档名称" prop="docName">
              <el-input v-model="uploadForm.docName" placeholder="请输入文档名称" />
            </el-form-item>
            <el-form-item label="文档类型" prop="docType">
              <el-select v-model="uploadForm.docType" placeholder="请选择文档类型">
                <el-option v-for="item in docTypeOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
            <el-form-item label="文档ID" prop="docId">
              <el-input v-model="uploadForm.docId" placeholder="请输入文档ID" />
            </el-form-item>
          </el-form>
          <span slot="footer">
            <el-button @click="uploadDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleUploadConfirm">确认</el-button>
          </span>
        </el-dialog>
      </template>
      <div v-else class="empty-tip">请先保存授权书后再上传附件</div>
    </el-card>

    <!-- 授权规则区域 -->
    <el-card class="rule-card">
      <div slot="header">授权规则</div>
      <div class="rule-buttons" v-if="editable">
        <el-button type="primary" size="small" @click="handleAddScene">添加场景</el-button>
        <el-button size="small" :disabled="selectedScenes.length === 0" @click="handleBatchDeleteScenes">删除</el-button>
      </div>
      <el-table
        v-if="authLetterId"
        ref="sceneTable"
        :data="sceneList"
        border
        style="width: 100%"
        @selection-change="handleSceneSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column label="序号" width="60">
          <template slot-scope="scope">
            {{ (scenePagination.currentPage - 1) * scenePagination.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" v-if="editable">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEditScene(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDeleteScene(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="场景" prop="sceneName" min-width="150" />
        <el-table-column label="产业" prop="industryName" min-width="150" />
        <el-table-column label="业务场景" prop="businessSceneName" min-width="120" />
        <el-table-column label="具体规则" prop="specificRule" min-width="200" />
        <el-table-column label="决策层级" prop="decisionLevelName" width="100" />
        <el-table-column label="创建人" prop="createdBy" width="100" />
        <el-table-column label="创建时间" prop="createdTime" width="160" />
      </el-table>
      <div v-if="authLetterId && sceneList.length > 0">
        <div class="pagination-container">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="scenePagination.total"
            :page-size="scenePagination.pageSize"
            :current-page="scenePagination.currentPage"
            :page-sizes="[10, 30, 50]"
            @size-change="handleScenePageSizeChange"
            @current-change="handleScenePageChange"
          />
        </div>
      </div>
      <div v-if="!authLetterId" class="empty-tip">请先保存授权书后再配置规则</div>
    </el-card>

    <!-- 日志区域（已发布/已失效状态显示） -->
    <el-card v-if="showLogPanel" class="log-card">
      <div slot="header" class="log-header">
        <span>日志</span>
        <el-switch v-model="logExpanded" active-text="展开" inactive-text="折叠" />
      </div>
      <div v-show="logExpanded">
        <el-table :data="logList" border style="width: 100%">
          <el-table-column label="序号" width="60">
            <template slot-scope="scope">
              {{ (logPagination.currentPage - 1) * logPagination.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" prop="operationName" width="100" />
          <el-table-column label="操作人" prop="operatorName" width="100" />
          <el-table-column label="操作时间" prop="operationTime" width="160" />
        </el-table>
        <div class="pagination-container">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="logPagination.total"
            :page-size="logPagination.pageSize"
            :current-page="logPagination.currentPage"
            :page-sizes="[10, 30, 50]"
            @size-change="handleLogPageSizeChange"
            @current-change="handleLogPageChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 底部按钮 -->
    <div class="bottom-buttons">
      <template v-if="editable">
        <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
        <el-button type="success" :loading="publishLoading" @click="handleSaveAndPublish">保存并发布</el-button>
        <el-button @click="handlePublish">发布</el-button>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
      <template v-else>
        <el-button @click="handleBack">返回</el-button>
        <el-button v-if="formData.status === 'PUBLISHED'" type="warning" @click="handleInvalidate">失效</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </div>

    <!-- 场景配置弹窗 - SceneConfigDialog 内联 -->
    <el-dialog
      title="场景配置"
      :visible.sync="sceneDialogVisible"
      width="900px"
      append-to-body
      @close="handleSceneDialogClose"
    >
      <el-form ref="sceneForm" :model="sceneFormData" :rules="sceneRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="场景名称" prop="sceneName">
              <el-input v-model="sceneFormData.sceneName" placeholder="请输入场景名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产业" prop="industry">
              <el-popover
                v-model="sceneTreeSelectVisible.industry"
                placement="bottom-start"
                trigger="click"
                width="400"
              >
                <div class="tree-select-content">
                  <el-tree
                    ref="industryTree"
                    :data="industryOptions"
                    :props="{ children: 'children', label: 'name' }"
                    show-checkbox
                    check-strictly
                    node-key="code"
                    :default-checked-keys="sceneFormData.industry"
                    @check="handleSceneTreeCheck('industry', $event)"
                  />
                </div>
                <el-input
                  slot="reference"
                  :value="getTreeDisplayText('industry', industryOptions, sceneFormData.industry)"
                  placeholder="请选择产业"
                  readonly
                  clearable
                  @clear="handleSceneTreeClear('industry')"
                >
                  <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="业务场景" prop="businessScene">
              <el-select v-model="sceneFormData.businessScene" placeholder="请选择业务场景">
                <el-option v-for="item in businessSceneOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="决策层级" prop="decisionLevel">
              <el-select v-model="sceneFormData.decisionLevel" placeholder="请选择决策层级">
                <el-option v-for="item in decisionLevelOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="具体规则" prop="specificRule">
              <el-input
                v-model="sceneFormData.specificRule"
                type="textarea"
                :rows="3"
                placeholder="请输入具体规则描述"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 规则配置区域 - RuleCondition 内联 -->
        <el-form-item label="规则配置">
          <div class="rule-section">
            <div class="conditions-container">
              <template v-for="(condition, index) in sceneFormData.rules">
                <span
                  v-if="index > 0"
                  :key="'logic-' + index"
                  class="logic-link"
                  @click="toggleLogic(condition)"
                >
                  [{{ condition.logicType === 'AND' ? '且' : '或' }}]
                </span>
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
                  <div class="condition-children">
                    <template v-for="(child, childIndex) in condition.children">
                      <span v-if="childIndex > 0" :key="'child-logic-' + childIndex" class="logic-link">
                        [{{ child.logicType === 'AND' ? '且' : '或' }}]
                      </span>
                      <div :key="'child-' + childIndex" class="condition-row">
                        <el-select v-model="child.fieldCode" placeholder="规则字段" size="small">
                          <el-option v-for="item in ruleParamOptions" :key="item.code" :label="item.name" :value="item.code" />
                        </el-select>
                        <el-select v-model="child.operator" placeholder="运算符" size="small">
                          <el-option v-for="item in operatorOptions" :key="item.code" :label="item.name" :value="item.code" />
                        </el-select>
                        <el-select v-model="child.compareType" placeholder="对比类型" size="small" @change="handleCompareTypeChange(child)">
                          <el-option v-for="item in compareTypeOptions" :key="item.code" :label="item.name" :value="item.code" />
                        </el-select>
                        <el-select v-if="child.compareType === 'FIELD'" v-model="child.compareFieldCode" placeholder="对比字段" size="small">
                          <el-option v-for="item in ruleParamOptions" :key="item.code" :label="item.name" :value="item.code" />
                        </el-select>
                        <el-input v-if="child.compareType === 'NUMBER'" v-model="child.compareValue" placeholder="数值" size="small" style="width: 120px" />
                        <el-select v-if="child.compareType === 'NUMBER'" v-model="child.compareUnit" placeholder="单位" size="small">
                          <el-option v-for="item in compareUnitOptions" :key="item.code" :label="item.name" :value="item.code" />
                        </el-select>
                        <el-input v-if="child.compareType === 'TEXT'" v-model="child.compareValue" placeholder="文本" size="small" style="width: 200px" />
                        <el-input v-if="child.compareType === 'RATIO'" v-model="child.compareValue" placeholder="比例" size="small" style="width: 120px" />
                        <el-button type="text" size="small" class="delete-btn" @click="removeChildCondition(condition, childIndex)">
                          <i class="el-icon-delete" />
                        </el-button>
                      </div>
                    </template>
                  </div>
                  <div class="group-actions">
                    <span class="action-link" @click="addChildCondition(condition)">[+新增条件]</span>
                    <span class="action-link delete" @click="removeConditionGroup(index)">[移除条件组]</span>
                  </div>
                </div>
                <div v-else :key="'condition-' + index" class="condition-row">
                  <el-select v-model="condition.fieldCode" placeholder="规则字段" size="small">
                    <el-option v-for="item in ruleParamOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                  <el-select v-model="condition.operator" placeholder="运算符" size="small">
                    <el-option v-for="item in operatorOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                  <el-select v-model="condition.compareType" placeholder="对比类型" size="small" @change="handleCompareTypeChange(condition)">
                    <el-option v-for="item in compareTypeOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                  <el-select v-if="condition.compareType === 'FIELD'" v-model="condition.compareFieldCode" placeholder="对比字段" size="small">
                    <el-option v-for="item in ruleParamOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                  <el-input v-if="condition.compareType === 'NUMBER'" v-model="condition.compareValue" placeholder="数值" size="small" style="width: 120px" />
                  <el-select v-if="condition.compareType === 'NUMBER'" v-model="condition.compareUnit" placeholder="单位" size="small">
                    <el-option v-for="item in compareUnitOptions" :key="item.code" :label="item.name" :value="item.code" />
                  </el-select>
                  <el-input v-if="condition.compareType === 'TEXT'" v-model="condition.compareValue" placeholder="文本" size="small" style="width: 200px" />
                  <el-input v-if="condition.compareType === 'RATIO'" v-model="condition.compareValue" placeholder="比例" size="small" style="width: 120px" />
                  <el-button type="text" size="small" class="delete-btn" @click="removeCondition(index)">
                    <i class="el-icon-delete" />
                  </el-button>
                </div>
              </template>
            </div>
            <div class="actions-container">
              <span class="action-link" @click="addCondition">[+新增条件]</span>
              <span class="action-link" @click="addConditionGroup">[+新增子条件组]</span>
            </div>
          </div>
        </el-form-item>
        <!-- 问卷配置区域 -->
        <el-form-item label="问卷配置">
          <div class="questionnaire-section">
            <div class="questionnaire-list">
              <div v-for="(item, index) in sceneFormData.questionnaires" :key="index" class="questionnaire-item">
                <span class="logic-link" v-if="index > 0">[且]</span>
                <el-select v-model="item.questionId" placeholder="选择题目" @change="handleQuestionChange(item)">
                  <el-option v-for="q in questionOptions" :key="q.questionId" :label="q.questionText" :value="q.questionId" />
                </el-select>
                <el-select v-model="item.correctAnswerId" placeholder="选择答案">
                  <el-option v-for="a in getAnswersForQuestion(item.questionId)" :key="a.answerId" :label="a.answerText" :value="a.answerId" />
                </el-select>
                <el-button type="text" class="delete-btn" @click="removeQuestionnaire(index)">
                  <i class="el-icon-delete" />
                </el-button>
              </div>
              <span class="action-link" @click="addQuestionnaire">[+问卷题目]</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="handleSceneDialogClose">取消</el-button>
        <el-button type="primary" :loading="sceneLoading" @click="handleSceneConfirm">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// ============================================
// API 函数 - 内联
// ============================================
import axios from 'axios'
import { Message } from 'element-ui'

const baseURL = '/api/v1'

const request = axios.create({
  baseURL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' }
})

request.interceptors.request.use(config => config, error => Promise.reject(error))

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    Message.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// API 函数定义
function getAuthLetterDetail(id) {
  return request({ url: `${baseURL}/auth-letters/${id}`, method: 'get' })
}

function createAuthLetter(data) {
  return request({ url: `${baseURL}/auth-letters`, method: 'post', data })
}

function updateAuthLetter(id, data) {
  return request({ url: `${baseURL}/auth-letters/${id}`, method: 'put', data })
}

function publishAuthLetter(id) {
  return request({ url: `${baseURL}/auth-letters/${id}/publish`, method: 'post' })
}

function invalidateAuthLetter(id) {
  return request({ url: `${baseURL}/auth-letters/${id}/invalidate`, method: 'post' })
}

function deleteAuthLetter(id) {
  return request({ url: `${baseURL}/auth-letters/${id}`, method: 'delete' })
}

function getAuthLetterLogs(authLetterId, params) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/logs`, method: 'get', params })
}

function getSceneList(authLetterId, params) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes`, method: 'get', params })
}

function getSceneDetail(authLetterId, id) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`, method: 'get' })
}

function createScene(authLetterId, data) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes`, method: 'post', data })
}

function updateScene(authLetterId, id, data) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`, method: 'put', data })
}

function deleteScene(authLetterId, id) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`, method: 'delete' })
}

function batchDeleteScenes(authLetterId, ids) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/scenes/batch-delete`, method: 'post', data: { ids } })
}

function getLookupValues(typeCode) {
  return request({ url: `${baseURL}/lookup-values/${typeCode}`, method: 'get' })
}

function getRuleParamOptions(status = 'ACTIVE') {
  return request({ url: `${baseURL}/rule-params/options`, method: 'get', params: { status } })
}

function getQuestionOptions(language = 'ZH') {
  return request({ url: `${baseURL}/questionnaire-questions/options`, method: 'get', params: { language } })
}

function getAttachmentList(authLetterId, params) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/attachments`, method: 'get', params })
}

function uploadAttachment(authLetterId, data) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/attachments`, method: 'post', data })
}

function downloadAttachment(authLetterId, id) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/attachments/${id}/download`, method: 'get', responseType: 'blob' })
}

function deleteAttachment(authLetterId, id) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/attachments/${id}`, method: 'delete' })
}

function batchDeleteAttachments(authLetterId, ids) {
  return request({ url: `${baseURL}/auth-letters/${authLetterId}/attachments/batch-delete`, method: 'post', data: { ids } })
}

const LOOKUP_TYPES = {
  AUTH_OBJECT_LEVEL: 'AUTH_OBJECT_LEVEL',
  AUTH_PUBLISH_LEVEL: 'AUTH_PUBLISH_LEVEL',
  APPLICABLE_REGION: 'APPLICABLE_REGION',
  AUTH_PUBLISH_ORG: 'AUTH_PUBLISH_ORG',
  DOC_TYPE: 'DOC_TYPE',
  INDUSTRY: 'INDUSTRY',
  BUSINESS_SCENE: 'BUSINESS_SCENE',
  DECISION_LEVEL: 'DECISION_LEVEL',
  OPERATOR: 'OPERATOR',
  COMPARE_TYPE: 'COMPARE_TYPE',
  COMPARE_UNIT: 'COMPARE_UNIT'
}

// ============================================
// 组件逻辑
// ============================================
export default {
  name: 'AuthLetterDetail',
  data() {
    return {
      isNew: true,
      authLetterId: null,
      formData: {
        name: '',
        authPublishLevel: [],
        authPublishOrg: [],
        authObjectLevel: [],
        applicableRegion: [],
        publishYear: '',
        summary: '',
        status: 'DRAFT'
      },
      rules: {
        name: [{ required: true, message: '请输入授权书名称', trigger: 'blur' }],
        authPublishLevel: [{ required: true, message: '请选择授权发布层级', trigger: 'change' }],
        authPublishOrg: [{ required: true, message: '请选择授权发布组织', trigger: 'change' }],
        authObjectLevel: [{ required: true, message: '请选择授权对象层级', trigger: 'change' }],
        applicableRegion: [{ required: true, message: '请选择适用区域', trigger: 'change' }],
        publishYear: [{ required: true, message: '请选择授权书发布年份', trigger: 'change' }],
        summary: [{ required: true, message: '请输入授权书内容摘要', trigger: 'blur' }]
      },
      authObjectLevelOptions: [],
      applicableRegionOptions: [],
      authPublishLevelOptions: [],
      authPublishOrgOptions: [],
      docTypeOptions: [],
      sceneList: [],
      selectedScenes: [],
      scenePagination: { total: 0, pageSize: 10, currentPage: 1 },
      logList: [],
      logExpanded: false,
      logPagination: { total: 0, pageSize: 10, currentPage: 1 },
      saveLoading: false,
      publishLoading: false,
      // TreeMultiSelect 状态
      treeSelectVisible: { applicableRegion: false, authPublishOrg: false },
      // 附件数据
      attachmentList: [],
      attachmentSelectedRows: [],
      attachmentPagination: { total: 0, pageSize: 10, currentPage: 1 },
      uploadDialogVisible: false,
      uploadForm: { docId: '', docName: '', docType: '' },
      uploadRules: {
        docId: [{ required: true, message: '请输入文档ID', trigger: 'blur' }],
        docName: [{ required: true, message: '请输入文档名称', trigger: 'blur' }],
        docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }]
      },
      // 场景配置弹窗数据
      sceneDialogVisible: false,
      sceneLoading: false,
      sceneIsEdit: false,
      sceneCurrentId: null,
      sceneFormData: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        rules: [],
        questionnaires: []
      },
      sceneRules: {
        sceneName: [{ required: true, message: '请输入场景名称', trigger: 'blur' }],
        industry: [{ required: true, message: '请选择产业', trigger: 'change' }],
        businessScene: [{ required: true, message: '请选择业务场景', trigger: 'change' }],
        decisionLevel: [{ required: true, message: '请选择决策层级', trigger: 'change' }]
      },
      sceneTreeSelectVisible: { industry: false },
      industryOptions: [],
      businessSceneOptions: [],
      decisionLevelOptions: [],
      ruleParamOptions: [],
      operatorOptions: [],
      compareTypeOptions: [],
      compareUnitOptions: [],
      questionOptions: []
    }
  },
  computed: {
    editable() {
      return this.isNew || this.formData.status === 'DRAFT'
    },
    showLogPanel() {
      return this.formData.status === 'PUBLISHED' || this.formData.status === 'INVALID'
    }
  },
  async mounted() {
    await this.loadLookupData()
    const id = this.$route.query.id
    if (id) {
      this.isNew = false
      this.authLetterId = parseInt(id)
      await this.loadDetail()
      await this.loadSceneList()
      await this.loadAttachmentList()
      if (this.showLogPanel) {
        await this.loadLogList()
      }
    }
  },
  methods: {
    async loadLookupData() {
      try {
        const [authObjectLevel, applicableRegion, authPublishLevel, authPublishOrg, docType, industry, businessScene, decisionLevel, operator, compareType, compareUnit] = await Promise.all([
          getLookupValues(LOOKUP_TYPES.AUTH_OBJECT_LEVEL),
          getLookupValues(LOOKUP_TYPES.APPLICABLE_REGION),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_LEVEL),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_ORG),
          getLookupValues(LOOKUP_TYPES.DOC_TYPE),
          getLookupValues(LOOKUP_TYPES.INDUSTRY),
          getLookupValues(LOOKUP_TYPES.BUSINESS_SCENE),
          getLookupValues(LOOKUP_TYPES.DECISION_LEVEL),
          getLookupValues(LOOKUP_TYPES.OPERATOR),
          getLookupValues(LOOKUP_TYPES.COMPARE_TYPE),
          getLookupValues(LOOKUP_TYPES.COMPARE_UNIT)
        ])
        this.authObjectLevelOptions = authObjectLevel.data || []
        this.applicableRegionOptions = applicableRegion.data || []
        this.authPublishLevelOptions = authPublishLevel.data || []
        this.authPublishOrgOptions = authPublishOrg.data || []
        this.docTypeOptions = docType.data || []
        this.industryOptions = industry.data || []
        this.businessSceneOptions = businessScene.data || []
        this.decisionLevelOptions = decisionLevel.data || []
        this.operatorOptions = operator.data || []
        this.compareTypeOptions = compareType.data || []
        this.compareUnitOptions = compareUnit.data || []
        // 加载规则参数和问卷选项
        const ruleParamRes = await getRuleParamOptions()
        this.ruleParamOptions = ruleParamRes.data || []
        const questionRes = await getQuestionOptions()
        this.questionOptions = questionRes.data || []
      } catch (error) {
        console.error('加载下拉数据失败:', error)
      }
    },
    async loadDetail() {
      try {
        const res = await getAuthLetterDetail(this.authLetterId)
        const data = res.data
        this.formData = {
          name: data.name || '',
          authPublishLevel: data.authPublishLevel || [],
          authPublishOrg: data.authPublishOrg || [],
          authObjectLevel: data.authObjectLevel || [],
          applicableRegion: data.applicableRegion || [],
          publishYear: data.publishYear ? String(data.publishYear) : '',
          summary: data.summary || '',
          status: data.status || 'DRAFT'
        }
        // 数据加载后同步 tree 组件的选中状态
        this.$nextTick(() => {
          if (this.$refs.authPublishOrgTree) {
            this.$refs.authPublishOrgTree.setCheckedKeys(this.formData.authPublishOrg || [])
          }
          if (this.$refs.applicableRegionTree) {
            this.$refs.applicableRegionTree.setCheckedKeys(this.formData.applicableRegion || [])
          }
        })
      } catch (error) {
        this.$message.error('加载详情失败')
        this.$router.push('/AuthLetterList')
      }
    },
    async loadSceneList() {
      if (!this.authLetterId) return
      try {
        const res = await getSceneList(this.authLetterId, {
          pageNum: this.scenePagination.currentPage,
          pageSize: this.scenePagination.pageSize
        })
        this.sceneList = res.data.list || []
        this.scenePagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载场景列表失败:', error)
      }
    },
    async loadAttachmentList() {
      if (!this.authLetterId) return
      try {
        const res = await getAttachmentList(this.authLetterId, {
          pageNum: this.attachmentPagination.currentPage,
          pageSize: this.attachmentPagination.pageSize
        })
        this.attachmentList = res.data.list || []
        this.attachmentPagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载附件列表失败:', error)
      }
    },
    async loadLogList() {
      if (!this.authLetterId) return
      try {
        const res = await getAuthLetterLogs(this.authLetterId, {
          pageNum: this.logPagination.currentPage,
          pageSize: this.logPagination.pageSize
        })
        this.logList = res.data.list || []
        this.logPagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载日志列表失败:', error)
      }
    },
    // TreeMultiSelect 方法
    getTreeCheckedNames(data, codes) {
      const names = []
      const traverse = (nodes) => {
        for (const node of nodes) {
          if (codes.includes(node.code)) names.push(node.name)
          if (node.children && node.children.length > 0) traverse(node.children)
        }
      }
      traverse(data)
      return names
    },
    getTreeDisplayText(field, options, keys) {
      if (!keys || keys.length === 0) return ''
      const names = this.getTreeCheckedNames(options, keys)
      if (names.length > 3) return names.slice(0, 3).join(', ') + '...'
      return names.join(', ')
    },
    handleTreeCheck(field, { checkedKeys }) {
      this.formData[field] = checkedKeys
    },
    handleTreeClear(field) {
      this.formData[field] = []
      const treeRef = field === 'applicableRegion' ? 'applicableRegionTree' : 'authPublishOrgTree'
      this.$refs[treeRef].setCheckedKeys([])
    },
    handleSceneTreeCheck(field, { checkedKeys }) {
      this.sceneFormData[field] = checkedKeys
    },
    handleSceneTreeClear(field) {
      this.sceneFormData[field] = []
      this.$refs.industryTree.setCheckedKeys([])
    },
    // 附件方法
    handleAttachmentSelectionChange(rows) {
      this.attachmentSelectedRows = rows
    },
    handleAttachmentPageSizeChange(val) {
      this.attachmentPagination.pageSize = val
      this.attachmentPagination.currentPage = 1
      this.loadAttachmentList()
    },
    handleAttachmentPageChange(val) {
      this.attachmentPagination.currentPage = val
      this.loadAttachmentList()
    },
    handleUpload() {
      this.uploadForm = { docId: '', docName: '', docType: '' }
      this.uploadDialogVisible = true
    },
    async handleUploadConfirm() {
      try {
        await this.$refs.uploadForm.validate()
        await uploadAttachment(this.authLetterId, this.uploadForm)
        this.uploadDialogVisible = false
        this.loadAttachmentList()
        this.$message.success('上传成功')
      } catch (error) {
        if (error.message) this.$message.error(error.message)
      }
    },
    async handleDownloadAttachments() {
      for (const row of this.attachmentSelectedRows) {
        await this.handleDownloadAttachmentRow(row)
      }
    },
    async handleDownloadAttachmentRow(row) {
      try {
        const res = await downloadAttachment(this.authLetterId, row.id)
        const blob = new Blob([res])
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = row.docName
        link.click()
        URL.revokeObjectURL(link.href)
      } catch (error) {
        this.$message.error('下载失败')
      }
    },
    async handleDeleteAttachments() {
      try {
        await this.$confirm('确定要删除选中的附件吗？', '提示', { type: 'warning' })
        const ids = this.attachmentSelectedRows.map(row => row.id)
        await batchDeleteAttachments(this.authLetterId, ids)
        this.loadAttachmentList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    async handleDeleteAttachmentRow(row) {
      try {
        await this.$confirm('确定要删除此附件吗？', '提示', { type: 'warning' })
        await deleteAttachment(this.authLetterId, row.id)
        this.loadAttachmentList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    // 场景方法
    handleSceneSelectionChange(rows) {
      this.selectedScenes = rows
    },
    handleScenePageSizeChange(val) {
      this.scenePagination.pageSize = val
      this.scenePagination.currentPage = 1
      this.loadSceneList()
    },
    handleScenePageChange(val) {
      this.scenePagination.currentPage = val
      this.loadSceneList()
    },
    handleLogPageSizeChange(val) {
      this.logPagination.pageSize = val
      this.logPagination.currentPage = 1
      this.loadLogList()
    },
    handleLogPageChange(val) {
      this.logPagination.currentPage = val
      this.loadLogList()
    },
    handleAddScene() {
      this.sceneIsEdit = false
      this.sceneCurrentId = null
      this.resetSceneForm()
      this.sceneDialogVisible = true
    },
    async handleEditScene(row) {
      this.sceneIsEdit = true
      this.sceneCurrentId = row.id
      this.sceneDialogVisible = true
      await this.loadSceneDetail(row.id)
    },
    async loadSceneDetail(id) {
      try {
        const res = await getSceneDetail(this.authLetterId, id)
        const data = res.data
        this.sceneFormData = {
          sceneName: data.sceneName || '',
          industry: data.industry || [],
          businessScene: data.businessScene || '',
          decisionLevel: data.decisionLevel || '',
          specificRule: data.specificRule || '',
          rules: this.formatRules(data.rules || []),
          questionnaires: this.formatQuestionnaires(data.questionnaires || [])
        }
        // 数据加载后同步 tree 组件的选中状态
        this.$nextTick(() => {
          if (this.$refs.industryTree) {
            this.$refs.industryTree.setCheckedKeys(this.sceneFormData.industry || [])
          }
        })
      } catch (error) {
        this.$message.error('加载详情失败')
        this.sceneDialogVisible = false
      }
    },
    formatRules(rules) {
      if (!rules || rules.length === 0) return []
      return rules.flatMap(rule => rule.conditions || [])
    },
    formatQuestionnaires(questionnaires) {
      return questionnaires.map(q => ({
        questionId: q.questionId,
        correctAnswerId: q.correctAnswerId,
        sortOrder: q.sortOrder
      }))
    },
    resetSceneForm() {
      this.sceneFormData = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        rules: [],
        questionnaires: []
      }
    },
    handleSceneDialogClose() {
      this.sceneDialogVisible = false
      this.resetSceneForm()
      if (this.$refs.sceneForm) this.$refs.sceneForm.resetFields()
    },
    async handleDeleteScene(row) {
      try {
        await this.$confirm('确定要删除此场景吗？', '提示', { type: 'warning' })
        await deleteScene(this.authLetterId, row.id)
        this.loadSceneList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    async handleBatchDeleteScenes() {
      try {
        await this.$confirm('确定要删除选中的场景吗？', '提示', { type: 'warning' })
        const ids = this.selectedScenes.map(row => row.id)
        await batchDeleteScenes(this.authLetterId, ids)
        this.loadSceneList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    // RuleCondition 方法
    addCondition() {
      this.sceneFormData.rules.push({
        fieldCode: '',
        operator: '',
        compareType: '',
        compareFieldCode: '',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false,
        sortOrder: this.sceneFormData.rules.length + 1
      })
    },
    addConditionGroup() {
      this.sceneFormData.rules.push({
        logicType: 'AND',
        isGroup: true,
        groupLogicType: 'AND',
        sortOrder: this.sceneFormData.rules.length + 1,
        children: []
      })
    },
    addChildCondition(group) {
      group.children.push({
        fieldCode: '',
        operator: '',
        compareType: '',
        compareFieldCode: '',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false,
        sortOrder: group.children.length + 1
      })
    },
    removeCondition(index) {
      this.sceneFormData.rules.splice(index, 1)
    },
    removeConditionGroup(index) {
      this.sceneFormData.rules.splice(index, 1)
    },
    removeChildCondition(group, childIndex) {
      group.children.splice(childIndex, 1)
    },
    toggleLogic(condition) {
      condition.logicType = condition.logicType === 'AND' ? 'OR' : 'AND'
    },
    toggleGroupLogic(condition) {
      condition.groupLogicType = condition.groupLogicType === 'AND' ? 'OR' : 'AND'
    },
    handleCompareTypeChange(condition) {
      condition.compareFieldCode = ''
      condition.compareValue = ''
      condition.compareUnit = ''
    },
    // 问卷方法
    getAnswersForQuestion(questionId) {
      const question = this.questionOptions.find(q => q.questionId === questionId)
      return question ? question.answers : []
    },
    handleQuestionChange(item) {
      item.correctAnswerId = null
    },
    addQuestionnaire() {
      this.sceneFormData.questionnaires.push({
        questionId: null,
        correctAnswerId: null,
        sortOrder: this.sceneFormData.questionnaires.length + 1
      })
    },
    removeQuestionnaire(index) {
      this.sceneFormData.questionnaires.splice(index, 1)
    },
    async handleSceneConfirm() {
      try {
        await this.$refs.sceneForm.validate()
        const hasRules = this.sceneFormData.rules.length > 0
        const hasQuestionnaires = this.sceneFormData.questionnaires.some(q => q.questionId && q.correctAnswerId)
        if (!hasRules && !hasQuestionnaires) {
          this.$message.warning('规则和问卷至少需要配置一项')
          return
        }
        this.sceneLoading = true
        const data = {
          sceneName: this.sceneFormData.sceneName,
          industry: this.sceneFormData.industry,
          businessScene: this.sceneFormData.businessScene,
          decisionLevel: this.sceneFormData.decisionLevel,
          specificRule: this.sceneFormData.specificRule,
          rules: [{
            ruleName: '规则组1',
            logicType: 'AND',
            conditions: this.sceneFormData.rules.map(c => ({ ...c, isGroup: c.isGroup || false }))
          }],
          questionnaires: this.sceneFormData.questionnaires.filter(q => q.questionId && q.correctAnswerId)
        }
        if (this.sceneIsEdit) {
          await updateScene(this.authLetterId, this.sceneCurrentId, data)
          this.$message.success('更新成功')
        } else {
          await createScene(this.authLetterId, data)
          this.$message.success('创建成功')
        }
        this.sceneDialogVisible = false
        this.loadSceneList()
      } catch (error) {
        if (error.message) this.$message.error(error.message)
      } finally {
        this.sceneLoading = false
      }
    },
    // 主要操作方法
    async handleSave() {
      try {
        await this.$refs.baseForm.validate()
        this.saveLoading = true
        const data = {
          name: this.formData.name,
          authPublishLevel: this.formData.authPublishLevel,
          authPublishOrg: this.formData.authPublishOrg,
          authObjectLevel: this.formData.authObjectLevel,
          applicableRegion: this.formData.applicableRegion,
          publishYear: this.formData.publishYear ? parseInt(this.formData.publishYear) : null,
          summary: this.formData.summary
        }
        if (this.isNew) {
          const res = await createAuthLetter(data)
          this.authLetterId = res.data.id
          this.isNew = false
          this.$message.success('创建成功')
        } else {
          await updateAuthLetter(this.authLetterId, data)
          this.$message.success('保存成功')
        }
      } catch (error) {
        if (error.message) this.$message.error(error.message)
      } finally {
        this.saveLoading = false
      }
    },
    async handleSaveAndPublish() {
      try {
        await this.handleSave()
        await this.handlePublish()
      } catch (error) {
        console.error('保存并发布失败:', error)
      }
    },
    async handlePublish() {
      try {
        await this.$confirm('确定要发布此授权书吗？', '提示', { type: 'warning' })
        this.publishLoading = true
        await publishAuthLetter(this.authLetterId)
        this.formData.status = 'PUBLISHED'
        this.$message.success('发布成功')
        await this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '发布失败')
      } finally {
        this.publishLoading = false
      }
    },
    async handleCancel() {
      try {
        await this.$confirm('确定要取消吗？未保存的数据将丢失', '提示', { type: 'warning' })
        this.$router.push('/AuthLetterList')
      } catch (error) {}
    },
    async handleDelete() {
      try {
        await this.$confirm('确定要删除此授权书吗？', '提示', { type: 'warning' })
        await deleteAuthLetter(this.authLetterId)
        this.$message.success('删除成功')
        this.$router.push('/AuthLetterList')
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '删除失败')
      }
    },
    async handleInvalidate() {
      try {
        await this.$confirm('确定要失效此授权书吗？', '提示', { type: 'warning' })
        await invalidateAuthLetter(this.authLetterId)
        this.formData.status = 'INVALID'
        this.$message.success('失效成功')
        await this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') this.$message.error(error.message || '失效失败')
      }
    },
    handleBack() {
      this.$router.push('/AuthLetterList')
    }
  }
}
</script>

<style scoped>
.auth-letter-detail { padding: 20px; }
.page-title { margin-bottom: 20px; font-size: 20px; font-weight: 500; }
.info-card, .attachment-card, .rule-card, .log-card { margin-bottom: 20px; }
.log-header { display: flex; justify-content: space-between; align-items: center; }
.rule-buttons { margin-bottom: 10px; }
.empty-tip { color: #909399; text-align: center; padding: 20px; }
.bottom-buttons { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 15px 20px; text-align: center; border-top: 1px solid #e8e8e8; z-index: 100; }
.pagination-container { padding: 15px 0; display: flex; justify-content: flex-end; }
.tree-select-content { max-height: 300px; overflow-y: auto; }
.attachment-buttons { margin-bottom: 10px; }
.attachment-upload { margin-top: 10px; }
.rule-section { background-color: #f9f9f9; padding: 10px; border-radius: 4px; }
.conditions-container { display: flex; flex-direction: column; gap: 8px; }
.condition-row { display: flex; align-items: center; gap: 8px; padding: 8px; background-color: #fff; border-radius: 4px; }
.condition-group { padding: 10px; border: 1px solid #ddd; border-radius: 4px; background-color: #fff; margin-left: 20px; }
.condition-children { display: flex; flex-direction: column; gap: 8px; }
.group-header { margin-bottom: 8px; }
.logic-link, .group-logic, .action-link { color: #409EFF; cursor: pointer; font-size: 12px; padding: 4px 8px; }
.logic-link:hover, .group-logic:hover, .action-link:hover { text-decoration: underline; }
.action-link.delete { color: #F56C6C; }
.delete-btn { color: #909399; }
.delete-btn:hover { color: #F56C6C; }
.actions-container { margin-top: 10px; }
.questionnaire-section { background-color: #f9f9f9; padding: 10px; border-radius: 4px; }
.questionnaire-list { margin-top: 10px; }
.questionnaire-item { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
</style>