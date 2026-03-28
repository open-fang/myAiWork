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
              <FlatMultiSelect
                v-model="searchParams.authObjectLevel"
                :options="authObjectLevelOptions"
                placeholder="请选择"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="适用区域">
              <TreeMultiSelect
                v-model="searchParams.applicableRegion"
                :tree-data="applicableRegionOptions"
                placeholder="请选择"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="授权发布层级">
              <FlatMultiSelect
                v-model="searchParams.authPublishLevel"
                :options="authPublishLevelOptions"
                placeholder="请选择"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="授权发布组织">
              <TreeMultiSelect
                v-model="searchParams.authPublishOrg"
                :tree-data="authPublishOrgOptions"
                placeholder="请选择"
              />
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

      <!-- 分页 -->
      <Pagination
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :current-page="pagination.currentPage"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script>
import FlatMultiSelect from '@/components/FlatMultiSelect.vue'
import TreeMultiSelect from '@/components/TreeMultiSelect.vue'
import Pagination from '@/components/Pagination.vue'
import { getAuthLetterList, batchAuthLetter, deleteAuthLetter } from '@/api/authLetter'
import { getLookupValues, LOOKUP_TYPES } from '@/api/lookup'

export default {
  name: 'AuthLetterList',
  components: {
    FlatMultiSelect,
    TreeMultiSelect,
    Pagination
  },
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
      authPublishOrgOptions: []
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
</style>