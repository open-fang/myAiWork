<template>
  <div class="page-container">
    <h1 class="page-title">规则参数配置</h1>

    <!-- 查询条件 -->
    <div class="search-form">
      <div class="form-row">
        <div class="form-item">
          <label>名称</label>
          <input type="text" v-model="searchForm.name" placeholder="请输入名称" />
        </div>
        <div class="form-item">
          <label>名称英文</label>
          <input type="text" v-model="searchForm.nameEn" placeholder="请输入名称英文" />
        </div>
        <div class="form-item">
          <label>状态</label>
          <custom-select
            v-model="searchForm.status"
            :options="statusOptions"
            placeholder="请选择状态"
          />
        </div>
      </div>
      <div class="form-row">
        <button class="btn btn-primary" @click="handleSearch">查询</button>
      </div>
    </div>

    <!-- 功能按钮 -->
    <div class="table-toolbar">
      <button class="btn btn-primary" @click="handleCreate">新建</button>
      <button class="btn" @click="handleBatchStatus('ACTIVE')" :disabled="selectedRows.length === 0">生效</button>
      <button class="btn" @click="handleBatchStatus('INACTIVE')" :disabled="selectedRows.length === 0">失效</button>
    </div>

    <!-- 表格 -->
    <div class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" @change="handleSelectAll" v-model="isAllSelected" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>名称</th>
            <th>名称英文</th>
            <th>状态</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in tableData" :key="item.id">
            <td><input type="checkbox" v-model="selectedRows" :value="item.id" /></td>
            <td>{{ (pageNum - 1) * pageSize + index + 1 }}</td>
            <td>
              <span class="icon-edit" @click="handleEdit(item)">✏️</span>
            </td>
            <td>{{ item.name }}</td>
            <td>{{ item.nameEn }}</td>
            <td>
              <span :class="['status-tag', item.status === 'ACTIVE' ? 'status-published' : 'status-draft']">
                {{ item.status === 'ACTIVE' ? '生效' : '失效' }}
              </span>
            </td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ item.updatedTime }}</td>
          </tr>
          <tr v-if="tableData.length === 0">
            <td colspan="10" style="text-align: center; color: #999;">暂无数据</td>
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

    <!-- 编辑弹窗 -->
    <div class="modal-overlay" v-if="showModal">
      <div class="modal-content" style="width: 600px;">
        <div class="modal-header">
          <h3>{{ editingItem ? '编辑规则参数' : '新建规则参数' }}</h3>
          <span class="modal-close" @click="showModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item">
              <label class="required">名称</label>
              <input type="text" v-model="formData.name" placeholder="请输入名称" />
            </div>
            <div class="form-item">
              <label class="required">名称英文</label>
              <input type="text" v-model="formData.nameEn" placeholder="请输入名称英文" />
            </div>
          </div>

          <div class="business-objects">
            <div class="business-object-row" v-for="(bo, index) in formData.businessObjects" :key="index">
              <div class="form-item">
                <label>业务对象</label>
                <input type="text" v-model="bo.businessObject" placeholder="请输入业务对象" />
              </div>
              <div class="form-item">
                <label>取值逻辑</label>
                <input type="text" v-model="bo.valueLogic" placeholder="如：$.order.amount" />
              </div>
              <span class="icon-delete" @click="removeBusinessObject(index)" v-if="formData.businessObjects.length > 1">🗑️</span>
            </div>
            <button class="btn btn-link" @click="addBusinessObject">+ 添加业务对象</button>
          </div>

          <div class="form-row">
            <div class="form-item">
              <label class="required">是否生效</label>
              <custom-select
                v-model="formData.status"
                :options="activeOptions"
                placeholder="请选择"
              />
            </div>
            <div class="form-item">
              <label class="required">数据类型</label>
              <custom-select
                v-model="formData.dataType"
                :options="dataTypeOptions"
                placeholder="请选择数据类型"
              />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleSave">确定</button>
          <button class="btn" @click="showModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// HTTP请求封装
const http = {
  get(url, params) {
    const query = new URLSearchParams(params).toString()
    return fetch(`/api/v1${url}${query ? '?' + query : ''}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    }).then(res => res.json())
  },
  post(url, data) {
    return fetch(`/api/v1${url}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  put(url, data) {
    return fetch(`/api/v1${url}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  delete(url) {
    return fetch(`/api/v1${url}`, {
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
    toggleDropdown() { this.isOpen = !this.isOpen },
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
        <div class="select-option" v-for="opt in options" :key="opt.value || opt.code" @click="selectOption(opt)">
          <input v-if="multiple" type="checkbox" :checked="isSelected(opt)" />
          <span>{{ opt.label || opt.name }}</span>
        </div>
      </div>
    </div>
  `
}

export default {
  name: 'RuleParamConfig',
  components: { CustomSelect },
  data() {
    return {
      searchForm: {
        name: '',
        nameEn: '',
        status: ''
      },
      tableData: [],
      selectedRows: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      isAllSelected: false,
      showModal: false,
      editingItem: null,
      formData: {
        name: '',
        nameEn: '',
        businessObjects: [{ businessObject: '', valueLogic: '' }],
        status: 'ACTIVE',
        dataType: 'TEXT'
      },
      statusOptions: [
        { value: 'ACTIVE', label: '生效' },
        { value: 'INACTIVE', label: '失效' }
      ],
      activeOptions: [
        { value: 'ACTIVE', label: '是' },
        { value: 'INACTIVE', label: '否' }
      ],
      dataTypeOptions: [
        { value: 'TEXT', label: '文本' },
        { value: 'NUMBER', label: '数值' },
        { value: 'FIELD', label: '比对字段' },
        { value: 'RATIO', label: '比率' }
      ]
    }
  },
  created() {
    this.loadTableData()
  },
  methods: {
    async loadTableData() {
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        }
        if (this.searchForm.name) params.name = this.searchForm.name
        if (this.searchForm.nameEn) params.nameEn = this.searchForm.nameEn
        if (this.searchForm.status) params.status = this.searchForm.status

        const res = await http.get('/rule-params', params)
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
    handleSelectAll() {
      if (this.isAllSelected) {
        this.selectedRows = this.tableData.map(item => item.id)
      } else {
        this.selectedRows = []
      }
    },
    handleCreate() {
      this.editingItem = null
      this.formData = {
        name: '',
        nameEn: '',
        businessObjects: [{ businessObject: '', valueLogic: '' }],
        status: 'ACTIVE',
        dataType: 'TEXT'
      }
      this.showModal = true
    },
    handleEdit(item) {
      this.editingItem = item
      this.formData = {
        name: item.name,
        nameEn: item.nameEn,
        businessObjects: item.businessObject ? [{ businessObject: item.businessObject, valueLogic: item.valueLogic }] : [{ businessObject: '', valueLogic: '' }],
        status: item.status,
        dataType: item.dataType
      }
      this.showModal = true
    },
    async handleSave() {
      if (!this.formData.name || !this.formData.nameEn) {
        alert('请填写必填项')
        return
      }

      try {
        const data = {
          name: this.formData.name,
          nameEn: this.formData.nameEn,
          businessObjects: this.formData.businessObjects,
          status: this.formData.status,
          dataType: this.formData.dataType
        }

        if (this.editingItem) {
          await http.put(`/rule-params/${this.editingItem.id}`, data)
          alert('更新成功')
        } else {
          await http.post('/rule-params', data)
          alert('创建成功')
        }

        this.showModal = false
        this.loadTableData()
      } catch (e) {
        console.error('保存失败', e)
        alert('保存失败')
      }
    },
    async handleBatchStatus(status) {
      if (this.selectedRows.length === 0) return
      if (!confirm(`确定要${status === 'ACTIVE' ? '生效' : '失效'}选中的数据吗？`)) return

      try {
        await http.post('/rule-params/batch-status', {
          ids: this.selectedRows,
          status: status
        })
        this.selectedRows = []
        this.loadTableData()
      } catch (e) {
        console.error('操作失败', e)
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
    addBusinessObject() {
      this.formData.businessObjects.push({ businessObject: '', valueLogic: '' })
    },
    removeBusinessObject(index) {
      this.formData.businessObjects.splice(index, 1)
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fafafa;
  border-radius: 4px;
}

.business-objects {
  margin-bottom: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 4px;
}

.business-object-row {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  align-items: center;
}

.business-object-row .form-item {
  flex: 1;
}

.business-object-row .icon-delete {
  margin-top: 8px;
}
</style>