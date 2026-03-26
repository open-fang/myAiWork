<template>
  <div class="page-container">
    <h1 class="page-title">{{ isEdit ? '编辑授权书' : isNew ? '新建授权书' : '授权书详情' }}</h1>

    <!-- 基本信息 -->
    <div class="section">
      <h3 class="section-title">基本信息</h3>
      <div class="form-row">
        <div class="form-item">
          <label class="required">授权书名称</label>
          <input type="text" v-model="formData.name" :disabled="!isEditable" placeholder="请输入授权书名称" />
        </div>
        <div class="form-item">
          <label class="required">授权发布层级</label>
          <custom-select
            v-model="formData.authPublishLevel"
            :options="lookupData.authPublishLevel"
            :disabled="!isEditable"
            placeholder="请选择授权发布层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">授权发布组织</label>
          <tree-select
            v-model="formData.authPublishOrg"
            :options="lookupData.authPublishOrg"
            :disabled="!isEditable"
            placeholder="请选择授权发布组织"
            multiple
          />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <label class="required">授权对象层级</label>
          <custom-select
            v-model="formData.authObjectLevel"
            :options="lookupData.authObjectLevel"
            :disabled="!isEditable"
            placeholder="请选择授权对象层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">适用区域</label>
          <tree-select
            v-model="formData.applicableRegion"
            :options="lookupData.applicableRegion"
            :disabled="!isEditable"
            placeholder="请选择适用区域"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">发布年份</label>
          <input type="number" v-model="formData.publishYear" :disabled="!isEditable" placeholder="请选择年份" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item" style="flex: 1; min-width: 100%;">
          <label class="required">内容摘要</label>
          <textarea v-model="formData.summary" :disabled="!isEditable" placeholder="请输入授权书内容摘要" maxlength="4000"></textarea>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">附件</h3>
        <div class="section-actions" v-if="isEditable">
          <button class="btn btn-primary" @click="handleUploadAttachment">上传</button>
          <button class="btn" @click="handleDownloadAttachment" :disabled="selectedAttachments.length === 0">下载</button>
          <button class="btn btn-danger" @click="handleDeleteAttachment" :disabled="selectedAttachments.length === 0">删除</button>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" v-model="isAllAttachmentSelected" @change="handleAttachmentSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>文档名称</th>
            <th>文档类型</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in attachmentList" :key="item.id">
            <td><input type="checkbox" v-model="selectedAttachments" :value="item.id" /></td>
            <td>{{ (attachmentPageNum - 1) * attachmentPageSize + index + 1 }}</td>
            <td>
              <span class="link" @click="handleDownloadFile(item)">下载</span>
            </td>
            <td><span class="link" @click="handleDownloadFile(item)">{{ item.docName }}</span></td>
            <td>{{ item.docType }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ item.updatedTime }}</td>
          </tr>
          <tr v-if="attachmentList.length === 0">
            <td colspan="9" style="text-align: center; color: #999;">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ attachmentTotal }} 条</span>
        <select v-model="attachmentPageSize" @change="loadAttachments">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="attachmentPageNum === 1" @click="attachmentPageNum--; loadAttachments()">上一页</button>
        <span>第 {{ attachmentPageNum }} 页</span>
        <button :disabled="attachmentPageNum * attachmentPageSize >= attachmentTotal" @click="attachmentPageNum++; loadAttachments()">下一页</button>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">授权规则</h3>
        <div class="section-actions" v-if="isEditable">
          <button class="btn btn-primary" @click="handleAddScene">添加场景</button>
          <button class="btn btn-danger" @click="handleDeleteScenes" :disabled="selectedScenes.length === 0">删除</button>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" v-model="isAllSceneSelected" @change="handleSceneSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>场景</th>
            <th>产业</th>
            <th>业务场景</th>
            <th>具体规则</th>
            <th>决策层级</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in sceneList" :key="item.id">
            <td><input type="checkbox" v-model="selectedScenes" :value="item.id" /></td>
            <td>{{ (scenePageNum - 1) * scenePageSize + index + 1 }}</td>
            <td>
              <span class="icon-edit" @click="handleEditScene(item)">✏️</span>
              <span class="icon-delete" @click="handleDeleteScene(item.id)">🗑️</span>
            </td>
            <td>{{ item.sceneName }}</td>
            <td>{{ formatArray(item.industry) }}</td>
            <td>{{ item.businessScene }}</td>
            <td>{{ item.specificRule }}</td>
            <td>{{ item.decisionLevel }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ item.updatedTime }}</td>
          </tr>
          <tr v-if="sceneList.length === 0">
            <td colspan="12" style="text-align: center; color: #999;">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ sceneTotal }} 条</span>
        <select v-model="scenePageSize" @change="loadScenes">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="scenePageNum === 1" @click="scenePageNum--; loadScenes()">上一页</button>
        <span>第 {{ scenePageNum }} 页</span>
        <button :disabled="scenePageNum * scenePageSize >= sceneTotal" @click="scenePageNum++; loadScenes()">下一页</button>
      </div>
    </div>

    <!-- 日志区块（已发布状态显示） -->
    <div class="section" v-if="formData.status === 'PUBLISHED' || formData.status === 'INVALID'">
      <div class="collapse-panel">
        <div class="collapse-header" @click="logCollapsed = !logCollapsed">
          <span>操作日志</span>
          <span :class="['collapse-icon', { collapsed: logCollapsed }]">▼</span>
        </div>
        <div class="collapse-content" v-show="!logCollapsed">
          <table>
            <thead>
              <tr>
                <th>序号</th>
                <th>操作</th>
                <th>操作人</th>
                <th>操作时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in logList" :key="item.id">
                <td>{{ (logPageNum - 1) * logPageSize + index + 1 }}</td>
                <td>{{ getOperationText(item.operation) }}</td>
                <td>{{ item.operator }}</td>
                <td>{{ item.operationTime }}</td>
              </tr>
            </tbody>
          </table>
          <div class="pagination">
            <span>共 {{ logTotal }} 条</span>
            <button :disabled="logPageNum === 1" @click="logPageNum--; loadLogs()">上一页</button>
            <span>第 {{ logPageNum }} 页</span>
            <button :disabled="logPageNum * logPageSize >= logTotal" @click="logPageNum++; loadLogs()">下一页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="footer-actions">
      <template v-if="formData.status === 'DRAFT'">
        <button class="btn btn-primary" @click="handleSave">保存</button>
        <button class="btn btn-primary" @click="handleSaveAndPublish">保存并发布</button>
        <button class="btn" @click="handlePublish">发布</button>
        <button class="btn" @click="handleCancel">取消</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
      <template v-else-if="formData.status === 'PUBLISHED'">
        <button class="btn" @click="handleBack">返回</button>
        <button class="btn btn-danger" @click="handleInvalidate">失效</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
      <template v-else>
        <button class="btn" @click="handleBack">返回</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal-overlay" v-if="showSceneModal">
      <div class="modal-content" style="width: 800px;">
        <div class="modal-header">
          <h3>{{ editingScene ? '编辑场景' : '添加场景' }}</h3>
          <span class="modal-close" @click="showSceneModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item">
              <label class="required">场景名称</label>
              <input type="text" v-model="sceneForm.sceneName" placeholder="请输入场景名称" />
            </div>
            <div class="form-item">
              <label class="required">产业</label>
              <tree-select
                v-model="sceneForm.industry"
                :options="lookupData.industry"
                placeholder="请选择产业"
                multiple
              />
            </div>
            <div class="form-item">
              <label class="required">业务场景</label>
              <custom-select
                v-model="sceneForm.businessScene"
                :options="lookupData.businessScene"
                placeholder="请选择业务场景"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="required">决策层级</label>
              <custom-select
                v-model="sceneForm.decisionLevel"
                :options="lookupData.decisionLevel"
                placeholder="请选择决策层级"
              />
            </div>
            <div class="form-item" style="flex: 2;">
              <label class="required">具体规则</label>
              <textarea v-model="sceneForm.specificRule" placeholder="请输入具体规则" maxlength="1000"></textarea>
            </div>
          </div>

          <!-- 规则配置 -->
          <div class="rule-config">
            <h4>规则配置</h4>
            <button class="btn btn-link" @click="addCondition">+ 新增条件</button>
            <button class="btn btn-link" @click="addConditionGroup">+ 新增子条件组</button>

            <div v-for="(condition, index) in sceneForm.conditions" :key="index">
              <div v-if="index > 0" class="logic-toggle" @click="toggleLogic(condition)">
                {{ condition.logicType === 'AND' ? '且' : '或' }}
              </div>
              <div v-if="condition.isGroup" class="condition-group">
                <div v-for="(subCond, subIndex) in condition.children" :key="subIndex">
                  <div v-if="subIndex > 0" class="logic-toggle" @click="toggleLogic(subCond)">
                    {{ subCond.logicType === 'AND' ? '且' : '或' }}
                  </div>
                  <div class="rule-condition-row">
                    <select v-model="subCond.fieldCode">
                      <option value="">请选择字段</option>
                      <option v-for="f in ruleParams" :key="f.nameEn" :value="f.nameEn">{{ f.name }}</option>
                    </select>
                    <select v-model="subCond.operator">
                      <option value=">">大于</option>
                      <option value="<">小于</option>
                      <option value="=">等于</option>
                      <option value=">=">大于等于</option>
                      <option value="<=">小于等于</option>
                      <option value="!=">不等于</option>
                    </select>
                    <select v-model="subCond.compareType">
                      <option value="NUMBER">数值</option>
                      <option value="TEXT">文本</option>
                      <option value="FIELD">对比字段</option>
                      <option value="RATIO">比例</option>
                    </select>
                    <input type="text" v-model="subCond.compareValue" placeholder="对比值" style="width: 150px;" />
                    <select v-model="subCond.compareUnit" v-if="subCond.compareType === 'NUMBER'">
                      <option value="">请选择单位</option>
                      <option value="CNY">人民币(元)</option>
                      <option value="USD">美元</option>
                      <option value="WAN">万</option>
                      <option value="YI">亿</option>
                    </select>
                    <span class="icon-delete" @click="removeCondition(condition.children, subIndex)">🗑️</span>
                  </div>
                </div>
                <button class="btn btn-link" @click="addSubCondition(condition)">+ 新增条件</button>
                <span class="icon-delete" @click="removeConditionGroup(index)" style="margin-left: 16px;">移除条件组</span>
              </div>
              <div v-else class="rule-condition-row">
                <select v-model="condition.fieldCode">
                  <option value="">请选择字段</option>
                  <option v-for="f in ruleParams" :key="f.nameEn" :value="f.nameEn">{{ f.name }}</option>
                </select>
                <select v-model="condition.operator">
                  <option value=">">大于</option>
                  <option value="<">小于</option>
                  <option value="=">等于</option>
                  <option value=">=">大于等于</option>
                  <option value="<=">小于等于</option>
                  <option value="!=">不等于</option>
                </select>
                <select v-model="condition.compareType">
                  <option value="NUMBER">数值</option>
                  <option value="TEXT">文本</option>
                  <option value="FIELD">对比字段</option>
                  <option value="RATIO">比例</option>
                </select>
                <input type="text" v-model="condition.compareValue" placeholder="对比值" style="width: 150px;" />
                <select v-model="condition.compareUnit" v-if="condition.compareType === 'NUMBER'">
                  <option value="">请选择单位</option>
                  <option value="CNY">人民币(元)</option>
                  <option value="USD">美元</option>
                  <option value="WAN">万</option>
                  <option value="YI">亿</option>
                </select>
                <span class="icon-delete" @click="removeCondition(sceneForm.conditions, index)">🗑️</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleSaveScene">确定</button>
          <button class="btn" @click="showSceneModal = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 附件上传弹窗 -->
    <div class="modal-overlay" v-if="showUploadModal">
      <div class="modal-content" style="width: 400px;">
        <div class="modal-header">
          <h3>上传附件</h3>
          <span class="modal-close" @click="showUploadModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>文档名称</label>
            <input type="text" v-model="uploadForm.docName" placeholder="请输入文档名称" />
          </div>
          <div class="form-item">
            <label>文档类型</label>
            <custom-select v-model="uploadForm.docType" :options="docTypeOptions" placeholder="请选择文档类型" />
          </div>
          <div class="form-item">
            <label>文档ID</label>
            <input type="text" v-model="uploadForm.docId" placeholder="请输入文档ID（模拟）" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleConfirmUpload">确定</button>
          <button class="btn" @click="showUploadModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// HTTP请求封装
// ============ 接口配置 ============
// 修改此处的 baseURL 以适配你的后端服务地址
const baseURL = 'http://localhost:8080/api/v1'
// ==================================

const http = {
  get(url, params) {
    const query = new URLSearchParams(params).toString()
    return fetch(`${baseURL}${url}${query ? '?' + query : ''}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    }).then(res => res.json())
  },
  post(url, data) {
    return fetch(`${baseURL}${url}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  put(url, data) {
    return fetch(`${baseURL}${url}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  delete(url) {
    return fetch(`${baseURL}${url}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    }).then(res => res.json())
  }
}

// 自定义下拉选择器组件
const CustomSelect = {
  props: {
    value: [String, Array],
    options: { type: Array, default: () => [] },
    placeholder: { type: String, default: '请选择' },
    multiple: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false }
  },
  data() {
    return { isOpen: false }
  },
  computed: {
    selectedItems() {
      if (!this.value || (Array.isArray(this.value) && this.value.length === 0)) return []
      const values = Array.isArray(this.value) ? this.value : [this.value]
      return values.map(v => {
        const opt = this.options.find(o => o.value === v || o.code === v)
        return { value: v, label: opt ? (opt.label || opt.name) : v }
      })
    }
  },
  methods: {
    toggleDropdown() { if (!this.disabled) this.isOpen = !this.isOpen },
    selectOption(option) {
      const val = option.value || option.code
      if (this.multiple) {
        const newValue = Array.isArray(this.value) ? [...this.value] : []
        const index = newValue.indexOf(val)
        if (index > -1) newValue.splice(index, 1)
        else newValue.push(val)
        this.$emit('input', newValue)
      } else {
        this.$emit('input', val)
        this.isOpen = false
      }
    },
    isSelected(option) {
      const val = option.value || option.code
      return this.multiple && Array.isArray(this.value) ? this.value.includes(val) : this.value === val
    },
    removeTag(item, e) {
      e.stopPropagation()
      const newValue = Array.isArray(this.value) ? [...this.value] : []
      const index = newValue.indexOf(item.value)
      if (index > -1) {
        newValue.splice(index, 1)
        this.$emit('input', newValue)
      }
    }
  },
  template: `
    <div class="custom-select">
      <div class="select-trigger" @click="toggleDropdown" :style="{cursor: disabled ? 'not-allowed' : 'pointer', background: disabled ? '#f5f5f5' : '#fff'}">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span v-if="multiple && !disabled" class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown" v-if="isOpen && !disabled">
        <div class="select-option" v-for="opt in options" :key="opt.value || opt.code" @click="selectOption(opt)">
          <input v-if="multiple" type="checkbox" :checked="isSelected(opt)" />
          <span>{{ opt.label || opt.name }}</span>
        </div>
      </div>
    </div>
  `
}

// 树形选择器组件
const TreeSelect = {
  props: {
    value: Array,
    options: { type: Array, default: () => [] },
    placeholder: { type: String, default: '请选择' },
    multiple: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false }
  },
  data() {
    return { isOpen: false, expandedKeys: [] }
  },
  computed: {
    selectedItems() {
      if (!this.value || this.value.length === 0) return []
      return this.value.map(v => {
        const node = this.findNode(this.options, v)
        return { value: v, label: node ? node.name : v }
      })
    }
  },
  methods: {
    findNode(nodes, code) {
      for (const node of nodes) {
        if (node.code === code) return node
        if (node.children) {
          const found = this.findNode(node.children, code)
          if (found) return found
        }
      }
      return null
    },
    toggleDropdown() { if (!this.disabled) this.isOpen = !this.isOpen },
    toggleExpand(code) {
      const index = this.expandedKeys.indexOf(code)
      if (index > -1) this.expandedKeys.splice(index, 1)
      else this.expandedKeys.push(code)
    },
    isExpanded(code) { return this.expandedKeys.includes(code) },
    isChecked(code) { return this.value && this.value.includes(code) },
    toggleCheck(code) {
      const newValue = this.value ? [...this.value] : []
      const index = newValue.indexOf(code)
      if (index > -1) newValue.splice(index, 1)
      else newValue.push(code)
      this.$emit('input', newValue)
    },
    removeTag(item, e) {
      e.stopPropagation()
      const newValue = this.value ? [...this.value] : []
      const index = newValue.indexOf(item.value)
      if (index > -1) {
        newValue.splice(index, 1)
        this.$emit('input', newValue)
      }
    }
  },
  template: `
    <div class="custom-select">
      <div class="select-trigger" @click="toggleDropdown" :style="{cursor: disabled ? 'not-allowed' : 'pointer', background: disabled ? '#f5f5f5' : '#fff'}">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span v-if="!disabled" class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown tree-select-dropdown" v-if="isOpen && !disabled">
        <template v-for="node in options">
          <div class="tree-node" :key="node.code">
            <div class="tree-node-content">
              <span class="tree-toggle" v-if="node.children && node.children.length > 0" @click="toggleExpand(node.code)">{{ isExpanded(node.code) ? '▼' : '▶' }}</span>
              <span v-else style="width: 20px; display: inline-block;"></span>
              <input type="checkbox" :checked="isChecked(node.code)" @click="toggleCheck(node.code)" />
              <span>{{ node.name }}</span>
            </div>
            <div class="tree-children" v-if="node.children && isExpanded(node.code)">
              <template v-for="child in node.children">
                <div class="tree-node" :key="child.code">
                  <div class="tree-node-content" :style="{paddingLeft: '20px'}">
                    <span class="tree-toggle" v-if="child.children && child.children.length > 0" @click="toggleExpand(child.code)">{{ isExpanded(child.code) ? '▼' : '▶' }}</span>
                    <span v-else style="width: 20px; display: inline-block;"></span>
                    <input type="checkbox" :checked="isChecked(child.code)" @click="toggleCheck(child.code)" />
                    <span>{{ child.name }}</span>
                  </div>
                  <div class="tree-children" v-if="child.children && isExpanded(child.code)">
                    <div v-for="grandChild in child.children" :key="grandChild.code" class="tree-node">
                      <div class="tree-node-content" :style="{paddingLeft: '40px'}">
                        <span style="width: 20px; display: inline-block;"></span>
                        <input type="checkbox" :checked="isChecked(grandChild.code)" @click="toggleCheck(grandChild.code)" />
                        <span>{{ grandChild.name }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>
      </div>
    </div>
  `
}

export default {
  name: 'AuthLetterDetail',
  components: { CustomSelect, TreeSelect },
  data() {
    return {
      id: null,
      isNew: false,
      isEdit: false,
      formData: {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        summary: '',
        status: 'DRAFT'
      },
      lookupData: {
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        industry: [],
        businessScene: [],
        decisionLevel: []
      },
      ruleParams: [],
      attachmentList: [],
      selectedAttachments: [],
      isAllAttachmentSelected: false,
      attachmentPageNum: 1,
      attachmentPageSize: 10,
      attachmentTotal: 0,
      sceneList: [],
      selectedScenes: [],
      isAllSceneSelected: false,
      scenePageNum: 1,
      scenePageSize: 10,
      sceneTotal: 0,
      logList: [],
      logPageNum: 1,
      logPageSize: 10,
      logTotal: 0,
      logCollapsed: true,
      showSceneModal: false,
      showUploadModal: false,
      editingScene: null,
      sceneForm: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        conditions: []
      },
      uploadForm: {
        docId: '',
        docName: '',
        docType: ''
      },
      docTypeOptions: [
        { value: 'PDF', label: 'PDF文档' },
        { value: 'WORD', label: 'Word文档' },
        { value: 'EXCEL', label: 'Excel文档' },
        { value: 'IMAGE', label: '图片' },
        { value: 'OTHER', label: '其他' }
      ]
    }
  },
  computed: {
    isEditable() {
      return this.isNew || (this.isEdit && this.formData.status === 'DRAFT')
    }
  },
  created() {
    this.id = this.$route.query.id
    this.isNew = this.$route.query.mode === 'create'
    this.isEdit = this.$route.query.mode === 'edit'

    this.loadLookupData()
    this.loadRuleParams()

    if (this.id) {
      this.loadData()
      this.loadAttachments()
      this.loadScenes()
    }
  },
  methods: {
    async loadLookupData() {
      const types = ['AUTH_OBJECT_LEVEL', 'APPLICABLE_REGION', 'AUTH_PUBLISH_LEVEL', 'AUTH_PUBLISH_ORG', 'INDUSTRY', 'BUSINESS_SCENE', 'DECISION_LEVEL']
      for (const type of types) {
        try {
          const res = await http.get(`/lookup-values/${type}`)
          if (res.code === 200 && res.data) {
            if (type === 'APPLICABLE_REGION' || type === 'AUTH_PUBLISH_ORG' || type === 'INDUSTRY') {
              const key = type.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
              this.lookupData[key] = this.buildTree(res.data)
            } else {
              const key = type.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
              this.lookupData[key] = res.data.map(item => ({ value: item.valueCode, label: item.valueName }))
            }
          }
        } catch (e) {
          console.error(`加载${type}失败`, e)
        }
      }
    },
    buildTree(data) {
      const map = {}
      const roots = []
      data.forEach(item => {
        map[item.valueCode] = { ...item, code: item.valueCode, name: item.valueName, children: [] }
      })
      data.forEach(item => {
        if (item.parentCode && map[item.parentCode]) {
          map[item.parentCode].children.push(map[item.valueCode])
        } else if (!item.parentCode) {
          roots.push(map[item.valueCode])
        }
      })
      return roots
    },
    async loadRuleParams() {
      try {
        const res = await http.get('/rule-params/active')
        if (res.code === 200) {
          this.ruleParams = res.data || []
        }
      } catch (e) {
        console.error('加载规则参数失败', e)
      }
    },
    async loadData() {
      try {
        const res = await http.get(`/auth-letters/${this.id}`)
        if (res.code === 200 && res.data) {
          this.formData = {
            ...res.data,
            authObjectLevel: this.parseArray(res.data.authObjectLevel),
            applicableRegion: this.parseArray(res.data.applicableRegion),
            authPublishLevel: this.parseArray(res.data.authPublishLevel),
            authPublishOrg: this.parseArray(res.data.authPublishOrg)
          }
          if (res.data.status !== 'DRAFT') {
            this.loadLogs()
          }
        }
      } catch (e) {
        console.error('加载数据失败', e)
      }
    },
    parseArray(value) {
      if (!value) return []
      try {
        return typeof value === 'string' ? JSON.parse(value) : value
      } catch (e) {
        return []
      }
    },
    async loadAttachments() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/attachments`, {
          pageNum: this.attachmentPageNum,
          pageSize: this.attachmentPageSize
        })
        if (res.code === 200) {
          this.attachmentList = res.data.list || []
          this.attachmentTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载附件失败', e)
      }
    },
    async loadScenes() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/scenes`, {
          pageNum: this.scenePageNum,
          pageSize: this.scenePageSize
        })
        if (res.code === 200) {
          this.sceneList = res.data.list || []
          this.sceneTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载场景失败', e)
      }
    },
    async loadLogs() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/logs`, {
          pageNum: this.logPageNum,
          pageSize: this.logPageSize
        })
        if (res.code === 200) {
          this.logList = res.data.list || []
          this.logTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载日志失败', e)
      }
    },
    async handleSave() {
      try {
        const data = {
          ...this.formData,
          authObjectLevel: this.formData.authObjectLevel,
          applicableRegion: this.formData.applicableRegion,
          authPublishLevel: this.formData.authPublishLevel,
          authPublishOrg: this.formData.authPublishOrg
        }

        if (this.isNew) {
          const res = await http.post('/auth-letters', data)
          if (res.code === 200) {
            this.id = res.data
            this.isNew = false
            alert('保存成功')
            this.$router.push({ path: '/#/AuthLetterDetail', query: { id: this.id } })
          }
        } else {
          await http.put(`/auth-letters/${this.id}`, data)
          alert('保存成功')
          this.loadData()
        }
      } catch (e) {
        console.error('保存失败', e)
        alert('保存失败')
      }
    },
    async handleSaveAndPublish() {
      await this.handleSave()
      if (this.id) {
        await this.handlePublish()
      }
    },
    async handlePublish() {
      if (!confirm('确定要发布吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/publish`)
        alert('发布成功')
        this.loadData()
      } catch (e) {
        console.error('发布失败', e)
        alert('发布失败')
      }
    },
    async handleInvalidate() {
      if (!confirm('确定要失效吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/invalidate`)
        alert('操作成功')
        this.loadData()
      } catch (e) {
        console.error('操作失败', e)
        alert('操作失败')
      }
    },
    async handleDelete() {
      if (!confirm('确定要删除吗？')) return
      try {
        await http.delete(`/auth-letters/${this.id}`)
        alert('删除成功')
        this.$router.push('/#/AuthLetterList')
      } catch (e) {
        console.error('删除失败', e)
        alert('删除失败')
      }
    },
    handleCancel() {
      this.$router.push('/#/AuthLetterList')
    },
    handleBack() {
      this.$router.push('/#/AuthLetterList')
    },
    handleAttachmentSelectAll() {
      this.selectedAttachments = this.isAllAttachmentSelected ? this.attachmentList.map(a => a.id) : []
    },
    handleUploadAttachment() {
      this.uploadForm = { docId: '', docName: '', docType: '' }
      this.showUploadModal = true
    },
    async handleConfirmUpload() {
      try {
        await http.post(`/auth-letters/${this.id}/attachments`, this.uploadForm)
        this.showUploadModal = false
        this.loadAttachments()
      } catch (e) {
        console.error('上传失败', e)
        alert('上传失败')
      }
    },
    handleDownloadAttachment() {
      this.selectedAttachments.forEach(id => {
        const att = this.attachmentList.find(a => a.id === id)
        if (att) this.handleDownloadFile(att)
      })
    },
    handleDownloadFile(item) {
      alert(`下载文件: ${item.docName}`)
    },
    async handleDeleteAttachment() {
      if (!confirm('确定要删除选中的附件吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/attachments/batch-delete`, this.selectedAttachments)
        this.selectedAttachments = []
        this.loadAttachments()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    handleSceneSelectAll() {
      this.selectedScenes = this.isAllSceneSelected ? this.sceneList.map(s => s.id) : []
    },
    handleAddScene() {
      this.editingScene = null
      this.sceneForm = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        conditions: []
      }
      this.showSceneModal = true
    },
    handleEditScene(scene) {
      this.editingScene = scene
      this.sceneForm = {
        sceneName: scene.sceneName,
        industry: this.parseArray(scene.industry),
        businessScene: scene.businessScene,
        decisionLevel: scene.decisionLevel,
        specificRule: scene.specificRule,
        conditions: []
      }
      this.showSceneModal = true
    },
    async handleSaveScene() {
      try {
        const data = {
          ...this.sceneForm,
          industry: this.sceneForm.industry
        }
        if (this.editingScene) {
          await http.put(`/auth-letters/${this.id}/scenes/${this.editingScene.id}`, data)
        } else {
          await http.post(`/auth-letters/${this.id}/scenes`, data)
        }
        this.showSceneModal = false
        this.loadScenes()
      } catch (e) {
        console.error('保存场景失败', e)
        alert('保存失败')
      }
    },
    async handleDeleteScene(sceneId) {
      if (!confirm('确定要删除此场景吗？')) return
      try {
        await http.delete(`/auth-letters/${this.id}/scenes/${sceneId}`)
        this.loadScenes()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    async handleDeleteScenes() {
      if (!confirm('确定要删除选中的场景吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/scenes/batch-delete`, this.selectedScenes)
        this.selectedScenes = []
        this.loadScenes()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    addCondition() {
      this.sceneForm.conditions.push({
        fieldCode: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false
      })
    },
    addConditionGroup() {
      this.sceneForm.conditions.push({
        isGroup: true,
        logicType: 'AND',
        children: [{
          fieldCode: '',
          operator: '>',
          compareType: 'NUMBER',
          compareValue: '',
          compareUnit: '',
          logicType: 'AND'
        }]
      })
    },
    addSubCondition(group) {
      group.children.push({
        fieldCode: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND'
      })
    },
    removeCondition(list, index) {
      list.splice(index, 1)
    },
    removeConditionGroup(index) {
      this.sceneForm.conditions.splice(index, 1)
    },
    toggleLogic(condition) {
      condition.logicType = condition.logicType === 'AND' ? 'OR' : 'AND'
    },
    formatArray(value) {
      if (!value) return ''
      try {
        const arr = typeof value === 'string' ? JSON.parse(value) : value
        return Array.isArray(arr) ? arr.join(', ') : value
      } catch (e) {
        return value
      }
    },
    getOperationText(operation) {
      const map = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'PUBLISH': '发布',
        'INVALIDATE': '失效',
        'DELETE': '删除'
      }
      return map[operation] || operation
    }
  }
}
</script>

<style scoped>
.section {
  margin-bottom: 24px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e8e8e8;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-actions {
  display: flex;
  gap: 8px;
}

.page-container {
  padding-bottom: 80px;
}
</style>