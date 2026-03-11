<template>
  <div class="auth-letter-list-page">
    <!-- 查询条件区 -->
    <div class="query-card">
      <div class="query-form">
        <div class="form-row">
          <div class="form-item">
            <label class="form-label">授权书名称</label>
            <input type="text" v-model="queryParams.name" class="form-input" placeholder="请输入授权书名称" />
          </div>
          <div class="form-item">
            <label class="form-label">授权对象层级</label>
            <div class="multi-select-wrapper">
              <div class="multi-select-trigger" @click="toggleSelect('authTargetLevel')">
                <span class="selected-tags" v-if="queryParams.authTargetLevel.length > 0">
                  <span class="tag" v-for="(item, index) in getSelectedLabels(queryParams.authTargetLevel, authTargetLevelOptions)" :key="index">
                    {{ item }}
                    <span class="tag-close" @click.stop="removeSelected('authTargetLevel', queryParams.authTargetLevel[index])">×</span>
                  </span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="multi-select-dropdown" v-show="activeDropdown === 'authTargetLevel'">
                <div class="select-option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                  <span class="checkbox" :class="{ checked: queryParams.authTargetLevel.includes(item.code) }"></span>
                  <span>{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label">适用区域</label>
            <div class="multi-select-wrapper">
              <div class="multi-select-trigger" @click="toggleSelect('applicableRegion')">
                <span class="selected-tags" v-if="queryParams.applicableRegion.length > 0">
                  <span class="tag" v-for="(item, index) in getSelectedLabels(queryParams.applicableRegion, applicableRegionOptions)" :key="index">
                    {{ item }}
                    <span class="tag-close" @click.stop="removeSelected('applicableRegion', queryParams.applicableRegion[index])">×</span>
                  </span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="multi-select-dropdown" v-show="activeDropdown === 'applicableRegion'">
                <div class="select-option" v-for="item in applicableRegionOptions" :key="item.code" @click="toggleMultiSelect('applicableRegion', item.code)">
                  <span class="checkbox" :class="{ checked: queryParams.applicableRegion.includes(item.code) }"></span>
                  <span>{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label class="form-label">授权发布层级</label>
            <div class="multi-select-wrapper">
              <div class="multi-select-trigger" @click="toggleSelect('authPublishLevel')">
                <span class="selected-tags" v-if="queryParams.authPublishLevel.length > 0">
                  <span class="tag" v-for="(item, index) in getSelectedLabels(queryParams.authPublishLevel, authPublishLevelOptions)" :key="index">
                    {{ item }}
                    <span class="tag-close" @click.stop="removeSelected('authPublishLevel', queryParams.authPublishLevel[index])">×</span>
                  </span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="multi-select-dropdown" v-show="activeDropdown === 'authPublishLevel'">
                <div class="select-option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                  <span class="checkbox" :class="{ checked: queryParams.authPublishLevel.includes(item.code) }"></span>
                  <span>{{ item.name }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label">授权发布组织</label>
            <div class="tree-select-wrapper">
              <div class="tree-select-trigger" @click="toggleSelect('authPublishOrg')">
                <span class="selected-tags" v-if="queryParams.authPublishOrg.length > 0">
                  <span class="tag" v-for="(item, index) in queryParams.authPublishOrg.slice(0, 2)" :key="index">
                    {{ getOrgName(item) }}
                    <span class="tag-close" @click.stop="removeSelected('authPublishOrg', item)">×</span>
                  </span>
                  <span class="tag" v-if="queryParams.authPublishOrg.length > 2">+{{ queryParams.authPublishOrg.length - 2 }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-show="activeDropdown === 'authPublishOrg'">
                <tree-node
                  v-for="node in orgTreeData"
                  :key="node.code"
                  :node="node"
                  :selected-codes="queryParams.authPublishOrg"
                  @toggle="toggleTreeNode"
                />
              </div>
            </div>
          </div>
          <div class="form-item">
            <label class="form-label">授权书发布年份</label>
            <div class="year-select-wrapper">
              <div class="year-select-trigger" @click="toggleSelect('publishYear')">
                <span>{{ queryParams.publishYear || '请选择年份' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="year-select-dropdown" v-show="activeDropdown === 'publishYear'">
                <div class="year-option" v-for="year in yearOptions" :key="year" @click="selectYear(year)">{{ year }}</div>
              </div>
            </div>
          </div>
        </div>
        <div class="form-row">
          <div class="form-item">
            <label class="form-label">状态</label>
            <div class="select-wrapper">
              <div class="select-trigger" @click="toggleSelect('status')">
                <span>{{ getStatusLabel(queryParams.status) || '请选择' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="select-dropdown" v-show="activeDropdown === 'status'">
                <div class="select-option" @click="selectStatus('')">全部</div>
                <div class="select-option" v-for="item in statusOptions" :key="item.value" @click="selectStatus(item.value)">
                  {{ item.label }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="query-buttons">
        <button class="btn btn-primary" @click="handleQuery">查询</button>
        <button class="btn btn-default" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 功能按钮区 -->
    <div class="action-buttons">
      <button class="btn btn-primary" @click="handleCreate">新建授权书</button>
      <button class="btn btn-default" @click="handleUpdate">更新</button>
      <button class="btn btn-success" @click="handleActivate">生效</button>
      <button class="btn btn-warning" @click="handleDeactivate">失效</button>
      <button class="btn btn-danger" @click="handleDelete">删除</button>
    </div>

    <!-- 数据表格区 -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th class="col-checkbox"><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th class="col-index">序号</th>
            <th class="col-action">操作</th>
            <th class="col-name">授权书名称</th>
            <th class="col-status">状态</th>
            <th class="col-level">授权对象层级</th>
            <th class="col-region">适用区域</th>
            <th class="col-level">授权发布层级</th>
            <th class="col-org">授权发布组织</th>
            <th class="col-year">授权书发布年份</th>
            <th class="col-user">创建人</th>
            <th class="col-time">创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="12" style="text-align: center; padding: 40px; color: #909399;">加载中...</td>
          </tr>
          <tr v-else-if="tableData.length === 0">
            <td colspan="12" style="text-align: center; padding: 40px; color: #909399;">暂无数据</td>
          </tr>
          <tr v-else v-for="(row, index) in tableData" :key="row.id">
            <td class="col-checkbox"><input type="checkbox" v-model="selectedRows" :value="row.id" /></td>
            <td class="col-index">{{ (pagination.pageNum - 1) * pagination.pageSize + index + 1 }}</td>
            <td class="col-action">
              <span v-if="row.status === 'DRAFT'" class="icon-btn" @click="goToDetail(row.id)" title="编辑">✏️</span>
            </td>
            <td class="col-name"><a class="link" @click="goToDetail(row.id)">{{ row.name }}</a></td>
            <td class="col-status"><span class="status-tag" :class="'status-' + row.status.toLowerCase()">{{ row.statusText }}</span></td>
            <td class="col-level">{{ formatArrayText(row.authTargetLevelText) }}</td>
            <td class="col-region">{{ formatArrayText(row.applicableRegionText) }}</td>
            <td class="col-level">{{ formatArrayText(row.authPublishLevelText) }}</td>
            <td class="col-org">{{ formatArrayText(row.authPublishOrgText) }}</td>
            <td class="col-year">{{ row.publishYear }}</td>
            <td class="col-user">{{ row.createdBy }}</td>
            <td class="col-time">{{ row.createdAt }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 分页组件 -->
      <div class="pagination">
        <span class="pagination-total">共 {{ pagination.total }} 条</span>
        <select class="pagination-size" v-model="pagination.pageSize" @change="handleSizeChange">
          <option :value="10">10条/页</option>
          <option :value="20">20条/页</option>
          <option :value="50">50条/页</option>
          <option :value="100">100条/页</option>
        </select>
        <button class="pagination-btn" :disabled="pagination.pageNum === 1" @click="handlePageChange(pagination.pageNum - 1)">上一页</button>
        <span class="pagination-page">{{ pagination.pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pagination.pageNum >= totalPages" @click="handlePageChange(pagination.pageNum + 1)">下一页</button>
        <span class="pagination-jump">跳至<input type="number" v-model.number="jumpPage" @keyup.enter="handleJumpPage" />页</span>
      </div>
    </div>

    <!-- 消息提示 -->
    <div class="message-box" v-if="message.show" :class="'message-' + message.type">
      {{ message.text }}
    </div>

    <!-- 确认对话框 -->
    <div class="modal-overlay" v-if="confirmDialog.show" @click.self="confirmDialog.onCancel">
      <div class="modal-dialog">
        <div class="modal-header">
          <span class="modal-title">提示</span>
        </div>
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
            const result = checkChildren(child.children)
            hasChecked = hasChecked || result.hasChecked
            hasUnchecked = hasUnchecked || result.hasUnchecked
          }
        }
        return { hasChecked, hasUnchecked }
      }
      const result = checkChildren(this.node.children)
      return result.hasChecked && result.hasUnchecked
    }
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
        <span class="tree-checkbox" :class="{ checked: isChecked, indeterminate: isIndeterminate }" @click="toggleCheck"></span>
        <span class="tree-node-label" @click="toggleCheck">{{ node.name }}</span>
      </div>
      <div v-if="hasChildren && expanded" class="tree-children">
        <tree-node v-for="child in node.children" :key="child.code" :node="{ ...child, level: (node.level || 0) + 1 }" :selected-codes="selectedCodes" @toggle="$emit('toggle', $event)" />
      </div>
    </div>
  `
}

// ========== API 请求方法 ==========
const BASE_URL = '/api'

async function request(url, options = {}) {
  const config = {
    headers: { 'Content-Type': 'application/json' },
    ...options,
    headers: { ...options.headers, 'Content-Type': 'application/json' }
  }
  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body)
  }
  const response = await fetch(BASE_URL + url, config)
  if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`)
  return response.json()
}

const api = {
  // Lookup服务
  getLookupOptions: (code) => request(`/lookup/${code}`),
  getOrgTree: () => request('/lookup/org/tree'),

  // 授权书管理
  getAuthLetterList: (params) => {
    const queryStr = Object.entries(params)
      .filter(([, v]) => v !== null && v !== undefined && v !== '')
      .map(([k, v]) => Array.isArray(v) ? v.map(x => `${k}=${encodeURIComponent(x)}`).join('&') : `${k}=${encodeURIComponent(v)}`)
      .join('&')
    return request(`/auth-letters?${queryStr}`)
  },
  batchPublish: (ids) => request('/auth-letters/batch/publish', { method: 'PUT', body: { ids } }),
  batchExpire: (ids) => request('/auth-letters/batch/expire', { method: 'PUT', body: { ids } }),
  batchDelete: (ids) => request('/auth-letters/batch', { method: 'DELETE', body: { ids } })
}

export default {
  name: 'AuthLetterList',
  components: { TreeNode },
  data() {
    return {
      loading: false,
      activeDropdown: '',
      selectAll: false,
      selectedRows: [],
      jumpPage: 1,
      message: { show: false, type: 'info', text: '' },
      confirmDialog: { show: false, text: '', onConfirm: () => {}, onCancel: () => {} },
      queryParams: {
        name: '',
        authTargetLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        status: ''
      },
      pagination: { pageNum: 1, pageSize: 10, total: 0 },
      authTargetLevelOptions: [],
      applicableRegionOptions: [],
      authPublishLevelOptions: [],
      orgTreeData: [],
      statusOptions: [
        { value: 'DRAFT', label: '草稿' },
        { value: 'PUBLISHED', label: '已发布' },
        { value: 'EXPIRED', label: '已失效' }
      ],
      tableData: []
    }
  },
  computed: {
    totalPages() { return Math.ceil(this.pagination.total / this.pagination.pageSize) || 1 },
    yearOptions() {
      const years = []
      for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) years.push(i)
      return years
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
    this.loadLookupData()
    this.loadTableData()
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    async loadLookupData() {
      try {
        const [targetLevel, region, publishLevel, orgTree] = await Promise.all([
          api.getLookupOptions('authTargetLevel'),
          api.getLookupOptions('applicableRegion'),
          api.getLookupOptions('authPublishLevel'),
          api.getOrgTree()
        ])
        this.authTargetLevelOptions = targetLevel.data || []
        this.applicableRegionOptions = region.data || []
        this.authPublishLevelOptions = publishLevel.data || []
        this.orgTreeData = orgTree.data || []
      } catch (error) {
        console.error('加载Lookup数据失败:', error)
      }
    },

    async loadTableData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          name: this.queryParams.name || undefined,
          publishYear: this.queryParams.publishYear || undefined,
          status: this.queryParams.status || undefined
        }
        if (this.queryParams.authTargetLevel.length > 0) params.authTargetLevel = this.queryParams.authTargetLevel
        if (this.queryParams.applicableRegion.length > 0) params.applicableRegion = this.queryParams.applicableRegion
        if (this.queryParams.authPublishLevel.length > 0) params.authPublishLevel = this.queryParams.authPublishLevel
        if (this.queryParams.authPublishOrg.length > 0) params.authPublishOrg = this.queryParams.authPublishOrg

        const res = await api.getAuthLetterList(params)
        this.tableData = res.data?.list || []
        this.pagination.total = res.data?.total || 0
      } catch (error) {
        console.error('加载数据失败:', error)
        this.showMessage('加载数据失败', 'error')
      } finally {
        this.loading = false
      }
    },

    toggleSelect(name) { this.activeDropdown = this.activeDropdown === name ? '' : name },
    toggleMultiSelect(field, value) {
      const index = this.queryParams[field].indexOf(value)
      if (index > -1) this.queryParams[field].splice(index, 1)
      else this.queryParams[field].push(value)
    },
    removeSelected(field, value) {
      const index = this.queryParams[field].indexOf(value)
      if (index > -1) this.queryParams[field].splice(index, 1)
    },
    selectYear(year) { this.queryParams.publishYear = year; this.activeDropdown = '' },
    selectStatus(value) { this.queryParams.status = value; this.activeDropdown = '' },
    getStatusLabel(value) {
      const item = this.statusOptions.find(s => s.value === value)
      return item ? item.label : ''
    },
    getSelectedLabels(codes, options) {
      return codes.map(code => { const item = options.find(o => o.code === code); return item ? item.name : code })
    },
    getOrgName(code) {
      const findNode = (nodes, targetCode) => {
        for (const node of nodes) {
          if (node.code === targetCode) return node.name
          if (node.children) { const found = findNode(node.children, targetCode); if (found) return found }
        }
        return null
      }
      return findNode(this.orgTreeData, code) || code
    },
    toggleTreeNode(node) {
      const toggleNodeAndChildren = (n, shouldCheck) => {
        const index = this.queryParams.authPublishOrg.indexOf(n.code)
        if (shouldCheck && index === -1) this.queryParams.authPublishOrg.push(n.code)
        else if (!shouldCheck && index > -1) this.queryParams.authPublishOrg.splice(index, 1)
        if (n.children) n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck))
      }
      toggleNodeAndChildren(node, !this.queryParams.authPublishOrg.includes(node.code))
    },
    handleSelectAll() {
      this.selectedRows = this.selectAll ? this.tableData.map(row => row.id) : []
    },
    handleSizeChange() { this.pagination.pageNum = 1; this.loadTableData() },
    handlePageChange(page) { this.pagination.pageNum = page; this.loadTableData() },
    handleJumpPage() {
      if (this.jumpPage >= 1 && this.jumpPage <= this.totalPages) {
        this.pagination.pageNum = this.jumpPage
        this.loadTableData()
      }
    },
    formatArrayText(arr) { return !arr || arr.length === 0 ? '-' : arr.join('、') },
    showMessage(text, type = 'info') {
      this.message = { show: true, type, text }
      setTimeout(() => { this.message.show = false }, 3000)
    },
    showConfirm(text) {
      return new Promise((resolve) => {
        this.confirmDialog = {
          show: true, text,
          onConfirm: () => { this.confirmDialog.show = false; resolve(true) },
          onCancel: () => { this.confirmDialog.show = false; resolve(false) }
        }
      })
    },
    handleQuery() { this.pagination.pageNum = 1; this.loadTableData() },
    handleReset() {
      Object.assign(this.queryParams, { name: '', authTargetLevel: [], applicableRegion: [], authPublishLevel: [], authPublishOrg: [], publishYear: null, status: '' })
      this.pagination.pageNum = 1
      this.loadTableData()
    },
    checkSelection() {
      if (this.selectedRows.length === 0) { this.showMessage('请先选择数据', 'warning'); return false }
      return true
    },
    handleCreate() { this.$router.push('/auth-letter/create') },
    handleUpdate() { if (this.checkSelection()) this.showMessage('更新功能待实现', 'info') },
    async handleActivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据发布生效吗？`)
      if (confirmed) {
        try {
          await api.batchPublish(this.selectedRows)
          this.showMessage('操作成功', 'success')
          this.selectedRows = []; this.selectAll = false; this.loadTableData()
        } catch (error) { this.showMessage('操作失败', 'error') }
      }
    },
    async handleDeactivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为失效吗？`)
      if (confirmed) {
        try {
          await api.batchExpire(this.selectedRows)
          this.showMessage('操作成功', 'success')
          this.selectedRows = []; this.selectAll = false; this.loadTableData()
        } catch (error) { this.showMessage('操作失败', 'error') }
      }
    },
    async handleDelete() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要删除选中的 ${this.selectedRows.length} 条数据吗？`)
      if (confirmed) {
        try {
          await api.batchDelete(this.selectedRows)
          this.showMessage('删除成功', 'success')
          this.selectedRows = []; this.selectAll = false; this.loadTableData()
        } catch (error) { this.showMessage('删除失败', 'error') }
      }
    },
    goToDetail(id) { this.$router.push(`/auth-letter/${id}`) },
    handleClickOutside(event) {
      if (!event.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
        this.activeDropdown = ''
      }
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }
.auth-letter-list-page { padding: 20px; background-color: #f5f7fa; min-height: 100vh; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 14px; color: #333; }
.query-card { background: #fff; border-radius: 4px; padding: 20px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1); }
.query-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.form-item { display: flex; align-items: center; }
.form-label { width: 100px; text-align: right; color: #666; flex-shrink: 0; margin-right: 12px; }
.form-input { flex: 1; height: 32px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; outline: none; transition: border-color 0.2s; }
.form-input:focus { border-color: #409eff; }
.query-buttons { display: flex; justify-content: flex-end; gap: 10px; margin-top: 16px; padding-top: 16px; border-top: 1px solid #eee; }
.btn { padding: 8px 16px; border-radius: 4px; border: 1px solid #dcdfe6; background: #fff; cursor: pointer; font-size: 14px; transition: all 0.2s; }
.btn:hover { opacity: 0.9; }
.btn-primary { background: #409eff; border-color: #409eff; color: #fff; }
.btn-success { background: #67c23a; border-color: #67c23a; color: #fff; }
.btn-warning { background: #e6a23c; border-color: #e6a23c; color: #fff; }
.btn-danger { background: #f56c6c; border-color: #f56c6c; color: #fff; }
.btn-default { background: #fff; color: #191919; border-color: #dcdfe6; }
.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper { flex: 1; position: relative; }
.multi-select-trigger, .tree-select-trigger, .select-trigger, .year-select-trigger { height: 32px; padding: 0 30px 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; position: relative; }
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.placeholder { color: #c0c4cc; }
.selected-tags { display: flex; flex-wrap: wrap; gap: 4px; }
.tag { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; display: flex; align-items: center; gap: 4px; }
.tag-close { cursor: pointer; color: #909399; }
.tag-close:hover { color: #409eff; }
.multi-select-dropdown, .tree-select-dropdown, .select-dropdown, .year-select-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #fff; border: 1px solid #dcdfe6; border-radius: 4px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); z-index: 100; max-height: 250px; overflow-y: auto; margin-top: 4px; }
.select-option, .year-option { padding: 8px 12px; cursor: pointer; display: flex; align-items: center; gap: 8px; }
.select-option:hover, .year-option:hover { background: #f5f7fa; }
.checkbox { width: 14px; height: 14px; border: 1px solid #dcdfe6; border-radius: 2px; display: inline-block; position: relative; }
.checkbox.checked { background: #409eff; border-color: #409eff; }
.checkbox.checked::after { content: '✓'; color: #fff; font-size: 10px; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); }
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
.action-buttons { display: flex; gap: 10px; margin-bottom: 16px; }
.table-card { background: #fff; border-radius: 4px; padding: 20px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1); }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 12px 8px; text-align: left; border-bottom: 1px solid #ebeef5; }
.data-table th { background: #f5f7fa; font-weight: 600; color: #909399; }
.data-table tbody tr:hover { background: #f5f7fa; }
.col-checkbox { width: 50px; text-align: center; }
.col-index { width: 60px; text-align: center; }
.col-action { width: 60px; text-align: center; white-space: nowrap; }
.col-status { width: 80px; text-align: center; }
.col-year, .col-user { width: 100px; text-align: center; }
.col-time { width: 160px; text-align: center; }
.icon-btn { cursor: pointer; font-size: 16px; }
.icon-btn:hover { opacity: 0.7; }
.link { color: #409eff; cursor: pointer; text-decoration: none; }
.link:hover { text-decoration: underline; }
.status-tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.status-draft { background: #f4f4f5; color: #909399; }
.status-published { background: #f0f9eb; color: #67c23a; }
.status-expired { background: #fef0f0; color: #f56c6c; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 10px; margin-top: 16px; font-size: 13px; color: #606266; }
.pagination-size { height: 28px; padding: 0 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.pagination-btn { padding: 4px 12px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination-btn:disabled { color: #c0c4cc; cursor: not-allowed; }
.pagination-jump input { width: 50px; height: 28px; border: 1px solid #dcdfe6; border-radius: 4px; text-align: center; margin: 0 4px; }
.message-box { position: fixed; top: 20px; left: 50%; transform: translateX(-50%); padding: 12px 24px; border-radius: 4px; z-index: 1000; animation: fadeIn 0.3s; }
@keyframes fadeIn { from { opacity: 0; transform: translateX(-50%) translateY(-10px); } to { opacity: 1; transform: translateX(-50%) translateY(0); } }
.message-info { background: #edf2fc; color: #909399; border: 1px solid #dcdfe6; }
.message-success { background: #f0f9eb; color: #67c23a; border: 1px solid #e1f3d8; }
.message-warning { background: #fdf6ec; color: #e6a23c; border: 1px solid #faecd8; }
.message-error { background: #fef0f0; color: #f56c6c; border: 1px solid #fde2e2; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-dialog { background: #fff; border-radius: 8px; min-width: 400px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2); }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #eee; }
.modal-title { font-weight: 600; }
.modal-body { padding: 20px; }
.modal-footer { padding: 16px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; }
</style>