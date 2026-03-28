---
name: frontend-dev
description: 前端代码开发、组件设计、状态管理。适用于 Vue2 项目，包含编码规范和最佳实践。
---

# 前端开发规范

## 角色职责边界（重要）

### 前端开发工程师职责

| 允许的操作 | 禁止的操作 |
|------------|------------|
| ✅ 编写/修改前端功能代码 | ❌ 修改后端代码（Java文件） |
| ✅ 编写Vue组件和样式 | ❌ 修改后端API接口 |
| ✅ 编写前端单元测试 | ❌ 修改测试代码（属于QA职责） |
| ✅ 修复自己代码的bug | ❌ 跳过自测直接提交 |
| ✅ 调用和调试后端API | ❌ 修改数据库相关代码 |

### 与其他角色的边界

| 场景 | 负责角色 |
|------|----------|
| 后端API问题 | backend-dev |
| 测试用例编写 | qa-agent |
| UI/UX设计决策 | ba-agent + frontend-dev 协作 |
| API契约定义 | backend-dev + frontend-dev 协作 |
| 性能优化 | 根据瓶颈位置确定 |

---

## 开发者自测要求（重要）

### 代码提交前必须完成的自测

前端开发工程师**必须**在提交代码前完成以下自测，确保代码质量：

### 1. 语法验证

```bash
# 确保无语法错误
# 打开浏览器开发者工具，检查控制台无报错

# 验证Vue模板编译正确
# 页面应正常渲染，无白屏
```

### 2. 页面功能验证

每个页面**必须**在浏览器中实际验证：

| 页面 | 验证项 |
|------|--------|
| 列表页 | 数据加载、分页、搜索、排序 |
| 详情页 | 数据展示、表单提交、状态切换 |
| 配置页 | 配置保存、数据刷新 |

### 3. 组件功能验证

| 组件 | 必须验证的功能 |
|------|----------------|
| CustomSelect | 打开/关闭、单选/多选、点击外部关闭 |
| TreeSelect | 展开/折叠、选中/取消、点击外部关闭 |
| YearSelect | 年份选择、已选高亮、禁用状态 |
| 表单 | 输入验证、必填校验、提交功能 |
| 按钮 | 点击响应、样式正确、无emoji异常 |

### 4. API 接口联调验证

```bash
# 启动前端服务
cd frontend
python3 -m http.server 8080

# 或使用 npm
npm run serve
```

**必须验证：**
- API 调用成功，数据正确显示
- API 调用失败，错误提示正确
- 网络超时，有合理的处理

### 5. 跨浏览器验证

至少在以下浏览器中验证：
- [ ] Chrome（最新版）
- [ ] Firefox（可选）
- [ ] Edge（可选）

### 自测完成清单

- [ ] 页面正常渲染，无白屏
- [ ] 控制台无 JavaScript 错误
- [ ] API 调用正常
- [ ] 表单验证正确
- [ ] 按钮点击响应正常
- [ ] 路由跳转正确
- [ ] 样式显示正常
- [ ] 无 emoji 显示异常

---

## 技术栈
- **Vue 2（原生）**
- **禁止使用** Element UI 和 Vue 3
- 样式、服务请求、页面逻辑必须汇聚在 vue 文件中

## 文件结构规范
- views 目录下一共 **3 个 .vue 文件**
- 样式、服务请求、页面逻辑等 **必须** 汇聚在这 3 个 vue 文件里
- 禁止拆分过多的组件文件

---

## 组件自包含原则（重要）

### 样式内联要求
每个 Vue 文件 **必须包含完整的样式**，不能依赖外部 CSS 文件：

```vue
<template>
  <!-- 模板内容 -->
</template>

<script>
// 脚本内容
</script>

<style>
/* 必须包含组件所需的所有样式 */
/* 包括：重置样式、布局样式、组件样式、交互样式 */
</style>
```

**原因**：用户可能只拷贝单个 Vue 文件到现有项目，必须保证独立运行时样式完整。

---

## 页面布局规范

### 流式布局
- 采用**流式布局**
- 页面最多展示到**一行 3 个字段**
- 页面继续拉伸时，保持 3 个字段不变

### 表单按钮对齐
查询/重置按钮需要与表单字段对齐：

```html
<!-- 正确做法 -->
<div class="form-row form-buttons">
  <button class="btn btn-primary">查询</button>
  <button class="btn">重置</button>
</div>
```

```css
.form-buttons {
  justify-content: flex-start;
  padding-left: 108px; /* 与表单 label 宽度对齐 */
}
```

---

## UI 组件规范

### 1. 下拉选择组件（CustomSelect）

本系统的下拉选择组件**不是**原生 HTML 的 `<select>`，而是**自定义组件**：

#### 基本结构
- **触发器**：显示已选值或 placeholder，右侧带下拉箭头
- **下拉面板**：点击触发器后显示，内含平铺的复选框列表
- **多选支持**：每行一个选项，带 checkbox，可多选
- **选中展示**：已选项以标签形式显示在触发器内
- **禁止使用**原生 `<select multiple>`

#### 必须实现的功能

**1) 点击外部自动关闭**
```javascript
mounted() {
  document.addEventListener('click', this.handleClickOutside)
},
beforeDestroy() {
  document.removeEventListener('click', this.handleClickOutside)
},
methods: {
  handleClickOutside(e) {
    if (!this.$el.contains(e.target)) {
      this.isOpen = false
    }
  }
}
```

**2) 复选框尺寸规范**
```css
.option-checkbox {
  width: 14px !important;
  height: 14px !important;
  margin-right: 8px !important;
  cursor: pointer;
  flex-shrink: 0;
}
```

**3) 选项高度规范**
```css
.select-option {
  padding: 6px 10px;  /* 紧凑间距 */
  font-size: 13px;
}
```

### 2. 树形选择组件（TreeSelect）

#### 必须实现的功能

**1) 折叠/展开与选择事件分离**
```javascript
methods: {
  // 折叠/展开 - 只控制显示隐藏
  handleExpand(code) {
    const index = this.expandedKeys.indexOf(code)
    if (index > -1) this.expandedKeys.splice(index, 1)
    else this.expandedKeys.push(code)
  },
  // 选择 - 只处理选中状态
  handleCheck(code) {
    const newValue = this.value ? [...this.value] : []
    const index = newValue.indexOf(code)
    if (index > -1) newValue.splice(index, 1)
    else newValue.push(code)
    this.$emit('input', newValue)
  }
}
```

**2) 模板事件绑定**
```html
<div class="tree-node-content">
  <!-- 折叠按钮：独立点击事件 -->
  <span class="tree-toggle" @click="handleExpand(node.code)">
    {{ isExpanded(node.code) ? '▼' : '▶' }}
  </span>
  <!-- 复选框：使用 @change 而非 @click -->
  <input type="checkbox"
         :checked="isChecked(node.code)"
         @change="handleCheck(node.code)" />
  <!-- 文字标签：点击也可选择 -->
  <span class="tree-label" @click="handleCheck(node.code)">{{ node.name }}</span>
</div>
```

**3) 折叠按钮样式**
```css
.tree-toggle {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  font-size: 10px;
}

.tree-toggle:hover {
  color: #1890ff;
}
```

### 3. 年份选择组件（YearSelect）

#### 功能要求
- 以**当前年份为中心**，显示前后各 5 年（共 11 年）
- **平铺网格布局**，4 列展示
- 支持禁用状态

#### 实现示例
```javascript
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
  }
}
```

```css
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
}

.year-item.selected {
  background: #1890ff;
  color: #fff;
}
```

### 4. 操作按钮规范

**禁止使用 emoji 作为操作图标**（会出现显示异常）：

```html
<!-- 错误做法 -->
<span class="icon-edit" @click="handleEdit">✏️</span>

<!-- 正确做法 -->
<span class="action-btn action-edit" @click="handleEdit">编辑</span>
```

```css
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
```

---

## 路由跳转规范

### 使用 window.location.hash

在可能被集成到其他项目的场景下，**推荐使用** `window.location.hash` 进行路由跳转：

```javascript
// 正确做法 - 兼容性好
handleCreate() {
  window.location.hash = '#/AuthLetterDetail?mode=create'
},

handleEdit(row) {
  window.location.hash = '#/AuthLetterDetail?id=' + row.id + '&mode=edit'
},

handleView(row) {
  window.location.hash = '#/AuthLetterDetail?id=' + row.id
}

// 错误做法 - 可能被父路由干扰
handleCreate() {
  this.$router.push({ path: '/#/AuthLetterDetail', query: { mode: 'create' } })
}
```

**原因**：`$router.push` 可能被父级路由拦截或拼接，导致路径错误。

---

## 表单编辑模式判断

详情页需要正确判断编辑模式：

```javascript
created() {
  this.id = this.$route.query.id
  // 无 id 参数时，默认为新建模式
  this.isNew = this.$route.query.mode === 'create' || !this.$route.query.id
  this.isEdit = this.$route.query.mode === 'edit'
},

computed: {
  isEditable() {
    return this.isNew || (this.isEdit && this.formData.status === 'DRAFT')
  }
}
```

---

## 前后端数据交互规范

### 1. 开发前必须确认数据格式
- 查看后端 API 实现（Service/Controller 代码）
- 确认返回的 JSON 结构（扁平列表 vs 树形结构）
- 确认字段命名约定（value/code, label/name）

### 2. 树形数据处理原则
- 如果后端已返回树形结构：只转换字段名，保留 children
- 如果后端返回扁平列表：前端使用 buildTree 构建
- 查找方法必须支持递归，不能用 Array.find()

### 3. 关联文件排查
- 搜索项目中是否有相同模式/结构的代码
- 检查其他页面是否有类似的组件/逻辑
- 使用 Grep 搜索关键词定位相关代码

### 4. 一致性检查
- 新增/修改的组件是否与其他页面风格一致
- 数据处理逻辑是否统一（如 transformLookupData）
- API 调用方式是否一致

### 5. 完整测试
- 列表页、详情页、配置页都需要验证
- 所有下拉选择器都需要测试数据加载和选择功能

---

## 验证要求

### 必须实际运行验证
- 前端代码必须**实际运行验证**，不能只检查编译通过
- 每个页面组件需要在浏览器中验证渲染效果

### 验证清单

在提交前端代码前，必须验证以下功能：

| 组件 | 验证项 |
|------|--------|
| CustomSelect | 点击打开/关闭、多选/单选、点击外部关闭 |
| TreeSelect | 树形展开/折叠、选中/取消、点击外部关闭 |
| YearSelect | 年份选择、已选高亮、禁用状态 |
| 表单按钮 | 对齐位置、点击响应 |
| 操作按钮 | 样式正确、无 emoji 异常 |
| 路由跳转 | 路径正确、参数传递正确 |
| 编辑模式 | 新建/编辑/查看模式正确切换 |

---

## 常见问题排查

### 问题：组件样式丢失
**原因**：样式定义在外部文件，未拷贝
**解决**：将所有样式内联到 Vue 文件的 `<style>` 中

### 问题：树形选择器点击节点自动折叠
**原因**：折叠和选择事件未分离
**解决**：使用独立的 `handleExpand` 和 `handleCheck` 方法

### 问题：路由跳转路径错误
**原因**：`$router.push` 被父路由干扰
**解决**：使用 `window.location.hash`

### 问题：emoji 图标显示异常
**原因**：不同环境 emoji 渲染不一致
**解决**：使用文字按钮替代 emoji

### 问题：下拉面板无法关闭
**原因**：未实现点击外部关闭
**解决**：添加 `document.addEventListener('click', this.handleClickOutside)`