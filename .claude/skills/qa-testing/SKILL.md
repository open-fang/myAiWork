---
name: qa-testing
description: 测试策略制定、测试用例编写、自动化测试。适用于功能测试、集成测试、回归测试。
---

# 测试规范

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

### 3. 前后端集成测试
- 验证前后端数据交互正确性
- 测试完整业务流程
- 确保接口契约一致性

## 测试用例设计

### 设计原则
- 按**测试设计思路**来设计用例，包括：
  - 等价类划分
  - 边界值分析
  - 错误推测法
  - 场景法
- 尽量保证**场景覆盖**完整

### 场景覆盖要求
- 正常流程（Happy Path）
- 异常流程（错误处理、边界情况）
- 数据边界（空值、最大值、最小值）
- 业务规则验证
- 权限和安全性验证

## 测试用例格式
- 每个用例包含：前置条件、测试步骤、预期结果、实际结果（待填）
- 使用 Gherkin 语法描述场景

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

## 自动化测试要求
- 使用 Playwright 或 Cypress 编写 E2E 测试
- API 测试使用 Postman/Newman 或 pytest

## 前端测试规范

### 测试框架配置（Vue 2 项目）

#### 单元测试：Jest + Vue Test Utils

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

**babel.config.js 配置：**
```javascript
module.exports = {
  presets: [
    ['@babel/preset-env', { targets: { node: 'current' } }]
  ]
}
```

#### E2E 测试：Cypress

**安装依赖：**
```bash
npm install --save-dev cypress
```

**cypress.config.js 配置：**
```javascript
const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:8080',
    specPattern: 'cypress/e2e/**/*.cy.js',
    video: false,
    screenshotOnRunFailure: true
  }
})
```

### 测试文件目录结构

```
frontend/
├── src/
│   ├── views/
│   │   └── __tests__/
│   │       └── AuthLetterList.spec.js
│   └── components/
│       └── __tests__/
├── cypress/
│   ├── e2e/
│   │   └── auth-letter.cy.js
│   └── support/
│       └── commands.js
├── jest.config.js
└── cypress.config.js
```

### package.json 测试脚本

```json
{
  "scripts": {
    "test:unit": "jest",
    "test:unit:coverage": "jest --coverage",
    "test:e2e": "cypress run",
    "test:e2e:open": "cypress open",
    "test": "npm run test:unit && npm run test:e2e"
  }
}
```

### 单元测试编写规范

#### 测试文件命名
- 文件名：`*.spec.js`
- 放在组件同级 `__tests__` 目录下

#### 测试内容要求
每个 Vue 组件测试**必须**覆盖：
- **Props 验证**：测试必填/可选 props，默认值，类型验证
- **事件触发**：测试用户交互事件（click, input 等）
- **方法测试**：测试组件 methods 中的每个方法
- **计算属性**：测试 computed 属性的计算逻辑
- **边界条件**：空数据、异常数据的处理

#### 示例测试文件

```javascript
// AuthLetterList.spec.js
import { shallowMount, createLocalVue } from '@vue/test-utils'
import ElementUI from 'element-ui'
import AuthLetterList from '@/views/AuthLetterList.vue'

const localVue = createLocalVue()
localVue.use(ElementUI)

describe('AuthLetterList.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = shallowMount(AuthLetterList, {
      localVue,
      mocks: {
        $router: { push: jest.fn() },
        $message: { success: jest.fn(), error: jest.fn(), warning: jest.fn() }
      }
    })
  })

  afterEach(() => {
    wrapper.destroy()
  })

  // 测试组件名称
  it('组件名称应为 AuthLetterList', () => {
    expect(wrapper.name()).toBe('AuthLetterList')
  })

  // 测试初始数据
  it('初始数据应正确', () => {
    expect(wrapper.vm.queryParams.name).toBe('')
    expect(wrapper.vm.pageNum).toBe(1)
    expect(wrapper.vm.pageSize).toBe(10)
  })

  // 测试方法：handleReset
  it('handleReset 应重置查询参数', () => {
    wrapper.vm.queryParams.name = 'test'
    wrapper.vm.queryParams.status = 'DRAFT'
    wrapper.vm.handleReset()
    expect(wrapper.vm.queryParams.name).toBe('')
    expect(wrapper.vm.queryParams.status).toBe('')
  })

  // 测试方法：getStatusType
  it('getStatusType 应返回正确的标签类型', () => {
    expect(wrapper.vm.getStatusType('DRAFT')).toBe('info')
    expect(wrapper.vm.getStatusType('PUBLISHED')).toBe('success')
    expect(wrapper.vm.getStatusType('INVALID')).toBe('danger')
  })

  // 测试方法：formatTime
  it('formatTime 应正确格式化时间', () => {
    expect(wrapper.vm.formatTime('2024-01-15T10:30:00')).toBe('2024-01-15 10:30:00')
    expect(wrapper.vm.formatTime(null)).toBe('')
  })

  // 测试边界条件
  it('flattenCascader 应处理空数组', () => {
    expect(wrapper.vm.flattenCascader([])).toEqual([])
    expect(wrapper.vm.flattenCascader(null)).toEqual([])
  })
})
```

### E2E 测试编写规范

#### 测试内容要求
- 登录流程
- 页面导航
- 表单提交
- 数据展示
- 完整业务流程

#### 示例测试文件

```javascript
// cypress/e2e/auth-letter.cy.js
describe('授权书管理', () => {
  beforeEach(() => {
    cy.visit('/')
  })

  it('应显示授权书列表页面', () => {
    cy.contains('授权书列表').should('be.visible')
  })

  it('应能搜索授权书', () => {
    cy.get('input[placeholder="请输入授权书名称"]').type('测试授权书')
    cy.contains('查询').click()
    cy.get('.el-table').should('exist')
  })

  it('应能重置搜索条件', () => {
    cy.get('input[placeholder="请输入授权书名称"]').type('测试')
    cy.contains('重置').click()
    cy.get('input[placeholder="请输入授权书名称"]').should('have.value', '')
  })
})
```

### 测试覆盖率要求

| 类型 | 最低覆盖率 |
|------|-----------|
| 语句覆盖率 | 80% |
| 分支覆盖率 | 70% |
| 函数覆盖率 | 80% |
| 行覆盖率 | 80% |

### 运行测试

```bash
# 运行单元测试
npm run test:unit

# 运行单元测试并生成覆盖率报告
npm run test:unit:coverage

# 运行 E2E 测试（无头模式）
npm run test:e2e

# 运行 E2E 测试（交互模式）
npm run test:e2e:open

# 运行所有测试
npm run test
```

## 交付物
- 测试计划文档
- 测试用例清单
- 缺陷报告模板
- 测试覆盖率报告