<template>
  <el-dialog
    title="规则参数详情"
    :visible.sync="visible"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <el-form ref="form" :model="formData" :rules="rules" label-width="100px">
      <!-- 第一行：名称和名称英文 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="名称英文" prop="nameEn">
            <el-input v-model="formData.nameEn" placeholder="请输入名称英文" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 业务对象和取值逻辑（可增减） -->
      <div class="business-objects-section">
        <label class="section-label">业务对象配置</label>
        <div class="business-objects-list">
          <el-row
            v-for="(item, index) in formData.businessObjects"
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
                v-if="index === formData.businessObjects.length - 1"
                type="primary"
                icon="el-icon-plus"
                size="small"
                @click="addBusinessObject"
              />
              <el-button
                v-if="formData.businessObjects.length > 1"
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
            <el-select v-model="formData.status" placeholder="请选择">
              <el-option label="是" value="ACTIVE" />
              <el-option label="否" value="INACTIVE" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="数据类型" prop="dataType">
            <el-select v-model="formData.dataType" placeholder="请选择">
              <el-option label="文本" value="TEXT" />
              <el-option label="数值" value="NUMBER" />
              <el-option label="比对字段" value="FIELD" />
              <el-option label="比率" value="RATIO" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 关联字段（FIELD类型时显示） -->
      <el-row v-if="formData.dataType === 'FIELD'" :gutter="20">
        <el-col :span="12">
          <el-form-item label="关联字段" prop="referenceFieldId">
            <el-select v-model="formData.referenceFieldId" placeholder="请选择关联字段">
              <el-option
                v-for="item in ruleParamOptions"
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
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确认</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { createRuleParam, updateRuleParam, getRuleParamDetail } from '@/api/ruleParam'

export default {
  name: 'RuleParamDetailDialog',
  props: {
    ruleParamOptions: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      isEdit: false,
      currentId: null,
      formData: {
        name: '',
        nameEn: '',
        businessObjects: [
          { businessObject: '', valueLogic: '' }
        ],
        status: 'ACTIVE',
        dataType: 'TEXT',
        referenceFieldId: null
      },
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        nameEn: [{ required: true, message: '请输入名称英文', trigger: 'blur' }],
        status: [{ required: true, message: '请选择是否生效', trigger: 'change' }],
        dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }]
      }
    }
  },
  methods: {
    async open(id) {
      this.visible = true
      if (id) {
        this.isEdit = true
        this.currentId = id
        await this.loadDetail(id)
      } else {
        this.isEdit = false
        this.currentId = null
        this.resetForm()
      }
    },
    async loadDetail(id) {
      try {
        const res = await getRuleParamDetail(id)
        this.formData = {
          name: res.data.name || '',
          nameEn: res.data.nameEn || '',
          businessObjects: res.data.businessObjects || [{ businessObject: '', valueLogic: '' }],
          status: res.data.status || 'ACTIVE',
          dataType: res.data.dataType || 'TEXT',
          referenceFieldId: res.data.referenceFieldId || null
        }
      } catch (error) {
        this.$message.error('加载详情失败')
        this.visible = false
      }
    },
    resetForm() {
      this.formData = {
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
      this.formData.businessObjects.push({ businessObject: '', valueLogic: '' })
    },
    removeBusinessObject(index) {
      this.formData.businessObjects.splice(index, 1)
    },
    handleClose() {
      this.visible = false
      this.resetForm()
      this.$refs.form.resetFields()
    },
    async handleConfirm() {
      try {
        await this.$refs.form.validate()
        this.loading = true

        // 过滤空的业务对象
        const businessObjects = this.formData.businessObjects.filter(
          item => item.businessObject || item.valueLogic
        )

        const data = {
          ...this.formData,
          businessObjects: businessObjects.length > 0 ? businessObjects : [{ businessObject: '', valueLogic: '' }]
        }

        if (this.isEdit) {
          await updateRuleParam(this.currentId, data)
          this.$message.success('更新成功')
        } else {
          await createRuleParam(data)
          this.$message.success('创建成功')
        }

        this.visible = false
        this.$emit('success')
      } catch (error) {
        if (error.message) {
          this.$message.error(error.message)
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
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