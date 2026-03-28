<template>
  <el-dialog
    title="问卷答案维护"
    :visible.sync="visible"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <!-- 答案编号 -->
    <el-form ref="form" :model="formData" :rules="rules" label-width="100px">
      <el-form-item label="答案编号">
        <span>{{ formData.answerCode || '自动生成' }}</span>
      </el-form-item>

      <!-- 可选答案表格 -->
      <el-form-item label="可选答案" prop="answerTexts">
        <div class="answer-text-table">
          <div class="table-buttons">
            <el-button type="text" size="small" @click="addAnswerText">添加行</el-button>
            <el-button
              type="text"
              size="small"
              :disabled="selectedAnswerTexts.length === 0"
              @click="removeAnswerTexts"
            >
              删除
            </el-button>
          </div>
          <el-table
            ref="answerTextTable"
            :data="formData.answerTexts"
            border
            @selection-change="handleAnswerTextSelection"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column label="序号" width="60">
              <template slot-scope="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="答案" prop="answerText">
              <template slot-scope="scope">
                <el-input v-model="scope.row.answerText" placeholder="请输入答案" />
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
    </el-form>

    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确认</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'AnswerMaintainDialog',
  data() {
    return {
      visible: false,
      existingAnswers: [],
      formData: {
        answerCode: '',
        answerTexts: [],
        sortOrder: 1
      },
      selectedAnswerTexts: [],
      rules: {}
    }
  },
  methods: {
    open(answer, existingAnswers) {
      this.visible = true
      this.existingAnswers = existingAnswers || []
      if (answer) {
        // 编辑模式
        this.formData = {
          answerCode: answer.answerCode || '',
          answerTexts: answer.answerTexts ? [...answer.answerTexts] : [{ answerText: '', language: 'ZH' }],
          sortOrder: answer.sortOrder || 1,
          id: answer.id
        }
      } else {
        // 新增模式
        this.formData = {
          answerCode: '',
          answerTexts: [{ answerText: '', language: 'ZH' }],
          sortOrder: existingAnswers.length + 1
        }
      }
    },
    handleClose() {
      this.visible = false
      this.formData = {
        answerCode: '',
        answerTexts: [],
        sortOrder: 1
      }
    },
    handleAnswerTextSelection(rows) {
      this.selectedAnswerTexts = rows
    },
    addAnswerText() {
      this.formData.answerTexts.push({ answerText: '', language: 'ZH' })
    },
    removeAnswerTexts() {
      this.selectedAnswerTexts.forEach(row => {
        const index = this.formData.answerTexts.indexOf(row)
        if (index > -1) {
          this.formData.answerTexts.splice(index, 1)
        }
      })
    },
    async handleConfirm() {
      try {
        // 校验至少有一条有效答案
        const validAnswerTexts = this.formData.answerTexts.filter(
          t => t.answerText && t.language
        )
        if (validAnswerTexts.length === 0) {
          this.$message.warning('请至少添加一条答案')
          return
        }

        // 校验不能有重复语言
        const languages = validAnswerTexts.map(t => t.language)
        const uniqueLanguages = [...new Set(languages)]
        if (languages.length !== uniqueLanguages.length) {
          this.$message.warning('相同语言只能维护一个答案')
          return
        }

        // 生成答案编号（如果是新增）
        if (!this.formData.answerCode) {
          this.formData.answerCode = 'ANS' + Date.now()
        }

        const answerData = {
          answerCode: this.formData.answerCode,
          answerTexts: validAnswerTexts,
          sortOrder: this.formData.sortOrder,
          id: this.formData.id,
          answerTextDisplay: this.getDisplayText(validAnswerTexts),
          languageDisplay: this.getDisplayLanguage(validAnswerTexts)
        }

        this.visible = false
        this.$emit('success', answerData)
      } catch (error) {
        if (error.message) {
          this.$message.error(error.message)
        }
      }
    },
    getDisplayText(answerTexts) {
      if (!answerTexts || answerTexts.length === 0) return ''
      const zhText = answerTexts.find(t => t.language === 'ZH')
      return zhText ? zhText.answerText : answerTexts[0].answerText
    },
    getDisplayLanguage(answerTexts) {
      if (!answerTexts || answerTexts.length === 0) return ''
      return answerTexts.map(t => t.language === 'ZH' ? '中文' : '英文').join('/')
    }
  }
}
</script>

<style scoped>
.answer-text-table {
  width: 100%;
}
.table-buttons {
  margin-bottom: 10px;
}
</style>