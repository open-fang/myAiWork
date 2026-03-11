<template>
  <div class="rule-param-page">
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
      </div>
    </div>

    <!-- 功能按钮区 -->
    <div class="action-buttons">
      <button class="btn btn-primary" @click="openDialog()">新建</button>
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
            <th class="col-name-en">名称英文</th>
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
              <span class="icon-btn" title="编辑" @click="openDialog(row)">✏️</span>
            </td>
            <td class="col-name">{{ row.name }}</td>
            <td class="col-name-en">{{ row.nameEn }}</td>
            <td class="col-status">
              <span class="status-tag" :class="row.status === 'ACTIVE' ? 'status-active' : 'status-inactive'">
                {{ row.status === 'ACTIVE' ? '生效' : '失效' }}
              </span>
            </td>
            <td class="col-user">{{ row.createdBy }}</td>
            <td class="col-time">{{ row.createdAt }}</td>
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

    <!-- 新建/编辑弹窗 -->
    <div class="modal-overlay" v-if="dialogVisible" @click.self="closeDialog">
      <div class="modal-dialog">
        <div class="modal-header">
          <span class="modal-title">{{ editingRow ? '编辑规则参数' : '新建规则参数' }}</span>
          <span class="modal-close" @click="closeDialog">×</span>
        </div>
        <div class="modal-body">
          <div class="form-content">
            <!-- 第一行：名称、名称英文 -->
            <div class="dialog-form-row">
              <div class="dialog-form-item">
                <label class="dialog-form-label required">名称</label>
                <input type="text" v-model="formData.name" class="form-input" placeholder="请输入名称" />
              </div>
              <div class="dialog-form-item">
                <label class="dialog-form-label required">名称英文</label>
                <input type="text" v-model="formData.nameEn" class="form-input" placeholder="请输入名称英文" />
              </div>
            </div>

            <!-- 业务对象和取值逻辑（可动态增减） -->
            <div class="business-rows">
              <div class="dialog-form-row" v-for="(row, index) in formData.businessRows" :key="index">
                <div class="dialog-form-item">
                  <label class="dialog-form-label" :class="{ required: index === 0 }">业务对象</label>
                  <div class="select-wrapper">
                    <div class="select-trigger" @click="toggleSelect('businessObject' + index)">
                      <span>{{ row.businessObject ? getBusinessObjectLabel(row.businessObject) : '请选择' }}</span>
                      <span class="arrow">▼</span>
                    </div>
                    <div class="select-dropdown" v-show="activeDropdown === 'businessObject' + index">
                      <div class="select-option" v-for="item in businessObjectOptions" :key="item.value" @click="selectBusinessObject(index, item.value)">
                        {{ item.label }}
                      </div>
                    </div>
                  </div>
                </div>
                <div class="dialog-form-item">
                  <label class="dialog-form-label" :class="{ required: index === 0 }">取值逻辑</label>
                  <input type="text" v-model="row.valueLogic" class="form-input" placeholder="请输入取值逻辑" />
                </div>
                <div class="add-btn-wrapper">
                  <span class="add-btn" v-if="index === formData.businessRows.length - 1" @click="addBusinessRow">+</span>
                  <span class="remove-btn" v-if="formData.businessRows.length > 1" @click="removeBusinessRow(index)">-</span>
                </div>
              </div>
            </div>

            <!-- 第三行：是否生效、数据类型 -->
            <div class="dialog-form-row">
              <div class="dialog-form-item">
                <label class="dialog-form-label required">是否生效</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('isActive')">
                    <span>{{ formData.isActive !== null ? (formData.isActive ? '是' : '否') : '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'isActive'">
                    <div class="select-option" @click="selectIsActive(true)">是</div>
                    <div class="select-option" @click="selectIsActive(false)">否</div>
                  </div>
                </div>
              </div>
              <div class="dialog-form-item">
                <label class="dialog-form-label required">数据类型</label>
                <div class="select-wrapper">
                  <div class="select-trigger" @click="toggleSelect('dataType')">
                    <span>{{ formData.dataType ? getDataTypeLabel(formData.dataType) : '请选择' }}</span>
                    <span class="arrow">▼</span>
                  </div>
                  <div class="select-dropdown" v-show="activeDropdown === 'dataType'">
                    <div class="select-option" v-for="item in dataTypeOptions" :key="item.value" @click="selectDataType(item.value)">
                      {{ item.label }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-default" @click="closeDialog">取消</button>
          <button class="btn btn-primary" @click="handleSave" :disabled="saving">{{ saving ? '保存中...' : '确定' }}</button>
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
import { lookupApi, ruleParamApi } from '@/api'

export default {
  name: 'RuleParamConfig',
  data() {
    return {
      loading: false,
      saving: false,
      activeDropdown: '',
      selectAll: false,
      selectedRows: [],
      jumpPage: 1,
      message: {
        show: false,
        type: 'info',
        text: ''
      },
      confirmDialog: {
        show: false,
        text: '',
        onConfirm: () => {},
        onCancel: () => {}
      },
      queryParams: {
        name: '',
        nameEn: '',
        status: ''
      },
      pagination: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      statusOptions: [
        { value: 'ACTIVE', label: '生效' },
        { value: 'INACTIVE', label: '失效' }
      ],
      businessObjectOptions: [],
      dataTypeOptions: [
        { value: 'TEXT', label: '文本' },
        { value: 'NUMBER', label: '数值' },
        { value: 'COMPARE_FIELD', label: '比对字段' },
        { value: 'RATIO', label: '比率' }
      ],
      tableData: [],
      dialogVisible: false,
      editingRow: null,
      formData: {
        name: '',
        nameEn: '',
        businessRows: [
          { businessObject: '', valueLogic: '' }
        ],
        isActive: null,
        dataType: ''
      }
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.pagination.total / this.pagination.pageSize) || 1
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
        const res = await lookupApi.getBusinessObjects()
        this.businessObjectOptions = res.data || []
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
          nameEn: this.queryParams.nameEn || undefined,
          status: this.queryParams.status || undefined
        }

        const res = await ruleParamApi.getList(params)
        this.tableData = res.data?.list || []
        this.pagination.total = res.data?.total || 0
      } catch (error) {
        console.error('加载数据失败:', error)
        this.showMessage('加载数据失败', 'error')
      } finally {
        this.loading = false
      }
    },

    toggleSelect(name) {
      this.activeDropdown = this.activeDropdown === name ? '' : name
    },
    selectStatus(value) {
      this.queryParams.status = value
      this.activeDropdown = ''
    },
    getStatusLabel(value) {
      const item = this.statusOptions.find(s => s.value === value)
      return item ? item.label : ''
    },
    getBusinessObjectLabel(value) {
      const item = this.businessObjectOptions.find(s => s.value === value)
      return item ? item.label : value
    },
    getDataTypeLabel(value) {
      const item = this.dataTypeOptions.find(s => s.value === value)
      return item ? item.label : value
    },
    selectBusinessObject(index, value) {
      this.formData.businessRows[index].businessObject = value
      this.activeDropdown = ''
    },
    selectIsActive(value) {
      this.formData.isActive = value
      this.activeDropdown = ''
    },
    selectDataType(value) {
      this.formData.dataType = value
      this.activeDropdown = ''
    },
    addBusinessRow() {
      this.formData.businessRows.push({ businessObject: '', valueLogic: '' })
    },
    removeBusinessRow(index) {
      this.formData.businessRows.splice(index, 1)
    },
    handleSelectAll() {
      if (this.selectAll) {
        this.selectedRows = this.tableData.map(row => row.id)
      } else {
        this.selectedRows = []
      }
    },
    handleSizeChange() {
      this.pagination.pageNum = 1
      this.loadTableData()
    },
    handlePageChange(page) {
      this.pagination.pageNum = page
      this.loadTableData()
    },
    handleJumpPage() {
      if (this.jumpPage >= 1 && this.jumpPage <= this.totalPages) {
        this.pagination.pageNum = this.jumpPage
        this.loadTableData()
      }
    },
    handleQuery() {
      this.pagination.pageNum = 1
      this.loadTableData()
    },
    checkSelection() {
      if (this.selectedRows.length === 0) {
        this.showMessage('请先选择数据', 'warning')
        return false
      }
      return true
    },
    async handleActivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为生效吗？`)
      if (confirmed) {
        try {
          await ruleParamApi.batchActivate(this.selectedRows)
          this.showMessage('操作成功', 'success')
          this.selectedRows = []
          this.selectAll = false
          this.loadTableData()
        } catch (error) {
          this.showMessage('操作失败', 'error')
        }
      }
    },
    async handleDeactivate() {
      if (!this.checkSelection()) return
      const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为失效吗？`)
      if (confirmed) {
        try {
          await ruleParamApi.batchDeactivate(this.selectedRows)
          this.showMessage('操作成功', 'success')
          this.selectedRows = []
          this.selectAll = false
          this.loadTableData()
        } catch (error) {
          this.showMessage('操作失败', 'error')
        }
      }
    },
    openDialog(row = null) {
      this.editingRow = row
      if (row) {
        this.formData.name = row.name
        this.formData.nameEn = row.nameEn
        this.formData.businessRows = row.businessMappings && row.businessMappings.length > 0
          ? row.businessMappings.map(m => ({ businessObject: m.businessObject, valueLogic: m.valueLogic }))
          : [{ businessObject: '', valueLogic: '' }]
        this.formData.isActive = row.status === 'ACTIVE'
        this.formData.dataType = row.dataType || ''
      } else {
        this.formData.name = ''
        this.formData.nameEn = ''
        this.formData.businessRows = [{ businessObject: '', valueLogic: '' }]
        this.formData.isActive = null
        this.formData.dataType = ''
      }
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
      this.editingRow = null
    },
    async handleSave() {
      if (!this.formData.name) {
        this.showMessage('请输入名称', 'warning')
        return
      }
      if (!this.formData.nameEn) {
        this.showMessage('请输入名称英文', 'warning')
        return
      }
      if (this.formData.isActive === null) {
        this.showMessage('请选择是否生效', 'warning')
        return
      }
      if (!this.formData.dataType) {
        this.showMessage('请选择数据类型', 'warning')
        return
      }

      this.saving = true
      try {
        const data = {
          name: this.formData.name,
          nameEn: this.formData.nameEn,
          status: this.formData.isActive ? 'ACTIVE' : 'INACTIVE',
          dataType: this.formData.dataType,
          businessMappings: this.formData.businessRows.filter(r => r.businessObject || r.valueLogic)
        }

        if (this.editingRow) {
          await ruleParamApi.update(this.editingRow.id, data)
        } else {
          await ruleParamApi.create(data)
        }

        this.showMessage(this.editingRow ? '编辑成功' : '新建成功', 'success')
        this.closeDialog()
        this.loadTableData()
      } catch (error) {
        this.showMessage('保存失败', 'error')
      } finally {
        this.saving = false
      }
    },
    showMessage(text, type = 'info') {
      this.message.text = text
      this.message.type = type
      this.message.show = true
      setTimeout(() => {
        this.message.show = false
      }, 3000)
    },
    showConfirm(text) {
      return new Promise((resolve) => {
        this.confirmDialog.show = true
        this.confirmDialog.text = text
        this.confirmDialog.onConfirm = () => {
          this.confirmDialog.show = false
          resolve(true)
        }
        this.confirmDialog.onCancel = () => {
          this.confirmDialog.show = false
          resolve(false)
        }
      })
    },
    handleClickOutside(event) {
      if (!event.target.closest('.select-wrapper')) {
        this.activeDropdown = ''
      }
    }
  }
}
</script>

<style scoped>
/* 样式保持不变 */
* {
  box-sizing: border-box;
}

.rule-param-page {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  font-size: 14px;
  color: #333;
}

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
  width: 80px;
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

.select-wrapper {
  flex: 1;
  position: relative;
}

.select-trigger {
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

.arrow {
  position: absolute;
  right: 10px;
  font-size: 12px;
  color: #c0c4cc;
}

.select-dropdown {
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

.select-option {
  padding: 8px 12px;
  cursor: pointer;
}

.select-option:hover {
  background: #f5f7fa;
}

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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

.action-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

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

.col-name {
  min-width: 120px;
}

.col-name-en {
  min-width: 140px;
}

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

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-active {
  background: #f0f9eb;
  color: #67c23a;
}

.status-inactive {
  background: #f4f4f5;
  color: #909399;
}

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
  min-width: 600px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.2);
}

.confirm-modal {
  min-width: 400px;
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-weight: 600;
  font-size: 16px;
}

.modal-close {
  cursor: pointer;
  font-size: 20px;
  color: #909399;
}

.modal-close:hover {
  color: #409eff;
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

.form-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr 40px;
  gap: 16px;
  align-items: center;
}

.dialog-form-row:last-child {
  grid-template-columns: 1fr 1fr;
}

.dialog-form-item {
  display: flex;
  align-items: center;
}

.dialog-form-label {
  width: 80px;
  text-align: right;
  color: #666;
  flex-shrink: 0;
  margin-right: 12px;
}

.dialog-form-label.required::before {
  content: '*';
  color: #f56c6c;
  margin-right: 4px;
}

.business-rows {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.add-btn-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.add-btn,
.remove-btn {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
}

.add-btn {
  background: #409eff;
  color: #fff;
}

.add-btn:hover {
  background: #66b1ff;
}

.remove-btn {
  background: #f56c6c;
  color: #fff;
}

.remove-btn:hover {
  background: #f78989;
}

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
</style>