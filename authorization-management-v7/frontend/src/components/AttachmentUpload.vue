<template>
  <div class="attachment-upload">
    <!-- 功能按钮 -->
    <div class="attachment-buttons">
      <el-button
        type="primary"
        size="small"
        :disabled="!editable"
        @click="handleUpload"
      >
        上传
      </el-button>
      <el-button
        size="small"
        :disabled="selectedRows.length === 0"
        @click="handleDownload"
      >
        下载
      </el-button>
      <el-button
        size="small"
        :disabled="!editable || selectedRows.length === 0"
        @click="handleDelete"
      >
        删除
      </el-button>
    </div>

    <!-- 附件表格 -->
    <el-table
      ref="attachmentTable"
      :data="attachmentList"
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
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button
            type="text"
            size="small"
            @click="handleDownloadRow(scope.row)"
          >
            下载
          </el-button>
          <el-button
            type="text"
            size="small"
            :disabled="!editable"
            @click="handleDeleteRow(scope.row)"
          >
            删除
          </el-button>
          <el-button
            type="text"
            size="small"
            disabled
          >
            加密
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="文档名称" prop="docName">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleDownloadRow(scope.row)">
            {{ scope.row.docName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="文档类型" prop="docTypeName" width="120" />
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

    <!-- 上传弹窗 -->
    <el-dialog
      title="上传附件"
      :visible.sync="uploadDialogVisible"
      width="400px"
      append-to-body
    >
      <el-form ref="uploadForm" :model="uploadForm" :rules="uploadRules" label-width="100px">
        <el-form-item label="文档名称" prop="docName">
          <el-input v-model="uploadForm.docName" placeholder="请输入文档名称" />
        </el-form-item>
        <el-form-item label="文档类型" prop="docType">
          <el-select v-model="uploadForm.docType" placeholder="请选择文档类型">
            <el-option
              v-for="item in docTypeOptions"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文档ID" prop="docId">
          <el-input v-model="uploadForm.docId" placeholder="请输入文档ID" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUploadConfirm">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from './Pagination.vue'
import { getAttachmentList, uploadAttachment, downloadAttachment, deleteAttachment, batchDeleteAttachments } from '@/api/attachment'

export default {
  name: 'AttachmentUpload',
  components: {
    Pagination
  },
  props: {
    authLetterId: {
      type: Number,
      required: true
    },
    editable: {
      type: Boolean,
      default: false
    },
    docTypeOptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      attachmentList: [],
      selectedRows: [],
      pagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      uploadDialogVisible: false,
      uploadForm: {
        docId: '',
        docName: '',
        docType: ''
      },
      uploadRules: {
        docId: [{ required: true, message: '请输入文档ID', trigger: 'blur' }],
        docName: [{ required: true, message: '请输入文档名称', trigger: 'blur' }],
        docType: [{ required: true, message: '请选择文档类型', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadAttachmentList()
  },
  methods: {
    async loadAttachmentList() {
      if (!this.authLetterId) return
      try {
        const res = await getAttachmentList(this.authLetterId, {
          pageNum: this.pagination.currentPage,
          pageSize: this.pagination.pageSize
        })
        this.attachmentList = res.data.list || []
        this.pagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载附件列表失败:', error)
      }
    },
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },
    handlePageSizeChange(val) {
      this.pagination.pageSize = val
      this.pagination.currentPage = 1
      this.loadAttachmentList()
    },
    handlePageChange(val) {
      this.pagination.currentPage = val
      this.loadAttachmentList()
    },
    handleUpload() {
      this.uploadForm = { docId: '', docName: '', docType: '' }
      this.uploadDialogVisible = true
    },
    async handleUploadConfirm() {
      try {
        await this.$refs.uploadForm.validate()
        await uploadAttachment(this.authLetterId, this.uploadForm)
        this.uploadDialogVisible = false
        this.loadAttachmentList()
        this.$message.success('上传成功')
      } catch (error) {
        if (error.message) {
          this.$message.error(error.message)
        }
      }
    },
    async handleDownload() {
      for (const row of this.selectedRows) {
        await this.handleDownloadRow(row)
      }
    },
    async handleDownloadRow(row) {
      try {
        const res = await downloadAttachment(this.authLetterId, row.id)
        const blob = new Blob([res])
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = row.docName
        link.click()
        URL.revokeObjectURL(link.href)
      } catch (error) {
        this.$message.error('下载失败')
      }
    },
    async handleDelete() {
      try {
        await this.$confirm('确定要删除选中的附件吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedRows.map(row => row.id)
        await batchDeleteAttachments(this.authLetterId, ids)
        this.loadAttachmentList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    async handleDeleteRow(row) {
      try {
        await this.$confirm('确定要删除此附件吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteAttachment(this.authLetterId, row.id)
        this.loadAttachmentList()
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
.attachment-upload {
  margin-top: 10px;
}
.attachment-buttons {
  margin-bottom: 10px;
}
</style>