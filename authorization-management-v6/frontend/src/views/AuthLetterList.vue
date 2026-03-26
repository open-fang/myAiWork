<template>
  <div class="page-container">
    <h1 class="page-title">授权书列表</h1>

    <!-- 查询条件 -->
    <div class="search-form">
      <div class="form-row">
        <div class="form-item">
          <label>授权书名称</label>
          <input type="text" v-model="searchForm.name" placeholder="请输入授权书名称" />
        </div>
        <div class="form-item">
          <label>授权对象层级</label>
          <custom-select
            v-model="searchForm.authObjectLevel"
            :options="lookupData.authObjectLevel"
            placeholder="请选择授权对象层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label>适用区域</label>
          <tree-select
            v-model="searchForm.applicableRegion"
            :options="lookupData.applicableRegion"
            placeholder="请选择适用区域"
            multiple
          />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <label>授权发布层级</label>
          <custom-select
            v-model="searchForm.authPublishLevel"
            :options="lookupData.authPublishLevel"
            placeholder="请选择授权发布层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label>授权发布组织</label>
          <tree-select
            v-model="searchForm.authPublishOrg"
            :options="lookupData.authPublishOrg"
            placeholder="请选择授权发布组织"
            multiple
          />
        </div>
        <div class="form-item">
          <label>发布年份</label>
          <input type="number" v-model="searchForm.publishYear" placeholder="请选择年份" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <label>状态</label>
          <custom-select
            v-model="searchForm.status"
            :options="statusOptions"
            placeholder="请选择状态"
          />
        </div>
      </div>
      <div class="form-row form-buttons">
        <button class="btn btn-primary" @click="handleSearch">查询</button>
        <button class="btn" @click="handleReset">重置</button>
      </div>
    </div>

    <!-- 功能按钮 -->
    <div class="table-toolbar">
      <button class="btn btn-primary" @click="handleCreate">新建授权书</button>
      <button class="btn" @click="handleBatchOperation('update')" :disabled="selectedRows.length === 0">更新</button>
      <button class="btn" @click="handleBatchOperation('publish')" :disabled="selectedRows.length === 0">生效</button>
      <button class="btn" @click="handleBatchOperation('invalidate')" :disabled="selectedRows.length === 0">失效</button>
      <button class="btn btn-danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">删除</button>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" @change="handleSelectAll" v-model="isAllSelected" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>授权书名称</th>
            <th>状态</th>
            <th>授权对象层级</th>
            <th>适用区域</th>
            <th>授权发布层级</th>
            <th>授权发布组织</th>
            <th>发布年份</th>
            <th>创建人</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in tableData" :key="item.id">
            <td><input type="checkbox" v-model="selectedRows" :value="item.id" /></td>
            <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
            <td>
              <span v-if="item.status === 'DRAFT'" class="icon-edit" @click="handleEdit(item)">✏️</span>
            </td>
            <td>
              <span class="link" @click="handleView(item)">{{ item.name }}</span>
            </td>
            <td>
              <span :class="['status-tag', getStatusClass(item.status)]">{{ getStatusText(item.status) }}</span>
            </td>
            <td>{{ formatArray(item.authObjectLevel) }}</td>
            <td>{{ formatArray(item.applicableRegion) }}</td>
            <td>{{ formatArray(item.authPublishLevel) }}</td>
            <td>{{ formatArray(item.authPublishOrg) }}</td>
            <td>{{ item.publishYear }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
          </tr>
          <tr v-if="tableData.length === 0">
            <td colspan="12" style="text-align: center; color: #999;">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <span>共 {{ total }} 条</span>
      <select v-model="pageSize" @change="handlePageSizeChange">
        <option :value="10">10条/页</option>
        <option :value="30">30条/页</option>
        <option :value="50">50条/页</option>
      </select>
      <button :disabled="pageNum === 1" @click="handlePageChange(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} 页</span>
      <button :disabled="pageNum * pageSize >= total" @click="handlePageChange(pageNum + 1)">下一页</button>
    </div>

    <!-- 自定义下拉选择器组件 -->
    <custom-select-popup
      v-if="showSelectPopup"
      :options="currentSelectOptions"
      :multiple="currentSelectMultiple"
      :value="currentSelectValue"
      @close="showSelectPopup = false"
      @confirm="handleSelectConfirm"
    />
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
    multiple: { type: Boolean, default: false }
  },
  data() {
    return {
      isOpen: false
    }
  },
  mounted() {
    // 点击外部关闭下拉
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  computed: {
    selectedItems() {
      if (!this.value || (Array.isArray(this.value) && this.value.length === 0)) {
        return []
      }
      const values = Array.isArray(this.value) ? this.value : [this.value]
      return values.map(v => {
        const opt = this.options.find(o => o.value === v || o.code === v)
        return { value: v, label: opt ? (opt.label || opt.name) : v }
      })
    }
  },
  methods: {
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    },
    toggleDropdown() {
      this.isOpen = !this.isOpen
    },
    selectOption(option) {
      const val = option.value || option.code
      if (this.multiple) {
        const newValue = Array.isArray(this.value) ? [...this.value] : []
        const index = newValue.indexOf(val)
        if (index > -1) {
          newValue.splice(index, 1)
        } else {
          newValue.push(val)
        }
        this.$emit('input', newValue)
      } else {
        this.$emit('input', val)
        this.isOpen = false
      }
    },
    isSelected(option) {
      const val = option.value || option.code
      if (this.multiple && Array.isArray(this.value)) {
        return this.value.includes(val)
      }
      return this.value === val
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
      <div class="select-trigger" @click="toggleDropdown">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span v-if="multiple" class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown" v-if="isOpen">
        <div
          class="select-option"
          v-for="opt in options"
          :key="opt.value || opt.code"
          @click.stop="selectOption(opt)"
        >
          <input v-if="multiple" type="checkbox" :checked="isSelected(opt)" class="option-checkbox" />
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
    multiple: { type: Boolean, default: true }
  },
  data() {
    return {
      isOpen: false,
      expandedKeys: []
    }
  },
  mounted() {
    // 点击外部关闭下拉
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
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
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    },
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
    toggleDropdown() {
      this.isOpen = !this.isOpen
    },
    toggleExpand(code, e) {
      e && e.stopPropagation()
      const index = this.expandedKeys.indexOf(code)
      if (index > -1) {
        this.expandedKeys.splice(index, 1)
      } else {
        this.expandedKeys.push(code)
      }
    },
    isExpanded(code) {
      return this.expandedKeys.includes(code)
    },
    isChecked(code) {
      return this.value && this.value.includes(code)
    },
    toggleCheck(code, e) {
      e && e.stopPropagation()
      const newValue = this.value ? [...this.value] : []
      const index = newValue.indexOf(code)
      if (index > -1) {
        newValue.splice(index, 1)
      } else {
        newValue.push(code)
      }
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
      <div class="select-trigger" @click="toggleDropdown">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown tree-select-dropdown" v-if="isOpen">
        <div v-for="node in options" :key="node.code">
          <div class="tree-node">
            <div class="tree-node-content" @click.stop="toggleCheck(node.code)">
              <span class="tree-toggle" v-if="node.children && node.children.length > 0" @click.stop="toggleExpand(node.code, $event)">
                {{ isExpanded(node.code) ? '▼' : '▶' }}
              </span>
              <span v-else class="tree-toggle-placeholder"></span>
              <input type="checkbox" :checked="isChecked(node.code)" @click.stop="toggleCheck(node.code)" class="option-checkbox" />
              <span>{{ node.name }}</span>
            </div>
            <div class="tree-children" v-if="node.children && isExpanded(node.code)">
              <div v-for="child in node.children" :key="child.code">
                <div class="tree-node">
                  <div class="tree-node-content" @click.stop="toggleCheck(child.code)">
                    <span class="tree-toggle" v-if="child.children && child.children.length > 0" @click.stop="toggleExpand(child.code, $event)">
                      {{ isExpanded(child.code) ? '▼' : '▶' }}
                    </span>
                    <span v-else class="tree-toggle-placeholder"></span>
                    <input type="checkbox" :checked="isChecked(child.code)" @click.stop="toggleCheck(child.code)" class="option-checkbox" />
                    <span>{{ child.name }}</span>
                  </div>
                  <div class="tree-children" v-if="child.children && isExpanded(child.code)">
                    <div v-for="grandChild in child.children" :key="grandChild.code" class="tree-node">
                      <div class="tree-node-content" @click.stop="toggleCheck(grandChild.code)">
                        <span class="tree-toggle-placeholder"></span>
                        <input type="checkbox" :checked="isChecked(grandChild.code)" @click.stop="toggleCheck(grandChild.code)" class="option-checkbox" />
                        <span>{{ grandChild.name }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
}

export default {
  name: 'AuthLetterList',
  components: {
    CustomSelect,
    TreeSelect
  },
  data() {
    return {
      searchForm: {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        status: ''
      },
      tableData: [],
      selectedRows: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      isAllSelected: false,
      showSelectPopup: false,
      currentSelectOptions: [],
      currentSelectMultiple: false,
      currentSelectValue: null,
      lookupData: {
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: []
      },
      statusOptions: [
        { value: 'DRAFT', label: '草稿' },
        { value: 'PUBLISHED', label: '已发布' },
        { value: 'INVALID', label: '已失效' }
      ]
    }
  },
  created() {
    this.loadLookupData()
    this.loadTableData()
  },
  methods: {
    async loadLookupData() {
      try {
        const types = ['AUTH_OBJECT_LEVEL', 'APPLICABLE_REGION', 'AUTH_PUBLISH_LEVEL', 'AUTH_PUBLISH_ORG']
        for (const type of types) {
          const res = await http.get(`/lookup-values/${type}`)
          if (res.code === 200 && res.data) {
            const key = type.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
              .replace('Auth', '').replace('Applicable', '').replace('Publish', '')
            if (type === 'AUTH_OBJECT_LEVEL') {
              this.lookupData.authObjectLevel = res.data.map(item => ({ value: item.valueCode, label: item.valueName }))
            } else if (type === 'APPLICABLE_REGION') {
              this.lookupData.applicableRegion = this.buildTree(res.data)
            } else if (type === 'AUTH_PUBLISH_LEVEL') {
              this.lookupData.authPublishLevel = res.data.map(item => ({ value: item.valueCode, label: item.valueName }))
            } else if (type === 'AUTH_PUBLISH_ORG') {
              this.lookupData.authPublishOrg = this.buildTree(res.data)
            }
          }
        }
      } catch (e) {
        console.error('加载下拉数据失败', e)
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
    async loadTableData() {
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.searchForm.name) params.name = this.searchForm.name
        if (this.searchForm.status) params.status = this.searchForm.status
        if (this.searchForm.publishYear) params.publishYear = this.searchForm.publishYear
        if (this.searchForm.authObjectLevel.length > 0) params.authObjectLevel = this.searchForm.authObjectLevel.join(',')
        if (this.searchForm.applicableRegion.length > 0) params.applicableRegion = this.searchForm.applicableRegion.join(',')
        if (this.searchForm.authPublishLevel.length > 0) params.authPublishLevel = this.searchForm.authPublishLevel.join(',')
        if (this.searchForm.authPublishOrg.length > 0) params.authPublishOrg = this.searchForm.authPublishOrg.join(',')

        const res = await http.get('/auth-letters', params)
        if (res.code === 200) {
          this.tableData = res.data.list || []
          this.total = res.data.total || 0
        }
      } catch (e) {
        console.error('加载列表失败', e)
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.loadTableData()
    },
    handleReset() {
      this.searchForm = {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        status: ''
      }
      this.handleSearch()
    },
    handleCreate() {
      window.location.hash = '#/AuthLetterDetail?mode=create'
    },
    handleEdit(row) {
      window.location.hash = '#/AuthLetterDetail?id=' + row.id + '&mode=edit'
    },
    handleView(row) {
      window.location.hash = '#/AuthLetterDetail?id=' + row.id
    },
    handleSelectAll() {
      if (this.isAllSelected) {
        this.selectedRows = this.tableData.map(item => item.id)
      } else {
        this.selectedRows = []
      }
    },
    async handleBatchOperation(operation) {
      if (this.selectedRows.length === 0) return
      if (!confirm(`确定要执行此操作吗？`)) return
      try {
        const operationMap = {
          'update': 'UPDATE',
          'publish': 'PUBLISH',
          'invalidate': 'INVALIDATE'
        }
        await http.post('/auth-letters/batch', {
          ids: this.selectedRows,
          operation: operationMap[operation] || operation
        })
        this.selectedRows = []
        this.loadTableData()
      } catch (e) {
        console.error('批量操作失败', e)
      }
    },
    async handleBatchDelete() {
      if (this.selectedRows.length === 0) return
      if (!confirm('确定要删除选中的数据吗？')) return
      try {
        await http.post('/auth-letters/batch', {
          ids: this.selectedRows,
          operation: 'DELETE'
        })
        this.selectedRows = []
        this.loadTableData()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    handlePageChange(page) {
      this.pageNum = page
      this.loadTableData()
    },
    handlePageSizeChange() {
      this.pageNum = 1
      this.loadTableData()
    },
    getStatusClass(status) {
      const map = {
        'DRAFT': 'status-draft',
        'PUBLISHED': 'status-published',
        'INVALID': 'status-invalid'
      }
      return map[status] || ''
    },
    getStatusText(status) {
      const map = {
        'DRAFT': '草稿',
        'PUBLISHED': '已发布',
        'INVALID': '已失效'
      }
      return map[status] || status
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
    handleSelectConfirm(value) {
      this.showSelectPopup = false
    }
  }
}
</script>

<style>
/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #f5f5f5;
}

/* 页面容器 */
.page-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 40px);
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

/* 搜索表单 */
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 4px;
}

/* 表单行 */
.form-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16px;
  gap: 16px;
}

.form-item {
  display: flex;
  align-items: center;
  flex: 0 0 calc(33.33% - 11px);
  min-width: 250px;
}

.form-item label {
  width: 100px;
  text-align: right;
  margin-right: 8px;
  color: #666;
  flex-shrink: 0;
}

.form-item label.required::before {
  content: '*';
  color: #ff4d4f;
  margin-right: 4px;
}

.form-item input,
.form-item textarea {
  flex: 1;
  height: 32px;
  padding: 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #1890ff;
  outline: none;
}

/* 按钮样式 */
.btn {
  display: inline-block;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  background: #fff;
  cursor: pointer;
  margin-right: 8px;
}

.btn-primary {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #fff1f0;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表格样式 */
.table-wrapper {
  margin-top: 16px;
}

.table-toolbar {
  margin-bottom: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
}

th, td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background: #fafafa;
  font-weight: 500;
}

tr:hover {
  background: #f5f5f5;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  gap: 8px;
}

.pagination span {
  color: #666;
}

.pagination button {
  padding: 4px 12px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination select {
  height: 28px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

/* 操作图标 */
.icon-edit {
  color: #1890ff;
  cursor: pointer;
  font-size: 16px;
}

.icon-delete {
  color: #ff4d4f;
  cursor: pointer;
  font-size: 16px;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-draft {
  background: #f0f0f0;
  color: #666;
}

.status-published {
  background: #e6f7ff;
  color: #1890ff;
}

.status-invalid {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 链接样式 */
.link {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
}

.link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 自定义下拉选择器 */
.custom-select {
  position: relative;
  flex: 1;
}

.select-trigger {
  display: flex;
  align-items: center;
  min-height: 32px;
  padding: 4px 30px 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  position: relative;
  flex-wrap: wrap;
  gap: 4px;
}

.select-trigger:hover {
  border-color: #40a9ff;
}

.select-trigger .arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: 5px solid transparent;
  border-top-color: #999;
}

.select-trigger .placeholder {
  color: #bfbfbf;
}

.select-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  margin: 2px;
}

.select-tag .close {
  margin-left: 4px;
  cursor: pointer;
  font-size: 14px;
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  max-height: 250px;
  overflow-y: auto;
  z-index: 1000;
}

.select-option {
  padding: 6px 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 13px;
}

.select-option:hover {
  background: #f5f5f5;
}

.option-checkbox {
  width: 14px !important;
  height: 14px !important;
  margin-right: 8px !important;
  cursor: pointer;
  flex-shrink: 0;
}

/* 树形选择器 */
.tree-select-dropdown {
  padding: 8px;
}

.tree-node {
  padding: 2px 0;
}

.tree-node-content {
  display: flex;
  align-items: center;
  padding: 4px 6px;
  cursor: pointer;
  border-radius: 4px;
}

.tree-node-content:hover {
  background: #f5f5f5;
}

.tree-toggle {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  font-size: 10px;
  flex-shrink: 0;
}

.tree-toggle:hover {
  color: #1890ff;
}

.tree-toggle-placeholder {
  width: 16px;
  height: 16px;
  display: inline-block;
  flex-shrink: 0;
}

.tree-children {
  padding-left: 16px;
}

/* 按钮区域对齐 */
.form-buttons {
  justify-content: flex-start;
  padding-left: 108px;
}

.form-buttons .btn {
  margin-right: 8px;
}
</style>