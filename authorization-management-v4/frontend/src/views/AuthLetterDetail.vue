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
                <tree-node v-for="node in orgTreeData" :key="node.code" :node="node" :selected-codes="formData.authPublishOrg" @toggle="toggleOrgTreeNode" />
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
            <textarea v-model="formData.contentSummary" class="form-textarea" :disabled="!isEditable" placeholder="请输入授权书内容摘要" maxlength="4000" rows="4"></textarea>
            <span class="char-count">{{ (formData.contentSummary || '').length }}/4000</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="info-card" v-if="isEditable || attachments.length > 0">
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
              <th class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectAllAttachment" @change="handleSelectAllAttachment" /></th>
              <th class="col-index">序号</th>
              <th class="col-action">操作</th>
              <th class="col-name">文档名称</th>
              <th class="col-type">文档类型</th>
              <th class="col-user">创建人</th>
              <th class="col-time">创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="attachments.length === 0">
              <td :colspan="isEditable ? 7 : 6" style="text-align: center; padding: 20px; color: #909399;">暂无附件</td>
            </tr>
            <tr v-else v-for="(row, index) in attachments" :key="row.id">
              <td class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectedAttachments" :value="row.id" /></td>
              <td class="col-index">{{ index + 1 }}</td>
              <td class="col-action">
                <span class="icon-btn" @click="downloadFile(row)" title="下载">📥</span>
                <span class="icon-btn disabled" title="加密(暂不可用)">🔒</span>
              </td>
              <td class="col-name"><a class="link" @click="downloadFile(row)">{{ row.docName }}</a></td>
              <td class="col-type">{{ row.docType || '-' }}</td>
              <td class="col-user">{{ row.createdBy || '-' }}</td>
              <td class="col-time">{{ row.createdAt || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="info-card">
      <div class="card-title">授权规则</div>
      <div class="section-content">
        <div class="action-row" v-if="isEditable">
          <button class="btn btn-primary btn-sm" @click="openSceneModal()">添加场景</button>
          <button class="btn btn-default btn-sm" @click="handleDeleteScene">删除</button>
        </div>
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectAllScene" @change="handleSelectAllScene" /></th>
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
            <tr v-if="scenes.length === 0">
              <td :colspan="isEditable ? 10 : 9" style="text-align: center; padding: 20px; color: #909399;">暂无授权规则</td>
            </tr>
            <tr v-else v-for="(row, index) in scenes" :key="row.id">
              <td class="col-checkbox" v-if="isEditable"><input type="checkbox" v-model="selectedScenes" :value="row.id" /></td>
              <td class="col-index">{{ index + 1 }}</td>
              <td class="col-action" v-if="isEditable">
                <span class="icon-btn" @click="openSceneModal(row)" title="编辑">✏️</span>
                <span class="icon-btn" @click="deleteScene(row.id)" title="删除">🗑️</span>
              </td>
              <td class="col-name">{{ row.sceneName }}</td>
              <td class="col-name">{{ row.industryText || '-' }}</td>
              <td class="col-name">{{ row.businessScenario || '-' }}</td>
              <td class="col-name">{{ row.ruleDetail || '-' }}</td>
              <td class="col-name">{{ row.decisionLevel || '-' }}</td>
              <td class="col-user">{{ row.createdBy || '-' }}</td>
              <td class="col-time">{{ row.createdAt || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 日志区块（已发布状态显示） -->
    <div class="info-card" v-if="formData.status === 'PUBLISHED'">
      <div class="card-title collapsible" @click="logExpanded = !logExpanded">
        日志
        <span class="expand-icon" :class="{ expanded: logExpanded }">▼</span>
      </div>
      <div class="section-content" v-show="logExpanded">
        <table class="data-table">
          <thead>
            <tr>
              <th class="col-index">序号</th>
              <th class="col-action">操作</th>
              <th class="col-user">操作人</th>
              <th class="col-time">操作时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="logs.length === 0">
              <td colspan="4" style="text-align: center; padding: 20px; color: #909399;">暂无日志</td>
            </tr>
            <tr v-else v-for="(row, index) in logs" :key="row.id">
              <td class="col-index">{{ index + 1 }}</td>
              <td class="col-action">{{ row.operationText }}</td>
              <td class="col-user">{{ row.operator || '-' }}</td>
              <td class="col-time">{{ row.operatedAt || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 底部按钮区 -->
    <div class="bottom-buttons" v-if="formData.status === 'DRAFT'">
      <button class="btn btn-primary" @click="handleSave">保存</button>
      <button class="btn btn-success" @click="handleSaveAndPublish">保存并发布</button>
      <button class="btn btn-warning" @click="handlePublish">发布</button>
      <button class="btn btn-default" @click="handleCancel">取消</button>
      <button class="btn btn-danger" v-if="authLetterId" @click="handleDelete">删除</button>
    </div>
    <div class="bottom-buttons" v-else-if="formData.status === 'PUBLISHED'">
      <button class="btn btn-default" @click="handleBack">返回</button>
      <button class="btn btn-warning" @click="handleExpire">失效</button>
      <button class="btn btn-danger" @click="handleDelete">删除</button>
    </div>
    <div class="bottom-buttons" v-else>
      <button class="btn btn-default" @click="handleBack">返回</button>
      <button class="btn btn-danger" @click="handleDelete">删除</button>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal-overlay" v-if="sceneModal.visible" @click.self="sceneModal.visible = false">
      <div class="modal-dialog large">
        <div class="modal-header">
          <span class="modal-title">{{ sceneModal.isEdit ? '编辑场景' : '添加场景' }}</span>
          <span class="modal-close" @click="sceneModal.visible = false">×</span>
        </div>
        <div class="modal-body">
          <div class="modal-form">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label required">场景名称</label>
                <input type="text" v-model="sceneModal.data.sceneName" class="form-input" placeholder="请输入场景名称" />
              </div>
              <div class="form-item">
                <label class="form-label required">产业</label>
                <div class="tree-select-wrapper">
                  <div class="tree-select-trigger" @click="toggleSelect('industry')">
                    <span class="selected-tags" v-if="sceneModal.data.industry.length > 0">
                      <span class="tag" v-for="(item, index) in sceneModal.data.industry.slice(0, 2)" :key="index">{{ item }}</span>
                      <span class="tag" v-if="sceneModal.data.industry.length > 2">+{{ sceneModal.data.industry.length - 2 }}</span>
                    </span>
                    <span class="placeholder" v-else>请选择</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="tree-select-dropdown" v-show="activeDropdown === 'industry'">
                    <div class="select-option" v-for="item in industryOptions" :key="item.code" @click="toggleIndustrySelect(item.code)">
                      <span class="checkbox" :class="{ checked: sceneModal.data.industry.includes(item.code) }"></span>
                      <span>{{ item.name }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label required">业务场景</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('businessScenario')">
                    <span>{{ sceneModal.data.businessScenario || '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'businessScenario'">
                    <div class="select-option" v-for="item in businessScenarioOptions" :key="item.code" @click="selectBusinessScenario(item.code)">{{ item.name }}</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label class="form-label required">决策层级</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('decisionLevel')">
                    <span>{{ sceneModal.data.decisionLevel || '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'decisionLevel'">
                    <div class="select-option" v-for="item in decisionLevelOptions" :key="item.code" @click="selectDecisionLevel(item.code)">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="form-item full">
                <label class="form-label required">具体规则</label>
                <textarea v-model="sceneModal.data.ruleDetail" class="form-textarea" placeholder="请输入具体规则" maxlength="1000" rows="3"></textarea>
              </div>
            </div>
            <!-- 规则配置区 -->
            <div class="rule-config-section">
              <div class="rule-config-header">
                <span>规则配置</span>
                <span class="text-btn" @click="addCondition">+ 新增条件</span>
                <span class="text-btn" @click="addConditionGroup">+ 新增子条件组</span>
              </div>
              <div class="rule-config-body">
                <div v-if="sceneModal.data.conditions.length === 0" class="empty-rule">暂无规则条件</div>
                <condition-item
                  v-for="(cond, index) in sceneModal.data.conditions"
                  :key="index"
                  :condition="cond"
                  :rule-fields="ruleFieldOptions"
                  @remove="removeCondition(index)"
                  @change="updateCondition(index, $event)"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="sceneModal.visible = false">取消</button>
          <button class="btn btn-primary" @click="saveScene">确定</button>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div class="message-box" v-if="message.show" :class="'message-' + message.type">
      {{ message.text }}
    </div>
  </div>
</template>

<script>
// ========== 树节点组件 ==========
const TreeNode = {
  name: 'TreeNode',
  props: { node: Object, selectedCodes: Array },
  data() { return { expanded: false } },
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

// ========== API ==========
const BASE_URL = '/api'
const request = (url, options = {}) => {
  const config = { headers: { 'Content-Type': 'application/json' }, ...options }
  if (config.body && typeof config.body === 'object') config.body = JSON.stringify(config.body)
  return fetch(BASE_URL + url, config).then(r => r.json())
}
const api = {
  getLookupOptions: (code) => request(`/lookup/${code}`),
  getOrgTree: () => request('/lookup/org/tree'),
  getAuthLetter: (id) => request(`/auth-letters/${id}`),
  createAuthLetter: (data) => request('/auth-letters', { method: 'POST', body: data }),
  updateAuthLetter: (id, data) => request(`/auth-letters/${id}`, { method: 'PUT', body: data }),
  deleteAuthLetter: (id) => request(`/auth-letters/${id}`, { method: 'DELETE' }),
  publishAuthLetter: (id) => request(`/auth-letters/${id}/publish`, { method: 'PUT' }),
  expireAuthLetter: (id) => request(`/auth-letters/${id}/expire`, { method: 'PUT' }),
  getRuleParams: () => request('/rule-params?pageSize=100')
}

export default {
  name: 'AuthLetterDetail',
  components: { TreeNode },
  data() {
    return {
      authLetterId: null,
      activeDropdown: '',
      message: { show: false, type: 'info', text: '' },
      logExpanded: false,
      selectAllAttachment: false,
      selectAllScene: false,
      selectedAttachments: [],
      selectedScenes: [],
      formData: {
        name: '',
        authPublishLevel: [],
        authPublishOrg: [],
        authTargetLevel: [],
        applicableRegion: [],
        publishYear: null,
        contentSummary: '',
        status: 'DRAFT'
      },
      attachments: [],
      scenes: [],
      logs: [],
      authTargetLevelOptions: [],
      authPublishLevelOptions: [],
      orgTreeData: [],
      applicableRegionOptions: [],
      applicableRegionTree: [],
      industryOptions: [
        { code: 'IT', name: 'IT产业' },
        { code: 'FINANCE', name: '金融产业' },
        { code: 'MANUFACTURE', name: '制造产业' }
      ],
      businessScenarioOptions: [
        { code: 'SCENARIO1', name: '业务场景1' },
        { code: 'SCENARIO2', name: '业务场景2' }
      ],
      decisionLevelOptions: [
        { code: 'LEVEL1', name: '一级决策' },
        { code: 'LEVEL2', name: '二级决策' },
        { code: 'LEVEL3', name: '三级决策' }
      ],
      ruleFieldOptions: [],
      sceneModal: {
        visible: false,
        isEdit: false,
        editId: null,
        data: {
          sceneName: '',
          industry: [],
          businessScenario: '',
          decisionLevel: '',
          ruleDetail: '',
          conditions: []
        }
      }
    }
  },
  computed: {
    isEditable() { return this.formData.status === 'DRAFT' },
    yearOptions() {
      const years = []
      for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) years.push(i)
      return years
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
    const params = new URLSearchParams(window.location.hash.split('?')[1])
    this.authLetterId = params.get('id')
    this.loadLookupData()
    if (this.authLetterId) this.loadDetail()
  },
  beforeDestroy() { document.removeEventListener('click', this.handleClickOutside) },
  methods: {
    async loadLookupData() {
      try {
        const [targetLevel, publishLevel, orgTree, region, ruleParams] = await Promise.all([
          api.getLookupOptions('AUTH_TARGET_LEVEL'),
          api.getLookupOptions('AUTH_PUBLISH_LEVEL'),
          api.getOrgTree(),
          api.getLookupOptions('APPLICABLE_REGION'),
          api.getRuleParams()
        ])
        this.authTargetLevelOptions = targetLevel.code === 200 ? (targetLevel.data || []) : []
        this.authPublishLevelOptions = publishLevel.code === 200 ? (publishLevel.data || []) : []
        this.orgTreeData = orgTree.code === 200 ? (orgTree.data || []) : []
        this.applicableRegionOptions = region.code === 200 ? (region.data || []) : []
        this.applicableRegionTree = this.applicableRegionOptions.map(item => ({ ...item, children: [] }))
        this.ruleFieldOptions = ruleParams.code === 200 ? (ruleParams.data?.list || []) : []
      } catch (e) { console.error('加载数据失败:', e) }
    },
    async loadDetail() {
      try {
        const res = await api.getAuthLetter(this.authLetterId)
        if (res.code === 200 && res.data) {
          const d = res.data
          this.formData = {
            name: d.name || '',
            authPublishLevel: d.authPublishLevel || [],
            authPublishOrg: d.authPublishOrg || [],
            authTargetLevel: d.authTargetLevel || [],
            applicableRegion: d.applicableRegion || [],
            publishYear: d.publishYear,
            contentSummary: d.contentSummary || '',
            status: d.status || 'DRAFT'
          }
          this.attachments = d.attachments || []
          this.scenes = d.scenes || []
          this.logs = d.logs || []
        }
      } catch (e) { console.error('加载详情失败:', e) }
    },
    toggleSelect(name) { this.activeDropdown = this.activeDropdown === name ? '' : name },
    toggleMultiSelect(field, value) {
      const idx = this.formData[field].indexOf(value)
      if (idx > -1) this.formData[field].splice(idx, 1)
      else this.formData[field].push(value)
    },
    selectYear(year) { this.formData.publishYear = year; this.activeDropdown = '' },
    getSelectedLabels(codes, options) {
      return codes.map(c => { const item = options.find(o => o.code === c); return item ? item.name : c })
    },
    getOrgName(code) {
      const find = (nodes, c) => {
        for (const n of nodes) {
          if (n.code === c) return n.name
          if (n.children) { const f = find(n.children, c); if (f) return f }
        }
        return null
      }
      return find(this.orgTreeData, code) || code
    },
    getRegionName(code) {
      const item = this.applicableRegionOptions.find(o => o.code === code)
      return item ? item.name : code
    },
    toggleOrgTreeNode(node) {
      const idx = this.formData.authPublishOrg.indexOf(node.code)
      if (idx > -1) this.formData.authPublishOrg.splice(idx, 1)
      else this.formData.authPublishOrg.push(node.code)
    },
    toggleRegionTreeNode(node) {
      const idx = this.formData.applicableRegion.indexOf(node.code)
      if (idx > -1) this.formData.applicableRegion.splice(idx, 1)
      else this.formData.applicableRegion.push(node.code)
    },
    showMessage(text, type = 'info') {
      this.message = { show: true, type, text }
      setTimeout(() => { this.message.show = false }, 3000)
    },
    handleSelectAllAttachment() { this.selectedAttachments = this.selectAllAttachment ? this.attachments.map(a => a.id) : [] },
    handleSelectAllScene() { this.selectedScenes = this.selectAllScene ? this.scenes.map(s => s.id) : [] },
    handleUpload() { this.showMessage('上传功能待实现', 'info') },
    handleDownloadAttachment() { this.showMessage('下载功能待实现', 'info') },
    handleDeleteAttachment() { this.selectedAttachments.forEach(id => { this.attachments = this.attachments.filter(a => a.id !== id) }); this.selectedAttachments = []; this.selectAllAttachment = false },
    downloadFile(row) { this.showMessage('下载: ' + row.docName, 'info') },
    openSceneModal(row = null) {
      this.sceneModal = {
        visible: true,
        isEdit: !!row,
        editId: row ? row.id : null,
        data: {
          sceneName: row ? row.sceneName : '',
          industry: row ? (row.industry || []) : [],
          businessScenario: row ? row.businessScenario : '',
          decisionLevel: row ? row.decisionLevel : '',
          ruleDetail: row ? row.ruleDetail : '',
          conditions: row ? (row.conditions || []) : []
        }
      }
    },
    toggleIndustrySelect(code) {
      const idx = this.sceneModal.data.industry.indexOf(code)
      if (idx > -1) this.sceneModal.data.industry.splice(idx, 1)
      else this.sceneModal.data.industry.push(code)
    },
    selectBusinessScenario(code) { this.sceneModal.data.businessScenario = code; this.activeDropdown = '' },
    selectDecisionLevel(code) { this.sceneModal.data.decisionLevel = code; this.activeDropdown = '' },
    addCondition() { this.sceneModal.data.conditions.push({ type: 'condition', field: '', operator: '', compareType: 'value', compareValue: '' }) },
    addConditionGroup() { this.sceneModal.data.conditions.push({ type: 'group', logic: 'AND', conditions: [] }) },
    removeCondition(index) { this.sceneModal.data.conditions.splice(index, 1) },
    updateCondition(index, data) { this.sceneModal.data.conditions[index] = { ...this.sceneModal.data.conditions[index], ...data } },
    saveScene() {
      if (!this.sceneModal.data.sceneName) { this.showMessage('请输入场景名称', 'warning'); return }
      const sceneData = {
        id: this.sceneModal.editId || Date.now(),
        sceneName: this.sceneModal.data.sceneName,
        industry: this.sceneModal.data.industry,
        industryText: this.sceneModal.data.industry.join('、'),
        businessScenario: this.sceneModal.data.businessScenario,
        decisionLevel: this.sceneModal.data.decisionLevel,
        ruleDetail: this.sceneModal.data.ruleDetail,
        conditions: this.sceneModal.data.conditions,
        createdBy: 'system',
        createdAt: new Date().toLocaleString()
      }
      if (this.sceneModal.isEdit) {
        const idx = this.scenes.findIndex(s => s.id === this.sceneModal.editId)
        if (idx > -1) this.scenes.splice(idx, 1, sceneData)
      } else {
        this.scenes.push(sceneData)
      }
      this.sceneModal.visible = false
    },
    deleteScene(id) { this.scenes = this.scenes.filter(s => s.id !== id) },
    handleDeleteScene() { this.selectedScenes.forEach(id => this.deleteScene(id)); this.selectedScenes = []; this.selectAllScene = false },
    async handleSave() {
      if (!this.formData.name) { this.showMessage('请输入授权书名称', 'warning'); return }
      try {
        let res
        if (this.authLetterId) {
          res = await api.updateAuthLetter(this.authLetterId, this.formData)
        } else {
          res = await api.createAuthLetter(this.formData)
          if (res.code === 200 && res.data) this.authLetterId = res.data
        }
        if (res.code === 200) this.showMessage('保存成功', 'success')
        else this.showMessage(res.message || '保存失败', 'error')
      } catch (e) { this.showMessage('保存失败', 'error') }
    },
    async handleSaveAndPublish() {
      await this.handleSave()
      if (this.authLetterId) await this.handlePublish()
    },
    async handlePublish() {
      if (!this.authLetterId) { this.showMessage('请先保存', 'warning'); return }
      try {
        const res = await api.publishAuthLetter(this.authLetterId)
        if (res.code === 200) {
          this.showMessage('发布成功', 'success')
          this.formData.status = 'PUBLISHED'
        } else this.showMessage(res.message || '发布失败', 'error')
      } catch (e) { this.showMessage('发布失败', 'error') }
    },
    async handleExpire() {
      try {
        const res = await api.expireAuthLetter(this.authLetterId)
        if (res.code === 200) {
          this.showMessage('操作成功', 'success')
          this.formData.status = 'EXPIRED'
        } else this.showMessage(res.message || '操作失败', 'error')
      } catch (e) { this.showMessage('操作失败', 'error') }
    },
    async handleDelete() {
      try {
        const res = await api.deleteAuthLetter(this.authLetterId)
        if (res.code === 200) {
          this.showMessage('删除成功', 'success')
          setTimeout(() => { window.location.href = '#/AuthLetterList' }, 1000)
        } else this.showMessage(res.message || '删除失败', 'error')
      } catch (e) { this.showMessage('删除失败', 'error') }
    },
    handleCancel() { window.location.href = '#/AuthLetterList' },
    handleBack() { window.location.href = '#/AuthLetterList' },
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
.auth-letter-detail-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 50px); padding-bottom: 80px; }
.info-card { background: #fff; border-radius: 4px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.card-title { padding: 16px 20px; border-bottom: 1px solid #eee; font-weight: 600; font-size: 16px; }
.card-title.collapsible { cursor: pointer; display: flex; justify-content: space-between; align-items: center; }
.expand-icon { font-size: 12px; color: #c0c4cc; transition: transform 0.2s; }
.expand-icon.expanded { transform: rotate(180deg); }
.info-form { padding: 20px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 16px; max-width: 1400px; }
.form-row.single { grid-template-columns: 1fr; }
.form-item { display: flex; align-items: flex-start; }
.form-item.full { flex: 1; }
.form-label { width: 100px; text-align: right; color: #666; flex-shrink: 0; margin-right: 12px; line-height: 32px; }
.form-label.required::before { content: '*'; color: #f56c6c; margin-right: 4px; }
.form-input { flex: 1; height: 32px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; outline: none; }
.form-input:focus { border-color: #409eff; }
.form-input:disabled { background: #f5f7fa; color: #c0c4cc; cursor: not-allowed; }
.form-textarea { flex: 1; padding: 8px 12px; border: 1px solid #dcdfe6; border-radius: 4px; outline: none; resize: vertical; }
.form-textarea:focus { border-color: #409eff; }
.form-textarea:disabled { background: #f5f7fa; color: #c0c4cc; }
.char-count { color: #909399; font-size: 12px; margin-left: 8px; }
.section-content { padding: 0 20px 20px; }
.action-row { display: flex; gap: 10px; margin-bottom: 12px; }
.btn { padding: 8px 16px; border-radius: 4px; border: 1px solid #dcdfe6; background: #fff; cursor: pointer; font-size: 14px; transition: all 0.2s; }
.btn-sm { padding: 5px 12px; font-size: 13px; }
.btn:hover { opacity: 0.9; }
.btn-primary { background: #409eff; border-color: #409eff; color: #fff; }
.btn-success { background: #67c23a; border-color: #67c23a; color: #fff; }
.btn-warning { background: #e6a23c; border-color: #e6a23c; color: #fff; }
.btn-danger { background: #f56c6c; border-color: #f56c6c; color: #fff; }
.btn-default { background: #fff; color: #333; }
.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper { flex: 1; position: relative; }
.multi-select-trigger, .tree-select-trigger, .select-trigger, .year-select-trigger { height: 32px; padding: 0 30px 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; position: relative; overflow: hidden; }
.multi-select-trigger.disabled, .tree-select-trigger.disabled, .select-trigger.disabled, .year-select-trigger.disabled { background: #f5f7fa; cursor: not-allowed; color: #c0c4cc; }
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.placeholder { color: #c0c4cc; }
.selected-tags { display: flex; flex-wrap: wrap; gap: 4px; overflow: hidden; }
.tag { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; white-space: nowrap; }
.multi-select-dropdown, .tree-select-dropdown, .select-dropdown, .year-select-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #fff; border: 1px solid #dcdfe6; border-radius: 4px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); z-index: 100; max-height: 250px; overflow-y: auto; margin-top: 4px; }
.select-option, .year-option { padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px; }
.select-option:hover, .year-option:hover { background: #f5f7fa; }
.checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; display: inline-block; position: relative; flex-shrink: 0; }
.checkbox.checked { background: #409eff; border-color: #409eff; }
.checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.tree-node-content { display: flex; align-items: center; padding: 6px 8px; cursor: pointer; }
.tree-node-content:hover { background: #f5f7fa; }
.tree-expand-icon { width: 16px; font-size: 10px; color: #c0c4cc; transition: transform 0.2s; flex-shrink: 0; }
.tree-expand-icon.expanded { transform: rotate(90deg); }
.tree-expand-placeholder { width: 16px; flex-shrink: 0; }
.tree-checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; margin-right: 8px; position: relative; flex-shrink: 0; }
.tree-checkbox.checked { background: #409eff; border-color: #409eff; }
.tree-checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
.tree-node-label { font-size: 14px; }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 10px 8px; text-align: left; border-bottom: 1px solid #ebeef5; }
.data-table th { background: #f5f7fa; font-weight: 600; color: #909399; font-size: 13px; }
.col-checkbox { width: 40px; text-align: center; }
.col-index { width: 50px; text-align: center; }
.col-action { width: 70px; text-align: center; }
.col-status, .col-type { width: 90px; text-align: center; }
.col-year, .col-user { width: 90px; text-align: center; }
.col-time { width: 150px; text-align: center; }
.icon-btn { cursor: pointer; font-size: 14px; margin: 0 4px; }
.icon-btn:hover { opacity: 0.7; }
.icon-btn.disabled { opacity: 0.4; cursor: not-allowed; }
.link { color: #409eff; cursor: pointer; }
.link:hover { text-decoration: underline; }
.bottom-buttons { position: fixed; bottom: 0; left: 0; right: 0; background: #fff; padding: 16px 20px; box-shadow: 0 -2px 8px rgba(0,0,0,0.1); display: flex; justify-content: center; gap: 16px; z-index: 100; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-dialog { background: #fff; border-radius: 8px; width: 90%; max-width: 800px; max-height: 90vh; overflow: hidden; display: flex; flex-direction: column; }
.modal-dialog.large { max-width: 900px; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
.modal-title { font-weight: 600; font-size: 16px; }
.modal-close { font-size: 20px; color: #909399; cursor: pointer; }
.modal-close:hover { color: #409eff; }
.modal-body { padding: 20px; overflow-y: auto; flex: 1; }
.modal-footer { padding: 16px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; }
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.rule-config-section { border: 1px solid #dcdfe6; border-radius: 4px; }
.rule-config-header { padding: 12px 16px; background: #f5f7fa; display: flex; align-items: center; gap: 16px; }
.rule-config-header span:first-child { font-weight: 600; }
.text-btn { color: #409eff; cursor: pointer; font-size: 13px; }
.text-btn:hover { text-decoration: underline; }
.rule-config-body { padding: 16px; min-height: 60px; }
.empty-rule { color: #909399; text-align: center; padding: 20px; }
.message-box { position: fixed; top: 70px; left: 50%; transform: translateX(-50%); padding: 12px 24px; border-radius: 4px; z-index: 1001; }
.message-info { background: #edf2fc; color: #909399; }
.message-success { background: #f0f9eb; color: #67c23a; }
.message-warning { background: #fdf6ec; color: #e6a23c; }
.message-error { background: #fef0f0; color: #f56c6c; }
</style>