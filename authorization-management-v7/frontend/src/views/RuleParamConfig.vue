<template>
  <div class="rule-param-config">
    <h2 class="page-title">规则参数配置</h2>

    <!-- 搜索条件区域 -->
    <el-card class="search-card">
      <el-form :model="searchParams" inline>
        <el-form-item label="名称">
          <el-input v-model="searchParams.name" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="名称英文">
          <el-input v-model="searchParams.nameEn" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchParams.status" placeholder="请选择" clearable>
            <el-option label="生效" value="ACTIVE" />
            <el-option label="失效" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleCreate">新建</el-button>
      <el-button @click="handleBatchStatus('ACTIVE')" :disabled="selectedRows.length === 0">生效</el-button>
      <el-button @click="handleBatchStatus('INACTIVE')" :disabled="selectedRows.length === 0">失效</el-button>
    </div>

    <!-- 列表表格 -->
    <el-card class="table-card">
      <el-table
        ref="dataTable"
        :data="dataList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column label="序号" width="60">
          <template slot-scope="scope">
            {{ (pagination.currentPage - 1) * pagination.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="60">
          <template slot-scope="scope">
            <el-button type="text" @click="handleEdit(scope.row)">
              <i class="el-icon-edit" />
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="名称" prop="name" min-width="150" />
        <el-table-column label="名称英文" prop="nameEn" min-width="150" />
        <el-table-column label="状态" prop="statusName" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ scope.row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="数据类型" prop="dataTypeName" width="100" />
        <el-table-column label="创建人" prop="createdBy" width="100" />
        <el-table-column label="创建时间" prop="createdTime" width="160" />
        <el-table-column label="更新人" prop="updatedBy" width="100" />
        <el-table-column label="更新时间" prop="updatedTime" width="160" />
      </el-table>

      <!-- Pagination 组件 - 内联 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 30, 50]"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 规则参数详情弹窗 - RuleParamDetailDialog 内联 -->
    <el-dialog
      title="规则参数详情"
      :visible.sync="detailDialogVisible"
      width="600px"
      append-to-body
      @close="handleDetailDialogClose"
    >
      <el-form ref="detailForm" :model="detailFormData" :rules="detailRules" label-width="100px">
        <!-- 第一行：名称和名称英文 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="name">
              <el-input v-model="detailFormData.name" placeholder="请输入名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="名称英文" prop="nameEn">
              <el-input v-model="detailFormData.nameEn" placeholder="请输入名称英文" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 业务对象和取值逻辑（可增减） -->
        <div class="business-objects-section">
          <label class="section-label">业务对象配置</label>
          <div class="business-objects-list">
            <el-row
              v-for="(item, index) in detailFormData.businessObjects"
              :key="index"
              :gutter="20"
            >
              <el-col :span="10">
                <el-input v-model="item.businessObject" placeholder="业务对象" />
              </el-col>
              <el-col :span="10">
                <el-input v-model="item.valueLogic" placeholder="取值逻辑" />
              </el-col>
              <el-col :span="4">
                <el-button
                  v-if="index === detailFormData.businessObjects.length - 1"
                  type="primary"
                  icon="el-icon-plus"
                  size="small"
                  @click="addBusinessObject"
                />
                <el-button
                  v-if="detailFormData.businessObjects.length > 1"
                  type="danger"
                  icon="el-icon-delete"
                  size="small"
                  @click="removeBusinessObject(index)"
                />
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 第二行：是否生效和数据类型 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="是否生效" prop="status">
              <el-select v-model="detailFormData.status" placeholder="请选择">
                <el-option label="是" value="ACTIVE" />
                <el-option label="否" value="INACTIVE" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据类型" prop="dataType">
              <el-select v-model="detailFormData.dataType" placeholder="请选择">
                <el-option label="文本" value="TEXT" />
                <el-option label="数值" value="NUMBER" />
                <el-option label="比对字段" value="FIELD" />
                <el-option label="比率" value="RATIO" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 关联字段（FIELD类型时显示） -->
        <el-row v-if="detailFormData.dataType === 'FIELD'" :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联字段" prop="referenceFieldId">
              <el-select v-model="detailFormData.referenceFieldId" placeholder="请选择关联字段">
                <el-option
                  v-for="item in dataList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <span slot="footer">
        <el-button @click="handleDetailDialogClose">取消</el-button>
        <el-button type="primary" :loading="detailLoading" @click="handleDetailConfirm">确认</el-button>
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

// API: 查询规则参数列表
function getRuleParamList(params) {
  return request({
    url: `${baseURL}/rule-params`,
    method: 'get',
    params
  })
}

// API: 获取规则参数详情
function getRuleParamDetail(id) {
  return request({
    url: `${baseURL}/rule-params/${id}`,
    method: 'get'
  })
}

// API: 创建规则参数
function createRuleParam(data) {
  return request({
    url: `${baseURL}/rule-params`,
    method: 'post',
    data
  })
}

// API: 更新规则参数
function updateRuleParam(id, data) {
  return request({
    url: `${baseURL}/rule-params/${id}`,
    method: 'put',
    data
  })
}

// API: 批量更新状态
function batchUpdateStatus(ids, status) {
  return request({
    url: `${baseURL}/rule-params/batch-status`,
    method: 'post',
    data: { ids, status }
  })
}

// ============================================
// 组件逻辑
// ============================================
export default {
  name: 'RuleParamConfig',
  data() {
    return {
      searchParams: {
        name: '',
        nameEn: '',
        status: ''
      },
      dataList: [],
      selectedRows: [],
      pagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      // 详情弹窗数据
      detailDialogVisible: false,
      detailLoading: false,
      detailIsEdit: false,
      detailCurrentId: null,
      detailFormData: {
        name: '',
        nameEn: '',
        businessObjects: [
          { businessObject: '', valueLogic: '' }
        ],
        status: 'ACTIVE',
        dataType: 'TEXT',
        referenceFieldId: null
      },
      detailRules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        nameEn: [{ required: true, message: '请输入名称英文', trigger: 'blur' }],
        status: [{ required: true, message: '请选择是否生效', trigger: 'change' }],
        dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadDataList()
  },
  methods: {
    async loadDataList() {
      try {
        const params = {
          name: this.searchParams.name,
          nameEn: this.searchParams.nameEn,
          status: this.searchParams.status,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getRuleParamList(params)
        this.dataList = res.data.list || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载列表失败:', error)
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadDataList()
    },
    handlePageSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadDataList()
    },
    handlePageChange(val) {
      this.pagination.currentPage = val
      this.loadDataList()
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    handleCreate() {
      this.detailIsEdit = false
      this.detailCurrentId = null
      this.resetDetailForm()
      this.detailDialogVisible = true
    },
    async handleEdit(row) {
      this.detailIsEdit = true
      this.detailCurrentId = row.id
      this.detailDialogVisible = true
      await this.loadDetail(row.id)
    },
    async loadDetail(id) {
      try {
        const res = await getRuleParamDetail(id)
        this.detailFormData = {
          name: res.data.name || '',
          nameEn: res.data.nameEn || '',
          businessObjects: res.data.businessObjects || [{ businessObject: '', valueLogic: '' }],
          status: res.data.status || 'ACTIVE',
          dataType: res.data.dataType || 'TEXT',
          referenceFieldId: res.data.referenceFieldId || null
        }
      } catch (error) {
        this.$message.error('加载详情失败')
        this.detailDialogVisible = false
      }
    },
    resetDetailForm() {
      this.detailFormData = {
        name: '',
        nameEn: '',
        businessObjects: [
          { businessObject: '', valueLogic: '' }
        ],
        status: 'ACTIVE',
        dataType: 'TEXT',
        referenceFieldId: null
      }
    },
    addBusinessObject() {
      this.detailFormData.businessObjects.push({ businessObject: '', valueLogic: '' })
    },
    removeBusinessObject(index) {
      this.detailFormData.businessObjects.splice(index, 1)
    },
    handleDetailDialogClose() {
      this.detailDialogVisible = false
      this.resetDetailForm()
      if (this.$refs.detailForm) {
        this.$refs.detailForm.resetFields()
      }
    },
    async handleDetailConfirm() {
      try {
        await this.$refs.detailForm.validate()
        this.detailLoading = true

        // 过滤空的业务对象
        const businessObjects = this.detailFormData.businessObjects.filter(
          item => item.businessObject || item.valueLogic
        )

        const data = {
          ...this.detailFormData,
          businessObjects: businessObjects.length > 0 ? businessObjects : [{ businessObject: '', valueLogic: '' }]
        }

        if (this.detailIsEdit) {
          await updateRuleParam(this.detailCurrentId, data)
          this.$message.success('更新成功')
        } else {
          await createRuleParam(data)
          this.$message.success('创建成功')
        }

        this.detailDialogVisible = false
        this.loadDataList()
      } catch (error) {
        if (error.message) {
          this.$message.error(error.message)
        }
      } finally {
        this.detailLoading = false
      }
    },
    async handleBatchStatus(status) {
      try {
        const statusText = status === 'ACTIVE' ? '生效' : '失效'
        await this.$confirm(`确定要${statusText}选中的规则参数吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchUpdateStatus(ids, status)
        this.loadDataList()
        this.$message.success(`${statusText}成功`)
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '操作失败')
        }
      }
    }
  }
}
</script>

<style scoped>
.rule-param-config {
  padding: 20px;
}
.page-title {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 500;
}
.search-card {
  margin-bottom: 20px;
}
.action-buttons {
  margin-bottom: 10px;
}
.table-card {
  margin-top: 10px;
}
.pagination-container {
  padding: 15px 0;
  display: flex;
  justify-content: flex-end;
}
.business-objects-section {
  margin-bottom: 20px;
}
.section-label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}
.business-objects-list {
  padding-left: 10px;
}
</style>