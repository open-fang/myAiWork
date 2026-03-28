<template>
  <el-dialog
    title="问卷题目配置"
    :visible.sync="visible"
    width="900px"
    append-to-body
    @close="handleClose"
  >
    <!-- 搜索条件 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input v-model="searchParams.questionCode" placeholder="题目编号" clearable size="small" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchParams.questionText" placeholder="题目" clearable size="small" />
        </el-col>
        <el-col :span="3">
          <el-select v-model="searchParams.language" placeholder="语言" clearable size="small">
            <el-option label="中文" value="ZH" />
            <el-option label="英文" value="EN" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchParams.createdBy" placeholder="创建人" clearable size="small" />
        </el-col>
        <el-col :span="4">
          <el-date-picker
            v-model="searchParams.createdTime"
            type="date"
            placeholder="创建时间"
            value-format="yyyy-MM-dd"
            size="small"
          />
        </el-col>
        <el-col :span="2">
          <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        </el-col>
        <el-col :span="2">
          <el-button size="small" @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 功能按钮 -->
    <div class="button-section">
      <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
      <el-button size="small" :disabled="selectedRows.length === 0" @click="handleBatchDelete">删除</el-button>
    </div>

    <!-- 题目表格 -->
    <el-table
      ref="questionTable"
      :data="questionList"
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
      <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="handleDeleteRow(scope.row)">删除</el-button>
        </template>
      </el-table-column>
      <el-table-column label="题目编号" prop="questionCode" width="150" />
      <el-table-column label="题目" prop="questionText" />
      <el-table-column label="语言" prop="language" width="80" />
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

    <!-- 问卷题目维护弹窗 -->
    <QuestionMaintainDialog ref="questionMaintainDialog" @success="loadQuestionList" />
  </el-dialog>
</template>

<script>
import Pagination from './Pagination.vue'
import QuestionMaintainDialog from './QuestionMaintainDialog.vue'
import { getQuestionList, deleteQuestion, batchDeleteQuestions } from '@/api/questionnaire'

export default {
  name: 'QuestionConfigDialog',
  components: {
    Pagination,
    QuestionMaintainDialog
  },
  data() {
    return {
      visible: false,
      questionList: [],
      selectedRows: [],
      searchParams: {
        questionCode: '',
        questionText: '',
        language: '',
        createdBy: '',
        createdTime: ''
      },
      pagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      }
    }
  },
  methods: {
    open() {
      this.visible = true
      this.loadQuestionList()
    },
    handleClose() {
      this.visible = false
    },
    async loadQuestionList() {
      try {
        const params = {
          ...this.searchParams,
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        }
        const res = await getQuestionList(params)
        this.questionList = res.data.list || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载题目列表失败:', error)
      }
    },
    handleSearch() {
      this.pagination.currentPage = 1
      this.loadQuestionList()
    },
    handleReset() {
      this.searchParams = {
        questionCode: '',
        questionText: '',
        language: '',
        createdBy: '',
        createdTime: ''
      }
      this.pagination.currentPage = 1
      this.loadQuestionList()
    },
    handlePageSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadQuestionList()
    },
    handlePageChange(val) {
      this.pagination.currentPage = val
      this.loadQuestionList()
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    handleAdd() {
      this.$refs.questionMaintainDialog.open()
    },
    handleEdit(row) {
      this.$refs.questionMaintainDialog.open(row.id)
    },
    async handleDeleteRow(row) {
      try {
        await this.$confirm('确定要删除此题目吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteQuestion(row.id)
        this.loadQuestionList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    async handleBatchDelete() {
      try {
        await this.$confirm('确定要删除选中的题目吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchDeleteQuestions(ids)
        this.loadQuestionList()
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
.search-section {
  margin-bottom: 15px;
}
.button-section {
  margin-bottom: 10px;
}
</style>