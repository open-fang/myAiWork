<template>
  <el-dialog
    title="问卷题目维护"
    :visible.sync="visible"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <!-- 题目编号 -->
    <el-form ref="form" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="题目编号">
        <span>{{ formData.questionCode || '自动生成' }}</span>
      </el-form-item>

      <!-- 题目表格 -->
      <el-form-item label="题目" prop="questionTexts">
        <div class="question-table">
          <div class="table-buttons">
            <el-button type="text" size="small" @click="addQuestionText">添加行</el-button>
            <el-button
              type="text"
              size="small"
              :disabled="selectedQuestionTexts.length === 0"
              @click="removeQuestionTexts"
            >
              删除
            </el-button>
          </div>
          <el-table
            ref="questionTextTable"
            :data="formData.questionTexts"
            border
            @selection-change="handleQuestionTextSelection"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column label="序号" width="60">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="题目" prop="questionText">
              <template slot-scope="scope">
                <el-input v-model="scope.row.questionText" placeholder="请输入题目" />
              </template>
            </el-table-column>
            <el-table-column label="语言" prop="language" width="100">
              <template slot-scope="scope">
                <el-select v-model="scope.row.language" placeholder="请选择">
                  <el-option label="中文" value="ZH" />
                  <el-option label="英文" value="EN" />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-form-item>

      <!-- 可选答案表格 -->
      <el-form-item label="可选答案" prop="answers">
        <div class="answer-table">
          <div class="table-buttons">
            <el-button type="text" size="small" @click="openAnswerMaintainDialog">新增</el-button>
            <el-button
              type="text"
              size="small"
              :disabled="selectedAnswers.length === 0"
              @click="removeAnswers"
            >
              删除
            </el-button>
          </div>
          <el-table
            ref="answerTable"
            :data="formData.answers"
            border
            @selection-change="handleAnswerSelection"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column label="序号" width="60">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="editAnswer(scope.row)">编辑</el-button>
              </template>
            </el-table-column>
            <el-table-column label="答案编号" prop="answerCode" width="150" />
            <el-table-column label="答案" prop="answerTextDisplay" />
            <el-table-column label="语言" prop="languageDisplay" width="80" />
            <el-table-column label="排序号" prop="sortOrder" width="80" />
          </el-table>
        </div>
      </el-form-item>
    </el-form>

    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确认</el-button>
    </span>

    <!-- 问卷答案维护弹窗 -->
    <AnswerMaintainDialog ref="answerMaintainDialog" @success="handleAnswerSuccess" />
  </el-dialog>
</template>

<script>
import AnswerMaintainDialog from './AnswerMaintainDialog.vue'
import { getQuestionDetail, createQuestion, updateQuestion } from '@/api/questionnaire'

export default {
  name: 'QuestionMaintainDialog',
  components: {
    AnswerMaintainDialog
  },
  data() {
    return {
      visible: false,
      loading: false,
      isEdit: false,
      currentId: null,
      formData: {
        questionCode: '',
        questionTexts: [],
        answers: []
      },
      selectedQuestionTexts: [],
      selectedAnswers: [],
      rules: {}
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
        const res = await getQuestionDetail(id)
        const data = res.data
        this.formData = {
          questionCode: data.questionCode || '',
          questionTexts: data.questionTexts || [],
          answers: this.formatAnswers(data.answers || [])
        }
      } catch (error) {
        this.$message.error('加载详情失败')
        this.visible = false
      }
    },
    formatAnswers(answers) {
      return answers.map(answer => ({
        id: answer.id,
        answerCode: answer.answerCode,
        answerTexts: answer.answerTexts || [],
        answerTextDisplay: this.getDisplayText(answer.answerTexts),
        languageDisplay: this.getDisplayLanguage(answer.answerTexts),
        sortOrder: answer.sortOrder
      }))
    },
    getDisplayText(answerTexts) {
      if (!answerTexts || answerTexts.length === 0) return ''
      const zhText = answerTexts.find(t => t.language === 'ZH')
      return zhText ? zhText.answerText : answerTexts[0].answerText
    },
    getDisplayLanguage(answerTexts) {
      if (!answerTexts || answerTexts.length === 0) return ''
      return answerTexts.map(t => t.language === 'ZH' ? '中文' : '英文').join('/')
    },
    resetForm() {
      this.formData = {
        questionCode: '',
        questionTexts: [
          { questionText: '', language: 'ZH' }
        ],
        answers: []
      }
    },
    handleClose() {
      this.visible = false
      this.resetForm()
    },
    handleQuestionTextSelection(rows) {
      this.selectedQuestionTexts = rows
    },
    handleAnswerSelection(rows) {
      this.selectedAnswers = rows
    },
    addQuestionText() {
      this.formData.questionTexts.push({ questionText: '', language: 'ZH' })
    },
    removeQuestionTexts() {
      this.selectedQuestionTexts.forEach(row => {
        const index = this.formData.questionTexts.indexOf(row)
        if (index > -1) {
          this.formData.questionTexts.splice(index, 1)
        }
      })
    },
    openAnswerMaintainDialog() {
      this.$refs.answerMaintainDialog.open(null, this.formData.answers)
    },
    editAnswer(answer) {
      this.$refs.answerMaintainDialog.open(answer, this.formData.answers)
    },
    removeAnswers() {
      this.selectedAnswers.forEach(row => {
        const index = this.formData.answers.indexOf(row)
        if (index > -1) {
          this.formData.answers.splice(index, 1)
        }
      })
    },
    handleAnswerSuccess(answer) {
      // 更新或添加答案
      const existingIndex = this.formData.answers.findIndex(a => a.answerCode === answer.answerCode)
      if (existingIndex > -1) {
        this.formData.answers.splice(existingIndex, 1, answer)
      } else {
        answer.sortOrder = this.formData.answers.length + 1
        this.formData.answers.push(answer)
      }
    },
    async handleConfirm() {
      try {
        // 校验题目表格至少有一行
        const validQuestionTexts = this.formData.questionTexts.filter(
          t => t.questionText && t.language
        )
        if (validQuestionTexts.length === 0) {
          this.$message.warning('请至少添加一条题目')
          return
        }

        // 校验答案表格至少有一行
        if (this.formData.answers.length === 0) {
          this.$message.warning('请至少添加一条可选答案')
          return
        }

        this.loading = true

        const data = {
          questionTexts: validQuestionTexts,
          answers: this.formData.answers.map(a => ({
            answerTexts: a.answerTexts,
            sortOrder: a.sortOrder
          }))
        }

        if (this.isEdit) {
          await updateQuestion(this.currentId, data)
          this.$message.success('更新成功')
        } else {
          const res = await createQuestion(data)
          this.formData.questionCode = res.data.questionCode
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
.question-table,
.answer-table {
  width: 100%;
}
.table-buttons {
  margin-bottom: 10px;
}
</style>