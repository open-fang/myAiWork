<template>
  <div class="page-container">
    <h1 class="page-title">{{ isEdit ? '编辑授权书' : isNew ? '新建授权书' : '授权书详情' }}</h1>

    <!-- 基本信息 -->
    <div class="section">
      <h3 class="section-title">基本信息</h3>
      <div class="form-row">
        <div class="form-item">
          <label class="required">授权书名称</label>
          <input type="text" v-model="formData.name" :disabled="!isEditable" placeholder="请输入授权书名称" />
        </div>
        <div class="form-item">
          <label class="required">授权发布层级</label>
          <custom-select
            v-model="formData.authPublishLevel"
            :options="lookupData.authPublishLevel"
            :disabled="!isEditable"
            placeholder="请选择授权发布层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">授权发布组织</label>
          <tree-select
            v-model="formData.authPublishOrg"
            :options="lookupData.authPublishOrg"
            :disabled="!isEditable"
            placeholder="请选择授权发布组织"
            multiple
          />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item">
          <label class="required">授权对象层级</label>
          <custom-select
            v-model="formData.authObjectLevel"
            :options="lookupData.authObjectLevel"
            :disabled="!isEditable"
            placeholder="请选择授权对象层级"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">适用区域</label>
          <tree-select
            v-model="formData.applicableRegion"
            :options="lookupData.applicableRegion"
            :disabled="!isEditable"
            placeholder="请选择适用区域"
            multiple
          />
        </div>
        <div class="form-item">
          <label class="required">发布年份</label>
          <year-select v-model="formData.publishYear" :disabled="!isEditable" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-item" style="flex: 1; min-width: 100%;">
          <label class="required">内容摘要</label>
          <textarea v-model="formData.summary" :disabled="!isEditable" placeholder="请输入授权书内容摘要" maxlength="4000"></textarea>
        </div>
      </div>
    </div>

    <!-- 附件区块 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">附件</h3>
        <div class="section-actions" v-if="isEditable">
          <button class="btn btn-primary" @click="handleUploadAttachment">上传</button>
          <button class="btn" @click="handleDownloadAttachment" :disabled="selectedAttachments.length === 0">下载</button>
          <button class="btn btn-danger" @click="handleDeleteAttachment" :disabled="selectedAttachments.length === 0">删除</button>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" v-model="isAllAttachmentSelected" @change="handleAttachmentSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>文档名称</th>
            <th>文档类型</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in attachmentList" :key="item.id">
            <td><input type="checkbox" v-model="selectedAttachments" :value="item.id" /></td>
            <td>{{ (attachmentPageNum - 1) * attachmentPageSize + index + 1 }}</td>
            <td>
              <span class="link" @click="handleDownloadFile(item)">下载</span>
            </td>
            <td><span class="link" @click="handleDownloadFile(item)">{{ item.docName }}</span></td>
            <td>{{ item.docType }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ item.updatedTime }}</td>
          </tr>
          <tr v-if="attachmentList.length === 0">
            <td colspan="9" style="text-align: center; color: #999;">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ attachmentTotal }} 条</span>
        <select v-model="attachmentPageSize" @change="loadAttachments">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="attachmentPageNum === 1" @click="attachmentPageNum--; loadAttachments()">上一页</button>
        <span>第 {{ attachmentPageNum }} 页</span>
        <button :disabled="attachmentPageNum * attachmentPageSize >= attachmentTotal" @click="attachmentPageNum++; loadAttachments()">下一页</button>
      </div>
    </div>

    <!-- 授权规则区块 -->
    <div class="section">
      <div class="section-header">
        <h3 class="section-title">授权规则</h3>
        <div class="section-actions" v-if="isEditable">
          <button class="btn btn-primary" @click="handleAddScene">添加场景</button>
          <button class="btn btn-danger" @click="handleDeleteScenes" :disabled="selectedScenes.length === 0">删除</button>
        </div>
      </div>
      <table>
        <thead>
          <tr>
            <th><input type="checkbox" v-model="isAllSceneSelected" @change="handleSceneSelectAll" /></th>
            <th>序号</th>
            <th>操作</th>
            <th>场景</th>
            <th>产业</th>
            <th>业务场景</th>
            <th>具体规则</th>
            <th>决策层级</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>更新人</th>
            <th>更新时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, index) in sceneList" :key="item.id">
            <td><input type="checkbox" v-model="selectedScenes" :value="item.id" /></td>
            <td>{{ (scenePageNum - 1) * scenePageSize + index + 1 }}</td>
            <td>
              <span class="action-btn action-edit" @click="handleEditScene(item)">编辑</span>
              <span class="action-btn action-delete" @click="handleDeleteScene(item.id)">删除</span>
            </td>
            <td>{{ item.sceneName }}</td>
            <td>{{ formatArray(item.industry) }}</td>
            <td>{{ item.businessScene }}</td>
            <td>{{ item.specificRule }}</td>
            <td>{{ item.decisionLevel }}</td>
            <td>{{ item.createdBy }}</td>
            <td>{{ item.createdTime }}</td>
            <td>{{ item.updatedBy }}</td>
            <td>{{ item.updatedTime }}</td>
          </tr>
          <tr v-if="sceneList.length === 0">
            <td colspan="12" style="text-align: center; color: #999;">暂无数据</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span>共 {{ sceneTotal }} 条</span>
        <select v-model="scenePageSize" @change="loadScenes">
          <option :value="10">10条/页</option>
          <option :value="30">30条/页</option>
          <option :value="50">50条/页</option>
        </select>
        <button :disabled="scenePageNum === 1" @click="scenePageNum--; loadScenes()">上一页</button>
        <span>第 {{ scenePageNum }} 页</span>
        <button :disabled="scenePageNum * scenePageSize >= sceneTotal" @click="scenePageNum++; loadScenes()">下一页</button>
      </div>
    </div>

    <!-- 日志区块（已发布状态显示） -->
    <div class="section" v-if="formData.status === 'PUBLISHED' || formData.status === 'INVALID'">
      <div class="collapse-panel">
        <div class="collapse-header" @click="logCollapsed = !logCollapsed">
          <span>操作日志</span>
          <span :class="['collapse-icon', { collapsed: logCollapsed }]">▼</span>
        </div>
        <div class="collapse-content" v-show="!logCollapsed">
          <table>
            <thead>
              <tr>
                <th>序号</th>
                <th>操作</th>
                <th>操作人</th>
                <th>操作时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in logList" :key="item.id">
                <td>{{ (logPageNum - 1) * logPageSize + index + 1 }}</td>
                <td>{{ getOperationText(item.operation) }}</td>
                <td>{{ item.operator }}</td>
                <td>{{ item.operationTime }}</td>
              </tr>
            </tbody>
          </table>
          <div class="pagination">
            <span>共 {{ logTotal }} 条</span>
            <button :disabled="logPageNum === 1" @click="logPageNum--; loadLogs()">上一页</button>
            <span>第 {{ logPageNum }} 页</span>
            <button :disabled="logPageNum * logPageSize >= logTotal" @click="logPageNum++; loadLogs()">下一页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="footer-actions">
      <template v-if="formData.status === 'DRAFT'">
        <button class="btn btn-primary" @click="handleSave">保存</button>
        <button class="btn btn-primary" @click="handleSaveAndPublish">保存并发布</button>
        <button class="btn" @click="handlePublish">发布</button>
        <button class="btn" @click="handleCancel">取消</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
      <template v-else-if="formData.status === 'PUBLISHED'">
        <button class="btn" @click="handleBack">返回</button>
        <button class="btn btn-danger" @click="handleInvalidate">失效</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
      <template v-else>
        <button class="btn" @click="handleBack">返回</button>
        <button class="btn btn-danger" @click="handleDelete">删除</button>
      </template>
    </div>

    <!-- 场景配置弹窗 -->
    <div class="modal-overlay" v-if="showSceneModal">
      <div class="modal-content" style="width: 800px;">
        <div class="modal-header">
          <h3>{{ editingScene ? '编辑场景' : '添加场景' }}</h3>
          <span class="modal-close" @click="showSceneModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-item">
              <label class="required">场景名称</label>
              <input type="text" v-model="sceneForm.sceneName" placeholder="请输入场景名称" />
            </div>
            <div class="form-item">
              <label class="required">产业</label>
              <tree-select
                v-model="sceneForm.industry"
                :options="lookupData.industry"
                placeholder="请选择产业"
                multiple
              />
            </div>
            <div class="form-item">
              <label class="required">业务场景</label>
              <custom-select
                v-model="sceneForm.businessScene"
                :options="lookupData.businessScene"
                placeholder="请选择业务场景"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-item">
              <label class="required">决策层级</label>
              <custom-select
                v-model="sceneForm.decisionLevel"
                :options="lookupData.decisionLevel"
                placeholder="请选择决策层级"
              />
            </div>
            <div class="form-item" style="flex: 2;">
              <label class="required">具体规则</label>
              <textarea v-model="sceneForm.specificRule" placeholder="请输入具体规则" maxlength="1000"></textarea>
            </div>
          </div>

          <!-- 规则配置 -->
          <div class="rule-config">
            <h4>规则配置</h4>
            <button class="btn btn-link" @click="addCondition">+ 新增条件</button>
            <button class="btn btn-link" @click="addConditionGroup">+ 新增子条件组</button>

            <div v-for="(condition, index) in sceneForm.conditions" :key="index">
              <div v-if="index > 0" class="logic-toggle" @click="toggleLogic(condition)">
                {{ condition.logicType === 'AND' ? '且' : '或' }}
              </div>
              <div v-if="condition.isGroup" class="condition-group">
                <div v-for="(subCond, subIndex) in condition.children" :key="subIndex">
                  <div v-if="subIndex > 0" class="logic-toggle" @click="toggleLogic(subCond)">
                    {{ subCond.logicType === 'AND' ? '且' : '或' }}
                  </div>
                  <div class="rule-condition-row">
                    <select v-model="subCond.fieldCode">
                      <option value="">请选择字段</option>
                      <option v-for="f in ruleParams" :key="f.nameEn" :value="f.nameEn">{{ f.name }}</option>
                    </select>
                    <select v-model="subCond.operator">
                      <option value=">">大于</option>
                      <option value="<">小于</option>
                      <option value="=">等于</option>
                      <option value=">=">大于等于</option>
                      <option value="<=">小于等于</option>
                      <option value="!=">不等于</option>
                    </select>
                    <select v-model="subCond.compareType">
                      <option value="NUMBER">数值</option>
                      <option value="TEXT">文本</option>
                      <option value="FIELD">对比字段</option>
                      <option value="RATIO">比例</option>
                    </select>
                    <input type="text" v-model="subCond.compareValue" placeholder="对比值" style="width: 150px;" />
                    <select v-model="subCond.compareUnit" v-if="subCond.compareType === 'NUMBER'">
                      <option value="">请选择单位</option>
                      <option value="CNY">人民币(元)</option>
                      <option value="USD">美元</option>
                      <option value="WAN">万</option>
                      <option value="YI">亿</option>
                    </select>
                    <span class="icon-delete" @click="removeCondition(condition.children, subIndex)">🗑️</span>
                  </div>
                </div>
                <button class="btn btn-link" @click="addSubCondition(condition)">+ 新增条件</button>
                <span class="icon-delete" @click="removeConditionGroup(index)" style="margin-left: 16px;">移除条件组</span>
              </div>
              <div v-else class="rule-condition-row">
                <select v-model="condition.fieldCode">
                  <option value="">请选择字段</option>
                  <option v-for="f in ruleParams" :key="f.nameEn" :value="f.nameEn">{{ f.name }}</option>
                </select>
                <select v-model="condition.operator">
                  <option value=">">大于</option>
                  <option value="<">小于</option>
                  <option value="=">等于</option>
                  <option value=">=">大于等于</option>
                  <option value="<=">小于等于</option>
                  <option value="!=">不等于</option>
                </select>
                <select v-model="condition.compareType">
                  <option value="NUMBER">数值</option>
                  <option value="TEXT">文本</option>
                  <option value="FIELD">对比字段</option>
                  <option value="RATIO">比例</option>
                </select>
                <input type="text" v-model="condition.compareValue" placeholder="对比值" style="width: 150px;" />
                <select v-model="condition.compareUnit" v-if="condition.compareType === 'NUMBER'">
                  <option value="">请选择单位</option>
                  <option value="CNY">人民币(元)</option>
                  <option value="USD">美元</option>
                  <option value="WAN">万</option>
                  <option value="YI">亿</option>
                </select>
                <span class="icon-delete" @click="removeCondition(sceneForm.conditions, index)">🗑️</span>
              </div>
            </div>
          </div>

          <!-- 问卷配置 -->
          <div class="questionnaire-section">
            <div class="questionnaire-header">
              <h4>问卷配置</h4>
              <button class="btn btn-link" @click="addSceneQuestion">+ 问卷题目</button>
              <button class="btn btn-primary btn-sm" @click="openQuestionnaireConfig">问卷题目配置</button>
            </div>
            <!-- 已配置的问卷题目列表 -->
            <div class="scene-question-list" v-if="sceneForm.sceneQuestions && sceneForm.sceneQuestions.length > 0">
              <div class="scene-question-item" v-for="(sq, index) in sceneForm.sceneQuestions" :key="index">
                <div class="scene-question-select">
                  <label>题目：</label>
                  <select v-model="sq.questionId" @change="onQuestionChange(sq)" style="flex: 1;">
                    <option value="">请选择题目</option>
                    <option v-for="q in allQuestions" :key="q.id" :value="q.id">{{ getQuestionText(q, 'ZH') }}</option>
                  </select>
                </div>
                <div class="scene-question-answer" v-if="sq.questionId">
                  <label>正确答案：</label>
                  <select v-model="sq.correctAnswerId" style="flex: 1;">
                    <option value="">请选择答案</option>
                    <option v-for="a in sq.availableAnswers" :key="a.id" :value="a.id">{{ getAnswerText(a, 'ZH') }}</option>
                  </select>
                </div>
                <span class="scene-question-delete" @click="removeSceneQuestion(index)">删除</span>
              </div>
            </div>
            <div class="form-tip" v-else>点击"+问卷题目"添加问卷题目，或通过"问卷题目配置"按钮管理题目库。</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleSaveScene">确定</button>
          <button class="btn" @click="showSceneModal = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 附件上传弹窗 -->
    <div class="modal-overlay" v-if="showUploadModal">
      <div class="modal-content" style="width: 400px;">
        <div class="modal-header">
          <h3>上传附件</h3>
          <span class="modal-close" @click="showUploadModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>文档名称</label>
            <input type="text" v-model="uploadForm.docName" placeholder="请输入文档名称" />
          </div>
          <div class="form-item">
            <label>文档类型</label>
            <custom-select v-model="uploadForm.docType" :options="docTypeOptions" placeholder="请选择文档类型" />
          </div>
          <div class="form-item">
            <label>文档ID</label>
            <input type="text" v-model="uploadForm.docId" placeholder="请输入文档ID（模拟）" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="handleConfirmUpload">确定</button>
          <button class="btn" @click="showUploadModal = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 问卷题目配置弹窗 -->
    <div class="modal-overlay" v-if="showQuestionnaireModal">
      <div class="modal-content questionnaire-modal">
        <div class="modal-header">
          <h3>问卷题目配置</h3>
          <span class="modal-close" @click="showQuestionnaireModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <!-- 搜索条件 -->
          <div class="search-form">
            <div class="form-row">
              <div class="form-item">
                <label>题目编号</label>
                <input type="text" v-model="questionSearch.questionCode" placeholder="请输入题目编号" />
              </div>
              <div class="form-item">
                <label>题目</label>
                <input type="text" v-model="questionSearch.questionText" placeholder="请输入题目" />
              </div>
              <div class="form-item">
                <label>语言</label>
                <custom-select v-model="questionSearch.language" :options="languageOptions" placeholder="请选择语言" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-item">
                <label>创建人</label>
                <input type="text" v-model="questionSearch.createdBy" placeholder="请输入创建人" />
              </div>
              <div class="form-item">
                <label>创建时间</label>
                <input type="date" v-model="questionSearch.createdDate" />
              </div>
              <div class="form-item">
                <label>更新人</label>
                <input type="text" v-model="questionSearch.updatedBy" placeholder="请输入更新人" />
              </div>
            </div>
            <div class="form-row">
              <button class="btn btn-primary" @click="loadQuestions">查询</button>
              <button class="btn" @click="resetQuestionSearch">重置</button>
            </div>
          </div>

          <!-- 功能按钮 -->
          <div class="table-toolbar">
            <button class="btn btn-primary" @click="openQuestionModal">新增</button>
            <button class="btn" @click="batchDeleteQuestions" :disabled="selectedQuestions.length === 0">删除</button>
          </div>

          <!-- 题目列表 -->
          <table>
            <thead>
              <tr>
                <th><input type="checkbox" v-model="isAllQuestionSelected" @change="handleQuestionSelectAll" /></th>
                <th>序号</th>
                <th>操作</th>
                <th>题目编号</th>
                <th>题目</th>
                <th>语言</th>
                <th>创建人</th>
                <th>创建时间</th>
                <th>更新人</th>
                <th>更新时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in questionList" :key="item.id">
                <td><input type="checkbox" v-model="selectedQuestions" :value="item.id" /></td>
                <td>{{ (questionPageNum - 1) * questionPageSize + index + 1 }}</td>
                <td>
                  <span class="action-btn action-edit" @click="editQuestion(item)">编辑</span>
                  <span class="action-btn action-delete" @click="deleteQuestion(item.id)">删除</span>
                </td>
                <td>{{ item.questionCode }}</td>
                <td>{{ getQuestionText(item, 'ZH') }}</td>
                <td>{{ getQuestionLanguage(item) }}</td>
                <td>{{ item.createdBy }}</td>
                <td>{{ item.createdTime }}</td>
                <td>{{ item.updatedBy }}</td>
                <td>{{ item.updatedTime }}</td>
              </tr>
              <tr v-if="questionList.length === 0">
                <td colspan="10" style="text-align: center; color: #999;">暂无数据</td>
              </tr>
            </tbody>
          </table>

          <!-- 分页 -->
          <div class="pagination">
            <span>共 {{ questionTotal }} 条</span>
            <select v-model="questionPageSize" @change="loadQuestions">
              <option :value="10">10条/页</option>
              <option :value="30">30条/页</option>
            </select>
            <button :disabled="questionPageNum === 1" @click="questionPageNum--; loadQuestions()">上一页</button>
            <span>第 {{ questionPageNum }} 页</span>
            <button :disabled="questionPageNum * questionPageSize >= questionTotal" @click="questionPageNum++; loadQuestions()">下一页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 问卷题目维护弹窗 -->
    <div class="modal-overlay" v-if="showQuestionModal">
      <div class="modal-content" style="width: 900px;">
        <div class="modal-header">
          <h3>{{ editingQuestion ? '编辑题目' : '新增题目' }}</h3>
          <span class="modal-close" @click="showQuestionModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <!-- 题目编号 -->
          <div class="form-item">
            <label>题目编号</label>
            <span class="readonly-value">{{ questionForm.questionCode || '自动生成' }}</span>
          </div>

          <!-- 题目表格 -->
          <div class="sub-section">
            <div class="sub-section-header">
              <h4>题目内容</h4>
              <div>
                <button class="btn btn-link" @click="addQuestionTextRow">+ 添加行</button>
                <button class="btn btn-link" @click="deleteSelectedQuestionTexts" :disabled="selectedQuestionTexts.length === 0">删除</button>
              </div>
            </div>
            <table class="sub-table">
              <thead>
                <tr>
                  <th><input type="checkbox" v-model="isAllQuestionTextSelected" @change="handleQuestionTextSelectAll" /></th>
                  <th>序号</th>
                  <th>操作</th>
                  <th>题目</th>
                  <th>语言</th>
                  <th>创建人</th>
                  <th>创建时间</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in questionForm.questionTexts" :key="index">
                  <td><input type="checkbox" v-model="selectedQuestionTexts" :value="index" /></td>
                  <td>{{ index + 1 }}</td>
                  <td><span class="action-btn action-delete" @click="removeQuestionText(index)">删除</span></td>
                  <td><input type="text" v-model="item.questionText" placeholder="请输入题目" class="table-input" /></td>
                  <td>
                    <select v-model="item.language" class="table-select">
                      <option value="">请选择</option>
                      <option value="ZH">中文</option>
                      <option value="EN">English</option>
                    </select>
                  </td>
                  <td>{{ item.createdBy || '-' }}</td>
                  <td>{{ item.createdTime || '-' }}</td>
                </tr>
                <tr v-if="questionForm.questionTexts.length === 0">
                  <td colspan="7" style="text-align: center; color: #999;">请点击"添加行"添加题目内容</td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- 可选答案表格 -->
          <div class="sub-section">
            <div class="sub-section-header">
              <h4>可选答案</h4>
              <div>
                <button class="btn btn-link" @click="openAnswerModal(null)">+ 新增</button>
                <button class="btn btn-link" @click="batchDeleteAnswers" :disabled="selectedAnswers.length === 0">删除</button>
              </div>
            </div>
            <table class="sub-table">
              <thead>
                <tr>
                  <th><input type="checkbox" v-model="isAllAnswerSelected" @change="handleAnswerSelectAll" /></th>
                  <th>序号</th>
                  <th>操作</th>
                  <th>答案编号</th>
                  <th>中文答案</th>
                  <th>英文答案</th>
                  <th>创建人</th>
                  <th>创建时间</th>
                </tr>
              </thead>
              <tbody>
                <template v-for="(item, index) in answerList" :key="item.id">
                  <tr>
                    <td><input type="checkbox" v-model="selectedAnswers" :value="item.id" /></td>
                    <td>{{ (answerPageNum - 1) * answerPageSize + index + 1 }}</td>
                    <td>
                      <span class="action-btn action-edit" @click="openAnswerModal(item)">编辑</span>
                      <span class="action-btn action-delete" @click="deleteAnswer(item.id)">删除</span>
                    </td>
                    <td>{{ item.answerCode }}</td>
                    <td>{{ getAnswerText(item, 'ZH') }}</td>
                    <td>{{ getAnswerText(item, 'EN') }}</td>
                    <td>{{ item.createdBy }}</td>
                    <td>{{ item.createdTime }}</td>
                  </tr>
                </template>
                <tr v-if="answerList.length === 0">
                  <td colspan="8" style="text-align: center; color: #999;">暂无答案，请点击"新增"添加</td>
                </tr>
              </tbody>
            </table>
            <div class="pagination" v-if="answerTotal > 0">
              <span>共 {{ answerTotal }} 条</span>
              <button :disabled="answerPageNum === 1" @click="answerPageNum--; loadAnswers()">上一页</button>
              <span>第 {{ answerPageNum }} 页</span>
              <button :disabled="answerPageNum * answerPageSize >= answerTotal" @click="answerPageNum++; loadAnswers()">下一页</button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveQuestion">确定</button>
          <button class="btn" @click="showQuestionModal = false">取消</button>
        </div>
      </div>
    </div>

    <!-- 问卷答案维护弹窗 -->
    <div class="modal-overlay" v-if="showAnswerModal">
      <div class="modal-content" style="width: 800px;">
        <div class="modal-header">
          <h3>{{ editingAnswer ? '编辑答案' : '新增答案' }}</h3>
          <span class="modal-close" @click="showAnswerModal = false">&times;</span>
        </div>
        <div class="modal-body">
          <!-- 答案编号 -->
          <div class="form-item">
            <label>答案编号</label>
            <span class="readonly-value">{{ answerForm.answerCode || '自动生成' }}</span>
          </div>

          <!-- 可选答案表格 -->
          <div class="sub-section">
            <div class="sub-section-header">
              <h4>可选答案</h4>
              <div>
                <button class="btn btn-link" @click="addAnswerItem">+ 添加行</button>
                <button class="btn btn-link" @click="deleteSelectedAnswerItems" :disabled="selectedAnswerItems.length === 0">删除</button>
              </div>
            </div>
            <table class="sub-table answer-group-table">
              <thead>
                <tr>
                  <th style="width: 40px;"><input type="checkbox" v-model="isAllAnswerItemSelected" @change="handleAnswerItemSelectAll" /></th>
                  <th style="width: 50px;">序号</th>
                  <th style="width: 60px;">操作</th>
                  <th>中文答案</th>
                  <th>英文答案</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in answerForm.answerItems" :key="index">
                  <td><input type="checkbox" v-model="selectedAnswerItems" :value="index" /></td>
                  <td>{{ index + 1 }}</td>
                  <td><span class="action-btn action-delete" @click="removeAnswerItem(index)">删除</span></td>
                  <td><input type="text" v-model="item.zhText" placeholder="请输入中文答案" class="table-input" /></td>
                  <td><input type="text" v-model="item.enText" placeholder="Please input English answer" class="table-input" /></td>
                </tr>
                <tr v-if="answerForm.answerItems.length === 0">
                  <td colspan="5" style="text-align: center; color: #999;">请点击"添加行"添加答案内容</td>
                </tr>
              </tbody>
            </table>
            <div class="form-tip" style="margin-top: 8px;">每个答案可同时维护中英文两个版本，方便多语言管理。</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveAnswer">确定</button>
          <button class="btn" @click="showAnswerModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// HTTP请求封装
// ============ 接口配置 ============
// 修改此处的 baseURL 以适配你的后端服务地址
const baseURL = 'http://localhost:8080/api/v1'
// ==================================

const http = {
  get(url, params) {
    const query = new URLSearchParams(params).toString()
    return fetch(`${baseURL}${url}${query ? '?' + query : ''}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' }
    }).then(res => res.json())
  },
  post(url, data) {
    return fetch(`${baseURL}${url}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  put(url, data) {
    return fetch(`${baseURL}${url}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(res => res.json())
  },
  delete(url) {
    return fetch(`${baseURL}${url}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    }).then(res => res.json())
  }
}

// 年份选择器组件
const YearSelect = {
  props: {
    value: [Number, String],
    disabled: { type: Boolean, default: false }
  },
  data() {
    return { isOpen: false }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  computed: {
    currentYear() {
      return new Date().getFullYear()
    },
    years() {
      const center = this.currentYear
      const years = []
      for (let i = center - 5; i <= center + 5; i++) {
        years.push(i)
      }
      return years
    },
    displayText() {
      return this.value ? String(this.value) : '请选择年份'
    }
  },
  methods: {
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    },
    toggleDropdown() {
      if (!this.disabled) this.isOpen = !this.isOpen
    },
    selectYear(year) {
      this.$emit('input', year)
      this.isOpen = false
    }
  },
  template: `
    <div class="custom-select">
      <div class="select-trigger" @click="toggleDropdown" :style="{cursor: disabled ? 'not-allowed' : 'pointer', background: disabled ? '#f5f5f5' : '#fff'}">
        <span :class="{ placeholder: !value }">{{ displayText }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown year-dropdown" v-if="isOpen && !disabled">
        <div class="year-grid">
          <div
            v-for="year in years"
            :key="year"
            :class="['year-item', { selected: value === year }]"
            @click.stop="selectYear(year)"
          >{{ year }}</div>
        </div>
      </div>
    </div>
  `
}

// 自定义下拉选择器组件
const CustomSelect = {
  props: {
    value: [String, Array],
    options: { type: Array, default: () => [] },
    placeholder: { type: String, default: '请选择' },
    multiple: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false }
  },
  data() {
    return { isOpen: false }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  computed: {
    selectedItems() {
      if (!this.value || (Array.isArray(this.value) && this.value.length === 0)) return []
      const values = Array.isArray(this.value) ? this.value : [this.value]
      return values.map(v => {
        const opt = this.options.find(o => o.value === v || o.code === v)
        return { value: v, label: opt ? (opt.label || opt.name) : v }
      })
    }
  },
  methods: {
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    },
    toggleDropdown() { if (!this.disabled) this.isOpen = !this.isOpen },
    selectOption(option) {
      const val = option.value || option.code
      if (this.multiple) {
        const newValue = Array.isArray(this.value) ? [...this.value] : []
        const index = newValue.indexOf(val)
        if (index > -1) newValue.splice(index, 1)
        else newValue.push(val)
        this.$emit('input', newValue)
      } else {
        this.$emit('input', val)
        this.isOpen = false
      }
    },
    isSelected(option) {
      const val = option.value || option.code
      return this.multiple && Array.isArray(this.value) ? this.value.includes(val) : this.value === val
    },
    removeTag(item, e) {
      e.stopPropagation()
      const newValue = Array.isArray(this.value) ? [...this.value] : []
      const index = newValue.indexOf(item.value)
      if (index > -1) {
        newValue.splice(index, 1)
        this.$emit('input', newValue)
      }
    }
  },
  template: `
    <div class="custom-select">
      <div class="select-trigger" @click="toggleDropdown" :style="{cursor: disabled ? 'not-allowed' : 'pointer', background: disabled ? '#f5f5f5' : '#fff'}">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span v-if="multiple && !disabled" class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown" v-if="isOpen && !disabled">
        <div class="select-option" v-for="opt in options" :key="opt.value || opt.code" @click.stop="selectOption(opt)">
          <input v-if="multiple" type="checkbox" :checked="isSelected(opt)" class="option-checkbox" />
          <span>{{ opt.label || opt.name }}</span>
        </div>
      </div>
    </div>
  `
}

// 树形选择器组件
const TreeSelect = {
  props: {
    value: Array,
    options: { type: Array, default: () => [] },
    placeholder: { type: String, default: '请选择' },
    multiple: { type: Boolean, default: true },
    disabled: { type: Boolean, default: false }
  },
  data() {
    return { isOpen: false, expandedKeys: [] }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  computed: {
    selectedItems() {
      if (!this.value || this.value.length === 0) return []
      return this.value.map(v => {
        const node = this.findNode(this.options, v)
        return { value: v, label: node ? node.name : v }
      })
    }
  },
  methods: {
    handleClickOutside(e) {
      if (!this.$el.contains(e.target)) {
        this.isOpen = false
      }
    },
    findNode(nodes, code) {
      for (const node of nodes) {
        if (node.code === code) return node
        if (node.children) {
          const found = this.findNode(node.children, code)
          if (found) return found
        }
      }
      return null
    },
    toggleDropdown() { if (!this.disabled) this.isOpen = !this.isOpen },
    handleExpand(code) {
      const index = this.expandedKeys.indexOf(code)
      if (index > -1) this.expandedKeys.splice(index, 1)
      else this.expandedKeys.push(code)
    },
    isExpanded(code) { return this.expandedKeys.includes(code) },
    isChecked(code) { return this.value && this.value.includes(code) },
    handleCheck(code) {
      const newValue = this.value ? [...this.value] : []
      const index = newValue.indexOf(code)
      if (index > -1) newValue.splice(index, 1)
      else newValue.push(code)
      this.$emit('input', newValue)
    },
    removeTag(item, e) {
      e.stopPropagation()
      const newValue = this.value ? [...this.value] : []
      const index = newValue.indexOf(item.value)
      if (index > -1) {
        newValue.splice(index, 1)
        this.$emit('input', newValue)
      }
    }
  },
  template: `
    <div class="custom-select">
      <div class="select-trigger" @click="toggleDropdown" :style="{cursor: disabled ? 'not-allowed' : 'pointer', background: disabled ? '#f5f5f5' : '#fff'}">
        <template v-if="selectedItems.length > 0">
          <span v-for="item in selectedItems.slice(0, 2)" :key="item.value" class="select-tag">
            {{ item.label }}
            <span v-if="!disabled" class="close" @click="removeTag(item, $event)">&times;</span>
          </span>
          <span v-if="selectedItems.length > 2" class="select-tag">+{{ selectedItems.length - 2 }}</span>
        </template>
        <span v-else class="placeholder">{{ placeholder }}</span>
        <span class="arrow"></span>
      </div>
      <div class="select-dropdown tree-select-dropdown" v-if="isOpen && !disabled">
        <template v-for="node in options">
          <div class="tree-node" :key="node.code">
            <div class="tree-node-content">
              <span class="tree-toggle" v-if="node.children && node.children.length > 0" @click="handleExpand(node.code)">{{ isExpanded(node.code) ? '▼' : '▶' }}</span>
              <span v-else class="tree-toggle-placeholder"></span>
              <input type="checkbox" :checked="isChecked(node.code)" @change="handleCheck(node.code)" class="option-checkbox" />
              <span class="tree-label" @click="handleCheck(node.code)">{{ node.name }}</span>
            </div>
            <div class="tree-children" v-if="node.children && isExpanded(node.code)">
              <template v-for="child in node.children">
                <div class="tree-node" :key="child.code">
                  <div class="tree-node-content">
                    <span class="tree-toggle" v-if="child.children && child.children.length > 0" @click="handleExpand(child.code)">{{ isExpanded(child.code) ? '▼' : '▶' }}</span>
                    <span v-else class="tree-toggle-placeholder"></span>
                    <input type="checkbox" :checked="isChecked(child.code)" @change="handleCheck(child.code)" class="option-checkbox" />
                    <span class="tree-label" @click="handleCheck(child.code)">{{ child.name }}</span>
                  </div>
                  <div class="tree-children" v-if="child.children && isExpanded(child.code)">
                    <div v-for="grandChild in child.children" :key="grandChild.code" class="tree-node">
                      <div class="tree-node-content">
                        <span class="tree-toggle-placeholder"></span>
                        <input type="checkbox" :checked="isChecked(grandChild.code)" @change="handleCheck(grandChild.code)" class="option-checkbox" />
                        <span class="tree-label" @click="handleCheck(grandChild.code)">{{ grandChild.name }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </template>
      </div>
    </div>
  `
}

export default {
  name: 'AuthLetterDetail',
  components: { CustomSelect, TreeSelect, YearSelect },
  data() {
    return {
      id: null,
      isNew: false,
      isEdit: false,
      formData: {
        name: '',
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        publishYear: null,
        summary: '',
        status: 'DRAFT'
      },
      lookupData: {
        authObjectLevel: [],
        applicableRegion: [],
        authPublishLevel: [],
        authPublishOrg: [],
        industry: [],
        businessScene: [],
        decisionLevel: []
      },
      ruleParams: [],
      attachmentList: [],
      selectedAttachments: [],
      isAllAttachmentSelected: false,
      attachmentPageNum: 1,
      attachmentPageSize: 10,
      attachmentTotal: 0,
      sceneList: [],
      selectedScenes: [],
      isAllSceneSelected: false,
      scenePageNum: 1,
      scenePageSize: 10,
      sceneTotal: 0,
      logList: [],
      logPageNum: 1,
      logPageSize: 10,
      logTotal: 0,
      logCollapsed: true,
      showSceneModal: false,
      showUploadModal: false,
      editingScene: null,
      sceneForm: {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        sceneQuestions: [], // 场景问卷题目配置
        conditions: []
      },
      uploadForm: {
        docId: '',
        docName: '',
        docType: ''
      },
      docTypeOptions: [
        { value: 'PDF', label: 'PDF文档' },
        { value: 'WORD', label: 'Word文档' },
        { value: 'EXCEL', label: 'Excel文档' },
        { value: 'IMAGE', label: '图片' },
        { value: 'OTHER', label: '其他' }
      ],
      // 问卷题目配置相关
      showQuestionnaireModal: false,
      showQuestionModal: false,
      showAnswerModal: false,
      questionSearch: {
        questionCode: '',
        questionText: '',
        language: '',
        createdBy: '',
        createdDate: '',
        updatedBy: '',
        updatedDate: ''
      },
      questionList: [],
      questionPageNum: 1,
      questionPageSize: 10,
      questionTotal: 0,
      selectedQuestions: [],
      isAllQuestionSelected: false,
      editingQuestion: null,
      questionForm: {
        questionCode: '',
        questionTexts: []
      },
      selectedQuestionTexts: [],
      isAllQuestionTextSelected: false,
      // 答案相关
      answerList: [],
      answerPageNum: 1,
      answerPageSize: 10,
      answerTotal: 0,
      selectedAnswers: [],
      isAllAnswerSelected: false,
      editingAnswer: null,
      answerForm: {
        answerCode: '',
        answerItems: [] // [{ zhText: '', enText: '' }, ...]
      },
      selectedAnswerItems: [],
      isAllAnswerItemSelected: false,
      languageOptions: [
        { value: 'ZH', label: '中文' },
        { value: 'EN', label: 'English' }
      ],
      // 场景问卷题目相关
      allQuestions: [] // 所有题目列表，用于场景问卷选择
    }
  },
  computed: {
    isEditable() {
      return this.isNew || (this.isEdit && this.formData.status === 'DRAFT')
    }
  },
  created() {
    this.id = this.$route.query.id
    this.isNew = this.$route.query.mode === 'create' || !this.$route.query.id
    this.isEdit = this.$route.query.mode === 'edit'

    this.loadLookupData()
    this.loadRuleParams()

    if (this.id) {
      this.loadData()
      this.loadAttachments()
      this.loadScenes()
    }
  },
  methods: {
    async loadLookupData() {
      const types = ['AUTH_OBJECT_LEVEL', 'APPLICABLE_REGION', 'AUTH_PUBLISH_LEVEL', 'AUTH_PUBLISH_ORG', 'INDUSTRY', 'BUSINESS_SCENE', 'DECISION_LEVEL']
      for (const type of types) {
        try {
          const res = await http.get(`/lookup-values/${type}`)
          if (res.code === 200 && res.data) {
            if (type === 'APPLICABLE_REGION' || type === 'AUTH_PUBLISH_ORG' || type === 'INDUSTRY') {
              const key = type.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
              this.lookupData[key] = this.buildTree(res.data)
            } else {
              const key = type.toLowerCase().replace(/_([a-z])/g, (g) => g[1].toUpperCase())
              this.lookupData[key] = res.data.map(item => ({ value: item.valueCode, label: item.valueName }))
            }
          }
        } catch (e) {
          console.error(`加载${type}失败`, e)
        }
      }
    },
    buildTree(data) {
      const map = {}
      const roots = []
      data.forEach(item => {
        map[item.valueCode] = { ...item, code: item.valueCode, name: item.valueName, children: [] }
      })
      data.forEach(item => {
        if (item.parentCode && map[item.parentCode]) {
          map[item.parentCode].children.push(map[item.valueCode])
        } else if (!item.parentCode) {
          roots.push(map[item.valueCode])
        }
      })
      return roots
    },
    async loadRuleParams() {
      try {
        const res = await http.get('/rule-params/active')
        if (res.code === 200) {
          this.ruleParams = res.data || []
        }
      } catch (e) {
        console.error('加载规则参数失败', e)
      }
    },
    async loadData() {
      try {
        const res = await http.get(`/auth-letters/${this.id}`)
        if (res.code === 200 && res.data) {
          this.formData = {
            ...res.data,
            authObjectLevel: this.parseArray(res.data.authObjectLevel),
            applicableRegion: this.parseArray(res.data.applicableRegion),
            authPublishLevel: this.parseArray(res.data.authPublishLevel),
            authPublishOrg: this.parseArray(res.data.authPublishOrg)
          }
          if (res.data.status !== 'DRAFT') {
            this.loadLogs()
          }
        }
      } catch (e) {
        console.error('加载数据失败', e)
      }
    },
    parseArray(value) {
      if (!value) return []
      try {
        return typeof value === 'string' ? JSON.parse(value) : value
      } catch (e) {
        return []
      }
    },
    async loadAttachments() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/attachments`, {
          pageNum: this.attachmentPageNum,
          pageSize: this.attachmentPageSize
        })
        if (res.code === 200) {
          this.attachmentList = res.data.list || []
          this.attachmentTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载附件失败', e)
      }
    },
    async loadScenes() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/scenes`, {
          pageNum: this.scenePageNum,
          pageSize: this.scenePageSize
        })
        if (res.code === 200) {
          this.sceneList = res.data.list || []
          this.sceneTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载场景失败', e)
      }
    },
    async loadLogs() {
      if (!this.id) return
      try {
        const res = await http.get(`/auth-letters/${this.id}/logs`, {
          pageNum: this.logPageNum,
          pageSize: this.logPageSize
        })
        if (res.code === 200) {
          this.logList = res.data.list || []
          this.logTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载日志失败', e)
      }
    },
    async handleSave() {
      try {
        const data = {
          ...this.formData,
          authObjectLevel: this.formData.authObjectLevel,
          applicableRegion: this.formData.applicableRegion,
          authPublishLevel: this.formData.authPublishLevel,
          authPublishOrg: this.formData.authPublishOrg
        }

        if (this.isNew) {
          const res = await http.post('/auth-letters', data)
          if (res.code === 200) {
            this.id = res.data
            this.isNew = false
            alert('保存成功')
            window.location.hash = '#/AuthLetterDetail?id=' + this.id
          }
        } else {
          await http.put(`/auth-letters/${this.id}`, data)
          alert('保存成功')
          this.loadData()
        }
      } catch (e) {
        console.error('保存失败', e)
        alert('保存失败')
      }
    },
    async handleSaveAndPublish() {
      await this.handleSave()
      if (this.id) {
        await this.handlePublish()
      }
    },
    async handlePublish() {
      if (!confirm('确定要发布吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/publish`)
        alert('发布成功')
        this.loadData()
      } catch (e) {
        console.error('发布失败', e)
        alert('发布失败')
      }
    },
    async handleInvalidate() {
      if (!confirm('确定要失效吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/invalidate`)
        alert('操作成功')
        this.loadData()
      } catch (e) {
        console.error('操作失败', e)
        alert('操作失败')
      }
    },
    async handleDelete() {
      if (!confirm('确定要删除吗？')) return
      try {
        await http.delete(`/auth-letters/${this.id}`)
        alert('删除成功')
        window.location.hash = '#/AuthLetterList'
      } catch (e) {
        console.error('删除失败', e)
        alert('删除失败')
      }
    },
    handleCancel() {
      window.location.hash = '#/AuthLetterList'
    },
    handleBack() {
      window.location.hash = '#/AuthLetterList'
    },
    handleAttachmentSelectAll() {
      this.selectedAttachments = this.isAllAttachmentSelected ? this.attachmentList.map(a => a.id) : []
    },
    handleUploadAttachment() {
      this.uploadForm = { docId: '', docName: '', docType: '' }
      this.showUploadModal = true
    },
    async handleConfirmUpload() {
      try {
        await http.post(`/auth-letters/${this.id}/attachments`, this.uploadForm)
        this.showUploadModal = false
        this.loadAttachments()
      } catch (e) {
        console.error('上传失败', e)
        alert('上传失败')
      }
    },
    handleDownloadAttachment() {
      this.selectedAttachments.forEach(id => {
        const att = this.attachmentList.find(a => a.id === id)
        if (att) this.handleDownloadFile(att)
      })
    },
    handleDownloadFile(item) {
      alert(`下载文件: ${item.docName}`)
    },
    async handleDeleteAttachment() {
      if (!confirm('确定要删除选中的附件吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/attachments/batch-delete`, this.selectedAttachments)
        this.selectedAttachments = []
        this.loadAttachments()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    handleSceneSelectAll() {
      this.selectedScenes = this.isAllSceneSelected ? this.sceneList.map(s => s.id) : []
    },
    handleAddScene() {
      this.editingScene = null
      this.sceneForm = {
        sceneName: '',
        industry: [],
        businessScene: '',
        decisionLevel: '',
        specificRule: '',
        sceneQuestions: [],
        conditions: []
      }
      this.showSceneModal = true
      this.loadAllQuestions()
    },
    handleEditScene(scene) {
      this.editingScene = scene
      this.sceneForm = {
        sceneName: scene.sceneName,
        industry: this.parseArray(scene.industry),
        businessScene: scene.businessScene,
        decisionLevel: scene.decisionLevel,
        specificRule: scene.specificRule,
        sceneQuestions: scene.sceneQuestions || [],
        conditions: []
      }
      this.showSceneModal = true
      this.loadAllQuestions()
    },
    async handleSaveScene() {
      try {
        const data = {
          ...this.sceneForm,
          industry: this.sceneForm.industry
        }
        if (this.editingScene) {
          await http.put(`/auth-letters/${this.id}/scenes/${this.editingScene.id}`, data)
        } else {
          await http.post(`/auth-letters/${this.id}/scenes`, data)
        }
        this.showSceneModal = false
        this.loadScenes()
      } catch (e) {
        console.error('保存场景失败', e)
        alert('保存失败')
      }
    },
    async handleDeleteScene(sceneId) {
      if (!confirm('确定要删除此场景吗？')) return
      try {
        await http.delete(`/auth-letters/${this.id}/scenes/${sceneId}`)
        this.loadScenes()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    handlePreviewQuestionnaire() {
      if (!this.sceneForm.questionnaire) {
        alert('问卷内容为空')
        return
      }
      try {
        const questionnaire = JSON.parse(this.sceneForm.questionnaire)
        alert('问卷预览：\n' + JSON.stringify(questionnaire, null, 2))
      } catch (e) {
        alert('问卷格式错误，请输入有效的JSON格式')
      }
    },
    handleClearQuestionnaire() {
      this.sceneForm.questionnaire = ''
    },
    async handleDeleteScenes() {
      if (!confirm('确定要删除选中的场景吗？')) return
      try {
        await http.post(`/auth-letters/${this.id}/scenes/batch-delete`, this.selectedScenes)
        this.selectedScenes = []
        this.loadScenes()
      } catch (e) {
        console.error('删除失败', e)
      }
    },
    addCondition() {
      this.sceneForm.conditions.push({
        fieldCode: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND',
        isGroup: false
      })
    },
    addConditionGroup() {
      this.sceneForm.conditions.push({
        isGroup: true,
        logicType: 'AND',
        children: [{
          fieldCode: '',
          operator: '>',
          compareType: 'NUMBER',
          compareValue: '',
          compareUnit: '',
          logicType: 'AND'
        }]
      })
    },
    addSubCondition(group) {
      group.children.push({
        fieldCode: '',
        operator: '>',
        compareType: 'NUMBER',
        compareValue: '',
        compareUnit: '',
        logicType: 'AND'
      })
    },
    removeCondition(list, index) {
      list.splice(index, 1)
    },
    removeConditionGroup(index) {
      this.sceneForm.conditions.splice(index, 1)
    },
    toggleLogic(condition) {
      condition.logicType = condition.logicType === 'AND' ? 'OR' : 'AND'
    },
    formatArray(value) {
      if (!value) return ''
      try {
        const arr = typeof value === 'string' ? JSON.parse(value) : value
        return Array.isArray(arr) ? arr.join(', ') : value
      } catch (e) {
        return value
      }
    },
    getOperationText(operation) {
      const map = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'PUBLISH': '发布',
        'INVALIDATE': '失效',
        'DELETE': '删除'
      }
      return map[operation] || operation
    },

    // ==================== 问卷题目配置相关方法 ====================
    openQuestionnaireConfig() {
      this.showQuestionnaireModal = true
      this.loadQuestions()
    },
    async loadQuestions() {
      try {
        const params = {
          pageNum: this.questionPageNum,
          pageSize: this.questionPageSize,
          ...this.questionSearch
        }
        const res = await http.get('/questions', params)
        if (res.code === 200) {
          this.questionList = res.data.list || []
          this.questionTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载题目列表失败', e)
      }
    },
    resetQuestionSearch() {
      this.questionSearch = {
        questionCode: '',
        questionText: '',
        language: '',
        createdBy: '',
        createdDate: '',
        updatedBy: '',
        updatedDate: ''
      }
      this.questionPageNum = 1
      this.loadQuestions()
    },
    handleQuestionSelectAll() {
      if (this.isAllQuestionSelected) {
        this.selectedQuestions = this.questionList.map(item => item.id)
      } else {
        this.selectedQuestions = []
      }
    },
    openQuestionModal() {
      this.editingQuestion = null
      this.questionForm = {
        questionCode: '',
        questionTexts: [{ questionText: '', language: 'ZH' }]
      }
      this.selectedQuestionTexts = []
      this.answerList = []
      this.answerTotal = 0
      this.showQuestionModal = true
    },
    async editQuestion(question) {
      this.editingQuestion = question
      this.questionForm = {
        questionCode: question.questionCode,
        questionTexts: question.questionTexts ? question.questionTexts.map(t => ({
          questionText: t.questionText,
          language: t.language,
          createdBy: t.createdBy,
          createdTime: t.createdTime
        })) : []
      }
      this.selectedQuestionTexts = []
      this.showQuestionModal = true
      this.loadAnswers()
    },
    async saveQuestion() {
      try {
        // 验证语言唯一性
        const languages = this.questionForm.questionTexts.map(t => t.language).filter(l => l)
        const uniqueLanguages = [...new Set(languages)]
        if (languages.length !== uniqueLanguages.length) {
          alert('相同语言只能维护一个题目')
          return
        }

        const data = {
          questionTexts: this.questionForm.questionTexts.filter(t => t.questionText && t.language)
        }

        if (this.editingQuestion) {
          await http.put(`/questions/${this.editingQuestion.id}`, data)
          alert('更新成功')
        } else {
          await http.post('/questions', data)
          alert('创建成功')
        }

        this.showQuestionModal = false
        this.loadQuestions()
      } catch (e) {
        console.error('保存题目失败', e)
        alert('保存失败')
      }
    },
    async deleteQuestion(id) {
      if (!confirm('确定要删除此题目吗？')) return
      try {
        await http.delete(`/questions/${id}`)
        this.loadQuestions()
      } catch (e) {
        console.error('删除题目失败', e)
        if (e.message) alert(e.message)
      }
    },
    async batchDeleteQuestions() {
      if (this.selectedQuestions.length === 0) return
      if (!confirm(`确定要删除选中的 ${this.selectedQuestions.length} 条题目吗？`)) return
      try {
        await http.post('/questions/batch-delete', { ids: this.selectedQuestions })
        this.selectedQuestions = []
        this.loadQuestions()
      } catch (e) {
        console.error('批量删除失败', e)
        alert('删除失败')
      }
    },
    addQuestionTextRow() {
      this.questionForm.questionTexts.push({ questionText: '', language: '' })
    },
    removeQuestionText(index) {
      this.questionForm.questionTexts.splice(index, 1)
    },
    handleQuestionTextSelectAll() {
      if (this.isAllQuestionTextSelected) {
        this.selectedQuestionTexts = this.questionForm.questionTexts.map((_, i) => i)
      } else {
        this.selectedQuestionTexts = []
      }
    },
    deleteSelectedQuestionTexts() {
      this.questionForm.questionTexts = this.questionForm.questionTexts.filter((_, i) => !this.selectedQuestionTexts.includes(i))
      this.selectedQuestionTexts = []
    },
    getQuestionText(question, lang) {
      if (!question.questionTexts || question.questionTexts.length === 0) return '-'
      const text = question.questionTexts.find(t => t.language === lang)
      return text ? text.questionText : question.questionTexts[0].questionText
    },
    getQuestionLanguage(question) {
      if (!question.questionTexts || question.questionTexts.length === 0) return '-'
      return question.questionTexts.map(t => t.language === 'ZH' ? '中文' : 'English').join('/')
    },

    // ==================== 答案相关方法 ====================
    async loadAnswers() {
      if (!this.editingQuestion) return
      try {
        const res = await http.get(`/questions/${this.editingQuestion.id}/answers`, {
          pageNum: this.answerPageNum,
          pageSize: this.answerPageSize
        })
        if (res.code === 200) {
          this.answerList = res.data.list || []
          this.answerTotal = res.data.total || 0
        }
      } catch (e) {
        console.error('加载答案列表失败', e)
      }
    },
    handleAnswerSelectAll() {
      if (this.isAllAnswerSelected) {
        this.selectedAnswers = this.answerList.map(item => item.id)
      } else {
        this.selectedAnswers = []
      }
    },
    openAnswerModal(answer) {
      this.editingAnswer = answer
      // 转换数据格式：从 answerTexts 数组转换为 answerItems 分组表格格式
      let answerItems = []
      if (answer && answer.answerTexts && answer.answerTexts.length > 0) {
        // 按答案内容分组（相同答案内容的中英文版本放在一起）
        const grouped = {}
        answer.answerTexts.forEach(t => {
          const key = t.answerText // 以答案内容作为分组key
          if (!grouped[key]) {
            grouped[key] = { zhText: '', enText: '' }
          }
          if (t.language === 'ZH') {
            grouped[key].zhText = t.answerText
          } else if (t.language === 'EN') {
            grouped[key].enText = t.answerText
          }
        })
        answerItems = Object.values(grouped)
      }
      // 如果没有数据，添加一个空行
      if (answerItems.length === 0) {
        answerItems = [{ zhText: '', enText: '' }]
      }
      this.answerForm = {
        answerCode: answer ? answer.answerCode : '',
        answerItems: answerItems
      }
      this.selectedAnswerItems = []
      this.showAnswerModal = true
    },
    async saveAnswer() {
      try {
        // 验证是否有有效的答案文本
        const validItems = this.answerForm.answerItems.filter(item => item.zhText || item.enText)
        if (validItems.length === 0) {
          alert('请至少添加一条有效的答案内容')
          return
        }

        // 转换数据格式：从 answerItems 分组表格格式转换为 answerTexts 数组
        const answerTexts = []
        validItems.forEach(item => {
          if (item.zhText) {
            answerTexts.push({ answerText: item.zhText, language: 'ZH' })
          }
          if (item.enText) {
            answerTexts.push({ answerText: item.enText, language: 'EN' })
          }
        })

        const data = {
          answerTexts: answerTexts
        }

        if (this.editingAnswer) {
          await http.put(`/questions/answers/${this.editingAnswer.id}`, data)
          alert('更新成功')
        } else {
          await http.post(`/questions/${this.editingQuestion.id}/answers`, data)
          alert('创建成功')
        }

        this.showAnswerModal = false
        this.loadAnswers()
      } catch (e) {
        console.error('保存答案失败', e)
        alert('保存失败')
      }
    },
    async deleteAnswer(id) {
      if (!confirm('确定要删除此答案吗？')) return
      try {
        await http.delete(`/questions/answers/${id}`)
        this.loadAnswers()
      } catch (e) {
        console.error('删除答案失败', e)
        alert('删除失败')
      }
    },
    async batchDeleteAnswers() {
      if (this.selectedAnswers.length === 0) return
      if (!confirm(`确定要删除选中的 ${this.selectedAnswers.length} 条答案吗？`)) return
      try {
        await http.post('/questions/answers/batch-delete', { ids: this.selectedAnswers })
        this.selectedAnswers = []
        this.loadAnswers()
      } catch (e) {
        console.error('批量删除失败', e)
        alert('删除失败')
      }
    },
    addAnswerItem() {
      this.answerForm.answerItems.push({ zhText: '', enText: '' })
    },
    removeAnswerItem(index) {
      this.answerForm.answerItems.splice(index, 1)
    },
    handleAnswerItemSelectAll() {
      if (this.isAllAnswerItemSelected) {
        this.selectedAnswerItems = this.answerForm.answerItems.map((_, i) => i)
      } else {
        this.selectedAnswerItems = []
      }
    },
    deleteSelectedAnswerItems() {
      this.answerForm.answerItems = this.answerForm.answerItems.filter((_, i) => !this.selectedAnswerItems.includes(i))
      this.selectedAnswerItems = []
    },
    getAnswerText(answer, lang) {
      if (!answer.answerTexts || answer.answerTexts.length === 0) return '-'
      const text = answer.answerTexts.find(t => t.language === lang)
      return text ? text.answerText : '-'
    },
    getAnswerLanguage(answer) {
      if (!answer.answerTexts || answer.answerTexts.length === 0) return '-'
      return answer.answerTexts.map(t => t.language === 'ZH' ? '中文' : 'English').join('/')
    },

    // ==================== 场景问卷题目相关方法 ====================
    async loadAllQuestions() {
      try {
        const res = await http.get('/questions', { pageNum: 1, pageSize: 1000 })
        if (res.code === 200) {
          this.allQuestions = res.data.list || []
        }
      } catch (e) {
        console.error('加载题目列表失败', e)
      }
    },
    addSceneQuestion() {
      if (!this.sceneForm.sceneQuestions) {
        this.sceneForm.sceneQuestions = []
      }
      this.sceneForm.sceneQuestions.push({
        questionId: '',
        correctAnswerId: '',
        availableAnswers: []
      })
    },
    removeSceneQuestion(index) {
      this.sceneForm.sceneQuestions.splice(index, 1)
    },
    async onQuestionChange(sq) {
      // 清空已选答案
      sq.correctAnswerId = ''
      sq.availableAnswers = []

      if (sq.questionId) {
        // 加载该题目的答案列表
        try {
          const res = await http.get(`/questions/${sq.questionId}/answers`, { pageNum: 1, pageSize: 100 })
          if (res.code === 200) {
            sq.availableAnswers = res.data.list || []
          }
        } catch (e) {
          console.error('加载答案列表失败', e)
        }
      }
    }
  }
}
</script>

<style>
/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #f5f5f5;
}

/* 页面容器 */
.page-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 40px);
  padding-bottom: 80px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

/* 区块样式 */
.section {
  margin-bottom: 24px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e8e8e8;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-actions {
  display: flex;
  gap: 8px;
}

/* 表单行 */
.form-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16px;
  gap: 16px;
}

.form-item {
  display: flex;
  align-items: center;
  flex: 0 0 calc(33.33% - 11px);
  min-width: 250px;
}

.form-item label {
  width: 100px;
  text-align: right;
  margin-right: 8px;
  color: #666;
  flex-shrink: 0;
}

.form-item label.required::before {
  content: '*';
  color: #ff4d4f;
  margin-right: 4px;
}

.form-item input,
.form-item textarea {
  flex: 1;
  height: 32px;
  padding: 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #1890ff;
  outline: none;
}

.form-item textarea {
  height: 80px;
  resize: vertical;
}

/* 按钮样式 */
.btn {
  display: inline-block;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  background: #fff;
  cursor: pointer;
  margin-right: 8px;
}

.btn-primary {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #fff1f0;
}

.btn-link {
  color: #1890ff;
  border: none;
  background: transparent;
  padding: 0 4px;
}

.btn-link:hover {
  color: #40a9ff;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表格样式 */
table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
}

th, td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background: #fafafa;
  font-weight: 500;
}

tr:hover {
  background: #f5f5f5;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  gap: 8px;
}

.pagination span {
  color: #666;
}

.pagination button {
  padding: 4px 12px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination select {
  height: 28px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

/* 操作图标 */
.icon-edit {
  color: #1890ff;
  cursor: pointer;
  font-size: 16px;
}

.icon-delete {
  color: #ff4d4f;
  cursor: pointer;
  font-size: 16px;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

/* 链接样式 */
.link {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
}

.link:hover {
  color: #40a9ff;
  text-decoration: underline;
}

/* 自定义下拉选择器 */
.custom-select {
  position: relative;
  flex: 1;
}

.select-trigger {
  display: flex;
  align-items: center;
  min-height: 32px;
  padding: 4px 30px 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  position: relative;
  flex-wrap: wrap;
  gap: 4px;
}

.select-trigger:hover {
  border-color: #40a9ff;
}

.select-trigger .arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: 5px solid transparent;
  border-top-color: #999;
}

.select-trigger .placeholder {
  color: #bfbfbf;
}

.select-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  margin: 2px;
}

.select-tag .close {
  margin-left: 4px;
  cursor: pointer;
  font-size: 14px;
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  max-height: 250px;
  overflow-y: auto;
  z-index: 1000;
}

.select-option {
  padding: 6px 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 13px;
}

.select-option:hover {
  background: #f5f5f5;
}

.option-checkbox {
  width: 14px !important;
  height: 14px !important;
  margin-right: 8px !important;
  cursor: pointer;
  flex-shrink: 0;
}

/* 树形选择器 */
.tree-select-dropdown {
  padding: 8px;
}

.tree-node {
  padding: 2px 0;
}

.tree-node-content {
  display: flex;
  align-items: center;
  padding: 4px 6px;
  cursor: pointer;
  border-radius: 4px;
}

.tree-node-content:hover {
  background: #f5f5f5;
}

.tree-toggle {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  font-size: 10px;
  flex-shrink: 0;
}

.tree-toggle:hover {
  color: #1890ff;
}

.tree-toggle-placeholder {
  width: 16px;
  height: 16px;
  display: inline-block;
  flex-shrink: 0;
}

.tree-children {
  padding-left: 16px;
}

.tree-label {
  cursor: pointer;
  flex: 1;
}

/* 操作按钮样式 */
.action-btn {
  display: inline-block;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
}

.action-edit {
  color: #1890ff;
}

.action-edit:hover {
  background: #e6f7ff;
}

.action-delete {
  color: #ff4d4f;
}

.action-delete:hover {
  background: #fff1f0;
}

/* 年份选择器 */
.year-dropdown {
  padding: 8px;
}

.year-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 4px;
}

.year-item {
  padding: 8px 4px;
  text-align: center;
  cursor: pointer;
  border-radius: 4px;
  font-size: 13px;
}

.year-item:hover {
  background: #f5f5f5;
}

.year-item.selected {
  background: #1890ff;
  color: #fff;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: #fff;
  border-radius: 8px;
  min-width: 600px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.modal-close {
  cursor: pointer;
  font-size: 20px;
  color: #999;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}

/* 问卷配置样式 */
.questionnaire-section {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 16px;
  margin-top: 16px;
}

.questionnaire-section h4 {
  margin-bottom: 12px;
}

.questionnaire-section .form-item {
  flex: 1;
  min-width: 100%;
}

.questionnaire-section textarea {
  width: 100%;
  min-height: 120px;
  padding: 8px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  resize: vertical;
}

.questionnaire-section textarea:focus {
  border-color: #1890ff;
  outline: none;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.questionnaire-actions {
  margin-top: 8px;
  text-align: right;
}

.questionnaire-actions .btn {
  margin-left: 8px;
}

/* 规则配置样式 */
.rule-config {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 16px;
  margin-top: 16px;
}

.rule-config h4 {
  margin-bottom: 12px;
}

.rule-condition-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}

.rule-condition-row select,
.rule-condition-row input {
  height: 32px;
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.logic-toggle {
  display: inline-block;
  padding: 4px 12px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  cursor: pointer;
  margin: 8px 0;
}

.condition-group {
  border: 1px dashed #1890ff;
  border-radius: 4px;
  padding: 12px;
  margin: 8px 0;
  background: #f0f5ff;
}

/* 底部按钮栏 */
.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  text-align: center;
  z-index: 100;
}

/* 折叠面板 */
.collapse-panel {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-top: 16px;
}

.collapse-header {
  padding: 12px 16px;
  background: #fafafa;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collapse-header:hover {
  background: #f5f5f5;
}

.collapse-content {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
}

.collapse-icon {
  transition: transform 0.3s;
}

.collapse-icon.collapsed {
  transform: rotate(-90deg);
}

/* 问卷配置样式 */
.questionnaire-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.questionnaire-header h4 {
  margin: 0;
}

.btn-sm {
  padding: 4px 12px;
  font-size: 12px;
}

/* 问卷弹窗样式 */
.questionnaire-modal {
  width: 1000px;
  max-width: 90vw;
}

.questionnaire-modal .modal-body {
  max-height: 70vh;
  overflow-y: auto;
}

.readonly-value {
  display: inline-block;
  padding: 6px 12px;
  background: #f5f5f5;
  border-radius: 4px;
  color: #666;
}

/* 子表格样式 */
.sub-section {
  margin-top: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.sub-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.sub-section-header h4 {
  margin: 0;
  font-size: 14px;
}

.sub-table {
  margin: 0;
}

.sub-table th,
.sub-table td {
  padding: 8px 10px;
  font-size: 13px;
}

.table-input {
  width: 100%;
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
}

.table-input:focus {
  border-color: #1890ff;
  outline: none;
}

.table-select {
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
  min-width: 80px;
}

.table-select:focus {
  border-color: #1890ff;
  outline: none;
}

/* 场景问卷题目列表样式 */
.scene-question-list {
  margin-top: 12px;
}

.scene-question-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.scene-question-select,
.scene-question-answer {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.scene-question-select label,
.scene-question-answer label {
  white-space: nowrap;
  font-size: 13px;
  color: #666;
}

.scene-question-select select,
.scene-question-answer select {
  height: 32px;
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 13px;
}

.scene-question-delete {
  color: #999;
  cursor: pointer;
  font-size: 13px;
  padding: 4px 8px;
}

.scene-question-delete:hover {
  color: #ff4d4f;
}

/* 答案分组表格样式 */
.answer-group-table th:nth-child(4),
.answer-group-table td:nth-child(4) {
  background: #fffbe6;
}

.answer-group-table th:nth-child(5),
.answer-group-table td:nth-child(5) {
  background: #e6f7ff;
}

.answer-group-table .table-input {
  width: 100%;
  background: transparent;
}
</style>