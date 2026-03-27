---
name: backend-dev
description: 后端 API 开发、数据库操作、业务逻辑实现。适用于 JAVA JDK8/PostgreSQL/Spring Boot 项目。
---

# 后端开发规范

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