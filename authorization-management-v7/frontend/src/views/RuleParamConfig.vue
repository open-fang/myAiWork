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

      <!-- 分页 -->
      <Pagination
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :current-page="pagination.currentPage"
        @size-change="handlePageSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 规则参数详情弹窗 -->
    <RuleParamDetailDialog ref="detailDialog" :rule-param-options="dataList" @success="loadDataList" />
  </div>
</template>

<script>
import Pagination from '@/components/Pagination.vue'
import RuleParamDetailDialog from '@/components/RuleParamDetailDialog.vue'
import { getRuleParamList, batchUpdateStatus } from '@/api/ruleParam'

export default {
  name: 'RuleParamConfig',
  components: {
    Pagination,
    RuleParamDetailDialog
  },
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
      this.$refs.detailDialog.open()
    },
    handleEdit(row) {
      this.$refs.detailDialog.open(row.id)
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
</style>