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
            <div class="tree-select-wrapper">
              <div class="tree-select-trigger" @click="toggleSelect('applicableRegion')">
                <span class="selected-tags" v-if="queryParams.applicableRegion.length > 0">
                  <span class="tag" v-for="(item, index) in queryParams.applicableRegion.slice(0, 2)" :key="index">
                    {{ getLookupName(item, applicableRegionOptions) }}
                    <span class="tag-close" @click.stop="removeSelected('applicableRegion', item)">×</span>
                  </span>
                  <span class="tag" v-if="queryParams.applicableRegion.length > 2">+{{ queryParams.applicableRegion.length - 2 }}</span>
                </span>
                <span class="placeholder" v-else>请选择</span>
                <span class="arrow">▼</span>
              </div>
              <div class="tree-select-dropdown" v-show="activeDropdown === 'applicableRegion'">
                <tree-node
                  v-for="node in applicableRegionTree"
                  :key="node.code"
                  :node="node"
                  :selected-codes="queryParams.applicableRegion"
                  @toggle="toggleTreeNodeSelect"
                />
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
                  @toggle="toggleOrgTreeNode"
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
            <td class="col-status"><span class="status-tag" :class="'status-' + row.status.toLowerCase()">{{ getStatusText(row.status) }}</span></td>
            <td class="col-level">{{ row.authTargetLevelLabel || '-' }}</td>
            <td class="col-region">{{ row.applicableRegionLabel || '-' }}</td>
            <td class="col-level">{{ row.authPublishLevelLabel || '-' }}</td>
            <td class="col-org">{{ row.authPublishOrgLabel || '-' }}</td>
            <td class="col-year">{{ row.publishYear || '-' }}</td>
            <td class="col-user">{{ row.createdBy || '-' }}</td>
            <td class="col-time">{{ formatTime(row.createdTime) }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 分页组件 -->
      <div class="pagination">
        <span class="pagination-total">共 {{ pagination.total }} 条</span>
        <select class="pagination-size" v-model="pagination.pageSize" @change="handleSizeChange">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button class="pagination-btn" :disabled="pagination.pageNum === 1" @click="handlePageChange(pagination.pageNum - 1)">上一页</button>
        <span class="pagination-page">{{ pagination.pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pagination.pageNum >= totalPages" @click="handlePageChange(pagination.pageNum + 1)">下一页</button>
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
  name: 'AuthLetterList',
  components: { TreeNode },
  data() {
    return {
      loading: false,
      activeDropdown: '',
      selectAll: false,
      selectedRows: [],
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
      applicableRegionTree: [],
      authPublishLevelOptions: [],
      orgTreeData: [],
      statusOptions: [
        { value: 'DRAFT', label: '草稿' },
        { value: 'PUBLISHED', label: '已发布' },
        { value: 'INVALID', label: '已失效' }
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
          request.get('/lookup/AUTH_TARGET_LEVEL'),
          request.get('/lookup/APPLICABLE_REGION'),
          request.get('/lookup/AUTH_PUBLISH_LEVEL'),
          request.get('/lookup/AUTH_PUBLISH_ORG')
        ])
        // 扁平列表类型（无层级结构）
        this.authTargetLevelOptions = targetLevel.code === 200 ? this.transformLookupData(targetLevel.data) : []
        this.authPublishLevelOptions = publishLevel.code === 200 ? this.transformLookupData(publishLevel.data) : []
        // 树形结构类型（有层级结构）- API已返回树形数据，直接转换字段名即可
        this.applicableRegionOptions = region.code === 200 ? this.transformLookupData(region.data) : []
        this.applicableRegionTree = this.applicableRegionOptions
        this.orgTreeData = orgTree.code === 200 ? this.transformLookupData(orgTree.data) : []
      } catch (error) {
        console.error('加载Lookup数据失败:', error)
      }
    },

    transformLookupData(data) {
      if (!data || !Array.isArray(data)) return []
      return data.map(item => ({
        code: item.value,
        name: item.label,
        children: item.children && item.children.length > 0 ? this.transformLookupData(item.children) : []
      }))
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

        const res = await request.post('/authorization/list', params)
        if (res.code === 200) {
          this.tableData = res.data?.list || []
          this.pagination.total = res.data?.total || 0
        }
      } catch (error) {
        console.error('加载数据失败:', error)
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
    getLookupName(code, options) {
      // 支持树形结构递归查找
      const findNode = (nodes, targetCode) => {
        for (const node of nodes) {
          if (node.code === targetCode) return node.name
          if (node.children && node.children.length > 0) {
            const found = findNode(node.children, targetCode)
            if (found) return found
          }
        }
        return null
      }
      return findNode(options, code) || code
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
    toggleTreeNodeSelect(node) {
      const toggleNodeAndChildren = (n, shouldCheck) => {
        const index = this.queryParams.applicableRegion.indexOf(n.code)
        if (shouldCheck && index === -1) this.queryParams.applicableRegion.push(n.code)
        else if (!shouldCheck && index > -1) this.queryParams.applicableRegion.splice(index, 1)
        if (n.children) n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck))
      }
      toggleNodeAndChildren(node, !this.queryParams.applicableRegion.includes(node.code))
    },
    toggleOrgTreeNode(node) {
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
    handleQuery() { this.pagination.pageNum = 1; this.loadTableData() },
    handleReset() {
      Object.assign(this.queryParams, { name: '', authTargetLevel: [], applicableRegion: [], authPublishLevel: [], authPublishOrg: [], publishYear: null, status: '' })
      this.pagination.pageNum = 1
      this.loadTableData()
    },
    handleCreate() { window.location.href = '#/AuthLetterDetail' },
    handleUpdate() {
      if (this.selectedRows.length === 0) { alert('请先选择数据'); return }
      if (this.selectedRows.length > 1) { alert('只能选择一条数据'); return }
      window.location.href = `#/AuthLetterDetail?id=${this.selectedRows[0]}`
    },
    async handleActivate() {
      if (this.selectedRows.length === 0) { alert('请先选择数据'); return }
      if (!confirm(`确定要将选中的 ${this.selectedRows.length} 条数据生效吗？`)) return
      try {
        for (const id of this.selectedRows) {
          await request.post(`/authorization/activate?id=${id}`)
        }
        alert('操作成功')
        this.selectedRows = []; this.selectAll = false; this.loadTableData()
      } catch (error) { alert('操作失败') }
    },
    async handleDeactivate() {
      if (this.selectedRows.length === 0) { alert('请先选择数据'); return }
      if (!confirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为失效吗？`)) return
      try {
        for (const id of this.selectedRows) {
          await request.post(`/authorization/deactivate?id=${id}`)
        }
        alert('操作成功')
        this.selectedRows = []; this.selectAll = false; this.loadTableData()
      } catch (error) { alert('操作失败') }
    },
    async handleDelete() {
      if (this.selectedRows.length === 0) { alert('请先选择数据'); return }
      if (!confirm(`确定要删除选中的 ${this.selectedRows.length} 条数据吗？`)) return
      try {
        for (const id of this.selectedRows) {
          await request.delete(`/authorization/delete?id=${id}`)
        }
        alert('删除成功')
        this.selectedRows = []; this.selectAll = false; this.loadTableData()
      } catch (error) { alert('删除失败') }
    },
    goToDetail(id) { window.location.href = `#/AuthLetterDetail?id=${id}` },
    handleClickOutside(event) {
      if (!event.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
        this.activeDropdown = ''
      }
    },
    getStatusText(status) {
      const map = { 'DRAFT': '草稿', 'PUBLISHED': '已发布', 'INVALID': '已失效' }
      return map[status] || status
    },
    formatTime(time) {
      if (!time) return '-'
      return time.replace('T', ' ').substring(0, 19)
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }
.auth-letter-list-page { padding: 20px; background-color: #f5f7fa; min-height: 100vh; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 14px; color: #333; }
.query-card { background: #fff; border-radius: 4px; padding: 20px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1); }
.query-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; max-width: 1400px; }
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
.multi-select-trigger, .tree-select-trigger, .select-trigger, .year-select-trigger { height: 32px; padding: 0 30px 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; position: relative; overflow: hidden; }
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.placeholder { color: #c0c4cc; }
.selected-tags { display: flex; flex-wrap: wrap; gap: 4px; overflow: hidden; }
.tag { background: #f0f2f5; padding: 2px 6px; border-radius: 3px; font-size: 12px; display: flex; align-items: center; gap: 4px; white-space: nowrap; }
.tag-close { cursor: pointer; color: #909399; }
.tag-close:hover { color: #409eff; }
.multi-select-dropdown, .tree-select-dropdown, .select-dropdown, .year-select-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #fff; border: 1px solid #dcdfe6; border-radius: 4px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); z-index: 100; max-height: 250px; overflow-y: auto; margin-top: 4px; }
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
.status-invalid { background: #fef0f0; color: #f56c6c; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 10px; margin-top: 16px; font-size: 13px; color: #606266; }
.pagination-size { height: 28px; padding: 0 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.pagination-btn { padding: 4px 12px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination-btn:disabled { color: #c0c4cc; cursor: not-allowed; }
</style>