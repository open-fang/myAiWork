// 授权书详情页组件
const AuthLetterDetail = {
    template: `
    <div class="page-container" :class="{ readonly: !isEditable }">
        <!-- 基本信息 -->
        <div class="card">
            <div class="card-header">
                <span class="card-title">基本信息</span>
            </div>
            <div class="query-form">
                <div class="form-row">
                    <div class="form-item">
                        <label class="form-label required">授权书名称</label>
                        <input type="text" v-model="formData.name" class="form-input" placeholder="请输入授权书名称" :disabled="!isEditable" />
                    </div>
                    <div class="form-item">
                        <label class="form-label required">授权发布层级</label>
                        <div class="multi-select-wrapper" v-click-outside="closeDropdown">
                            <div class="multi-select-trigger" @click="isEditable && toggleDropdown('authPublishLevel')">
                                <span class="selected-tags" v-if="formData.authPublishLevel.length > 0">
                                    <span class="tag" v-for="(item, index) in getSelectedLabels(formData.authPublishLevel, authPublishLevelOptions)" :key="index">
                                        {{ item }}
                                        <span v-if="isEditable" class="tag-close" @click.stop="removeSelected('authPublishLevel', formData.authPublishLevel[index])">×</span>
                                    </span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="multi-select-dropdown" v-show="activeDropdown === 'authPublishLevel' && isEditable">
                                <div class="select-option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                                    <span class="tree-checkbox" :class="{ checked: formData.authPublishLevel.includes(item.code) }"></span>
                                    <span>{{ item.name }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label required">授权发布组织</label>
                        <div class="tree-select-wrapper" v-click-outside="closeDropdown">
                            <div class="tree-select-trigger" @click="isEditable && toggleDropdown('authPublishOrg')">
                                <span class="selected-tags" v-if="formData.authPublishOrg.length > 0">
                                    <span class="tag" v-for="(item, index) in formData.authPublishOrg.slice(0, 2)" :key="index">
                                        {{ getTreeName(item, orgTree) }}
                                        <span v-if="isEditable" class="tag-close" @click.stop="removeSelected('authPublishOrg', item)">×</span>
                                    </span>
                                    <span class="tag" v-if="formData.authPublishOrg.length > 2">+{{ formData.authPublishOrg.length - 2 }}</span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="tree-select-dropdown" v-show="activeDropdown === 'authPublishOrg' && isEditable">
                                <tree-node
                                    v-for="node in orgTree"
                                    :key="node.code"
                                    :node="node"
                                    :selected-codes="formData.authPublishOrg"
                                    @toggle="toggleTreeNode('authPublishOrg', $event)"
                                ></tree-node>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-item">
                        <label class="form-label required">授权对象层级</label>
                        <div class="multi-select-wrapper" v-click-outside="closeDropdown">
                            <div class="multi-select-trigger" @click="isEditable && toggleDropdown('authTargetLevel')">
                                <span class="selected-tags" v-if="formData.authTargetLevel.length > 0">
                                    <span class="tag" v-for="(item, index) in getSelectedLabels(formData.authTargetLevel, authTargetLevelOptions)" :key="index">
                                        {{ item }}
                                        <span v-if="isEditable" class="tag-close" @click.stop="removeSelected('authTargetLevel', formData.authTargetLevel[index])">×</span>
                                    </span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="multi-select-dropdown" v-show="activeDropdown === 'authTargetLevel' && isEditable">
                                <div class="select-option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                                    <span class="tree-checkbox" :class="{ checked: formData.authTargetLevel.includes(item.code) }"></span>
                                    <span>{{ item.name }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label required">适用区域</label>
                        <div class="tree-select-wrapper" v-click-outside="closeDropdown">
                            <div class="tree-select-trigger" @click="isEditable && toggleDropdown('applicableRegion')">
                                <span class="selected-tags" v-if="formData.applicableRegion.length > 0">
                                    <span class="tag" v-for="(item, index) in formData.applicableRegion.slice(0, 2)" :key="index">
                                        {{ getTreeName(item, applicableRegionTree) }}
                                        <span v-if="isEditable" class="tag-close" @click.stop="removeSelected('applicableRegion', item)">×</span>
                                    </span>
                                    <span class="tag" v-if="formData.applicableRegion.length > 2">+{{ formData.applicableRegion.length - 2 }}</span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="tree-select-dropdown" v-show="activeDropdown === 'applicableRegion' && isEditable">
                                <tree-node
                                    v-for="node in applicableRegionTree"
                                    :key="node.code"
                                    :node="node"
                                    :selected-codes="formData.applicableRegion"
                                    @toggle="toggleTreeNode('applicableRegion', $event)"
                                ></tree-node>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label required">授权书发布年份</label>
                        <div class="year-select-wrapper" v-click-outside="closeDropdown">
                            <div class="year-select-trigger" @click="isEditable && toggleDropdown('publishYear')">
                                <span>{{ formData.publishYear || '请选择年份' }}</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="year-select-dropdown" v-show="activeDropdown === 'publishYear' && isEditable">
                                <div class="year-option" v-for="year in yearOptions" :key="year" @click="selectYear(year)">{{ year }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-item full-width">
                        <label class="form-label required">授权书内容摘要</label>
                        <textarea v-model="formData.contentSummary" class="form-textarea" placeholder="请输入授权书内容摘要（最大4000字）" maxlength="4000" :disabled="!isEditable"></textarea>
                    </div>
                </div>
            </div>
        </div>

        <!-- 附件区块 -->
        <div class="card">
            <div class="card-header">
                <span class="card-title">附件</span>
            </div>
            <div style="display: flex; align-items: flex-start;">
                <span class="section-label">附件</span>
                <div style="flex: 1;">
                    <div class="action-buttons" v-if="isEditable">
                        <button class="btn btn-primary" @click="handleUploadAttachment">上传</button>
                        <button class="btn btn-default" @click="handleDownloadAttachment">下载</button>
                        <button class="btn btn-danger" @click="handleDeleteAttachment">删除</button>
                    </div>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="col-checkbox"><input type="checkbox" v-model="attachmentSelectAll" @change="handleAttachmentSelectAll" /></th>
                                <th class="col-index">序号</th>
                                <th class="col-action">操作</th>
                                <th>文档名称</th>
                                <th>文档类型</th>
                                <th>创建人</th>
                                <th>创建时间</th>
                                <th>更新人</th>
                                <th>更新时间</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="formData.attachments.length === 0">
                                <td colspan="9" class="empty-data">暂无附件</td>
                            </tr>
                            <tr v-else v-for="(row, index) in formData.attachments" :key="row.id || index">
                                <td class="col-checkbox"><input type="checkbox" v-model="selectedAttachments" :value="row.id || index" /></td>
                                <td class="col-index">{{ index + 1 }}</td>
                                <td class="col-action">
                                    <span class="icon-btn" @click="downloadAttachment(row)" title="下载">⬇️</span>
                                </td>
                                <td><a class="link" @click="downloadAttachment(row)">{{ row.docName }}</a></td>
                                <td>{{ row.docTypeText || row.docType || '-' }}</td>
                                <td>{{ row.createdBy || '-' }}</td>
                                <td>{{ row.createdAt || '-' }}</td>
                                <td>{{ row.updatedBy || '-' }}</td>
                                <td>{{ row.updatedAt || '-' }}</td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="pagination" v-if="formData.attachments.length > 0">
                        <span class="pagination-total">共 {{ formData.attachments.length }} 条</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 授权规则区块 -->
        <div class="card" style="margin-bottom: 80px;">
            <div class="card-header">
                <span class="card-title">授权规则</span>
            </div>
            <div style="display: flex; align-items: flex-start;">
                <span class="section-label">授权规则</span>
                <div style="flex: 1;">
                    <div class="action-buttons" v-if="isEditable">
                        <button class="btn btn-primary" @click="handleAddScene">添加场景</button>
                        <button class="btn btn-danger" @click="handleDeleteScene">删除</button>
                    </div>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th class="col-checkbox"><input type="checkbox" v-model="sceneSelectAll" @change="handleSceneSelectAll" /></th>
                                <th class="col-index">序号</th>
                                <th class="col-action">操作</th>
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
                            <tr v-if="formData.scenes.length === 0">
                                <td colspan="12" class="empty-data">暂无场景</td>
                            </tr>
                            <tr v-else v-for="(row, index) in formData.scenes" :key="row.id || index">
                                <td class="col-checkbox"><input type="checkbox" v-model="selectedScenes" :value="row.id || index" /></td>
                                <td class="col-index">{{ index + 1 }}</td>
                                <td class="col-action">
                                    <span class="icon-btn" @click="editScene(row)" title="编辑">✏️</span>
                                    <span class="icon-btn" @click="deleteSingleScene(index)" title="删除">🗑️</span>
                                </td>
                                <td>{{ row.name }}</td>
                                <td>{{ row.industryText || '-' }}</td>
                                <td>{{ row.businessScenarioText || '-' }}</td>
                                <td>{{ row.ruleDetail || '-' }}</td>
                                <td>{{ row.decisionLevelText || '-' }}</td>
                                <td>{{ row.createdBy || '-' }}</td>
                                <td>{{ row.createdAt || '-' }}</td>
                                <td>{{ row.updatedBy || '-' }}</td>
                                <td>{{ row.updatedAt || '-' }}</td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="pagination" v-if="formData.scenes.length > 0">
                        <span class="pagination-total">共 {{ formData.scenes.length }} 条</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 操作日志区块（已发布状态显示） -->
        <div class="card" v-if="formData.status === 'PUBLISHED' || formData.status === 'EXPIRED'">
            <div class="collapse-header" @click="toggleLogCollapse">
                <span class="collapse-icon" :class="{ expanded: logExpanded }">▶</span>
                <span>操作日志</span>
            </div>
            <div class="collapse-content" v-show="logExpanded">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th class="col-index">序号</th>
                            <th>操作</th>
                            <th>操作人</th>
                            <th>操作时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="operationLogs.length === 0">
                            <td colspan="4" class="empty-data">暂无日志</td>
                        </tr>
                        <tr v-else v-for="(log, index) in operationLogs" :key="log.id">
                            <td class="col-index">{{ index + 1 }}</td>
                            <td>{{ log.operationTypeText }}</td>
                            <td>{{ log.operator }}</td>
                            <td>{{ log.operatedAt }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 底部固定按钮 -->
        <div class="footer-buttons">
            <template v-if="isEditable">
                <button class="btn btn-primary" @click="handleSave">保存</button>
                <button class="btn btn-success" @click="handleSaveAndPublish">保存并发布</button>
                <button class="btn btn-warning" @click="handlePublish">发布</button>
                <button class="btn btn-default" @click="handleCancel">取消</button>
                <button class="btn btn-danger" v-if="authLetterId" @click="handleDelete">删除</button>
            </template>
            <template v-else>
                <button class="btn btn-default" @click="handleBack">返回</button>
                <button class="btn btn-warning" v-if="formData.status === 'PUBLISHED'" @click="handleExpire">失效</button>
                <button class="btn btn-danger" @click="handleDelete">删除</button>
            </template>
        </div>

        <!-- 场景配置弹窗 -->
        <div class="modal-overlay" v-if="sceneDialog.show" @click.self="sceneDialog.show = false">
            <div class="modal-dialog" style="min-width: 700px;">
                <div class="modal-header">
                    <span class="modal-title">{{ sceneDialog.isEdit ? '编辑场景' : '添加场景' }}</span>
                    <span class="modal-close" @click="sceneDialog.show = false">×</span>
                </div>
                <div class="modal-body">
                    <div class="form-row">
                        <div class="form-item">
                            <label class="form-label required">场景名称</label>
                            <input type="text" v-model="sceneDialog.data.name" class="form-input" placeholder="请输入场景名称" />
                        </div>
                        <div class="form-item">
                            <label class="form-label required">产业</label>
                            <div class="tree-select-wrapper" v-click-outside="closeSceneDropdown">
                                <div class="tree-select-trigger" @click="toggleSceneDropdown('industry')">
                                    <span class="selected-tags" v-if="sceneDialog.data.industry.length > 0">
                                        <span class="tag">{{ getTreeName(sceneDialog.data.industry[0], industryTree) }}</span>
                                        <span class="tag" v-if="sceneDialog.data.industry.length > 1">+{{ sceneDialog.data.industry.length - 1 }}</span>
                                    </span>
                                    <span class="placeholder" v-else>请选择</span>
                                    <span class="arrow">▼</span>
                                </div>
                                <div class="tree-select-dropdown" v-show="sceneDialog.activeDropdown === 'industry'">
                                    <tree-node
                                        v-for="node in industryTree"
                                        :key="node.code"
                                        :node="node"
                                        :selected-codes="sceneDialog.data.industry"
                                        @toggle="toggleSceneTreeNode('industry', $event)"
                                    ></tree-node>
                                </div>
                            </div>
                        </div>
                        <div class="form-item">
                            <label class="form-label required">业务场景</label>
                            <div class="select-wrapper" v-click-outside="closeSceneDropdown">
                                <div class="select-trigger" @click="toggleSceneDropdown('businessScenario')">
                                    <span>{{ getSelectLabel(sceneDialog.data.businessScenario, businessScenarioOptions) || '请选择' }}</span>
                                    <span class="arrow">▼</span>
                                </div>
                                <div class="select-dropdown" v-show="sceneDialog.activeDropdown === 'businessScenario'">
                                    <div class="select-option" v-for="item in businessScenarioOptions" :key="item.code" @click="selectBusinessScenario(item.code)">
                                        {{ item.name }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-item">
                            <label class="form-label required">决策层级</label>
                            <div class="select-wrapper" v-click-outside="closeSceneDropdown">
                                <div class="select-trigger" @click="toggleSceneDropdown('decisionLevel')">
                                    <span>{{ getSelectLabel(sceneDialog.data.decisionLevel, decisionLevelOptions) || '请选择' }}</span>
                                    <span class="arrow">▼</span>
                                </div>
                                <div class="select-dropdown" v-show="sceneDialog.activeDropdown === 'decisionLevel'">
                                    <div class="select-option" v-for="item in decisionLevelOptions" :key="item.code" @click="selectDecisionLevel(item.code)">
                                        {{ item.name }}
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-item" style="grid-column: span 2;">
                            <label class="form-label required">具体规则</label>
                            <textarea v-model="sceneDialog.data.ruleDetail" class="form-textarea" placeholder="请输入具体规则（最大1000字）" maxlength="1000"></textarea>
                        </div>
                    </div>

                    <!-- 规则配置 -->
                    <div style="margin-top: 16px;">
                        <div style="display: flex; align-items: center; margin-bottom: 12px;">
                            <span style="font-weight: 500; margin-right: 16px;">规则配置</span>
                            <button class="btn btn-text" @click="addCondition">+ 新增条件</button>
                            <button class="btn btn-text" @click="addConditionGroup">+ 新增子条件组</button>
                        </div>
                        <div v-if="sceneDialog.data.conditionGroups && sceneDialog.data.conditionGroups.conditions && sceneDialog.data.conditionGroups.conditions.length > 0">
                            <condition-group
                                :group="sceneDialog.data.conditionGroups"
                                :rule-params="ruleParamOptions"
                                :operators="operatorOptions"
                                :compare-types="compareTypeOptions"
                                :number-units="numberUnitOptions"
                                @remove-condition="removeCondition"
                                @remove-group="removeConditionGroup"
                            ></condition-group>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-default" @click="sceneDialog.show = false">取消</button>
                    <button class="btn btn-primary" @click="confirmScene">确定</button>
                </div>
            </div>
        </div>

        <!-- 消息提示 -->
        <div class="message-box" v-if="message.show" :class="'message-' + message.type">
            {{ message.text }}
        </div>

        <!-- 确认对话框 -->
        <div class="modal-overlay" v-if="confirmDialog.show" @click.self="confirmDialog.onCancel">
            <div class="modal-dialog">
                <div class="modal-header">
                    <span class="modal-title">提示</span>
                </div>
                <div class="modal-body">{{ confirmDialog.text }}</div>
                <div class="modal-footer">
                    <button class="btn btn-default" @click="confirmDialog.onCancel">取消</button>
                    <button class="btn btn-primary" @click="confirmDialog.onConfirm">确定</button>
                </div>
            </div>
        </div>
    </div>
    `,

    data() {
        return {
            authLetterId: null,
            loading: false,
            activeDropdown: '',
            message: { show: false, type: 'info', text: '' },
            confirmDialog: { show: false, text: '', onConfirm: () => {}, onCancel: () => {} },
            logExpanded: false,

            formData: {
                name: '',
                authPublishLevel: [],
                authPublishOrg: [],
                authTargetLevel: [],
                applicableRegion: [],
                publishYear: null,
                contentSummary: '',
                status: 'DRAFT',
                attachments: [],
                scenes: []
            },

            // 下拉选项
            authPublishLevelOptions: [],
            authTargetLevelOptions: [],
            orgTree: [],
            applicableRegionTree: [],
            industryTree: [],
            businessScenarioOptions: [],
            decisionLevelOptions: [],
            ruleParamOptions: [],
            operatorOptions: [
                { code: 'OP_GT', name: '>' },
                { code: 'OP_LT', name: '<' },
                { code: 'OP_EQ', name: '=' },
                { code: 'OP_GTE', name: '>=' },
                { code: 'OP_LTE', name: '<=' },
                { code: 'OP_NEQ', name: '!=' }
            ],
            compareTypeOptions: [
                { code: 'COMPARE_FIELD', name: '对比字段' },
                { code: 'NUMBER', name: '数值' },
                { code: 'TEXT', name: '文本' }
            ],
            numberUnitOptions: [
                { code: 'UNIT_K', name: '千' },
                { code: 'UNIT_W', name: '万' },
                { code: 'UNIT_M', name: '百万' }
            ],

            // 选择状态
            selectedAttachments: [],
            attachmentSelectAll: false,
            selectedScenes: [],
            sceneSelectAll: false,

            // 操作日志
            operationLogs: [],

            // 场景弹窗
            sceneDialog: {
                show: false,
                isEdit: false,
                editIndex: -1,
                activeDropdown: '',
                data: {
                    name: '',
                    industry: [],
                    businessScenario: '',
                    decisionLevel: '',
                    ruleDetail: '',
                    conditionGroups: { logicOperator: 'AND', conditions: [] }
                }
            }
        };
    },

    computed: {
        isEditable() {
            return this.formData.status === 'DRAFT';
        },
        yearOptions() {
            const years = [];
            for (let i = new Date().getFullYear(); i >= new Date().getFullYear() - 10; i--) {
                years.push(i);
            }
            return years;
        }
    },

    mounted() {
        const urlParams = new URLSearchParams(window.location.hash.split('?')[1] || '');
        this.authLetterId = urlParams.get('id');

        this.loadLookupData();

        if (this.authLetterId) {
            this.loadDetail();
        }
    },

    methods: {
        async loadLookupData() {
            try {
                const [publishLevel, targetLevel, orgTree, regionTree, industry, bizScenario, decLevel, ruleParams] = await Promise.all([
                    this.fetchApi('/api/lookup/AUTH_PUBLISH_LEVEL'),
                    this.fetchApi('/api/lookup/AUTH_TARGET_LEVEL'),
                    this.fetchApi('/api/lookup/tree/ORG_TREE'),
                    this.fetchApi('/api/lookup/tree/APPLICABLE_REGION'),
                    this.fetchApi('/api/lookup/tree/INDUSTRY'),
                    this.fetchApi('/api/lookup/BUSINESS_SCENARIO'),
                    this.fetchApi('/api/lookup/DECISION_LEVEL'),
                    this.fetchApi('/api/rule-params/active')
                ]);
                this.authPublishLevelOptions = publishLevel.data || [];
                this.authTargetLevelOptions = targetLevel.data || [];
                this.orgTree = orgTree.data || [];
                this.applicableRegionTree = regionTree.data || [];
                this.industryTree = industry.data || [];
                this.businessScenarioOptions = bizScenario.data || [];
                this.decisionLevelOptions = decLevel.data || [];
                this.ruleParamOptions = ruleParams.data || [];
            } catch (error) {
                console.error('加载Lookup数据失败:', error);
            }
        },

        async loadDetail() {
            this.loading = true;
            try {
                const res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}`);
                if (res.code === 200) {
                    const data = res.data;
                    this.formData = {
                        name: data.name || '',
                        authPublishLevel: data.authPublishLevel || [],
                        authPublishOrg: data.authPublishOrg || [],
                        authTargetLevel: data.authTargetLevel || [],
                        applicableRegion: data.applicableRegion || [],
                        publishYear: data.publishYear || null,
                        contentSummary: data.contentSummary || '',
                        status: data.status || 'DRAFT',
                        attachments: data.attachments || [],
                        scenes: data.scenes || []
                    };
                    if (data.status !== 'DRAFT') {
                        this.loadOperationLogs();
                    }
                } else {
                    this.showMessage(res.message || '加载数据失败', 'error');
                }
            } catch (error) {
                console.error('加载数据失败:', error);
                this.showMessage('加载数据失败', 'error');
            } finally {
                this.loading = false;
            }
        },

        async loadOperationLogs() {
            try {
                const res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}/logs`);
                if (res.code === 200) {
                    this.operationLogs = res.data?.list || [];
                }
            } catch (error) {
                console.error('加载日志失败:', error);
            }
        },

        async fetchApi(url, options = {}) {
            const config = {
                headers: { 'Content-Type': 'application/json' },
                ...options
            };
            if (config.body && typeof config.body === 'object') {
                config.body = JSON.stringify(config.body);
            }
            const response = await fetch(url, config);
            return response.json();
        },

        // 通用方法
        toggleDropdown(name) {
            this.activeDropdown = this.activeDropdown === name ? '' : name;
        },

        closeDropdown() {
            this.activeDropdown = '';
        },

        showMessage(text, type = 'info') {
            this.message = { show: true, type, text };
            setTimeout(() => { this.message.show = false; }, 3000);
        },

        showConfirm(text) {
            return new Promise((resolve) => {
                this.confirmDialog = {
                    show: true, text,
                    onConfirm: () => { this.confirmDialog.show = false; resolve(true); },
                    onCancel: () => { this.confirmDialog.show = false; resolve(false); }
                };
            });
        },

        getSelectedLabels(codes, options) {
            return codes.map(code => {
                const item = options.find(o => o.code === code);
                return item ? item.name : code;
            });
        },

        getSelectLabel(code, options) {
            const item = options.find(o => o.code === code);
            return item ? item.name : '';
        },

        getTreeName(code, tree) {
            const findNode = (nodes, targetCode) => {
                for (const node of nodes) {
                    if (node.code === targetCode) return node.name;
                    if (node.children) {
                        const found = findNode(node.children, targetCode);
                        if (found) return found;
                    }
                }
                return null;
            };
            return findNode(tree, code) || code;
        },

        toggleMultiSelect(field, value) {
            const index = this.formData[field].indexOf(value);
            if (index > -1) {
                this.formData[field].splice(index, 1);
            } else {
                this.formData[field].push(value);
            }
        },

        removeSelected(field, value) {
            const index = this.formData[field].indexOf(value);
            if (index > -1) {
                this.formData[field].splice(index, 1);
            }
        },

        toggleTreeNode(field, node) {
            const toggleNodeAndChildren = (n, shouldCheck) => {
                const index = this.formData[field].indexOf(n.code);
                if (shouldCheck && index === -1) {
                    this.formData[field].push(n.code);
                } else if (!shouldCheck && index > -1) {
                    this.formData[field].splice(index, 1);
                }
                if (n.children) {
                    n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck));
                }
            };
            toggleNodeAndChildren(node, !this.formData[field].includes(node.code));
        },

        selectYear(year) {
            this.formData.publishYear = year;
            this.activeDropdown = '';
        },

        // 附件相关
        handleAttachmentSelectAll() {
            this.selectedAttachments = this.attachmentSelectAll
                ? this.formData.attachments.map(a => a.id)
                : [];
        },

        handleUploadAttachment() {
            this.showMessage('附件上传功能待实现', 'info');
        },

        handleDownloadAttachment() {
            if (this.selectedAttachments.length === 0) {
                this.showMessage('请先选择附件', 'warning');
                return;
            }
            this.showMessage('附件下载功能待实现', 'info');
        },

        handleDeleteAttachment() {
            if (this.selectedAttachments.length === 0) {
                this.showMessage('请先选择附件', 'warning');
                return;
            }
            this.formData.attachments = this.formData.attachments.filter(a => !this.selectedAttachments.includes(a.id));
            this.selectedAttachments = [];
            this.attachmentSelectAll = false;
        },

        downloadAttachment(row) {
            this.showMessage('附件下载功能待实现', 'info');
        },

        // 场景相关
        handleSceneSelectAll() {
            this.selectedScenes = this.sceneSelectAll
                ? this.formData.scenes.map(s => s.id)
                : [];
        },

        handleAddScene() {
            this.sceneDialog = {
                show: true,
                isEdit: false,
                editIndex: -1,
                activeDropdown: '',
                data: {
                    name: '',
                    industry: [],
                    businessScenario: '',
                    decisionLevel: '',
                    ruleDetail: '',
                    conditionGroups: { logicOperator: 'AND', conditions: [] }
                }
            };
        },

        handleDeleteScene() {
            if (this.selectedScenes.length === 0) {
                this.showMessage('请先选择场景', 'warning');
                return;
            }
            this.formData.scenes = this.formData.scenes.filter(s => !this.selectedScenes.includes(s.id));
            this.selectedScenes = [];
            this.sceneSelectAll = false;
        },

        editScene(row) {
            const index = this.formData.scenes.indexOf(row);
            this.sceneDialog = {
                show: true,
                isEdit: true,
                editIndex: index,
                activeDropdown: '',
                data: {
                    name: row.name,
                    industry: row.industry || [],
                    businessScenario: row.businessScenario || '',
                    decisionLevel: row.decisionLevel || '',
                    ruleDetail: row.ruleDetail || '',
                    conditionGroups: row.conditionGroups || { logicOperator: 'AND', conditions: [] }
                }
            };
        },

        deleteSingleScene(index) {
            this.formData.scenes.splice(index, 1);
        },

        // 场景弹窗方法
        toggleSceneDropdown(name) {
            this.sceneDialog.activeDropdown = this.sceneDialog.activeDropdown === name ? '' : name;
        },

        closeSceneDropdown() {
            this.sceneDialog.activeDropdown = '';
        },

        toggleSceneTreeNode(field, node) {
            const toggleNodeAndChildren = (n, shouldCheck) => {
                const index = this.sceneDialog.data[field].indexOf(n.code);
                if (shouldCheck && index === -1) {
                    this.sceneDialog.data[field].push(n.code);
                } else if (!shouldCheck && index > -1) {
                    this.sceneDialog.data[field].splice(index, 1);
                }
                if (n.children) {
                    n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck));
                }
            };
            toggleNodeAndChildren(node, !this.sceneDialog.data[field].includes(node.code));
        },

        selectBusinessScenario(code) {
            this.sceneDialog.data.businessScenario = code;
            this.sceneDialog.activeDropdown = '';
        },

        selectDecisionLevel(code) {
            this.sceneDialog.data.decisionLevel = code;
            this.sceneDialog.activeDropdown = '';
        },

        // 规则条件配置
        addCondition() {
            this.sceneDialog.data.conditionGroups.conditions.push({
                type: 'condition',
                fieldName: '',
                operator: 'OP_EQ',
                compareConfig: { type: 'TEXT', value: '' }
            });
        },

        addConditionGroup() {
            this.sceneDialog.data.conditionGroups.conditions.push({
                type: 'group',
                logicOperator: 'AND',
                conditions: []
            });
        },

        removeCondition(index) {
            this.sceneDialog.data.conditionGroups.conditions.splice(index, 1);
        },

        removeConditionGroup(index) {
            this.sceneDialog.data.conditionGroups.conditions.splice(index, 1);
        },

        confirmScene() {
            if (!this.sceneDialog.data.name) {
                this.showMessage('请输入场景名称', 'warning');
                return;
            }
            if (this.sceneDialog.data.industry.length === 0) {
                this.showMessage('请选择产业', 'warning');
                return;
            }
            if (!this.sceneDialog.data.businessScenario) {
                this.showMessage('请选择业务场景', 'warning');
                return;
            }
            if (!this.sceneDialog.data.decisionLevel) {
                this.showMessage('请选择决策层级', 'warning');
                return;
            }
            if (!this.sceneDialog.data.ruleDetail) {
                this.showMessage('请输入具体规则', 'warning');
                return;
            }

            const sceneData = {
                id: this.sceneDialog.isEdit ? this.formData.scenes[this.sceneDialog.editIndex].id : Date.now(),
                name: this.sceneDialog.data.name,
                industry: this.sceneDialog.data.industry,
                industryText: this.sceneDialog.data.industry.map(c => this.getTreeName(c, this.industryTree)).join(', '),
                businessScenario: this.sceneDialog.data.businessScenario,
                businessScenarioText: this.getSelectLabel(this.sceneDialog.data.businessScenario, this.businessScenarioOptions),
                decisionLevel: this.sceneDialog.data.decisionLevel,
                decisionLevelText: this.getSelectLabel(this.sceneDialog.data.decisionLevel, this.decisionLevelOptions),
                ruleDetail: this.sceneDialog.data.ruleDetail,
                conditionGroups: this.sceneDialog.data.conditionGroups
            };

            if (this.sceneDialog.isEdit) {
                this.formData.scenes.splice(this.sceneDialog.editIndex, 1, sceneData);
            } else {
                this.formData.scenes.push(sceneData);
            }

            this.sceneDialog.show = false;
        },

        // 操作日志
        toggleLogCollapse() {
            this.logExpanded = !this.logExpanded;
        },

        // 底部按钮操作
        async handleSave() {
            if (!this.validateForm()) return;

            try {
                const payload = {
                    name: this.formData.name,
                    authPublishLevel: this.formData.authPublishLevel,
                    authPublishOrg: this.formData.authPublishOrg,
                    authTargetLevel: this.formData.authTargetLevel,
                    applicableRegion: this.formData.applicableRegion,
                    publishYear: this.formData.publishYear,
                    contentSummary: this.formData.contentSummary,
                    attachments: this.formData.attachments,
                    scenes: this.formData.scenes
                };

                let res;
                if (this.authLetterId) {
                    res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}`, {
                        method: 'PUT',
                        body: payload
                    });
                } else {
                    res = await this.fetchApi('/api/auth-letters', {
                        method: 'POST',
                        body: payload
                    });
                    if (res.code === 200) {
                        this.authLetterId = res.data;
                    }
                }

                if (res.code === 200) {
                    this.showMessage('保存成功', 'success');
                    this.loadDetail();
                } else {
                    this.showMessage(res.message || '保存失败', 'error');
                }
            } catch (error) {
                this.showMessage('保存失败', 'error');
            }
        },

        async handleSaveAndPublish() {
            if (!this.validateForm()) return;

            const saved = await this.handleSave();
            if (saved !== false) {
                await this.handlePublish();
            }
        },

        async handlePublish() {
            if (!this.authLetterId) {
                this.showMessage('请先保存', 'warning');
                return;
            }

            const confirmed = await this.showConfirm('确定要发布该授权书吗？');
            if (confirmed) {
                try {
                    const res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}/publish`, {
                        method: 'PUT'
                    });
                    if (res.code === 200) {
                        this.showMessage('发布成功', 'success');
                        this.loadDetail();
                    } else {
                        this.showMessage(res.message || '发布失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('发布失败', 'error');
                }
            }
        },

        handleCancel() {
            window.location.href = '#/AuthLetterList';
        },

        handleBack() {
            window.location.href = '#/AuthLetterList';
        },

        async handleExpire() {
            const confirmed = await this.showConfirm('确定要将该授权书设为失效吗？');
            if (confirmed) {
                try {
                    const res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}/expire`, {
                        method: 'PUT'
                    });
                    if (res.code === 200) {
                        this.showMessage('操作成功', 'success');
                        this.loadDetail();
                    } else {
                        this.showMessage(res.message || '操作失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('操作失败', 'error');
                }
            }
        },

        async handleDelete() {
            const confirmed = await this.showConfirm('确定要删除该授权书吗？');
            if (confirmed) {
                try {
                    const res = await this.fetchApi(`/api/auth-letters/${this.authLetterId}`, {
                        method: 'DELETE'
                    });
                    if (res.code === 200) {
                        this.showMessage('删除成功', 'success');
                        setTimeout(() => {
                            window.location.href = '#/AuthLetterList';
                        }, 1000);
                    } else {
                        this.showMessage(res.message || '删除失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('删除失败', 'error');
                }
            }
        },

        validateForm() {
            if (!this.formData.name) {
                this.showMessage('请输入授权书名称', 'warning');
                return false;
            }
            if (this.formData.authPublishLevel.length === 0) {
                this.showMessage('请选择授权发布层级', 'warning');
                return false;
            }
            if (this.formData.authPublishOrg.length === 0) {
                this.showMessage('请选择授权发布组织', 'warning');
                return false;
            }
            if (this.formData.authTargetLevel.length === 0) {
                this.showMessage('请选择授权对象层级', 'warning');
                return false;
            }
            if (this.formData.applicableRegion.length === 0) {
                this.showMessage('请选择适用区域', 'warning');
                return false;
            }
            if (!this.formData.publishYear) {
                this.showMessage('请选择授权书发布年份', 'warning');
                return false;
            }
            if (!this.formData.contentSummary) {
                this.showMessage('请输入授权书内容摘要', 'warning');
                return false;
            }
            return true;
        }
    }
};

// 条件组组件
const ConditionGroup = {
    name: 'ConditionGroup',
    props: {
        group: Object,
        ruleParams: Array,
        operators: Array,
        compareTypes: Array,
        numberUnits: Array,
        level: {
            type: Number,
            default: 0
        }
    },
    methods: {
        getLabel(code, list) {
            const item = list.find(i => i.code === code);
            return item ? item.name : code;
        },
        toggleLogic() {
            this.group.logicOperator = this.group.logicOperator === 'AND' ? 'OR' : 'AND';
        },
        addCondition() {
            this.group.conditions.push({
                type: 'condition',
                fieldName: '',
                operator: 'OP_EQ',
                compareConfig: { type: 'TEXT', value: '' }
            });
        },
        addSubGroup() {
            this.group.conditions.push({
                type: 'group',
                logicOperator: 'AND',
                conditions: []
            });
        },
        removeCondition(index) {
            this.group.conditions.splice(index, 1);
        },
        removeSelf() {
            this.$emit('remove-group');
        }
    },
    template: `
        <div class="condition-group" :style="{ marginLeft: level * 20 + 'px' }">
            <div v-for="(item, index) in group.conditions" :key="index" style="margin-bottom: 8px;">
                <div v-if="item.type === 'condition'" class="condition-row">
                    <span v-if="index > 0" class="logic-operator" :class="{ or: group.logicOperator === 'OR' }" @click="toggleLogic">
                        {{ group.logicOperator }}
                    </span>
                    <span v-else style="width: 40px;"></span>
                    <select v-model="item.fieldName" class="form-input" style="width: 150px;">
                        <option value="">请选择字段</option>
                        <option v-for="p in ruleParams" :key="p.code" :value="p.code">{{ p.name }}</option>
                    </select>
                    <select v-model="item.operator" class="form-input" style="width: 80px;">
                        <option v-for="op in operators" :key="op.code" :value="op.code">{{ op.name }}</option>
                    </select>
                    <select v-model="item.compareConfig.type" class="form-input" style="width: 100px;">
                        <option v-for="ct in compareTypes" :key="ct.code" :value="ct.code">{{ ct.name }}</option>
                    </select>
                    <template v-if="item.compareConfig.type === 'COMPARE_FIELD'">
                        <select v-model="item.compareConfig.value" class="form-input" style="width: 150px;">
                            <option value="">请选择字段</option>
                            <option v-for="p in ruleParams" :key="p.code" :value="p.code">{{ p.name }}</option>
                        </select>
                    </template>
                    <template v-else-if="item.compareConfig.type === 'NUMBER'">
                        <input type="number" v-model="item.compareConfig.value" class="form-input" style="width: 100px;" />
                        <select v-model="item.compareConfig.unit" class="form-input" style="width: 80px;">
                            <option v-for="u in numberUnits" :key="u.code" :value="u.code">{{ u.name }}</option>
                        </select>
                    </template>
                    <template v-else>
                        <input type="text" v-model="item.compareConfig.value" class="form-input" style="width: 150px;" />
                    </template>
                    <span class="condition-delete" @click="removeCondition(index)">×</span>
                </div>
                <div v-else>
                    <condition-group
                        :group="item"
                        :rule-params="ruleParams"
                        :operators="operators"
                        :compare-types="compareTypes"
                        :number-units="numberUnits"
                        :level="1"
                        @remove-group="removeCondition(index)"
                    ></condition-group>
                </div>
            </div>
            <div style="margin-top: 8px;">
                <button class="btn btn-text" @click="addCondition">+ 新增条件</button>
                <button class="btn btn-text" @click="addSubGroup">+ 新增子条件组</button>
                <button class="btn btn-text" v-if="level > 0" @click="removeSelf">移除条件组</button>
            </div>
        </div>
    `
};

Vue.component('condition-group', ConditionGroup);
Vue.component('tree-node', TreeNode);

// 点击外部指令
Vue.directive('click-outside', {
    bind(el, binding, vnode) {
        el.clickOutsideEvent = function(event) {
            if (!(el === event.target || el.contains(event.target))) {
                vnode.context[binding.expression](event);
            }
        };
        document.body.addEventListener('click', el.clickOutsideEvent);
    },
    unbind(el) {
        document.body.removeEventListener('click', el.clickOutsideEvent);
    }
});