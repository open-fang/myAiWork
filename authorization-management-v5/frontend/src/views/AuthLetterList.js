// 授权书列表页组件
const AuthLetterList = {
    template: `
    <div class="page-container">
        <!-- 查询条件区 -->
        <div class="card">
            <div class="query-form">
                <div class="form-row">
                    <div class="form-item">
                        <label class="form-label">授权书名称</label>
                        <input type="text" v-model="queryParams.name" class="form-input" placeholder="请输入授权书名称" />
                    </div>
                    <div class="form-item">
                        <label class="form-label">授权对象层级</label>
                        <div class="multi-select-wrapper" v-click-outside="closeDropdown">
                            <div class="multi-select-trigger" @click="toggleDropdown('authTargetLevel')">
                                <span class="selected-tags" v-if="queryParams.authTargetLevel.length > 0">
                                    <span class="tag" v-for="(item, index) in getSelectedLabels(queryParams.authTargetLevel, authTargetLevelOptions)" :key="index">
                                        {{ item }}
                                        <span class="tag-close" @click.stop="removeSelected('authTargetLevel', queryParams.authTargetLevel[index])">×</span>
                                    </span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="multi-select-dropdown" v-show="activeDropdown === 'authTargetLevel'">
                                <div class="select-option" v-for="item in authTargetLevelOptions" :key="item.code" @click="toggleMultiSelect('authTargetLevel', item.code)">
                                    <span class="tree-checkbox" :class="{ checked: queryParams.authTargetLevel.includes(item.code) }"></span>
                                    <span>{{ item.name }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label">适用区域</label>
                        <div class="tree-select-wrapper" v-click-outside="closeDropdown">
                            <div class="tree-select-trigger" @click="toggleDropdown('applicableRegion')">
                                <span class="selected-tags" v-if="queryParams.applicableRegion.length > 0">
                                    <span class="tag" v-for="(item, index) in queryParams.applicableRegion.slice(0, 2)" :key="index">
                                        {{ getTreeName(item, applicableRegionTree) }}
                                        <span class="tag-close" @click.stop="removeSelected('applicableRegion', item)">×</span>
                                    </span>
                                    <span class="tag" v-if="queryParams.applicableRegion.length > 2">+{{ queryParams.applicableRegion.length - 2 }}</span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="tree-select-dropdown" v-show="activeDropdown === 'applicableRegion'">
                                <tree-node
                                    v-for="node in applicableRegionTree"
                                    :key="node.code"
                                    :node="node"
                                    :selected-codes="queryParams.applicableRegion"
                                    @toggle="toggleTreeNode('applicableRegion', $event)"
                                ></tree-node>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-item">
                        <label class="form-label">授权发布层级</label>
                        <div class="multi-select-wrapper" v-click-outside="closeDropdown">
                            <div class="multi-select-trigger" @click="toggleDropdown('authPublishLevel')">
                                <span class="selected-tags" v-if="queryParams.authPublishLevel.length > 0">
                                    <span class="tag" v-for="(item, index) in getSelectedLabels(queryParams.authPublishLevel, authPublishLevelOptions)" :key="index">
                                        {{ item }}
                                        <span class="tag-close" @click.stop="removeSelected('authPublishLevel', queryParams.authPublishLevel[index])">×</span>
                                    </span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="multi-select-dropdown" v-show="activeDropdown === 'authPublishLevel'">
                                <div class="select-option" v-for="item in authPublishLevelOptions" :key="item.code" @click="toggleMultiSelect('authPublishLevel', item.code)">
                                    <span class="tree-checkbox" :class="{ checked: queryParams.authPublishLevel.includes(item.code) }"></span>
                                    <span>{{ item.name }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label">授权发布组织</label>
                        <div class="tree-select-wrapper" v-click-outside="closeDropdown">
                            <div class="tree-select-trigger" @click="toggleDropdown('authPublishOrg')">
                                <span class="selected-tags" v-if="queryParams.authPublishOrg.length > 0">
                                    <span class="tag" v-for="(item, index) in queryParams.authPublishOrg.slice(0, 2)" :key="index">
                                        {{ getTreeName(item, orgTree) }}
                                        <span class="tag-close" @click.stop="removeSelected('authPublishOrg', item)">×</span>
                                    </span>
                                    <span class="tag" v-if="queryParams.authPublishOrg.length > 2">+{{ queryParams.authPublishOrg.length - 2 }}</span>
                                </span>
                                <span class="placeholder" v-else>请选择</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="tree-select-dropdown" v-show="activeDropdown === 'authPublishOrg'">
                                <tree-node
                                    v-for="node in orgTree"
                                    :key="node.code"
                                    :node="node"
                                    :selected-codes="queryParams.authPublishOrg"
                                    @toggle="toggleTreeNode('authPublishOrg', $event)"
                                ></tree-node>
                            </div>
                        </div>
                    </div>
                    <div class="form-item">
                        <label class="form-label">授权书发布年份</label>
                        <div class="year-select-wrapper" v-click-outside="closeDropdown">
                            <div class="year-select-trigger" @click="toggleDropdown('publishYear')">
                                <span>{{ queryParams.publishYear || '请选择年份' }}</span>
                                <span class="arrow">▼</span>
                            </div>
                            <div class="year-select-dropdown" v-show="activeDropdown === 'publishYear'">
                                <div class="year-option" @click="selectYear(null)">全部</div>
                                <div class="year-option" v-for="year in yearOptions" :key="year" @click="selectYear(year)">{{ year }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="form-row">
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
                </div>
            </div>
            <div class="query-buttons">
                <button class="btn btn-primary" @click="handleQuery">查询</button>
                <button class="btn btn-default" @click="handleReset">重置</button>
            </div>
        </div>

        <!-- 功能按钮区 -->
        <div class="action-buttons">
            <button class="btn btn-primary" @click="handleCreate">新建授权书</button>
            <button class="btn btn-default" @click="handleUpdate">更新</button>
            <button class="btn btn-success" @click="handleActivate">生效</button>
            <button class="btn btn-warning" @click="handleDeactivate">失效</button>
            <button class="btn btn-danger" @click="handleDelete">删除</button>
        </div>

        <!-- 数据表格区 -->
        <div class="card">
            <table class="data-table">
                <thead>
                    <tr>
                        <th class="col-checkbox"><input type="checkbox" v-model="selectAll" @change="handleSelectAll" /></th>
                        <th class="col-index">序号</th>
                        <th class="col-action">操作</th>
                        <th>授权书名称</th>
                        <th style="width:80px;text-align:center">状态</th>
                        <th>授权对象层级</th>
                        <th>适用区域</th>
                        <th>授权发布层级</th>
                        <th>授权发布组织</th>
                        <th style="width:100px;text-align:center">授权书发布年份</th>
                        <th style="width:100px;text-align:center">创建人</th>
                        <th style="width:160px;text-align:center">创建时间</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="loading">
                        <td colspan="12" class="empty-data">加载中...</td>
                    </tr>
                    <tr v-else-if="tableData.length === 0">
                        <td colspan="12" class="empty-data">暂无数据</td>
                    </tr>
                    <tr v-else v-for="(row, index) in tableData" :key="row.id">
                        <td class="col-checkbox"><input type="checkbox" v-model="selectedRows" :value="row.id" /></td>
                        <td class="col-index">{{ (pagination.pageNum - 1) * pagination.pageSize + index + 1 }}</td>
                        <td class="col-action">
                            <span v-if="row.status === 'DRAFT'" class="icon-btn" @click="goToDetail(row.id)" title="编辑">✏️</span>
                        </td>
                        <td><a class="link" @click="goToDetail(row.id)">{{ row.name }}</a></td>
                        <td style="text-align:center"><span class="status-tag" :class="'status-' + row.status.toLowerCase()">{{ row.statusText }}</span></td>
                        <td>{{ row.authTargetLevelText || '-' }}</td>
                        <td>{{ row.applicableRegionText || '-' }}</td>
                        <td>{{ row.authPublishLevelText || '-' }}</td>
                        <td>{{ row.authPublishOrgText || '-' }}</td>
                        <td style="text-align:center">{{ row.publishYear || '-' }}</td>
                        <td style="text-align:center">{{ row.createdBy || '-' }}</td>
                        <td style="text-align:center">{{ row.createdAt || '-' }}</td>
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
                authTargetLevel: [],
                applicableRegion: [],
                authPublishLevel: [],
                authPublishOrg: [],
                publishYear: null,
                status: ''
            },
            pagination: { pageNum: 1, pageSize: 10, total: 0 },
            authTargetLevelOptions: [],
            authPublishLevelOptions: [],
            applicableRegionTree: [],
            orgTree: [],
            statusOptions: [
                { value: 'DRAFT', label: '草稿' },
                { value: 'PUBLISHED', label: '已发布' },
                { value: 'EXPIRED', label: '已失效' }
            ],
            tableData: []
        };
    },

    computed: {
        totalPages() {
            return Math.ceil(this.pagination.total / this.pagination.pageSize) || 1;
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
        this.loadLookupData();
        this.loadTableData();
    },

    methods: {
        async loadLookupData() {
            try {
                const [targetLevel, publishLevel, regionTree, orgTree] = await Promise.all([
                    this.fetchApi('/api/lookup/AUTH_TARGET_LEVEL'),
                    this.fetchApi('/api/lookup/AUTH_PUBLISH_LEVEL'),
                    this.fetchApi('/api/lookup/tree/APPLICABLE_REGION'),
                    this.fetchApi('/api/lookup/tree/ORG_TREE')
                ]);
                this.authTargetLevelOptions = targetLevel.data || [];
                this.authPublishLevelOptions = publishLevel.data || [];
                this.applicableRegionTree = regionTree.data || [];
                this.orgTree = orgTree.data || [];
            } catch (error) {
                console.error('加载Lookup数据失败:', error);
            }
        },

        async loadTableData() {
            this.loading = true;
            try {
                const params = new URLSearchParams();
                params.append('pageNum', this.pagination.pageNum);
                params.append('pageSize', this.pagination.pageSize);
                if (this.queryParams.name) params.append('name', this.queryParams.name);
                if (this.queryParams.publishYear) params.append('publishYear', this.queryParams.publishYear);
                if (this.queryParams.status) params.append('status', this.queryParams.status);
                this.queryParams.authTargetLevel.forEach(v => params.append('authTargetLevel', v));
                this.queryParams.applicableRegion.forEach(v => params.append('applicableRegion', v));
                this.queryParams.authPublishLevel.forEach(v => params.append('authPublishLevel', v));
                this.queryParams.authPublishOrg.forEach(v => params.append('authPublishOrg', v));

                const res = await this.fetchApi(`/api/auth-letters?${params.toString()}`);
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
            const response = await fetch(url, config);
            return response.json();
        },

        toggleDropdown(name) {
            this.activeDropdown = this.activeDropdown === name ? '' : name;
        },

        closeDropdown() {
            this.activeDropdown = '';
        },

        toggleMultiSelect(field, value) {
            const index = this.queryParams[field].indexOf(value);
            if (index > -1) {
                this.queryParams[field].splice(index, 1);
            } else {
                this.queryParams[field].push(value);
            }
        },

        removeSelected(field, value) {
            const index = this.queryParams[field].indexOf(value);
            if (index > -1) {
                this.queryParams[field].splice(index, 1);
            }
        },

        selectYear(year) {
            this.queryParams.publishYear = year;
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

        getSelectedLabels(codes, options) {
            return codes.map(code => {
                const item = options.find(o => o.code === code);
                return item ? item.name : code;
            });
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

        toggleTreeNode(field, node) {
            const toggleNodeAndChildren = (n, shouldCheck) => {
                const index = this.queryParams[field].indexOf(n.code);
                if (shouldCheck && index === -1) {
                    this.queryParams[field].push(n.code);
                } else if (!shouldCheck && index > -1) {
                    this.queryParams[field].splice(index, 1);
                }
                if (n.children) {
                    n.children.forEach(child => toggleNodeAndChildren(child, shouldCheck));
                }
            };
            toggleNodeAndChildren(node, !this.queryParams[field].includes(node.code));
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
            setTimeout(() => {
                this.message.show = false;
            }, 3000);
        },

        showConfirm(text) {
            return new Promise((resolve) => {
                this.confirmDialog = {
                    show: true,
                    text,
                    onConfirm: () => {
                        this.confirmDialog.show = false;
                        resolve(true);
                    },
                    onCancel: () => {
                        this.confirmDialog.show = false;
                        resolve(false);
                    }
                };
            });
        },

        handleQuery() {
            this.pagination.pageNum = 1;
            this.loadTableData();
        },

        handleReset() {
            this.queryParams = {
                name: '',
                authTargetLevel: [],
                applicableRegion: [],
                authPublishLevel: [],
                authPublishOrg: [],
                publishYear: null,
                status: ''
            };
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
            window.location.href = '#/AuthLetterDetail';
        },

        handleUpdate() {
            if (this.checkSelection()) {
                this.showMessage('更新功能待实现', 'info');
            }
        },

        async handleActivate() {
            if (!this.checkSelection()) return;
            const confirmed = await this.showConfirm(`确定要将选中的 ${this.selectedRows.length} 条数据发布生效吗？`);
            if (confirmed) {
                try {
                    const res = await this.fetchApi('/api/auth-letters/batch/publish', {
                        method: 'PUT',
                        body: JSON.stringify({ ids: this.selectedRows })
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
                    const res = await this.fetchApi('/api/auth-letters/batch/expire', {
                        method: 'PUT',
                        body: JSON.stringify({ ids: this.selectedRows })
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

        async handleDelete() {
            if (!this.checkSelection()) return;
            const confirmed = await this.showConfirm(`确定要删除选中的 ${this.selectedRows.length} 条数据吗？`);
            if (confirmed) {
                try {
                    const res = await this.fetchApi('/api/auth-letters/batch', {
                        method: 'DELETE',
                        body: JSON.stringify({ ids: this.selectedRows })
                    });
                    if (res.code === 200) {
                        this.showMessage('删除成功', 'success');
                        this.selectedRows = [];
                        this.selectAll = false;
                        this.loadTableData();
                    } else {
                        this.showMessage(res.message || '删除失败', 'error');
                    }
                } catch (error) {
                    this.showMessage('删除失败', 'error');
                }
            }
        },

        goToDetail(id) {
            window.location.href = `#/AuthLetterDetail?id=${id}`;
        }
    }
};

// 树节点组件
const TreeNode = {
    name: 'TreeNode',
    props: {
        node: Object,
        selectedCodes: Array
    },
    data() {
        return {
            expanded: false
        };
    },
    computed: {
        hasChildren() {
            return this.node.children && this.node.children.length > 0;
        },
        isChecked() {
            return this.selectedCodes.includes(this.node.code);
        },
        isIndeterminate() {
            if (!this.hasChildren) return false;
            const checkChildren = (children) => {
                let hasChecked = false, hasUnchecked = false;
                for (const child of children) {
                    if (this.selectedCodes.includes(child.code)) hasChecked = true;
                    else hasUnchecked = true;
                    if (child.children) {
                        const result = checkChildren(child.children);
                        hasChecked = hasChecked || result.hasChecked;
                        hasUnchecked = hasUnchecked || result.hasUnchecked;
                    }
                }
                return { hasChecked, hasUnchecked };
            };
            const result = checkChildren(this.node.children);
            return result.hasChecked && result.hasUnchecked;
        }
    },
    methods: {
        toggleCheck() {
            this.$emit('toggle', this.node);
        },
        toggleExpand() {
            this.expanded = !this.expanded;
        }
    },
    template: `
        <div class="tree-node">
            <div class="tree-node-content" :style="{ paddingLeft: (node.level || 0) * 20 + 'px' }">
                <span v-if="hasChildren" class="tree-expand-icon" :class="{ expanded: expanded }" @click="toggleExpand">▶</span>
                <span v-else class="tree-expand-placeholder"></span>
                <span class="tree-checkbox" :class="{ checked: isChecked, indeterminate: isIndeterminate }" @click="toggleCheck"></span>
                <span class="tree-node-label" @click="toggleCheck">{{ node.name }}</span>
            </div>
            <div v-if="hasChildren && expanded" class="tree-children">
                <tree-node v-for="child in node.children" :key="child.code" :node="{ ...child, level: (node.level || 0) + 1 }" :selected-codes="selectedCodes" @toggle="$emit('toggle', $event)" />
            </div>
        </div>
    `
};

// 注册树节点组件
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