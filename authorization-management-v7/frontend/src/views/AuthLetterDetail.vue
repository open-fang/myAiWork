<template>
  <div class="auth-letter-detail">
    <h2 class="page-title">{{ isNew ? '新建授权书' : '授权书详情' }}</h2>

    <!-- 基本信息区域 -->
    <el-card class="info-card">
      <div slot="header">基本信息</div>
      <el-form ref="baseForm" :model="formData" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授权书名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入授权书名称"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权发布层级" prop="authPublishLevel">
              <FlatMultiSelect
                v-model="formData.authPublishLevel"
                :options="authPublishLevelOptions"
                placeholder="请选择"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授权发布组织" prop="authPublishOrg">
              <TreeMultiSelect
                v-model="formData.authPublishOrg"
                :tree-data="authPublishOrgOptions"
                placeholder="请选择"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权对象层级" prop="authObjectLevel">
              <FlatMultiSelect
                v-model="formData.authObjectLevel"
                :options="authObjectLevelOptions"
                placeholder="请选择"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="适用区域" prop="applicableRegion">
              <TreeMultiSelect
                v-model="formData.applicableRegion"
                :tree-data="applicableRegionOptions"
                placeholder="请选择"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授权书发布年份" prop="publishYear">
              <el-date-picker
                v-model="formData.publishYear"
                type="year"
                placeholder="请选择"
                value-format="yyyy"
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="授权书内容摘要" prop="summary">
              <el-input
                v-model="formData.summary"
                type="textarea"
                :rows="4"
                placeholder="请输入授权书内容摘要"
                maxlength="4000"
                show-word-limit
                :disabled="!editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 附件区域 -->
    <el-card class="attachment-card">
      <div slot="header">附件</div>
      <AttachmentUpload
        v-if="authLetterId"
        :auth-letter-id="authLetterId"
        :editable="editable"
        :doc-type-options="docTypeOptions"
      />
      <div v-else class="empty-tip">请先保存授权书后再上传附件</div>
    </el-card>

    <!-- 授权规则区域 -->
    <el-card class="rule-card">
      <div slot="header">授权规则</div>
      <div class="rule-buttons" v-if="editable">
        <el-button type="primary" size="small" @click="handleAddScene">添加场景</el-button>
        <el-button size="small" :disabled="selectedScenes.length === 0" @click="handleBatchDeleteScenes">删除</el-button>
      </div>
      <el-table
        v-if="authLetterId"
        ref="sceneTable"
        :data="sceneList"
        border
        style="width: 100%"
        @selection-change="handleSceneSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column label="序号" width="60">
          <template slot-scope="scope">
            {{ (scenePagination.currentPage - 1) * scenePagination.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" v-if="editable">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleEditScene(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="handleDeleteScene(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <el-table-column label="场景" prop="sceneName" min-width="150" />
        <el-table-column label="产业" prop="industryName" min-width="150" />
        <el-table-column label="业务场景" prop="businessSceneName" min-width="120" />
        <el-table-column label="具体规则" prop="specificRule" min-width="200" />
        <el-table-column label="决策层级" prop="decisionLevelName" width="100" />
        <el-table-column label="创建人" prop="createdBy" width="100" />
        <el-table-column label="创建时间" prop="createdTime" width="160" />
        <el-table-column label="更新人" prop="updatedBy" width="100" />
        <el-table-column label="更新时间" prop="updatedTime" width="160" />
      </el-table>
      <div v-if="authLetterId && sceneList.length > 0">
        <Pagination
          :total="scenePagination.total"
          :page-size="scenePagination.pageSize"
          :current-page="scenePagination.currentPage"
          @size-change="handleScenePageSizeChange"
          @current-change="handleScenePageChange"
        />
      </div>
      <div v-if="!authLetterId" class="empty-tip">请先保存授权书后再配置规则</div>
    </el-card>

    <!-- 日志区域（已发布/已失效状态显示） -->
    <el-card v-if="showLogPanel" class="log-card">
      <div slot="header" class="log-header">
        <span>日志</span>
        <el-switch v-model="logExpanded" active-text="展开" inactive-text="折叠" />
      </div>
      <div v-show="logExpanded">
        <el-table :data="logList" border style="width: 100%">
          <el-table-column label="序号" width="60">
            <template slot-scope="scope">
              {{ (logPagination.currentPage - 1) * logPagination.pageSize + scope.$index + 1 }}
            </template>
          </el-table-column>
          <el-table-column label="操作" prop="operationName" width="100" />
          <el-table-column label="操作人" prop="operatorName" width="100" />
          <el-table-column label="操作时间" prop="operationTime" width="160" />
        </el-table>
        <Pagination
          :total="logPagination.total"
          :page-size="logPagination.pageSize"
          :current-page="logPagination.currentPage"
          @size-change="handleLogPageSizeChange"
          @current-change="handleLogPageChange"
        />
      </div>
    </el-card>

    <!-- 底部按钮 -->
    <div class="bottom-buttons">
      <template v-if="editable">
        <el-button type="primary" :loading="saveLoading" @click="handleSave">保存</el-button>
        <el-button type="success" :loading="publishLoading" @click="handleSaveAndPublish">保存并发布</el-button>
        <el-button @click="handlePublish">发布</el-button>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
      <template v-else>
        <el-button @click="handleBack">返回</el-button>
        <el-button v-if="formData.status === 'PUBLISHED'" type="warning" @click="handleInvalidate">失效</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </div>

    <!-- 场景配置弹窗 -->
    <SceneConfigDialog ref="sceneConfigDialog" :auth-letter-id="authLetterId" @success="loadSceneList" />
  </div>
</template>

<script>
import FlatMultiSelect from '@/components/FlatMultiSelect.vue'
import TreeMultiSelect from '@/components/TreeMultiSelect.vue'
import Pagination from '@/components/Pagination.vue'
import AttachmentUpload from '@/components/AttachmentUpload.vue'
import SceneConfigDialog from '@/components/SceneConfigDialog.vue'
import { getAuthLetterDetail, createAuthLetter, updateAuthLetter, publishAuthLetter, invalidateAuthLetter, deleteAuthLetter, getAuthLetterLogs } from '@/api/authLetter'
import { getSceneList, deleteScene, batchDeleteScenes } from '@/api/scene'
import { getLookupValues, LOOKUP_TYPES } from '@/api/lookup'

export default {
  name: 'AuthLetterDetail',
  components: {
    FlatMultiSelect,
    TreeMultiSelect,
    Pagination,
    AttachmentUpload,
    SceneConfigDialog
  },
  data() {
    return {
      isNew: true,
      authLetterId: null,
      formData: {
        name: '',
        authPublishLevel: [],
        authPublishOrg: [],
        authObjectLevel: [],
        applicableRegion: [],
        publishYear: '',
        summary: '',
        status: 'DRAFT'
      },
      rules: {
        name: [{ required: true, message: '请输入授权书名称', trigger: 'blur' }],
        authPublishLevel: [{ required: true, message: '请选择授权发布层级', trigger: 'change' }],
        authPublishOrg: [{ required: true, message: '请选择授权发布组织', trigger: 'change' }],
        authObjectLevel: [{ required: true, message: '请选择授权对象层级', trigger: 'change' }],
        applicableRegion: [{ required: true, message: '请选择适用区域', trigger: 'change' }],
        publishYear: [{ required: true, message: '请选择授权书发布年份', trigger: 'change' }],
        summary: [{ required: true, message: '请输入授权书内容摘要', trigger: 'blur' }]
      },
      authObjectLevelOptions: [],
      applicableRegionOptions: [],
      authPublishLevelOptions: [],
      authPublishOrgOptions: [],
      docTypeOptions: [],
      sceneList: [],
      selectedScenes: [],
      scenePagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      logList: [],
      logExpanded: false,
      logPagination: {
        total: 0,
        pageSize: 10,
        currentPage: 1
      },
      saveLoading: false,
      publishLoading: false
    }
  },
  computed: {
    editable() {
      return this.isNew || this.formData.status === 'DRAFT'
    },
    showLogPanel() {
      return this.formData.status === 'PUBLISHED' || this.formData.status === 'INVALID'
    }
  },
  async mounted() {
    await this.loadLookupData()
    const id = this.$route.query.id
    if (id) {
      this.isNew = false
      this.authLetterId = parseInt(id)
      await this.loadDetail()
      await this.loadSceneList()
      if (this.showLogPanel) {
        await this.loadLogList()
      }
    }
  },
  methods: {
    async loadLookupData() {
      try {
        const [authObjectLevel, applicableRegion, authPublishLevel, authPublishOrg, docType] = await Promise.all([
          getLookupValues(LOOKUP_TYPES.AUTH_OBJECT_LEVEL),
          getLookupValues(LOOKUP_TYPES.APPLICABLE_REGION),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_LEVEL),
          getLookupValues(LOOKUP_TYPES.AUTH_PUBLISH_ORG),
          getLookupValues(LOOKUP_TYPES.DOC_TYPE)
        ])
        this.authObjectLevelOptions = authObjectLevel.data || []
        this.applicableRegionOptions = applicableRegion.data || []
        this.authPublishLevelOptions = authPublishLevel.data || []
        this.authPublishOrgOptions = authPublishOrg.data || []
        this.docTypeOptions = docType.data || []
      } catch (error) {
        console.error('加载下拉数据失败:', error)
      }
    },
    async loadDetail() {
      try {
        const res = await getAuthLetterDetail(this.authLetterId)
        const data = res.data
        this.formData = {
          name: data.name || '',
          authPublishLevel: data.authPublishLevel || [],
          authPublishOrg: data.authPublishOrg || [],
          authObjectLevel: data.authObjectLevel || [],
          applicableRegion: data.applicableRegion || [],
          publishYear: data.publishYear ? String(data.publishYear) : '',
          summary: data.summary || '',
          status: data.status || 'DRAFT'
        }
      } catch (error) {
        this.$message.error('加载详情失败')
        this.$router.push('/AuthLetterList')
      }
    },
    async loadSceneList() {
      if (!this.authLetterId) return
      try {
        const res = await getSceneList(this.authLetterId, {
          pageNum: this.scenePagination.currentPage,
          pageSize: this.scenePagination.pageSize
        })
        this.sceneList = res.data.list || []
        this.scenePagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载场景列表失败:', error)
      }
    },
    async loadLogList() {
      if (!this.authLetterId) return
      try {
        const res = await getAuthLetterLogs(this.authLetterId, {
          pageNum: this.logPagination.currentPage,
          pageSize: this.logPagination.pageSize
        })
        this.logList = res.data.list || []
        this.logPagination.total = res.data.total || 0
      } catch (error) {
        console.error('加载日志列表失败:', error)
      }
    },
    handleSceneSelectionChange(rows) {
      this.selectedScenes = rows
    },
    handleScenePageSizeChange(val) {
      this.scenePagination.pageSize = val
      this.scenePagination.currentPage = 1
      this.loadSceneList()
    },
    handleScenePageChange(val) {
      this.scenePagination.currentPage = val
      this.loadSceneList()
    },
    handleLogPageSizeChange(val) {
      this.logPagination.pageSize = val
      this.logPagination.currentPage = 1
      this.loadLogList()
    },
    handleLogPageChange(val) {
      this.logPagination.currentPage = val
      this.loadLogList()
    },
    handleAddScene() {
      this.$refs.sceneConfigDialog.open()
    },
    handleEditScene(row) {
      this.$refs.sceneConfigDialog.open(row.id)
    },
    async handleDeleteScene(row) {
      try {
        await this.$confirm('确定要删除此场景吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteScene(this.authLetterId, row.id)
        this.loadSceneList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    async handleBatchDeleteScenes() {
      try {
        await this.$confirm('确定要删除选中的场景吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const ids = this.selectedScenes.map(row => row.id)
        await batchDeleteScenes(this.authLetterId, ids)
        this.loadSceneList()
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    async handleSave() {
      try {
        await this.$refs.baseForm.validate()
        this.saveLoading = true

        const data = {
          name: this.formData.name,
          authPublishLevel: this.formData.authPublishLevel,
          authPublishOrg: this.formData.authPublishOrg,
          authObjectLevel: this.formData.authObjectLevel,
          applicableRegion: this.formData.applicableRegion,
          publishYear: this.formData.publishYear ? parseInt(this.formData.publishYear) : null,
          summary: this.formData.summary
        }

        if (this.isNew) {
          const res = await createAuthLetter(data)
          this.authLetterId = res.data.id
          this.isNew = false
          this.$message.success('创建成功')
        } else {
          await updateAuthLetter(this.authLetterId, data)
          this.$message.success('保存成功')
        }
      } catch (error) {
        if (error.message) {
          this.$message.error(error.message)
        }
      } finally {
        this.saveLoading = false
      }
    },
    async handleSaveAndPublish() {
      try {
        await this.handleSave()
        await this.handlePublish()
      } catch (error) {
        console.error('保存并发布失败:', error)
      }
    },
    async handlePublish() {
      try {
        await this.$confirm('确定要发布此授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.publishLoading = true
        await publishAuthLetter(this.authLetterId)
        this.formData.status = 'PUBLISHED'
        this.$message.success('发布成功')
        await this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '发布失败')
        }
      } finally {
        this.publishLoading = false
      }
    },
    async handleCancel() {
      try {
        await this.$confirm('确定要取消吗？未保存的数据将丢失', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.$router.push('/AuthLetterList')
      } catch (error) {
        // 用户取消
      }
    },
    async handleDelete() {
      try {
        await this.$confirm('确定要删除此授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteAuthLetter(this.authLetterId)
        this.$message.success('删除成功')
        this.$router.push('/AuthLetterList')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '删除失败')
        }
      }
    },
    async handleInvalidate() {
      try {
        await this.$confirm('确定要失效此授权书吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await invalidateAuthLetter(this.authLetterId)
        this.formData.status = 'INVALID'
        this.$message.success('失效成功')
        await this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '失效失败')
        }
      }
    },
    handleBack() {
      this.$router.push('/AuthLetterList')
    }
  }
}
</script>

<style scoped>
.auth-letter-detail {
  padding: 20px;
}
.page-title {
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 500;
}
.info-card,
.attachment-card,
.rule-card,
.log-card {
  margin-bottom: 20px;
}
.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.rule-buttons {
  margin-bottom: 10px;
}
.empty-tip {
  color: #909399;
  text-align: center;
  padding: 20px;
}
.bottom-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 15px 20px;
  text-align: center;
  border-top: 1px solid #e8e8e8;
  z-index: 100;
}
</style>