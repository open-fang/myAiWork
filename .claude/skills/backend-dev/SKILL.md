---
name: backend-dev
description: 后端 API 开发、数据库操作、业务逻辑实现。适用于 JAVA JDK8/PostgreSQL/Spring Boot 项目。
---

# 后端开发规范

## 角色职责边界（重要）

### 后端开发工程师职责

| 允许的操作 | 禁止的操作 |
|------------|------------|
| ✅ 编写/修改后端功能代码 | ❌ 修改前端代码（Vue文件） |
| ✅ 编写数据库迁移脚本（Flyway） | ❌ 修改测试代码（属于QA职责） |
| ✅ 编写API接口和业务逻辑 | ❌ 跳过自测直接提交 |
| ✅ 编写单元测试验证自己的代码 | ❌ 使用模拟数据库（H2）进行开发测试 |
| ✅ 修复自己代码的bug | ❌ 修改其他开发者的代码（需协调） |

### 与其他角色的边界

| 场景 | 负责角色 |
|------|----------|
| 前端页面问题 | frontend-dev |
| 测试用例编写 | qa-agent |
| 架构设计变更 | architect-agent |
| 需求澄清 | ba-agent |
| API契约定义 | backend-dev + frontend-dev 协作 |

---

## 开发者自测要求（重要）

### 代码提交前必须完成的自测

后端开发工程师**必须**在提交代码前完成以下自测，确保代码质量：

### 1. 编译验证

```bash
# 确保无编译错误
mvn clean compile

# 确保无语法问题
mvn checkstyle:check  # 如有配置
```

### 2. 单元测试验证

```bash
# 运行所有单元测试
mvn test

# 确保测试通过率 100%
# 如有失败，必须修复后才能提交
```

### 3. 数据库要求（严禁模拟）

**必须使用 Docker 启动的真实 PostgreSQL 数据库进行开发测试**

```bash
# 启动 PostgreSQL 容器
docker run -d --name postgres-dev \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=authorization_management \
    -p 5432:5432 \
    postgres:13

# 等待数据库就绪
sleep 5

# 验证连接
docker exec -it postgres-dev psql -U postgres -c "SELECT 1"
```

**禁止事项：**
- ❌ 禁止使用 H2 内存数据库进行开发测试
- ❌ 禁止使用 Mockito 模拟数据库操作进行集成测试
- ❌ 禁止跳过数据库相关测试

**原因：**
- H2 与 PostgreSQL 语法存在差异，可能掩盖 SQL 问题
- 真实数据库才能发现索引、约束、性能问题

### 4. API 接口验证

```bash
# 启动后端服务
mvn spring-boot:run

# 验证接口可访问
curl http://localhost:8081/api/authLetter/list \
    -H "Content-Type: application/json" \
    -d '{"pageNum":1,"pageSize":10}'
```

### 5. 自测覆盖率要求

| 测试类型 | 最低要求 |
|----------|----------|
| Service 层单元测试 | 覆盖所有公开方法 |
| 分支覆盖 | 覆盖主要业务分支 |
| 异常处理 | 覆盖异常场景 |
| 边界条件 | 覆盖空值、最大值等 |

### 自测完成清单

- [ ] 编译通过，无语法错误
- [ ] 单元测试 100% 通过
- [ ] 使用 Docker PostgreSQL 测试通过
- [ ] API 接口可正常调用
- [ ] 无 SQL 语法错误
- [ ] 无 NullPointerException
- [ ] 日志输出正常

---

## 技术栈
- JAVA JDK8
- 数据库：PostgreSQL
- 框架：Spring Boot + JAX-RS + MyBatis
- 数据库迁移：Flyway

## 编码要求与规范

### 数据库访问
- 使用 Flyway 已创建的数据库表
- 开发时可以使用 docker 临时起一个 pg 数据库来调试

### 分层架构
后端代码需符合分层架构，命名基于业务语义，注解完整，确保可直接集成到项目中。严格遵循 Spring Boot、JAX-RS 和 MyBatis 规范：

#### a) 服务接口
- 使用 JAX-RS 注解（如 @GET、@POST 等）定义 REST 端点
- 方法参数包含业务 VO 和分页 VO（如需分页）
- 返回业务 VO 或 PagedResult<业务VO>

#### b) 服务实现类
- 用 @Override 实现接口
- 通过 @Inject 或 @Autowired 注入 DAO
- 执行业务逻辑（含分页处理、参数校验等）
- 添加权限注解：
  - 类级别：`@JalorResource(code = "xx", desc = "xx")`
  - 方法级别：`@JalorOperation(code = "xxx", desc = "xxx")`

#### c) MyBatis DAO 接口
- 声明数据库操作方法
- 使用 @Param 注解明确参数

#### d) MyBatis XML 映射
- 编写对应的 SQL 语句
- 支持增删改查及分页查询

### Import 规范
- **禁止使用** `import xx.xx.*`
- 必须把每个 import 的类单独一行

### 开发流程
1. **API 契约先行**：在编写任何代码之前，必须先定义清晰的 API 契约
2. **契约驱动开发**：严格基于同一契约生成前后端代码，确保接口完全匹配
3. **本地调试**：开发完成后检查语法错误，本地调试通过

### 完成后核对清单
- [ ] URL 和方法是否匹配？
- [ ] 请求字段名、类型是否一致？
- [ ] 响应字段名、类型是否一致？
- [ ] 前端是否正确处理了成功和失败的响应？
- [ ] 如有不一致，立即修正

### 权限注解（预留）
当前不用考虑用户登录鉴权等能力，这部分有通用框架能力，后面再接入。但可以提前在服务实现类添加权限注解供框架扫描：
- 类级别：`@JalorResource(code = "xx", desc = "xx")`
- 方法级别：`@JalorOperation(code = "xxx", desc = "xxx")`

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

### 6. 明确执行要求
- "全面排查"必须转化为具体的检查动作
- 列出需要检查的文件清单
- 逐个文件对比并记录结果