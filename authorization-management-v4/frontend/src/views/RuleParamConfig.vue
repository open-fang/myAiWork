<template>
  <div class="rule-param-config-page">
    <!-- 查询条件区 -->
    <div class="query-card">
      <div class="query-form">
        <div class="form-row">
          <div class="form-item">
            <label class="form-label">名称</label>
            <input type="text" v-model="queryParams.name" class="form-input" placeholder="请输入名称" />
          </div>
          <div class="form-item">
            <label class="form-label">名称英文</label>
            <input type="text" v-model="queryParams.nameEn" class="form-input" placeholder="请输入名称英文" />
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <div class="select-wrapper">
              <div class="select-trigger" @click="toggleSelect('status')">
                <span>{{ getStatusLabel(queryParams.status) || '请选择' }}</span>
                <span class="arrow">▼</span>
              </div>
              <div class="select-dropdown" v-show="activeDropdown === 'status'">
                <div class="select-option" @click="selectStatus('')">全部</div>
                <div class="select-option" v-for="item in statusOptions" :key="item.value" @click="selectStatus(item.value)">{{ item.label }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="query-buttons">
        <button class="btn btn-primary" @click="handleQuery">查询</button>
      </div>
    </div>

    <!-- 功能按钮区 -->
    <div class="action-buttons">
      <button class="btn btn-primary" @click="openModal()">新建</button>
      <button class="btn btn-success" @click="handleActivate">生效</button>
      <button class="btn btn-warning" @click="handleDeactivate">失效</button>
    </div>

    <!-- 数据表格区 -->
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th class="col-checkbox"><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
            <th class="col-index">序号</th>
            <th class="col-action">操作</th>
            <th class="col-name">名称</th>
            <th class="col-name">名称英文</th>
            <th class="col-status">状态</th>
            <th class="col-user">创建人</th>
            <th class="col-time">创建时间</th>
            <th class="col-user">更新人</th>
            <th class="col-time">更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="10" style="text-align: center; padding: 40px; color: #909399;">加载中...</td>
          </tr>
          <tr v-else-if="tableData.length === 0">
            <td colspan="10" style="text-align: center; padding: 40px; color: #909399;">暂无数据</td>
          </tr>
          <tr v-else v-for="(row, index) in tableData" :key="row.id">
            <td class="col-checkbox"><input type="checkbox" v-model="selectedRows" :value="row.id" /></td>
            <td class="col-index">{{ (pagination.pageNum - 1) * pagination.pageSize + index + 1 }}</td>
            <td class="col-action">
              <span class="icon-btn" @click="openModal(row)" title="编辑">✏️</span>
            </td>
            <td class="col-name">{{ row.name }}</td>
            <td class="col-name">{{ row.nameEn }}</td>
            <td class="col-status"><span class="status-tag" :class="'status-' + row.status.toLowerCase()">{{ row.statusText }}</span></td>
            <td class="col-user">{{ row.createdBy || '-' }}</td>
            <td class="col-time">{{ row.createdAt || '-' }}</td>
            <td class="col-user">{{ row.updatedBy || '-' }}</td>
            <td class="col-time">{{ row.updatedAt || '-' }}</td>
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

    <!-- 字段详情弹窗 -->
    <div class="modal-overlay" v-if="modal.visible" @click.self="modal.visible = false">
      <div class="modal-dialog">
        <div class="modal-header">
          <span class="modal-title">{{ modal.isEdit ? '编辑规则参数' : '新建规则参数' }}</span>
          <span class="modal-close" @click="modal.visible = false">×</span>
        </div>
        <div class="modal-body">
          <div class="modal-form">
            <div class="form-row">
              <div class="form-item">
                <label class="form-label required">名称</label>
                <input type="text" v-model="modal.data.name" class="form-input" placeholder="请输入名称" />
              </div>
              <div class="form-item">
                <label class="form-label required">名称英文</label>
                <input type="text" v-model="modal.data.nameEn" class="form-input" placeholder="请输入名称英文" />
              </div>
            </div>
            <div class="form-row" v-for="(mapping, index) in modal.data.businessMappings" :key="index">
              <div class="form-item">
                <label class="form-label">业务对象</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('businessObject' + index)">
                    <span>{{ mapping.businessObject || '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'businessObject' + index">
                    <div class="select-option" v-for="item in businessObjectOptions" :key="item.code" @click="selectBusinessObject(index, item.code)">{{ item.name }}</div>
                  </div>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label">取值逻辑</label>
                <input type="text" v-model="mapping.valueLogic" class="form-input" placeholder="请输入取值逻辑" />
              </div>
              <div class="form-item action">
                <span class="icon-btn add" @click="addBusinessMapping" title="添加">➕</span>
                <span class="icon-btn remove" v-if="modal.data.businessMappings.length > 1" @click="removeBusinessMapping(index)" title="删除">➖</span>
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label class="form-label required">是否生效</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('isActive')">
                    <span>{{ modal.data.status === 'ACTIVE' ? '是' : '否' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'isActive'">
                    <div class="select-option" @click="modal.data.status = 'ACTIVE'">是</div>
                    <div class="select-option" @click="modal.data.status = 'INACTIVE'">否</div>
                  </div>
                </div>
              </div>
              <div class="form-item">
                <label class="form-label required">数据类型</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('dataType')">
                    <span>{{ getDataTypeLabel(modal.data.dataType) || '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'dataType'">
                    <div class="select-option" v-for="item in dataTypeOptions" :key="item.value" @click="modal.data.dataType = item.value">{{ item.label }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="modal.visible = false">取消</button>
          <button class="btn btn-primary" @click="saveRuleParam">确定</button>
        </div>
      </div>
    </div>

    <!-- 消息提示 -->
    <div class="message-box" v-if="message.show" :class="'message-' + message.type">
      {{ message.text }}
    </div>

    <!-- 确认对话框 -->
    <div class="modal-overlay" v-if="confirmDialog.show" @click.self="confirmDialog.onCancel">
      <div class="modal-dialog small">
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
// ========== API ==========
const BASE_URL = '/api'
const request = (url, options = {}) => {
  const config = { headers: { 'Content-Type': 'application/json' }, ...options }
  if (config.body && typeof config.body === 'object') config.body = JSON.stringify(config.body)
  return fetch(BASE_URL + url, config).then(r => r.json())
}
const api = {
  getRuleParamList: (params) => {
    const queryStr = Object.entries(params)
      .filter(([, v]) => v !== null && v !== undefined && v !== '')
      .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
      .join('&')
    return request(`/rule-params?${queryStr}`)
  },
  getRuleParam: (id) => request(`/rule-params/${id}`),
  createRuleParam: (data) => request('/rule-params', { method: 'POST', body: data }),
  updateRuleParam: (id, data) => request(`/rule-params/${id}`, { method: 'PUT', body: data }),
  batchActivate: (ids) => request('/rule-params/batch/activate', { method: 'PUT', body: { ids } }),
  batchDeactivate: (ids) => request('/rule-params/batch/deactivate', { method: 'PUT', body: { ids } })
}

export default {
  name: 'RuleParamConfig',
  data() {
    return {
      loading: false,
      activeDropdown: '',
      selectAll: false,
      selectedRows: [],
      message: { show: false, type: 'info', text: '' },
      confirmDialog: { show: false, text: '', onConfirm: () => {}, onCancel: () => {} },
      queryParams: { name: '', nameEn: '', status: '', pageNum: 1, pageSize: 10 },
      pagination: { pageNum: 1, pageSize: 10, total: 0 },
      statusOptions: [
        { value: 'ACTIVE', label: '生效' },
        { value: 'INACTIVE', label: '失效' }
      ],
      dataTypeOptions: [
        { value: 'TEXT', label: '文本' },
        { value: 'NUMBER', label: '数值' },
        { value: 'COMPARE_FIELD', label: '比对字段' },
        { value: 'RATIO', label: '比率' }
      ],
      businessObjectOptions: [
        { code: 'CONTRACT', name: '合同' },
        { code: 'ORDER', name: '订单' },
        { code: 'PROJECT', name: '项目' },
        { code: 'PAYMENT', name: '付款' }
      ],
      tableData: [],
      modal: {
        visible: false,
        isEdit: false,
        editId: null,
        data: {
          name: '',
          nameEn: '',
          status: 'ACTIVE',
          dataType: '',
          businessMappings: [{ businessObject: '', valueLogic: '' }]
        }
      }
    }
  },
  computed: {
    totalPages() { return Math.ceil(this.pagination.total / this.pagination.pageSize) || 1 }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
    this.loadTableData()
  },
  beforeDestroy() { document.removeEventListener('click', this.handleClickOutside) },
  methods: {
    async loadTableData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pagination.pageNum,
          pageSize: this.pagination.pageSize,
          name: this.queryParams.name || undefined,
          nameEn: this.queryParams.nameEn || undefined,
          status: this.queryParams.status || undefined
        }
        const res = await api.getRuleParamList(params)
        if (res.code === 200) {
          this.tableData = res.data?.list || []
          this.pagination.total = res.data?.total || 0
        } else {
          this.showMessage(res.message || '加载数据失败', 'error')
        }
      } catch (e) {
        this.showMessage('加载数据失败', 'error')
      } finally {
        this.loading = false
      }
    },
    toggleSelect(name) { this.activeDropdown = this.activeDropdown === name ? '' : name },
    selectStatus(value) { this.queryParams.status = value; this.activeDropdown = '' },
    getStatusLabel(value) {
      const item = this.statusOptions.find(s => s.value === value)
      return item ? item.label : ''
    },
    getDataTypeLabel(value) {
      const item = this.dataTypeOptions.find(d => d.value === value)
      return item ? item.label : ''
    },
    selectBusinessObject(index, code) {
      this.modal.data.businessMappings[index].businessObject = code
      this.activeDropdown = ''
    },
    handleSelectAll() { this.selectedRows = this.selectAll ? this.tableData.map(r => r.id) : [] },
    handleSizeChange() { this.pagination.pageNum = 1; this.loadTableData() },
    handlePageChange(page) { this.pagination.pageNum = page; this.loadTableData() },
    handleQuery() { this.pagination.pageNum = 1; this.loadTableData() },
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
    checkSelection() {
      if (this.selectedRows.length === 0) { this.showMessage('请先选择数据', 'warning'); return false }
      return true
    },
    openModal(row = null) {
      this.modal = {
        visible: true,
        isEdit: !!row,
        editId: row ? row.id : null,
        data: {
          name: row ? row.name : '',
          nameEn: row ? row.nameEn : '',
          status: row ? row.status : 'ACTIVE',
          dataType: row ? row.dataType : '',
          businessMappings: row && row.businessMappings ? row.businessMappings : [{ businessObject: '', valueLogic: '' }]
        }
      }
    },
    addBusinessMapping() {
      this.modal.data.businessMappings.push({ businessObject: '', valueLogic: '' })
    },
    removeBusinessMapping(index) {
      this.modal.data.businessMappings.splice(index, 1)
    },
    async saveRuleParam() {
      if (!this.modal.data.name) { this.showMessage('请输入名称', 'warning'); return }
      if (!this.modal.data.nameEn) { this.showMessage('请输入名称英文', 'warning'); return }
      if (!this.modal.data.dataType) { this.showMessage('请选择数据类型', 'warning'); return }
      try {
        let res
        if (this.modal.isEdit) {
          res = await api.updateRuleParam(this.modal.editId, this.modal.data)
        } else {
          res = await api.createRuleParam(this.modal.data)
        }
        if (res.code === 200) {
          this.showMessage('保存成功', 'success')
          this.modal.visible = false
          this.loadTableData()
        } else {
          this.showMessage(res.message || '保存失败', 'error')
        }
      } catch (e) {
        this.showMessage('保存失败', 'error')
      }
    },
    async handleActivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为生效吗？`)
      if (confirmed) {
        try {
          const res = await api.batchActivate(this.selectedRows)
          if (res.code === 200) {
            this.showMessage('操作成功', 'success')
            this.selectedRows = []; this.selectAll = false; this.loadTableData()
          } else {
            this.showMessage(res.message || '操作失败', 'error')
          }
        } catch (e) { this.showMessage('操作失败', 'error') }
      }
    },
    async handleDeactivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为失效吗？`)
      if (confirmed) {
        try {
          const res = await api.batchDeactivate(this.selectedRows)
          if (res.code === 200) {
            this.showMessage('操作成功', 'success')
            this.selectedRows = []; this.selectAll = false; this.loadTableData()
          } else {
            this.showMessage(res.message || '操作失败', 'error')
          }
        } catch (e) { this.showMessage('操作失败', 'error') }
      }
    },
    handleClickOutside(e) {
      if (!e.target.closest('.select-wrapper')) this.activeDropdown = ''
    }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }
.rule-param-config-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 50px); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 14px; color: #333; }
.query-card { background: #fff; border-radius: 4px; padding: 20px; margin-bottom: 16px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.query-form { display: flex; flex-direction: column; gap: 16px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; max-width: 1200px; }
.form-item { display: flex; align-items: center; }
.form-item.action { flex-direction: row; gap: 8px; flex: 0 0 auto; width: auto; }
.form-label { width: 80px; text-align: right; color: #666; flex-shrink: 0; margin-right: 12px; }
.form-label.required::before { content: '*'; color: #f56c6c; margin-right: 4px; }
.form-input { flex: 1; height: 32px; padding: 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; outline: none; }
.form-input:focus { border-color: #409eff; }
.query-buttons { display: flex; justify-content: flex-end; gap: 10px; margin-top: 16px; padding-top: 16px; border-top: 1px solid #eee; }
.btn { padding: 8px 16px; border-radius: 4px; border: 1px solid #dcdfe6; background: #fff; cursor: pointer; font-size: 14px; transition: all 0.2s; }
.btn:hover { opacity: 0.9; }
.btn-primary { background: #409eff; border-color: #409eff; color: #fff; }
.btn-success { background: #67c23a; border-color: #67c23a; color: #fff; }
.btn-warning { background: #e6a23c; border-color: #e6a23c; color: #fff; }
.btn-default { background: #fff; color: #333; }
.select-wrapper { flex: 1; position: relative; }
.select-trigger { height: 32px; padding: 0 30px 0 12px; border: 1px solid #dcdfe6; border-radius: 4px; background: #fff; cursor: pointer; display: flex; align-items: center; position: relative; }
.arrow { position: absolute; right: 10px; font-size: 12px; color: #c0c4cc; }
.select-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #fff; border: 1px solid #dcdfe6; border-radius: 4px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); z-index: 100; max-height: 250px; overflow-y: auto; margin-top: 4px; }
.select-option { padding: 8px 12px; cursor: pointer; }
.select-option:hover { background: #f5f7fa; }
.action-buttons { display: flex; gap: 10px; margin-bottom: 16px; }
.table-card { background: #fff; border-radius: 4px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.data-table { width: 100%; border-collapse: collapse; }
.data-table th, .data-table td { padding: 12px 8px; text-align: left; border-bottom: 1px solid #ebeef5; }
.data-table th { background: #f5f7fa; font-weight: 600; color: #909399; }
.data-table tbody tr:hover { background: #f5f7fa; }
.col-checkbox { width: 50px; text-align: center; }
.col-index { width: 60px; text-align: center; }
.col-action { width: 60px; text-align: center; }
.col-status { width: 80px; text-align: center; }
.col-name { min-width: 120px; }
.col-user { width: 90px; text-align: center; }
.col-time { width: 150px; text-align: center; }
.icon-btn { cursor: pointer; font-size: 14px; }
.icon-btn:hover { opacity: 0.7; }
.icon-btn.add { color: #67c23a; }
.icon-btn.remove { color: #f56c6c; }
.status-tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }
.status-active { background: #f0f9eb; color: #67c23a; }
.status-inactive { background: #fef0f0; color: #f56c6c; }
.pagination { display: flex; align-items: center; justify-content: flex-end; gap: 10px; margin-top: 16px; font-size: 13px; color: #606266; }
.pagination-size { height: 28px; padding: 0 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.pagination-btn { padding: 4px 12px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; cursor: pointer; }
.pagination-btn:disabled { color: #c0c4cc; cursor: not-allowed; }
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-dialog { background: #fff; border-radius: 8px; width: 600px; max-height: 90vh; overflow: hidden; display: flex; flex-direction: column; }
.modal-dialog.small { width: 400px; }
.modal-header { padding: 16px 20px; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center; }
.modal-title { font-weight: 600; font-size: 16px; }
.modal-close { font-size: 20px; color: #909399; cursor: pointer; }
.modal-close:hover { color: #409eff; }
.modal-body { padding: 20px; overflow-y: auto; flex: 1; }
.modal-footer { padding: 16px 20px; border-top: 1px solid #eee; display: flex; justify-content: flex-end; gap: 10px; }
.modal-form { display: flex; flex-direction: column; gap: 16px; }
.message-box { position: fixed; top: 70px; left: 50%; transform: translateX(-50%); padding: 12px 24px; border-radius: 4px; z-index: 1001; }
.message-info { background: #edf2fc; color: #909399; }
.message-success { background: #f0f9eb; color: #67c23a; }
.message-warning { background: #fdf6ec; color: #e6a23c; }
.message-error { background: #fef0f0; color: #f56c6c; }
</style>