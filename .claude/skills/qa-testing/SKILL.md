---
name: qa-testing
description: 测试策略制定、测试用例编写、自动化测试。适用于功能测试、集成测试、回归测试。
---

# 测试规范

## 角色职责边界（重要）

### 测试工程师职责

测试工程师**只负责测试相关工作**，具体包括：

| 允许的操作 | 禁止的操作 |
|------------|------------|
| ✅ 编写测试代码（测试类、测试脚本） | ❌ 修改功能代码（业务代码） |
| ✅ 创建测试配置文件（application-test.yml） | ❌ 修改源代码实体类、服务类、控制器 |
| ✅ **初始化测试框架（Playwright、Jest等）** | ❌ 修复编译错误（属于开发职责） |
| ✅ **编写E2E测试脚本** | ❌ 修改 pom.xml 添加依赖（需通知后端开发） |
| ✅ 运行测试并收集结果 | ❌ 修改数据库迁移脚本 |
| ✅ 记录测试问题到 `docs/test-issues.md` | ❌ 直接修改任何非测试文件 |
| ✅ 编写测试报告 | ❌ 修改前端功能代码 |
| ✅ 提供修复建议 | |

### 问题处理流程（严格遵守）

当测试发现问题时，**必须**按照以下流程处理：

1. **记录问题**：在 `docs/test-issues.md` 中详细记录问题
   - 问题描述
   - 复现步骤
   - 预期结果 vs 实际结果
   - 错误日志/截图
   - 根因分析（如能确定）

2. **通知开发工程师**：通过 SendMessage 通知相关开发角色
   - 后端问题 → 通知 backend-dev
   - 前端问题 → 通知 frontend-dev

3. **等待修复**：**不要自行修改功能代码**

4. **验证修复**：开发工程师修复后，重新运行测试确认通过

5. **更新问题状态**：在问题记录中标记状态为"已修复"

### 编译/运行错误的处理

如果遇到编译错误或运行环境问题：

| 错误类型 | 正确处理方式 |
|----------|--------------|
| 源代码编译失败 | 记录问题 → 通知 backend-dev/frontend-dev |
| 缺少依赖 | 记录问题 → 通知 backend-dev 添加依赖 |
| 数据库连接失败 | 记录问题 → 通知 backend-dev 检查配置 |
| 前端构建失败 | 记录问题 → 通知 frontend-dev |
| 测试代码本身有问题 | **允许自行修改测试代码** |

### 测试工程师可修改的文件

以下文件测试工程师**可以修改**：

- `backend/src/test/**/*.java` - 后端测试代码
- `backend/src/test/resources/**` - 测试配置文件
- `frontend/tests/**` - 前端测试代码
- `docs/test-report.md` - 测试报告
- `docs/test-issues.md` - 问题记录

以下文件测试工程师**禁止修改**：

- `backend/src/main/**/*.java` - 后端功能代码
- `backend/src/main/resources/**` - 后端配置（不含测试配置）
- `backend/pom.xml` - Maven 配置
- `frontend/src/**` - 前端功能代码
- `frontend/package.json` - 前端依赖配置
- 任何数据库迁移脚本

---

## 目标

执行端到端（E2E）自动化测试，验证系统完整功能。

---

## 测试执行三阶段流程（重要）

测试工程师执行测试时，**必须**按照以下三阶段流程操作：

### 阶段一：Setup（环境检查与准备）

```bash
# ============================================
# Phase 1: Setup - 环境检查与准备
# 注意：开发交付环境，停止所有服务后重新启动
# ============================================

echo "=== Phase 1: Setup - 环境检查与准备 ==="

# 1. 停止已存在的服务
echo "停止已存在的服务..."

# 停止后端服务
pkill -f "java.*authorization-management" 2>/dev/null || echo "后端服务未运行"
sleep 1

# 停止前端服务
pkill -f "python.*http.server.*8080" 2>/dev/null || echo "前端服务未运行"
pkill -f "node.*vue-cli-service.*8080" 2>/dev/null || true
sleep 1

# 停止数据库 Docker 容器
echo "停止数据库容器..."
docker stop postgres 2>/dev/null || echo "postgres容器未运行"
docker rm postgres 2>/dev/null || true

echo "已停止所有原有服务"

# 2. 拉取最新代码
echo "拉取最新代码..."
cd $PROJECT_DIR
git pull origin main || echo "Git pull 失败，使用当前代码"

# 3. 启动数据库
echo "启动 PostgreSQL 容器..."
docker run -d --name postgres \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=authorization_management \
    -p 5432:5432 \
    postgres:13
sleep 10
echo "数据库已启动"

# 5. 编译并启动后端服务
echo "编译并启动后端服务..."
cd $PROJECT_DIR/backend
mvn clean compile -q
nohup mvn spring-boot:run > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "后端服务启动中 (PID: $BACKEND_PID)..."

# 等待后端启动
for i in {1..30}; do
    if curl -s http://localhost:8081 > /dev/null 2>&1; then
        echo "后端服务已就绪"
        break
    fi
    sleep 1
done

# 6. 启动前端服务
echo "启动前端服务..."
cd $PROJECT_DIR/frontend
nohup python3 -m http.server 8080 > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!
sleep 2
echo "前端服务已启动 (PID: $FRONTEND_PID)"

# 7. 验证环境就绪
echo "验证环境就绪..."
curl -s http://localhost:8081 > /dev/null && echo "✅ 后端服务: 正常" || echo "❌ 后端服务: 异常"
curl -s http://localhost:8080 > /dev/null && echo "✅ 前端服务: 正常" || echo "❌ 前端服务: 异常"
docker ps --filter "name=postgres" -q > /dev/null && echo "✅ 数据库: 正常" || echo "❌ 数据库: 异常"

echo "=== Setup 完成 ==="
```

### 阶段二：Execute（执行测试）

执行测试前，**必须先确认测试用例已通过评审**。

#### 2.1 测试用例来源

测试用例应从以下文档获取（按优先级）：

| 来源 | 路径 | 说明 |
|------|------|------|
| 测试用例文档 | `docs/test-cases.md` | 评审通过的测试用例清单 |
| 测试报告 | `docs/test-report.md` | 历史测试报告中的用例 |
| API契约文档 | `docs/api-contract.md` | API接口定义 |

#### 2.2 必须执行的测试类型

| 测试类型 | 命令/方式 | 必要性 |
|----------|-----------|--------|
| **后端单元测试** | `mvn test` | 必须 |
| **API接口测试** | curl / Postman | 必须 |
| **E2E自动化测试** | `npx playwright test` | **必须** |

#### 2.3 E2E测试编写与执行（重要）

**测试工程师必须编写E2E测试代码**，不能因为测试框架不存在就跳过。

```bash
echo "=== Phase 2: Execute - 执行测试 ==="

# 1. 读取测试用例文档
if [ -f "docs/test-cases.md" ]; then
    echo "基于 docs/test-cases.md 执行测试"
fi

# 2. E2E测试框架初始化（如果不存在）
echo "检查E2E测试框架..."
cd frontend

if [ ! -f "playwright.config.ts" ]; then
    echo "Playwright未配置，正在初始化..."
    npm init playwright@latest -- --quiet
    npx playwright install chromium
    echo "Playwright已初始化"
fi

# 3. 编写E2E测试代码（根据测试用例文档）
echo "编写E2E测试代码..."
# 测试工程师必须根据 docs/test-cases.md 编写测试脚本
# 测试脚本应放在 frontend/tests/ 或 tests/ 目录

# 4. 执行后端单元测试
cd backend
mvn test 2>&1 | tee test-result.log

# 5. 执行E2E测试
cd frontend
npx playwright test --reporter=list

# 6. 执行API测试（根据测试用例文档）
# 按文档中的API测试用例逐个执行

echo "=== Execute 完成 ==="
```

#### 2.4 测试结果记录

执行过程中必须记录：
- 每个测试用例的执行结果（通过/失败/跳过）
- 失败用例的错误信息和截图
- 测试耗时
- 测试数据量

### 阶段三：Teardown（清理环境）

```bash
# ============================================
# Phase 3: Teardown - 清理环境
# 注意：开发交付环境，测试完成后清理所有服务，不需要还原
# ============================================

echo "=== Phase 3: Teardown - 清理环境 ==="

# 1. 停止所有服务
echo "停止所有服务..."

# 停止后端服务
if pgrep -f "java.*authorization-management" > /dev/null; then
    echo "停止后端服务..."
    pkill -f "java.*authorization-management"
    echo "后端服务已停止"
fi

# 停止前端服务
if pgrep -f "python.*http.server.*8080" > /dev/null; then
    echo "停止前端服务..."
    pkill -f "python.*http.server.*8080"
    echo "前端服务已停止"
fi

# 停止数据库容器
echo "停止数据库容器..."
docker stop postgres 2>/dev/null && docker rm postgres 2>/dev/null && echo "数据库容器已停止并删除" || echo "数据库容器未运行"

# 2. 清理临时文件
echo "清理临时文件..."
rm -f /tmp/backend.log /tmp/frontend.log /tmp/test-result.log

# 3. 验证清理结果
echo "验证清理结果..."
pgrep -f "java.*authorization-management" && echo "⚠️ 后端进程仍在运行" || echo "✅ 后端进程已停止"
pgrep -f "python.*http.server.*8080" && echo "⚠️ 前端进程仍在运行" || echo "✅ 前端进程已停止"
docker ps --filter "name=postgres" -q && echo "⚠️ 数据库容器仍在运行" || echo "✅ 数据库容器已停止"

echo "=== Teardown 完成 ==="
```

### 三阶段执行清单

| 阶段 | 步骤 | 检查项 |
|------|------|--------|
| **Setup** | 1. 停止后端服务 | ✅ pkill java |
| | 2. 停止前端服务 | ✅ pkill python/node |
| | 3. 停止数据库容器 | ✅ docker stop/rm postgres |
| | 4. 拉取最新代码 | ✅ git pull |
| | 5. 启动数据库 | ✅ docker run postgres |
| | 6. 编译并启动后端 | ✅ mvn spring-boot:run |
| | 7. 启动前端 | ✅ python http.server |
| | 8. 验证环境就绪 | ✅ curl 检查端口 |
| **Execute** | 1. 确认测试用例已评审 | ✅ 检查 docs/test-cases.md |
| | 2. **初始化Playwright（如不存在）** | ✅ npm init playwright |
| | 3. **编写E2E测试代码** | ✅ 根据测试用例编写 |
| | 4. 执行后端单元测试 | ✅ mvn test |
| | 5. 执行 E2E 测试 | ✅ playwright test |
| | 6. 执行 API 接口测试 | ✅ 按用例文档执行 |
| | 7. 记录测试结果 | ✅ 通过/失败/截图 |
| **Teardown** | 1. 停止后端服务 | ✅ pkill java |
| | 2. 停止前端服务 | ✅ pkill python |
| | 3. 停止并删除数据库容器 | ✅ docker stop/rm postgres |
| | 4. 清理临时文件 | ✅ rm 日志文件 |
| | 5. 验证清理结果 | ✅ pgrep/docker ps 确认 |

---

## 测试用例管理（重要）

### 测试用例评审流程

测试用例**必须先评审通过**才能执行：

```
1. 编写测试用例 → docs/test-cases.md
2. 提交评审 → 团队评审
3. 评审通过 → 标记状态为"已评审"
4. 执行测试 → 基于"已评审"用例执行
```

### 测试用例文档格式

`docs/test-cases.md` 标准格式：

```markdown
# 测试用例文档

## 文档信息
- 版本：v1.0
- 评审状态：已评审 ✅ / 待评审 ⏳
- 评审人：xxx
- 评审时间：YYYY-MM-DD

## 测试用例清单

### 后端单元测试
| 用例ID | 测试项 | 前置条件 | 测试步骤 | 预期结果 | 优先级 |
|--------|--------|----------|----------|----------|--------|
| UT-001 | 查询授权书列表 | 数据库有数据 | 调用list方法 | 返回分页数据 | P0 |

### API接口测试
| 用例ID | 接口 | 方法 | 请求参数 | 预期响应 | 优先级 |
|--------|------|------|----------|----------|--------|
| API-001 | /api/authLetter/list | POST | {"pageNum":1,"pageSize":10} | {"code":"0",...} | P0 |

### E2E测试
| 用例ID | 测试场景 | 操作步骤 | 预期结果 | 优先级 |
|--------|----------|----------|----------|--------|
| E2E-001 | 新建授权书 | 点击新建→填写→保存 | 跳转详情页 | P0 |
```

### 测试用例优先级

| 优先级 | 说明 | 执行要求 |
|--------|------|----------|
| P0 | 核心功能，阻塞上线 | 必须执行，必须通过 |
| P1 | 重要功能 | 必须执行 |
| P2 | 一般功能 | 建议执行 |
| P3 | 边界场景 | 可选执行 |

### 测试数据准备

测试执行前应准备足够的数据：

| 数据类型 | 最少数量 | 用途 |
|----------|----------|------|
| 授权书 | 15条 | 触发分页（每页10条） |
| 场景 | 15个 | 触发场景表格分页 |
| 问卷题目 | 15个 | 触发题目列表分页 |

---

## 核心原则（重要）

### 必须执行测试

测试工程师**不能只写测试用例**，必须：

1. **部署真实环境**：前端、后端、数据库都必须运行
2. **实际运行测试**：在真实环境中执行所有测试
3. **验证测试通过**：确保所有测试用例通过
4. **生成测试报告**：输出 Markdown 格式的测试报告
5. **记录问题并反馈**：测试失败时，记录问题到 `docs/test-issues.md`
6. **还原测试环境**：测试完成后执行 Teardown 清理

### 测试数据要求（重要）

**严禁使用最简单的单条数据测试**，必须：

1. **多条数据测试**：
   - 表单列表至少添加 15 条数据（触发分页）
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

## 测试报告格式（重要）

### 测试报告必须包含

```markdown
# 测试报告

## 测试环境
- 数据库：PostgreSQL (Docker)
- 后端：Spring Boot (端口)
- 前端：Vue.js (端口)
- 执行时间：YYYY-MM-DD HH:mm

## 测试执行记录

| 编号 | 测试用例 | 执行时间 | 数据量 | 状态 | 备注 |
|------|----------|----------|--------|------|------|
| TC-001 | 新建授权书 | 2.3s | 1条 | ✅ | - |
| TC-002 | 分页查询 | 1.5s | 15条 | ✅ | 翻页正常 |

## 数据量统计
- 创建授权书：15条
- 创建场景：15个
- 创建题目：15个

## 问题记录
（链接到 test-issues.md）
```

### 必须记录的信息

1. **执行时间**：每个测试用例的耗时（秒）
2. **数据量**：测试过程中操作的数据数量
3. **操作类型**：创建/查询/更新/删除

---

## 问题处理流程（重要）

当测试发现问题时：

1. **记录问题**：在 `docs/test-issues.md` 中记录问题详情
2. **不要修改测试代码来"通过"测试**：测试失败说明代码有问题
3. **通知开发工程师**：让开发分析根因并修复
4. **验证修复**：开发修复后重新运行测试确认通过
5. **更新问题状态**：在问题记录中标记状态为"已修复"

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

### 1. 环境准备与框架初始化（重要）

**测试工程师必须执行以下步骤，不能跳过：**

```bash
# 1. 检查前端项目目录
cd frontend

# 2. 检查 Playwright 是否安装（不存在必须初始化）
if [ ! -f "playwright.config.ts" ]; then
    echo "Playwright未配置，正在初始化..."
    npm init playwright@latest -- --quiet
fi

# 3. 安装浏览器
npx playwright install chromium

# 4. 创建测试目录（如果不存在）
mkdir -p tests

# 5. 确保后端服务运行
curl -s http://localhost:8081/actuator/health || echo "后端未启动"

# 6. 确保前端服务运行
curl -s http://localhost:8080 || echo "前端未启动"
```

### 2. 测试代码编写（必须）

**测试工程师必须根据测试用例文档编写测试脚本。**

测试脚本文件位置：
- `frontend/tests/*.spec.ts` 或 `tests/*.spec.ts`

编写流程：
1. 读取 `docs/test-cases.md` 中的E2E测试用例
2. 分析页面 DOM 结构和路由
3. 编写测试脚本覆盖所有测试场景
4. 运行测试验证通过

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

测试工程师**必须**按照三阶段流程完成以下检查：

### Phase 1: Setup 检查清单
- [ ] 停止后端服务
- [ ] 停止前端服务
- [ ] 停止并删除数据库容器
- [ ] 拉取最新代码（git pull）
- [ ] 启动数据库（Docker PostgreSQL）
- [ ] 编译后端代码（mvn clean compile）
- [ ] 启动后端服务（mvn spring-boot:run）
- [ ] 启动前端服务（python http.server / npm serve）
- [ ] 验证后端服务就绪（curl localhost:8081）
- [ ] 验证前端服务就绪（curl localhost:8080）
- [ ] 验证数据库连接正常

### Phase 2: Execute 检查清单
- [ ] 执行后端单元测试（mvn test）
- [ ] **初始化Playwright框架（如不存在）**
- [ ] **编写E2E测试代码**
- [ ] **执行E2E测试（npx playwright test）**
- [ ] 执行 API 接口测试
- [ ] 收集测试结果和退出码
- [ ] 记录失败的测试用例
- [ ] 生成 Markdown 测试报告
- [ ] 保存失败截图（如有）

### Phase 3: Teardown 检查清单
- [ ] 停止后端服务
- [ ] 停止前端服务
- [ ] 停止并删除数据库容器
- [ ] 清理临时日志文件
- [ ] 验证进程已全部停止
- [ ] 验证容器已删除

### 报告输出
- [ ] Markdown 测试报告已生成（docs/test-report.md）
- [ ] 问题记录已更新（docs/test-issues.md）
- [ ] 失败截图已保存
- [ ] 修复建议已编写
- [ ] 报告已反馈给开发工程师

---

## 交付物
- 测试计划文档
- 测试用例清单
- 测试覆盖率报告
- **Markdown 测试报告**（包含通过率、失败截图、修复建议）
- **问题记录文档**（记录发现的 bug 和环境问题）