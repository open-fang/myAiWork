---
name: qa-testing
description: 测试策略制定、测试用例编写、自动化测试。适用于功能测试、集成测试、回归测试。
---

# 测试规范

## 目标

对 Web 页面执行自动化测试，包括功能验证、UI 渲染检查及跨浏览器兼容性。

## 核心原则（重要）

### 必须执行测试

测试工程师**不能只写测试用例**，必须：

1. **实际运行测试**：在真实环境中执行所有测试
2. **验证测试通过**：确保所有测试用例通过
3. **生成测试报告**：输出 Markdown 格式的测试报告
4. **记录问题并反馈**：测试失败时，记录问题到 `docs/test-issues.md`，反馈给开发工程师修复

### 测试环境要求

- **禁止使用 Mock 数据库**：必须连接真实的 Docker 数据库
- **前后端必须运行**：测试前确保前后端服务已启动
- **浏览器必须可用**：E2E 测试需要真实浏览器环境

### 测试数据要求（重要）

**严禁使用最简单的单条数据测试**，必须：

1. **多条数据测试**：
   - 表单列表至少添加 3-5 条数据
   - 验证批量操作、分页功能
   - 测试数据间的关联和约束

2. **多语言组合测试**：
   - 中英文同时填写
   - 只填中文/只填英文
   - 空值边界测试

3. **边界条件测试**：
   - 最大长度输入
   - 特殊字符输入
   - 空数据/异常数据处理

4. **分页功能测试**：
   - 准备超过分页阈值的数据量（如 15 条以上）
   - 测试翻页、每页条数切换

---

### 问题处理流程（重要）

当测试发现问题时：

1. **记录问题**：在 `docs/test-issues.md` 中记录问题详情
2. **不要修改测试代码来"通过"测试**：测试失败说明代码有问题
3. **通知开发工程师**：让开发分析根因并修复
4. **验证修复**：开发修复后重新运行测试确认通过
5. **更新问题状态**：在问题记录中标记状态为"已修复"

```markdown
# 问题记录格式
## 问题X: [问题标题]
- **发现时间**: YYYY-MM-DD
- **问题描述**: 详细描述
- **复现步骤**: 1. xxx 2. xxx
- **预期结果**: xxx
- **实际结果**: xxx
- **根因分析**: （待开发答复）
- **解决措施**: （待开发答复）
- **状态**: 待修复/已修复
```

---

## 测试类型要求

本项目**必须**包含以下三种测试类型：

### 1. 单元测试
- 覆盖核心业务逻辑
- 测试每个方法/函数的正确性
- 确保边界条件和异常情况被覆盖

### 2. API 测试
- 覆盖所有 REST API 端点
- 验证请求参数、响应格式、状态码
- 测试正常场景和异常场景

### 3. E2E 自动化测试（重点）

#### 框架选择
- **优先使用 Playwright**：若项目未配置则自动初始化
- 备选：Cypress（仅当项目已有 Cypress 配置时）

#### 元素定位规范
**严禁使用脆弱的定位方式**：

```javascript
// ❌ 错误：使用脆弱的 XPath 或 CSS 类名
await page.locator('.el-table')
await page.locator('//div[@class="form-item"]')

// ✅ 正确：使用语义化定位器
await page.getByRole('button', { name: '查询' })
await page.getByText('授权书列表')
await page.getByTestId('auth-name-input')
```

#### 必须测试的场景

| 场景类型 | 测试内容 | 示例 |
|----------|----------|------|
| 页面加载 | 标题、关键元素可见 | 页面标题显示正确 |
| 导航跳转 | 列表→详情→返回 | 点击新建跳转到详情页 |
| 表单交互 | 输入、下拉选择、提交 | 搜索功能、新建保存 |
| UI 组件 | 下拉展开/收起、树形折叠 | CustomSelect 点击外部关闭 |
| 异常处理 | 网络错误、表单校验 | 必填项为空时提示错误 |
| 响应式布局 | 移动端/PC端适配 | 窗口缩放后布局正确 |

---

## E2E 页面测试流程

### 1. 环境准备

```bash
# 检查 Playwright 是否安装
if [ ! -f "playwright.config.ts" ]; then
  npm init playwright@latest
  npx playwright install
fi

# 确保后端服务运行
curl -s http://localhost:8080/api/v1/health || echo "后端未启动"

# 确保前端服务运行
curl -s http://localhost:8080 || echo "前端未启动"
```

### 2. 测试用例生成

分析页面 DOM 结构，生成包含以下场景的测试脚本：

```javascript
// tests/auth-letter.spec.ts
import { test, expect } from '@playwright/test';

test.describe('授权书列表页', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/#/AuthLetterList');
  });

  // 页面加载测试
  test('应正确显示页面标题', async ({ page }) => {
    await expect(page.getByText('授权书列表')).toBeVisible();
  });

  // 表单交互测试
  test('应能搜索授权书', async ({ page }) => {
    await page.getByPlaceholder('请输入授权书名称').fill('测试');
    await page.getByRole('button', { name: '查询' }).click();
    // 等待表格加载
    await expect(page.locator('table')).toBeVisible();
  });

  // 导航测试
  test('应能跳转到新建页面', async ({ page }) => {
    await page.getByRole('button', { name: '新建' }).click();
    await expect(page).toHaveURL(/#\/AuthLetterDetail/);
    await expect(page.getByText('新建授权书')).toBeVisible();
  });
});

test.describe('自定义下拉组件', () => {
  test('点击外部应关闭下拉面板', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    // 打开下拉面板
    await page.getByText('请选择状态').click();
    const dropdown = page.locator('.select-dropdown');
    await expect(dropdown).toBeVisible();

    // 点击页面其他区域
    await page.locator('body').click({ position: { x: 10, y: 10 } });

    // 验证面板已关闭
    await expect(dropdown).not.toBeVisible();
  });

  test('多选下拉应正确显示已选项', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    // 选择多个选项
    await page.getByText('请选择适用区域').click();
    await page.getByRole('checkbox').first().check();
    await page.getByRole('checkbox').nth(1).check();

    // 验证已选项显示
    await expect(page.locator('.select-tag')).toHaveCount(2);
  });
});

test.describe('树形选择组件', () => {
  test('应能展开和折叠节点', async ({ page }) => {
    await page.goto('/#/AuthLetterDetail?mode=create');

    // 打开树形选择器
    await page.getByText('请选择授权对象').click();

    // 点击折叠按钮展开节点
    const toggleBtn = page.locator('.tree-toggle').first();
    await toggleBtn.click();

    // 验证子节点可见
    await expect(page.locator('.tree-node').nth(1)).toBeVisible();

    // 再次点击折叠
    await toggleBtn.click();
  });

  test('选择节点不应触发折叠', async ({ page }) => {
    await page.goto('/#/AuthLetterDetail?mode=create');

    await page.getByText('请选择授权对象').click();

    // 展开节点
    await page.locator('.tree-toggle').first().click();
    const childCheckbox = page.locator('.tree-node input[type="checkbox"]').nth(1);

    // 选中子节点
    await childCheckbox.check();

    // 验证节点仍然展开（不会因为选中而折叠）
    await expect(page.locator('.tree-node').nth(1)).toBeVisible();
  });
});

test.describe('年份选择组件', () => {
  test('应显示以当前年为中心的年份范围', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    await page.getByText('请选择年份').click();

    const currentYear = new Date().getFullYear();

    // 验证显示前后各5年
    await expect(page.getByText(String(currentYear - 5))).toBeVisible();
    await expect(page.getByText(String(currentYear))).toBeVisible();
    await expect(page.getByText(String(currentYear + 5))).toBeVisible();
  });

  test('应能选择年份', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    const currentYear = new Date().getFullYear();

    await page.getByText('请选择年份').click();
    await page.getByText(String(currentYear)).click();

    // 验证已选年份显示
    await expect(page.getByText(String(currentYear))).toBeVisible();
  });
});
```

### 3. 执行与调试

```bash
# 运行所有测试
npx playwright test

# 运行特定测试文件
npx playwright test auth-letter.spec.ts

# 以调试模式运行
npx playwright test --debug

# 生成 HTML 报告
npx playwright test --reporter=html
```

#### 测试失败处理

当测试失败时：

1. **自动截图**：Playwright 自动保存失败截图
2. **查看 trace**：`npx playwright show-trace trace.zip`
3. **分析错误类型**：
   - `TimeoutError`：增加等待时间或使用 `waitFor`
   - `LocatorNotFoundError`：检查选择器是否正确
   - `AssertionError`：检查预期值是否正确

### 4. 测试报告生成

测试执行后，**必须**生成 Markdown 报告：

```markdown
# 测试报告

## 概述
- 执行时间：2026-03-26 14:30:00
- 总用例数：25
- 通过：23
- 失败：2
- 通过率：92%

## 测试环境
- 前端地址：http://localhost:8080
- 后端地址：http://localhost:8080/api/v1
- 数据库：PostgreSQL (Docker)
- 浏览器：Chromium 120.0

## 通过的测试
| 用例名称 | 耗时 |
|----------|------|
| 应正确显示页面标题 | 1.2s |
| 应能搜索授权书 | 2.5s |
| 应能跳转到新建页面 | 1.8s |

## 失败的测试

### 1. 树形选择组件 - 选择节点不应触发折叠
**错误类型**：AssertionError
**错误信息**：Expected element to be visible, but it was hidden
**截图**：./test-results/tree-select-failure.png
**修复建议**：
- 检查 handleExpand 和 handleCheck 方法是否分离
- 确认复选框使用 @change 而非 @click

### 2. 自定义下拉组件 - 点击外部应关闭下拉面板
**错误类型**：TimeoutError
**错误信息**：Element not found within 5000ms
**截图**：./test-results/custom-select-failure.png
**修复建议**：
- 检查 handleClickOutside 方法是否正确实现
- 确认 document.addEventListener 在 mounted 中注册

## 覆盖率统计
| 页面 | 测试用例数 | 覆盖场景 |
|------|-----------|----------|
| AuthLetterList | 8 | 列表查询、分页、新建跳转 |
| AuthLetterDetail | 10 | 表单填写、保存、模式切换 |
| RuleParamConfig | 7 | 参数配置、批量操作 |

## 修复建议汇总
1. [高优先级] 树形选择组件：分离折叠和选择事件
2. [高优先级] 下拉组件：实现点击外部关闭功能
3. [中优先级] 表单按钮：调整与表单字段对齐

---
报告生成时间：2026-03-26 14:35:00
```

---

## 前端组件测试清单

每个前端组件**必须**通过以下测试：

### CustomSelect 组件
- [ ] 点击触发器打开下拉面板
- [ ] 点击外部关闭下拉面板
- [ ] 单选模式：选择后自动关闭
- [ ] 多选模式：选择后保持打开
- [ ] 已选项以标签形式显示
- [ ] 标签数量超过2个显示 "+N"

### TreeSelect 组件
- [ ] 点击折叠按钮展开/折叠节点
- [ ] 选中节点不会触发折叠
- [ ] 复选框使用 @change 事件
- [ ] 点击外部关闭下拉面板
- [ ] 已选项以标签形式显示

### YearSelect 组件
- [ ] 显示以当前年为中心的前后5年
- [ ] 网格布局4列显示
- [ ] 点击年份选中并高亮
- [ ] 点击外部关闭下拉面板

### 表单按钮
- [ ] 查询/重置按钮与表单字段对齐
- [ ] 按钮点击响应正常

### 操作按钮
- [ ] 使用文字按钮而非 emoji
- [ ] 悬停效果正常
- [ ] 点击跳转正确

### 路由导航
- [ ] 新建按钮跳转到 `#/Detail?mode=create`
- [ ] 编辑按钮跳转到 `#/Detail?id=xxx&mode=edit`
- [ ] 查看按钮跳转到 `#/Detail?id=xxx`

---

## 单元测试规范

### 测试目录
- 单元测试在同一个子目录（或按项目惯例的测试目录）中编写测试代码

### 测试框架选择
- 使用项目已有的测试框架（如 pytest、JUnit 等）
- 如果项目没有测试框架，默认使用：
  - Python 项目：pytest
  - Java 项目：JUnit 5

### 运行测试
- 运行测试（例如 `pytest` 或 `mvn test`），确保所有测试通过
- 如果测试失败，**返回给开发工程师**修复代码，直到测试全部通过

### 测试覆盖率要求
- 测试用例要尽量确保每一行代码都能被测试到
- 前端每一个组件都要验证通过
- 如果测试覆盖不到，**返回给开发工程师**整改使代码可被测试
- 如果有不通过，**返回给开发工程师**修复代码，直到测试全部通过

## 测试必须遵守的规则

### 数据库测试
- **禁止使用 H2 模拟数据库**
- **必须使用 Docker 拉起真实的数据库来测试**

## 后端 DAO/Mapper 层测试规范（重要）

### 测试范围
后端测试**必须**覆盖以下层次：

| 层次 | 测试内容 | 要求 |
|------|----------|------|
| **Mapper/DAO 层** | SQL 语句正确性 | **必须测试** |
| **Service 层** | 业务逻辑 | **必须测试** |
| **Controller 层** | API 接口 | **必须测试** |

### Mapper 层测试重点

#### 1. 多表 JOIN 测试（必须）
```java
@Test
void testSelectByTypeCode_WithJoin() {
    // 测试涉及 JOIN 的查询
    // 确保 SQL 中字段都有表别名，避免歧义
    List<LookupValue> result = lookupValueDao.selectByTypeCode("AUTH_OBJECT_LEVEL");
    assertNotNull(result);
}
```

#### 2. 列名歧义检查（必须）
- 所有 JOIN 查询必须验证字段无歧义
- SQL 中字段必须使用表别名（如 `lv.id` 而非 `id`）
- 测试必须实际执行 SQL，不能只验证语法

#### 3. 测试文件示例

```java
// LookupValueDaoTest.java
@SpringBootTest
@Transactional
class LookupValueDaoTest {

    @Autowired
    private LookupValueDao lookupValueDao;

    @Test
    void testSelectByTypeCode_ShouldReturnData() {
        // Given: 准备测试数据
        String typeCode = "AUTH_OBJECT_LEVEL";

        // When: 执行查询
        List<LookupValue> result = lookupValueDao.selectByTypeCode(typeCode);

        // Then: 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testSelectByTypeCode_WithJoin_NoColumnAmbiguity() {
        // 测试 JOIN 查询不产生列名歧义
        // 这个测试会暴露类似 "column reference 'created_by' is ambiguous" 的问题
        assertDoesNotThrow(() -> {
            List<LookupValue> result = lookupValueDao.selectByTypeCode("AUTH_OBJECT_LEVEL");
        });
    }

    @Test
    void testSelectByParentCode_ShouldReturnChildren() {
        List<LookupValue> result = lookupValueDao.selectByParentCode(
            "APPLICABLE_REGION", "ORG_REGION"
        );
        assertNotNull(result);
    }
}
```

### MyBatis XML 检查清单

在编写 Mapper 测试前，检查以下项：

- [ ] JOIN 查询中所有字段是否都有表别名
- [ ] `created_by`、`created_time`、`updated_by`、`updated_time`、`delete_flag` 这些审计字段是否加别名
- [ ] ORDER BY 子句中的字段是否加别名
- [ ] WHERE 子句中的字段是否加别名

### 常见 SQL 歧义场景

| 场景 | 问题 | 解决方案 |
|------|------|----------|
| 两表都有 `created_by` | 列名歧义 | 使用 `lv.created_by` |
| 两表都有 `delete_flag` | 列名歧义 | 使用 `lv.delete_flag` |
| ORDER BY 无别名 | 可能歧义 | 使用 `lv.sort_order` |

### 测试运行命令

```bash
# 运行所有 Mapper 测试
mvn test -Dtest=*DaoTest

# 运行单个测试类
mvn test -Dtest=LookupValueDaoTest

# 运行特定方法
mvn test -Dtest=LookupValueDaoTest#testSelectByTypeCode_ShouldReturnData
```

---

## 前端单元测试（Jest）

### 测试框架配置（Vue 2 项目）

**安装依赖：**
```bash
npm install --save-dev jest@27 vue-jest@3 @vue/test-utils@1 babel-jest@27 @babel/preset-env
```

**jest.config.js 配置：**
```javascript
module.exports = {
  moduleFileExtensions: ['js', 'json', 'vue'],
  transform: {
    '^.+\\.vue$': 'vue-jest',
    '^.+\\.js$': 'babel-jest'
  },
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1'
  },
  testMatch: ['**/__tests__/**/*.spec.js', '**/*.spec.js'],
  collectCoverageFrom: [
    'src/**/*.{js,vue}',
    '!src/main.js',
    '!**/node_modules/**'
  ],
  coverageDirectory: 'coverage',
  coverageReporters: ['lcov', 'text-summary']
}
```

### 单元测试编写规范

#### 测试内容要求
每个 Vue 组件测试**必须**覆盖：
- **Props 验证**：测试必填/可选 props，默认值，类型验证
- **事件触发**：测试用户交互事件（click, input 等）
- **方法测试**：测试组件 methods 中的每个方法
- **计算属性**：测试 computed 属性的计算逻辑
- **边界条件**：空数据、异常数据的处理

### 测试覆盖率要求

| 类型 | 最低覆盖率 |
|------|-----------|
| 语句覆盖率 | 80% |
| 分支覆盖率 | 70% |
| 函数覆盖率 | 80% |
| 行覆盖率 | 80% |

---

## 测试执行检查清单

在提交代码前，测试工程师**必须**完成以下检查：

### 环境检查
- [ ] Docker 数据库容器已启动
- [ ] 后端服务已启动并可访问
- [ ] 前端服务已启动并可访问
- [ ] Playwright 已安装并可用

### 测试执行
- [ ] 单元测试全部通过
- [ ] API 测试全部通过
- [ ] E2E 测试全部通过
- [ ] 生成了测试报告

### 报告输出
- [ ] Markdown 测试报告已生成
- [ ] 失败截图已保存
- [ ] 修复建议已编写
- [ ] 报告已反馈给开发工程师

---

## 交付物
- 测试计划文档
- 测试用例清单
- 测试覆盖率报告
- **Markdown 测试报告**（包含通过率、失败截图、修复建议）