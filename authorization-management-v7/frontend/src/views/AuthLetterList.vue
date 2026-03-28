<template>
  <div class="auth-letter-list">
    <h2 class="page-title">授权书列表</h2>

    <!-- 搜索条件区域 -->
    <el-card class="search-card">
      <el-form :model="searchParams" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="授权书名称">
              <el-input v-model="searchParams.name" placeholder="请输入" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="授权对象层级">
              <!-- FlatMultiSelect 组件 - 内联 -->
              <el-select
                v-model="searchParams.authObjectLevel"
                multiple
                collapse-tags
                placeholder="请选择"
                clearable
              >
                <el-option
                  v-for="item in authObjectLevelOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="适用区域">
              <!-- TreeMultiSelect 组件 - 内联 -->
              <el-popover
                v-model="treeSelectVisible.applicableRegion"
                placement="bottom-start"
                trigger="click"
                width="400"
              >
                <div class="tree-select-content">
                  <el-tree
                    ref="applicableRegionTree"
                    :data="applicableRegionOptions"
                    :props="{ children: 'children', label: 'name' }"
                    show-checkbox
                    check-strictly
                    node-key="code"
                    :default-checked-keys="searchParams.applicableRegion"
                    @check="handleTreeCheck('applicableRegion', $event)"
                  />
                </div>
                <el-input
                  slot="reference"
                  :value="getTreeDisplayText('applicableRegion')"
                  placeholder="请选择"
                  readonly
                  clearable
                  @clear="handleTreeClear('applicableRegion')"
                >
                  <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="授权发布层级">
              <el-select
                v-model="searchParams.authPublishLevel"
                multiple
                collapse-tags
                placeholder="请选择"
                clearable
              >
                <el-option
                  v-for="item in authPublishLevelOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="授权发布组织">
              <el-popover
                v-model="treeSelectVisible.authPublishOrg"
                placement="bottom-start"
                trigger="click"
                width="400"
              >
                <div class="tree-select-content">
                  <el-tree
                    ref="authPublishOrgTree"
                    :data="authPublishOrgOptions"
                    :props="{ children: 'children', label: 'name' }"
                    show-checkbox
                    check-strictly
                    node-key="code"
                    :default-checked-keys="searchParams.authPublishOrg"
                    @check="handleTreeCheck('authPublishOrg', $event)"
                  />
                </div>
                <el-input
                  slot="reference"
                  :value="getTreeDisplayText('authPublishOrg')"
                  placeholder="请选择"
                  readonly
                  clearable
                  @clear="handleTreeClear('authPublishOrg')"
                >
                  <i slot="suffix" class="el-input__icon el-icon-arrow-down" />
                </el-input>
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="授权书发布年份">
              <el-date-picker
                v-model="searchParams.publishYear"
                type="year"
                placeholder="请选择"
                value-format="yyyy"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="searchParams.status" placeholder="请选择" clearable>
                <el-option label="草稿" value="DRAFT" />
                <el-option label="已发布" value="PUBLISHED" />
                <el-option label="已失效" value="INVALID" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleCreate">新建授权书</el-button>
      <el-button @click="handleBatchUpdate" :disabled="selectedRows.length === 0">更新</el-button>
      <el-button @click="handleBatchPublish" :disabled="selectedRows.length === 0">生效</el-button>
      <el-button @click="handleBatchInvalidate" :disabled="selectedRows.length === 0">失效</el-button>
      <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">删除</el-button>
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
            <el-button
              v-if="scope.row.status === 'DRAFT'"
              type="text"
              @click="handleEdit(scope.row)"
            >
              <i class="el-icon-edit" />
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="授权书名称" prop="name" min-width="200">
          <template slot-scope="scope">
            <el-link type="primary" @click="handleView(scope.row)">
              {{ scope.row.name }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="statusName" width="100" />
        <el-table-column label="授权对象层级" prop="authObjectLevel" min-width="150" />
        <el-table-column label="适用区域" prop="applicableRegion" min-width="150" />
        <el-table-column label="授权发布层级" prop="authPublishLevel" min-width="150" />
        <el-table-column label="授权发布组织" prop="authPublishOrg" min-width="150" />
        <el-table-column label="授权书发布年份" prop="publishYear" width="120" />
        <el-table-column label="创建人" prop="createdByName" width="100" />
        <el-table-column label="创建时间" prop="createdTime" width="160" />
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
  </div>
</template>

<script>
// ============================================
// API 函数 - 内联
// ============================================
import axios from 'axios'
import { Message } from 'element-ui'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

const request = axios.create({
  baseURL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => config,
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
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
    console.error('Response error:', error)
    Message.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// API: 查询授权书列表
function getAuthLetterList(params) {
  return request({
    url: `${baseURL}/auth-letters`,
    method: 'get',
    params
  })
}

// API: 批量操作授权书
function batchAuthLetter(data) {
  return request({
    url: `${baseURL}/auth-letters/batch`,
    method: 'post',
    data
  })
}

// API: 删除授权书
function deleteAuthLetter(id) {
  return request({
    url: `${baseURL}/auth-letters/${id}`,
    method: 'delete'
  })
}

// API: 获取下拉列表数据
function getLookupValues(typeCode) {
  return request({
    url: `${baseURL}/lookup-values/${typeCode}`,
    method: 'get'
  })
}

// 下拉类型编码常量
const LOOKUP_TYPES = {
  AUTH_OBJECT_LEVEL: 'AUTH_OBJECT_LEVEL',
  AUTH_PUBLISH_LEVEL: 'AUTH_PUBLISH_LEVEL',
  APPLICABLE_REGION: 'APPLICABLE_REGION',
  AUTH_PUBLISH_ORG: 'AUTH_PUBLISH_ORG'
}

// ============================================
// 组件逻辑
// ============================================
export default {
  name: 'AuthLetterList',
  data() {
    return {
      searchParams: {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: '',
        status: ''
      },
      dataList: [],
      selectedRows: [],
      pagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      authObjectLevelOptions: [],
      applicableRegionOptions: [],
      authPublishLevelOptions: [],
      authPublishOrgOptions: [],
      // TreeMultiSelect 状态
      treeSelectVisible: {
        applicableRegion: false,
        authPublishOrg: false
      },
      treeCheckedKeys: {
        applicableRegion: [],
        authPublishOrg: []
      }
    }
  },
  async mounted() {
    await this.loadLookupData()
    await this.loadDataList()
  },
  methods: {
    async loadLookupData() {
      try {
        const [authObjectLevel, applicableRegion, authPublishLevel, authPublishOrg] = await Promise.all([
          getLookupValues(LOOKUP_TYPES.AUTH_OBJECT_LEVEL),
          getLookupValues(LOOKUP_TYPES.APPLICABLE_REGION),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_LEVEL),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_ORG)
        ])
        this.authObjectLevelOptions = authObjectLevel.data || []
        this.applicableRegionOptions = applicableRegion.data || []
        this.authPublishLevelOptions = authPublishLevel.data || []
        this.authPublishOrgOptions = authPublishOrg.data || []
      } catch (error) {
        console.error('加载下拉数据失败:', error)
      }
    },
    async loadDataList() {
      try {
        const params = {
          name: this.searchParams.name,
          authObjectLevel: this.searchParams.authObjectLevel.join(','),
          applicableRegion: this.searchParams.applicableRegion.join(','),
          authPublishLevel: this.searchParams.authPublishLevel.join(','),
          authPublishOrg: this.searchParams.authPublishOrg.join(','),
          publishYear: this.searchParams.publishYear ? parseInt(this.searchParams.publishYear) : null,
          status: this.searchParams.status,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getAuthLetterList(params)
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
    handleReset() {
      this.searchParams = {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: '',
        status: ''
      }
      this.treeCheckedKeys = {
        applicableRegion: [],
        authPublishOrg: []
      }
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
      this.$router.push('/AuthLetterDetail')
    },
    handleEdit(row) {
      this.$router.push({ path: '/AuthLetterDetail', query: { id: row.id } })
    },
    handleView(row) {
      this.$router.push({ path: '/AuthLetterDetail', query: { id: row.id } })
    },
    async handleBatchUpdate() {
      try {
        await this.$confirm('确定要更新选中的授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchAuthLetter({ ids, operation: 'UPDATE' })
        this.loadDataList()
        this.$message.success('更新成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '更新失败')
        }
      }
    },
    async handleBatchPublish() {
      try {
        await this.$confirm('确定要发布选中的授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        const res = await batchAuthLetter({ ids, operation: 'PUBLISH' })
        this.loadDataList()
        if (res.data.failCount > 0) {
          this.$message.warning(`成功${res.data.successCount}条，失败${res.data.failCount}条`)
        } else {
          this.$message.success('发布成功')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '发布失败')
        }
      }
    },
    async handleBatchInvalidate() {
      try {
        await this.$confirm('确定要失效选中的授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchAuthLetter({ ids, operation: 'INVALIDATE' })
        this.loadDataList()
        this.$message.success('失效成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '失效失败')
        }
      }
    },
    async handleBatchDelete() {
      try {
        const count = this.selectedRows.length
        await this.$confirm(`确定要删除选中的${count}条授权书吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchAuthLetter({ ids, operation: 'DELETE' })
        this.loadDataList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    // TreeMultiSelect 方法
    getTreeCheckedNames(data, codes) {
      const names = []
      const traverse = (nodes) => {
        for (const node of nodes) {
          if (codes.includes(node.code)) {
            names.push(node.name)
          }
          if (node.children && node.children.length > 0) {
            traverse(node.children)
          }
        }
      }
      traverse(data)
      return names
    },
    getTreeDisplayText(field) {
      const keys = this.treeCheckedKeys[field] || []
      if (keys.length === 0) return ''
      let data = []
      if (field === 'applicableRegion') {
        data = this.applicableRegionOptions
      } else if (field === 'authPublishOrg') {
        data = this.authPublishOrgOptions
      }
      const names = this.getTreeCheckedNames(data, keys)
      if (names.length > 3) {
        return names.slice(0, 3).join(', ') + '...'
      }
      return names.join(', ')
    },
    handleTreeCheck(field, { checkedKeys }) {
      this.treeCheckedKeys[field] = checkedKeys
      this.searchParams[field] = checkedKeys
    },
    handleTreeClear(field) {
      this.treeCheckedKeys[field] = []
      this.searchParams[field] = []
      const treeRef = field === 'applicableRegion' ? 'applicableRegionTree' : 'authPublishOrgTree'
      this.$refs[treeRef].setCheckedKeys([])
    }
  }
}
</script>

<style scoped>
.auth-letter-list {
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
.tree-select-content {
  max-height: 300px;
  overflow-y: auto;
}
</style>