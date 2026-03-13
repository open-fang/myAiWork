// 规则参数配置页组件
const RuleParamConfig = {
    template: `
    <div class="page-container">
        <!-- 查询条件区 -->
        <div class="card">
            <div class="query-form">
                <div class="form-row">
                    <div class="form-item">
                        <label class="form-label">名称</label>
                        <input type="text" v-model="queryParams.name" class="form-input" placeholder="请输入名称" />
                    </div>
                    <div class="form-item">
                        <label class="form-label">名称英文</label>
                        <input type="text" v-model="queryParams.nameEn" class="form-input" placeholder="请输入名称英文" />
                    </div>
                    <div class="form-item">
                        <label class="form-label">状态</label>
                        <div class="select-wrapper" v-click-outside="closeDropdown">
                            <div class="select-trigger" @click="toggleDropdown('status')">
                                <span>{{ getStatusLabel(queryParams.status) || '请选择' }}</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="select-dropdown" v-show="activeDropdown === 'status'">
                                <div class="select-option" @click="selectStatus('')">全部</div>
                                <div class="select-option" v-for="item in statusOptions" :key="item.value" @click="selectStatus(item.value)">
                                    {{ item.label }}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <button class="btn btn-primary" @click="handleQuery">查询</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 功能按钮区 -->
        <div class="action-buttons">
            <button class="btn btn-primary" @click="handleCreate">新建</button>
            <button class="btn btn-success" @click="handleActivate">生效</button>
            <button class="btn btn-warning" @click="handleDeactivate">失效</button>
        </div>

        <!-- 数据表格区 -->
        <div class="card">
            <table class="data-table">
                <thead>
                    <tr>
                        <th class="col-checkbox"><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
                        <th class="col-index">序号</th>
                        <th class="col-action">操作</th>
                        <th>名称</th>
                        <th>名称英文</th>
                        <th style="width:80px;text-align:center">状态</th>
                        <th style="width:100px;text-align:center">创建人</th>
                        <th style="width:160px;text-align:center">创建时间</th>
                        <th style="width:100px;text-align:center">更新人</th>
                        <th style="width:160px;text-align:center">更新时间</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="loading">
                        <td colspan="10" class="empty-data">加载中...</td>
                    </tr>
                    <tr v-else-if="tableData.length === 0">
                        <td colspan="10" class="empty-data">暂无数据</td>
                    </tr>
                    <tr v-else v-for="(row, index) in tableData" :key="row.id">
                        <td class="col-checkbox"><input type="checkbox" v-model="selectedRows" :value="row.id" /></td>
                        <td class="col-index">{{ (pagination.pageNum - 1) * pagination.pageSize + index + 1 }}</td>
                        <td class="col-action">
                            <span class="icon-btn" @click="handleEdit(row)" title="编辑">✏️</span>
                        </td>
                        <td>{{ row.name }}</td>
                        <td>{{ row.nameEn }}</td>
                        <td style="text-align:center">
                            <span class="status-tag" :class="row.status === 'ACTIVE' ? 'status-published' : 'status-expired'">
                                {{ row.status === 'ACTIVE' ? '生效' : '失效' }}
                            </span>
                        </td>
                        <td style="text-align:center">{{ row.createdBy || '-' }}</td>
                        <td style="text-align:center">{{ row.createdAt || '-' }}</td>
                        <td style="text-align:center">{{ row.updatedBy || '-' }}</td>
                        <td style="text-align:center">{{ row.updatedAt || '-' }}</td>
                    </tr>
                </tbody>
            </table>

            <!-- 分页组件 -->
            <div class="pagination">
                <span class="pagination-total">共 {{ pagination.total }} 条</span>
                <select class="pagination-size" v-model="pagination.pageSize" @change="handleSizeChange">
                    <option :value="10">10条/页</option>
                    <option :value="30">30条/页</option>
                    <option :value="50">50条/页</option>
                </select>
                <button class="pagination-btn" :disabled="pagination.pageNum === 1" @click="handlePageChange(pagination.pageNum - 1)">上一页</button>
                <span class="pagination-page">{{ pagination.pageNum }} / {{ totalPages }}</span>
                <button class="pagination-btn" :disabled="pagination.pageNum >= totalPages" @click="handlePageChange(pagination.pageNum + 1)">下一页</button>
                <span class="pagination-jump">跳至<input type="number" v-model.number="jumpPage" @keyup.enter="handleJumpPage" />页</span>
            </div>
        </div>

        <!-- 新建/编辑弹窗 -->
        <div class="modal-overlay" v-if="paramDialog.show" @click.self="paramDialog.show = false">
            <div class="modal-dialog">
                <div class="modal-header">
                    <span class="modal-title">{{ paramDialog.isEdit ? '编辑规则参数' : '新建规则参数' }}</span>
                    <span class="modal-close" @click="paramDialog.show = false">×</span>
                </div>
                <div class="modal-body">
                    <div class="form-row">
                        <div class="form-item">
                            <label class="form-label required">名称</label>
                            <input type="text" v-model="paramDialog.data.name" class="form-input" placeholder="请输入名称" />
                        </div>
                        <div class="form-item">
                            <label class="form-label required">名称英文</label>
                            <input type="text" v-model="paramDialog.data.nameEn" class="form-input" placeholder="请输入名称英文" />
                        </div>
                    </div>

                    <!-- 业务对象映射 -->
                    <div style="margin-bottom: 16px;">
                        <div style="display: flex; align-items: center; margin-bottom: 8px;">
                            <span style="font-weight: 500; margin-right: 8px;">业务对象映射</span>
                            <button class="btn btn-text" @click="addBusinessMapping">+ 添加</button>
                        </div>
                        <div v-for="(mapping, index) in paramDialog.data.businessMappings" :key="index" class="form-row" style="margin-bottom: 8px;">
                            <div class="form-item">
                                <label class="form-label">业务对象</label>
                                <input type="text" v-model="mapping.businessObject" class="form-input" placeholder="业务对象" />
                            </div>
                            <div class="form-item">
                                <label class="form-label">取值逻辑</label>
                                <input type="text" v-model="mapping.valueLogic" class="form-input" placeholder="取值逻辑" />
                            </div>
                            <span class="condition-delete" @click="removeBusinessMapping(index)" style="cursor: pointer; color: #909399;">×</span>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-item">
                            <label class="form-label required">是否生效</label>
                            <div class="select-wrapper" v-click-outside="closeParamDropdown">
                                <div class="select-trigger" @click="toggleParamDropdown('isActive')">
                                    <span>{{ paramDialog.data.status === 'ACTIVE' ? '是' : '否' }}</span>
                                    <span class="arrow">▼</span>
                                </div>
                                <div class="select-dropdown" v-show="paramDialog.activeDropdown === 'isActive'">
                                    <div class="select-option" @click="selectParamStatus('ACTIVE')">是</div>
                                    <div class="select-option" @click="selectParamStatus('INACTIVE')">否</div>
                                </div>
                            </div>
                        </div>
                        <div class="form-item">
                            <label class="form-label required">数据类型</label>
                            <div class="select-wrapper" v-click-outside="closeParamDropdown">
                                <div class="select-trigger" @click="toggleParamDropdown('dataType')">
                                    <span>{{ getDataTypeLabel(paramDialog.data.dataType) || '请选择' }}</span>
                                    <span class="arrow">▼</span>
                                </div>
                                <div class="select-dropdown" v-show="paramDialog.activeDropdown === 'dataType'">
                                    <div class="select-option" v-for="item in dataTypeOptions" :key="item.value" @click="selectDataType(item.value)">
                                        {{ item.label }}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-default" @click="paramDialog.show = false">取消</button>
                    <button class="btn btn-primary" @click="confirmParam">确定</button>
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
            loading: false,
            activeDropdown: '',
            selectAll: false,
            selectedRows: [],
            jumpPage: 1,
            message: { show: false, type: 'info', text: '' },
            confirmDialog: { show: false, text: '', onConfirm: () => {}, onCancel: () => {} },

            queryParams: {
                name: '',
                nameEn: '',
                status: ''
            },

            pagination: { pageNum: 1, pageSize: 10, total: 0 },

            statusOptions: [
                { value: 'ACTIVE', label: '生效' },
                { value: 'INACTIVE', label: '失效' }
            ],

            dataTypeOptions: [
                { value: 'TEXT', label: '文本' },
                { value: 'NUMBER', label: '数值' },
                { value: 'FIELD', label: '比对字段' },
                { value: 'RATIO', label: '比率' }
            ],

            tableData: [],

            paramDialog: {
                show: false,
                isEdit: false,
                editId: null,
                activeDropdown: '',
                data: {
                    name: '',
                    nameEn: '',
                    status: 'ACTIVE',
                    dataType: 'TEXT',
                    businessMappings: []
                }
            }
        };
    },

    computed: {
        totalPages() {
            return Math.ceil(this.pagination.total / this.pagination.pageSize) || 1;
        }
    },

    mounted() {
        this.loadTableData();
    },

    methods: {
        async loadTableData() {
            this.loading = true;
            try {
                const params = new URLSearchParams();
                params.append('pageNum', this.pagination.pageNum);
                params.append('pageSize', this.pagination.pageSize);
                if (this.queryParams.name) params.append('name', this.queryParams.name);
                if (this.queryParams.nameEn) params.append('nameEn', this.queryParams.nameEn);
                if (this.queryParams.status) params.append('status', this.queryParams.status);

                const res = await this.fetchApi(`/api/rule-params?${params.toString()}`);
                if (res.code === 200) {
                    this.tableData = res.data?.list || [];
                    this.pagination.total = res.data?.total || 0;
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

        toggleDropdown(name) {
            this.activeDropdown = this.activeDropdown === name ? '' : name;
        },

        closeDropdown() {
            this.activeDropdown = '';
        },

        selectStatus(value) {
            this.queryParams.status = value;
            this.activeDropdown = '';
        },

        getStatusLabel(value) {
            const item = this.statusOptions.find(s => s.value === value);
            return item ? item.label : '';
        },

        handleSelectAll() {
            this.selectedRows = this.selectAll ? this.tableData.map(row => row.id) : [];
        },

        handleSizeChange() {
            this.pagination.pageNum = 1;
            this.loadTableData();
        },

        handlePageChange(page) {
            this.pagination.pageNum = page;
            this.loadTableData();
        },

        handleJumpPage() {
            if (this.jumpPage >= 1 && this.jumpPage <= this.totalPages) {
                this.pagination.pageNum = this.jumpPage;
                this.loadTableData();
            }
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

        handleQuery() {
            this.pagination.pageNum = 1;
            this.loadTableData();
        },

        checkSelection() {
            if (this.selectedRows.length === 0) {
                this.showMessage('请先选择数据', 'warning');
                return false;
            }
            return true;
        },

        handleCreate() {
            this.paramDialog = {
                show: true,
                isEdit: false,
                editId: null,
                activeDropdown: '',
                data: {
                    name: '',
                    nameEn: '',
                    status: 'ACTIVE',
                    dataType: 'TEXT',
                    businessMappings: []
                }
            };
        },

        async handleEdit(row) {
            try {
                const res = await this.fetchApi(`/api/rule-params/${row.id}`);
                if (res.code === 200) {
                    const data = res.data;
                    this.paramDialog = {
                        show: true,
                        isEdit: true,
                        editId: row.id,
                        activeDropdown: '',
                        data: {
                            name: data.name || '',
                            nameEn: data.nameEn || '',
                            status: data.status || 'ACTIVE',
                            dataType: data.dataType || 'TEXT',
                            businessMappings: data.businessMappings || []
                        }
                    };
                } else {
                    this.showMessage(res.message || '加载数据失败', 'error');
                }
            } catch (error) {
                this.showMessage('加载数据失败', 'error');
            }
        },

        async handleActivate() {
            if (!this.checkSelection()) return;
            const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为生效吗？`);
            if (confirmed) {
                try {
                    const res = await this.fetchApi('/api/rule-params/batch/activate', {
                        method: 'PUT',
                        body: { ids: this.selectedRows }
                    });
                    if (res.code === 200) {
                        this.showMessage('操作成功', 'success');
                        this.selectedRows = [];
                        this.selectAll = false;
                        this.loadTableData();
                    } else {
                        this.showMessage(res.message || '操作失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('操作失败', 'error');
                }
            }
        },

        async handleDeactivate() {
            if (!this.checkSelection()) return;
            const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据设为失效吗？`);
            if (confirmed) {
                try {
                    const res = await this.fetchApi('/api/rule-params/batch/deactivate', {
                        method: 'PUT',
                        body: { ids: this.selectedRows }
                    });
                    if (res.code === 200) {
                        this.showMessage('操作成功', 'success');
                        this.selectedRows = [];
                        this.selectAll = false;
                        this.loadTableData();
                    } else {
                        this.showMessage(res.message || '操作失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('操作失败', 'error');
                }
            }
        },

        // 弹窗方法
        toggleParamDropdown(name) {
            this.paramDialog.activeDropdown = this.paramDialog.activeDropdown === name ? '' : name;
        },

        closeParamDropdown() {
            this.paramDialog.activeDropdown = '';
        },

        selectParamStatus(value) {
            this.paramDialog.data.status = value;
            this.paramDialog.activeDropdown = '';
        },

        selectDataType(value) {
            this.paramDialog.data.dataType = value;
            this.paramDialog.activeDropdown = '';
        },

        getDataTypeLabel(value) {
            const item = this.dataTypeOptions.find(d => d.value === value);
            return item ? item.label : '';
        },

        addBusinessMapping() {
            this.paramDialog.data.businessMappings.push({
                businessObject: '',
                valueLogic: ''
            });
        },

        removeBusinessMapping(index) {
            this.paramDialog.data.businessMappings.splice(index, 1);
        },

        async confirmParam() {
            if (!this.paramDialog.data.name) {
                this.showMessage('请输入名称', 'warning');
                return;
            }
            if (!this.paramDialog.data.nameEn) {
                this.showMessage('请输入名称英文', 'warning');
                return;
            }
            if (!this.paramDialog.data.dataType) {
                this.showMessage('请选择数据类型', 'warning');
                return;
            }

            try {
                const payload = {
                    name: this.paramDialog.data.name,
                    nameEn: this.paramDialog.data.nameEn,
                    status: this.paramDialog.data.status,
                    dataType: this.paramDialog.data.dataType,
                    businessMappings: this.paramDialog.data.businessMappings
                };

                let res;
                if (this.paramDialog.isEdit) {
                    res = await this.fetchApi(`/api/rule-params/${this.paramDialog.editId}`, {
                        method: 'PUT',
                        body: payload
                    });
                } else {
                    res = await this.fetchApi('/api/rule-params', {
                        method: 'POST',
                        body: payload
                    });
                }

                if (res.code === 200) {
                    this.showMessage(this.paramDialog.isEdit ? '更新成功' : '创建成功', 'success');
                    this.paramDialog.show = false;
                    this.loadTableData();
                } else {
                    this.showMessage(res.message || '操作失败', 'error');
                }
            } catch (error) {
                this.showMessage('操作失败', 'error');
            }
        }
    }
};

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