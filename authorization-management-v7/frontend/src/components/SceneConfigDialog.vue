<template>
  <el-dialog
    title="场景配置"
    :visible.sync="visible"
    width="900px"
    append-to-body
    @close="handleClose"
  >
    <el-form ref="form" :model="formData" :rules="rules" label-width="100px">
      <!-- 第一行：场景名称、产业、业务场景 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="场景名称" prop="sceneName">
            <el-input v-model="formData.sceneName" placeholder="请输入场景名称" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产业" prop="industry">
            <TreeMultiSelect
              v-model="formData.industry"
              :tree-data="industryOptions"
              placeholder="请选择产业"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="业务场景" prop="businessScene">
            <el-select v-model="formData.businessScene" placeholder="请选择业务场景">
              <el-option
                v-for="item in businessSceneOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第二行：决策层级 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="决策层级" prop="decisionLevel">
            <el-select v-model="formData.decisionLevel" placeholder="请选择决策层级">
              <el-option
                v-for="item in decisionLevelOptions"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 第三行：具体规则 -->
      <el-row :gutter="20">
        <el-col :span="16">
          <el-form-item label="具体规则" prop="specificRule">
            <el-input
              v-model="formData.specificRule"
              type="textarea"
              :rows="3"
              placeholder="请输入具体规则描述"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 规则配置区域 -->
      <el-form-item label="规则配置">
        <div class="rule-section">
          <RuleCondition
            :conditions="formData.rules"
            :rule-param-options="ruleParamOptions"
            :operator-options="operatorOptions"
            :compare-type-options="compareTypeOptions"
            :compare-unit-options="compareUnitOptions"
            @update="handleRulesUpdate"
          />
        </div>
      </el-form-item>

      <!-- 问卷配置区域 -->
      <el-form-item label="问卷配置">
        <div class="questionnaire-section">
          <el-button type="text" @click="openQuestionConfigDialog">问卷题目配置</el-button>
          <div class="questionnaire-list">
            <div
              v-for="(item, index) in formData.questionnaires"
              :key="index"
              class="questionnaire-item"
            >
              <span class="logic-link" v-if="index > 0">[且]</span>
              <el-select v-model="item.questionId" placeholder="选择题目" @change="handleQuestionChange(item)">
                <el-option
                  v-for="q in questionOptions"
                  :key="q.questionId"
                  :label="q.questionText"
                  :value="q.questionId"
                />
              </el-select>
              <el-select v-model="item.correctAnswerId" placeholder="选择答案">
                <el-option
                  v-for="a in getAnswersForQuestion(item.questionId)"
                  :key="a.answerId"
                  :label="a.answerText"
                  :value="a.answerId"
                />
              </el-select>
              <el-button type="text" class="delete-btn" @click="removeQuestionnaire(index)">
                <i class="el-icon-delete" />
              </el-button>
            </div>
            <span class="action-link" @click="addQuestionnaire">[+问卷题目]</span>
          </div>
        </div>
      </el-form-item>
    </el-form>

    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确认</el-button>
    </span>

    <!-- 问卷题目配置弹窗 -->
    <QuestionConfigDialog ref="questionConfigDialog" @success="loadQuestionOptions" />
  </el-dialog>
</template>

<script>
import TreeMultiSelect from './TreeMultiSelect.vue'
import RuleCondition from './RuleCondition.vue'
import QuestionConfigDialog from './QuestionConfigDialog.vue'
import { getSceneDetail, createScene, updateScene } from '@/api/scene'
import { getQuestionOptions } from '@/api/questionnaire'
import { getLookupValues, LOOKUP_TYPES } from '@/api/lookup'
import { getRuleParamOptions } from '@/api/ruleParam'

export default {
  name: 'SceneConfigDialog',
  components: {
    TreeMultiSelect,
    RuleCondition,
    QuestionConfigDialog
  },
  props: {
    authLetterId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      visible: false,
      loading: false,
      isEdit: false,
      currentId: null,
      formData: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        rules: [],
        questionnaires: []
      },
      rules: {
        sceneName: [{ required: true, message: '请输入场景名称', trigger: 'blur' }],
        industry: [{ required: true, message: '请选择产业', trigger: 'change' }],
        businessScene: [{ required: true, message: '请选择业务场景', trigger: 'change' }],
        decisionLevel: [{ required: true, message: '请选择决策层级', trigger: 'change' }]
      },
      industryOptions: [],
      businessSceneOptions: [],
      decisionLevelOptions: [],
      ruleParamOptions: [],
      operatorOptions: [],
      compareTypeOptions: [],
      compareUnitOptions: [],
      questionOptions: []
    }
  },
  async mounted() {
    await this.loadLookupData()
    await this.loadRuleParamOptions()
    await this.loadQuestionOptions()
  },
  methods: {
    async loadLookupData() {
      try {
        const [industry, businessScene, decisionLevel, operator, compareType, compareUnit] = await Promise.all([
          getLookupValues(LOOKUP_TYPES.INDUSTRY),
          getLookupValues(LOOKUP_TYPES.BUSINESS_SCENE),
          getLookupValues(LOOKUP_TYPES.DECISION_LEVEL),
          getLookupValues(LOOKUP_TYPES.OPERATOR),
          getLookupValues(LOOKUP_TYPES.COMPARE_TYPE),
          getLookupValues(LOOKUP_TYPES.COMPARE_UNIT)
        ])
        this.industryOptions = industry.data || []
        this.businessSceneOptions = businessScene.data || []
        this.decisionLevelOptions = decisionLevel.data || []
        this.operatorOptions = operator.data || []
        this.compareTypeOptions = compareType.data || []
        this.compareUnitOptions = compareUnit.data || []
      } catch (error) {
        console.error('加载下拉数据失败:', error)
      }
    },
    async loadRuleParamOptions() {
      try {
        const res = await getRuleParamOptions()
        this.ruleParamOptions = res.data || []
      } catch (error) {
        console.error('加载规则参数失败:', error)
      }
    },
    async loadQuestionOptions() {
      try {
        const res = await getQuestionOptions()
        this.questionOptions = res.data || []
      } catch (error) {
        console.error('加载问卷题目失败:', error)
      }
    },
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
        const res = await getSceneDetail(this.authLetterId, id)
        const data = res.data
        this.formData = {
          sceneName: data.sceneName || '',
          industry: data.industry || [],
          businessScene: data.businessScene || '',
          decisionLevel: data.decisionLevel || '',
          specificRule: data.specificRule || '',
          rules: this.formatRules(data.rules || []),
          questionnaires: this.formatQuestionnaires(data.questionnaires || [])
        }
      } catch (error) {
        this.$message.error('加载详情失败')
        this.visible = false
      }
    },
    formatRules(rules) {
      if (!rules || rules.length === 0) return []
      return rules.flatMap(rule => rule.conditions || [])
    },
    formatQuestionnaires(questionnaires) {
      return questionnaires.map(q => ({
        questionId: q.questionId,
        correctAnswerId: q.correctAnswerId,
        sortOrder: q.sortOrder
      }))
    },
    resetForm() {
      this.formData = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        rules: [],
        questionnaires: []
      }
    },
    handleClose() {
      this.visible = false
      this.resetForm()
      this.$refs.form.resetFields()
    },
    handleRulesUpdate(conditions) {
      this.formData.rules = conditions
    },
    openQuestionConfigDialog() {
      this.$refs.questionConfigDialog.open()
    },
    getAnswersForQuestion(questionId) {
      const question = this.questionOptions.find(q => q.questionId === questionId)
      return question ? question.answers : []
    },
    handleQuestionChange(item) {
      item.correctAnswerId = null
    },
    addQuestionnaire() {
      this.formData.questionnaires.push({
        questionId: null,
        correctAnswerId: null,
        sortOrder: this.formData.questionnaires.length + 1
      })
    },
    removeQuestionnaire(index) {
      this.formData.questionnaires.splice(index, 1)
    },
    async handleConfirm() {
      try {
        await this.$refs.form.validate()

        // 校验规则和问卷至少配置一项
        const hasRules = this.formData.rules.length > 0
        const hasQuestionnaires = this.formData.questionnaires.some(q => q.questionId && q.correctAnswerId)
        if (!hasRules && !hasQuestionnaires) {
          this.$message.warning('规则和问卷至少需要配置一项')
          return
        }

        this.loading = true

        const data = {
          sceneName: this.formData.sceneName,
          industry: this.formData.industry,
          businessScene: this.formData.businessScene,
          decisionLevel: this.formData.decisionLevel,
          specificRule: this.formData.specificRule,
          rules: [{
            ruleName: '规则组1',
            logicType: 'AND',
            conditions: this.formData.rules.map(c => ({
              ...c,
              isGroup: c.isGroup || false
            }))
          }],
          questionnaires: this.formData.questionnaires.filter(q => q.questionId && q.correctAnswerId)
        }

        if (this.isEdit) {
          await updateScene(this.authLetterId, this.currentId, data)
          this.$message.success('更新成功')
        } else {
          await createScene(this.authLetterId, data)
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
.rule-section {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}
.questionnaire-section {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
}
.questionnaire-list {
  margin-top: 10px;
}
.questionnaire-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.logic-link {
  color: #409EFF;
  font-size: 12px;
}
.action-link {
  color: #409EFF;
  cursor: pointer;
  font-size: 12px;
}
.action-link:hover {
  text-decoration: underline;
}
.delete-btn {
  color: #909399;
}
.delete-btn:hover {
  color: #F56C6C;
}
</style>