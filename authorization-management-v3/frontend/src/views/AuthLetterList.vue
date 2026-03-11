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
          <tr v-for="(row, index) in tableData" :key="row.id">
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
        <button class="pagination-btn" :disabled="pagination.pageNum === 1" @click="pagination.pageNum--">上一页</button>
        <span class="pagination-page">{{ pagination.pageNum }} / {{ totalPages }}</span>
        <button class="pagination-btn" :disabled="pagination.pageNum >= totalPages" @click="pagination.pageNum++">下一页</button>
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
        let hasChecked = false
        let hasUnchecked = false
        for (const child of children) {
          if (props.selectedCodes.includes(child.code)) {
            hasChecked = true
          } else {
            hasUnchecked = true
          }
          if (child.children) {
            const result = checkChildren(child.children)
            hasChecked = hasChecked || result.hasChecked
            hasUnchecked = hasUnchecked || result.hasUnchecked
          }
        }
        return { hasChecked, hasUnchecked }
      }
      const result = checkChildren(props.node.children)
      return result.hasChecked && result.hasUnchecked
    })

    const toggleCheck = () => {
      emit('toggle', props.node)
    }

    const toggleExpand = () => {
      expanded.value = !expanded.value
    }

    return () => h('div', { class: 'tree-node' }, [
      h('div', {
        class: 'tree-node-content',
        style: { paddingLeft: (props.node.level || 0) * 20 + 'px' }
      }, [
        hasChildren.value ? h('span', {
          class: 'tree-expand-icon' + (expanded.value ? ' expanded' : ''),
          onClick: toggleExpand
        }, '▶') : h('span', { class: 'tree-expand-placeholder' }),
        h('span', {
          class: 'tree-checkbox' + (isChecked.value ? ' checked' : '') + (isIndeterminate.value ? ' indeterminate' : ''),
          onClick: toggleCheck
        }),
        h('span', { class: 'tree-node-label', onClick: toggleCheck }, props.node.name)
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

// ========== 页面逻辑 ==========
const activeDropdown = ref('')
const selectAll = ref(false)
const selectedRows = ref([])
const jumpPage = ref(1)

const message = reactive({
  show: false,
  type: 'info',
  text: ''
})

const confirmDialog = reactive({
  show: false,
  text: '',
  onConfirm: () => {},
  onCancel: () => {}
})

const queryParams = reactive({
  name: '',
  authTargetLevel: [],
  applicableRegion: [],
  authPublishLevel: [],
  authPublishOrg: [],
  publishYear: null,
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 30
})

const totalPages = computed(() => Math.ceil(pagination.total / pagination.pageSize))

// 下拉选项数据
const authTargetLevelOptions = ref([
  { code: 'ORGANIZATION', name: '机关' },
  { code: 'REGIONAL_DEPT', name: '地区部' },
  { code: 'REPRESENTATIVE_OFFICE', name: '代表处' },
  { code: 'OFFICE', name: '办事处' }
])

const applicableRegionOptions = ref([
  { code: 'EAST', name: '华东' },
  { code: 'NORTH', name: '华北' },
  { code: 'SOUTH', name: '华南' },
  { code: 'WEST', name: '西部' },
  { code: 'CENTRAL', name: '华中' }
])

const authPublishLevelOptions = ref([...authTargetLevelOptions.value])

const statusOptions = ref([
  { value: 'DRAFT', label: '草稿' },
  { value: 'PUBLISHED', label: '已发布' },
  { value: 'EXPIRED', label: '已失效' }
])

const yearOptions = computed(() => {
  const years = []
  const currentYear = new Date().getFullYear()
  for (let i = currentYear; i >= currentYear - 10; i--) {
    years.push(i)
  }
  return years
})

// 组织树数据
const orgTreeData = ref([
  {
    code: 'ORG001',
    name: '总部',
    level: 0,
    children: [
      {
        code: 'ORG002',
        name: '华东区',
        level: 1,
        children: [
          { code: 'ORG003', name: '上海办事处', level: 2 },
          { code: 'ORG004', name: '杭州办事处', level: 2 }
        ]
      },
      {
        code: 'ORG005',
        name: '华北区',
        level: 1,
        children: [
          { code: 'ORG006', name: '北京办事处', level: 2 },
          { code: 'ORG007', name: '天津办事处', level: 2 }
        ]
      }
    ]
  }
])

// 表格数据
const tableData = ref([
  {
    id: 1,
    name: '2024年度销售授权书',
    status: 'DRAFT',
    statusText: '草稿',
    authTargetLevelText: ['机关', '地区部'],
    applicableRegionText: ['华东', '华北'],
    authPublishLevelText: ['机关'],
    authPublishOrgText: ['总部'],
    publishYear: 2024,
    createdBy: 'admin',
    createdAt: '2024-03-10 10:30:00'
  },
  {
    id: 2,
    name: '2023年度采购授权书',
    status: 'PUBLISHED',
    statusText: '已发布',
    authTargetLevelText: ['代表处'],
    applicableRegionText: ['华南'],
    authPublishLevelText: ['地区部'],
    authPublishOrgText: ['华南区'],
    publishYear: 2023,
    createdBy: 'admin',
    createdAt: '2023-12-15 14:20:00'
  },
  {
    id: 3,
    name: '2022年度财务授权书',
    status: 'EXPIRED',
    statusText: '已失效',
    authTargetLevelText: ['办事处'],
    applicableRegionText: ['西部'],
    authPublishLevelText: ['代表处'],
    authPublishOrgText: ['西部区'],
    publishYear: 2022,
    createdBy: 'admin',
    createdAt: '2022-06-20 09:15:00'
  }
])

// ========== 下拉选择相关 ==========
function toggleSelect(name) {
  activeDropdown.value = activeDropdown.value === name ? '' : name
}

function toggleMultiSelect(field, value) {
  const index = queryParams[field].indexOf(value)
  if (index > -1) {
    queryParams[field].splice(index, 1)
  } else {
    queryParams[field].push(value)
  }
}

function removeSelected(field, value) {
  const index = queryParams[field].indexOf(value)
  if (index > -1) {
    queryParams[field].splice(index, 1)
  }
}

function selectYear(year) {
  queryParams.publishYear = year
  activeDropdown.value = ''
}

function selectStatus(value) {
  queryParams.status = value
  activeDropdown.value = ''
}

function getStatusLabel(value) {
  const item = statusOptions.value.find(s => s.value === value)
  return item ? item.label : ''
}

function getSelectedLabels(codes, options) {
  return codes.map(code => {
    const item = options.find(o => o.code === code)
    return item ? item.name : code
  })
}

function getOrgName(code) {
  const findNode = (nodes, targetCode) => {
    for (const node of nodes) {
      if (node.code === targetCode) return node.name
      if (node.children) {
        const found = findNode(node.children, targetCode)
        if (found) return found
      }
    }
    return null
  }
  return findNode(orgTreeData.value, code) || code
}

// ========== 树形选择相关 ==========
function toggleTreeNode(node) {
  const toggleNodeAndChildren = (n, shouldCheck) => {
    const index = queryParams.authPublishOrg.indexOf(n.code)
    if (shouldCheck && index === -1) {
      queryParams.authPublishOrg.push(n.code)
    } else if (!shouldCheck && index > -1) {
      queryParams.authPublishOrg.splice(index, 1)
    }
    if (n.children) {
      n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck))
    }
  }

  const isChecked = queryParams.authPublishOrg.includes(node.code)
  toggleNodeAndChildren(node, !isChecked)
}

// ========== 表格相关 ==========
function handleSelectAll() {
  if (selectAll.value) {
    selectedRows.value = tableData.value.map(row => row.id)
  } else {
    selectedRows.value = []
  }
}

function handleSizeChange() {
  pagination.pageNum = 1
}

function handleJumpPage() {
  if (jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    pagination.pageNum = jumpPage.value
  }
}

function formatArrayText(arr) {
  if (!arr || arr.length === 0) return '-'
  return arr.join('、')
}

// ========== 消息提示 ==========
function showMessage(text, type = 'info') {
  message.text = text
  message.type = type
  message.show = true
  setTimeout(() => {
    message.show = false
  }, 3000)
}

// ========== 确认对话框 ==========
function showConfirm(text) {
  return new Promise((resolve) => {
    confirmDialog.show = true
    confirmDialog.text = text
    confirmDialog.onConfirm = () => {
      confirmDialog.show = false
      resolve(true)
    }
    confirmDialog.onCancel = () => {
      confirmDialog.show = false
      resolve(false)
    }
  })
}

// ========== 业务操作 ==========
function handleQuery() {
  pagination.pageNum = 1
  showMessage('查询成功', 'success')
}

function handleReset() {
  queryParams.name = ''
  queryParams.authTargetLevel = []
  queryParams.applicableRegion = []
  queryParams.authPublishLevel = []
  queryParams.authPublishOrg = []
  queryParams.publishYear = null
  queryParams.status = ''
  pagination.pageNum = 1
  showMessage('已重置查询条件', 'info')
}

function checkSelection() {
  if (selectedRows.value.length === 0) {
    showMessage('请先选择数据', 'warning')
    return false
  }
  return true
}

function handleCreate() {
  showMessage('新建授权书功能待实现', 'info')
}

function handleUpdate() {
  if (!checkSelection()) return
  showMessage('更新功能待实现', 'info')
}

async function handleActivate() {
  if (!checkSelection()) return
  const confirmed = await showConfirm(`确定要将选中的 ${selectedRows.value.length} 条数据发布生效吗？`)
  if (confirmed) {
    showMessage('操作成功', 'success')
  }
}

async function handleDeactivate() {
  if (!checkSelection()) return
  const confirmed = await showConfirm(`确定要将选中的 ${selectedRows.value.length} 条数据设为失效吗？`)
  if (confirmed) {
    showMessage('操作成功', 'success')
  }
}

async function handleDelete() {
  if (!checkSelection()) return
  const confirmed = await showConfirm(`确定要删除选中的 ${selectedRows.value.length} 条数据吗？`)
  if (confirmed) {
    showMessage('删除成功', 'success')
  }
}

function goToDetail(id) {
  showMessage(`跳转到详情页: ID=${id}`, 'info')
}

// ========== 生命周期 ==========
function handleClickOutside(event) {
  if (!event.target.closest('.multi-select-wrapper, .tree-select-wrapper, .select-wrapper, .year-select-wrapper')) {
    activeDropdown.value = ''
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.auth-letter-list-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  color: #333;
}

/* 查询卡片 */
.query-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.query-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.form-item {
  display: flex;
  align-items: center;
}

.form-label {
  width: 100px;
  text-align: right;
  color: #666;
  flex-shrink: 0;
  margin-right: 12px;
}

.form-input {
  flex: 1;
  height: 32px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  outline: none;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #409eff;
}

.query-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

/* 按钮样式 */
.btn {
  padding: 8px 16px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background: #fff;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn:hover {
  opacity: 0.9;
}

.btn-primary {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

.btn-success {
  background: #67c23a;
  border-color: #67c23a;
  color: #fff;
}

.btn-warning {
  background: #e6a23c;
  border-color: #e6a23c;
  color: #fff;
}

.btn-danger {
  background: #f56c6c;
  border-color: #f56c6c;
  color: #fff;
}

.btn-default {
  background: #fff;
  color: #191919;
  border-color: #dcdfe6;
}

/* 下拉选择 */
.multi-select-wrapper,
.tree-select-wrapper,
.select-wrapper,
.year-select-wrapper {
  flex: 1;
  position: relative;
}

.multi-select-trigger,
.tree-select-trigger,
.select-trigger,
.year-select-trigger {
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

.multi-select-trigger:hover,
.tree-select-trigger:hover,
.select-trigger:hover,
.year-select-trigger:hover {
  border-color: #c0c4cc;
}

.arrow {
  position: absolute;
  right: 10px;
  font-size: 12px;
  color: #c0c4cc;
}

.placeholder {
  color: #c0c4cc;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag {
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.tag-close {
  cursor: pointer;
  color: #909399;
}

.tag-close:hover {
  color: #409eff;
}

.multi-select-dropdown,
.tree-select-dropdown,
.select-dropdown,
.year-select-dropdown {
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

.select-option,
.year-option {
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}

.select-option:hover,
.year-option:hover {
  background: #f5f7fa;
}

.checkbox {
  width: 14px;
  height: 14px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  display: inline-block;
  position: relative;
}

.checkbox.checked {
  background: #409eff;
  border-color: #409eff;
}

.checkbox.checked::after {
  content: '✓';
  color: #fff;
  font-size: 10px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* 树形选择 */
.tree-node-content {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  cursor: pointer;
}

.tree-node-content:hover {
  background: #f5f7fa;
}

.tree-expand-icon {
  width: 16px;
  font-size: 10px;
  color: #c0c4cc;
  transition: transform 0.2s;
}

.tree-expand-icon.expanded {
  transform: rotate(90deg);
}

.tree-expand-placeholder {
  width: 16px;
}

.tree-checkbox {
  width: 14px;
  height: 14px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  margin-right: 8px;
  position: relative;
}

.tree-checkbox.checked {
  background: #409eff;
  border-color: #409eff;
}

.tree-checkbox.checked::after {
  content: '✓';
  color: #fff;
  font-size: 10px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.tree-checkbox.indeterminate {
  background: #409eff;
  border-color: #409eff;
}

.tree-checkbox.indeterminate::after {
  content: '-';
  color: #fff;
  font-size: 12px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.tree-node-label {
  font-size: 14px;
}

/* 功能按钮区 */
.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

/* 表格 */
.table-card {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.data-table th {
  background: #f5f7fa;
  font-weight: 600;
  color: #909399;
}

.data-table tbody tr:hover {
  background: #f5f7fa;
}

.col-checkbox {
  width: 50px;
  text-align: center;
}

.col-index {
  width: 60px;
  text-align: center;
}

.col-action {
  width: 60px;
  text-align: center;
  white-space: nowrap;
}

.col-status {
  width: 80px;
  text-align: center;
}

.col-year,
.col-user {
  width: 100px;
  text-align: center;
}

.col-time {
  width: 160px;
  text-align: center;
}

.icon-btn {
  cursor: pointer;
  font-size: 16px;
}

.icon-btn:hover {
  opacity: 0.7;
}

.link {
  color: #409eff;
  cursor: pointer;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-draft {
  background: #f4f4f5;
  color: #909399;
}

.status-published {
  background: #f0f9eb;
  color: #67c23a;
}

.status-expired {
  background: #fef0f0;
  color: #f56c6c;
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  font-size: 13px;
  color: #606266;
}

.pagination-size {
  height: 28px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.pagination-btn {
  padding: 4px 12px;
  border: 1px solid #dcdfe6;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-btn:disabled {
  color: #c0c4cc;
  cursor: not-allowed;
}

.pagination-jump input {
  width: 50px;
  height: 28px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  text-align: center;
  margin: 0 4px;
}

/* 消息提示 */
.message-box {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 4px;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-10px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}

.message-info {
  background: #edf2fc;
  color: #909399;
  border: 1px solid #dcdfe6;
}

.message-success {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

.message-warning {
  background: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #faecd8;
}

.message-error {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fde2e2;
}

/* 对话框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-dialog {
  background: #fff;
  border-radius: 8px;
  min-width: 400px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-weight: 600;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>