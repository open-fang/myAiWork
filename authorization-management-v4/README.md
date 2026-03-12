# 授权书管理系统 V4

## 项目简介

授权书管理系统用于管理授权书的创建、配置规则、发布和生命周期管理。

## 技术栈

### 后端
- Java 8
- Spring Boot 2.7.18
- MyBatis 2.3.1
- JAX-RS (Jersey)
- PostgreSQL
- Flyway

### 前端
- 原生 Vue 2

## 项目结构

```
authorization-management-v4/
├── backend/                    # 后端代码
│   ├── src/main/
│   │   ├── java/com/auth/letter/
│   │   │   ├── api/           # JAX-RS接口定义
│   │   │   ├── api/impl/      # 接口实现
│   │   │   ├── service/       # 服务层
│   │   │   ├── mapper/        # MyBatis Mapper
│   │   │   ├── entity/        # 实体类
│   │   │   ├── dto/           # 数据传输对象
│   │   │   └── enums/         # 枚举类
│   │   └── resources/
│   │       ├── db/migration/  # Flyway迁移脚本
│   │       └── application.yml
│   └── src/test/              # 测试代码
├── frontend/                   # 前端代码
│   ├── index.html
│   └── src/views/
│       ├── AuthLetterList.vue    # 授权书列表页
│       ├── AuthLetterDetail.vue  # 授权书详情页
│       └── RuleParamConfig.vue   # 规则参数配置页
└── README.md
```

## 功能模块

### 1. 授权书列表页 (`#/AuthLetterList`)
- 授权书查询（支持多条件筛选）
- 新建/更新/生效/失效/删除授权书
- 分页显示

### 2. 授权书详情页 (`#/AuthLetterDetail`)
- 基本信息维护
- 附件管理
- 授权规则配置
- 操作日志查看

### 3. 规则参数配置页 (`#/RuleParamConfig`)
- 规则参数CRUD
- 批量生效/失效

## 数据库表

- `auth_letter` - 授权书主表
- `auth_attachment` - 附件表
- `auth_scene` - 场景表
- `rule_param` - 规则参数表
- `auth_operation_log` - 操作日志表

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

### 前端访问

直接打开 `frontend/index.html` 或使用静态服务器托管。

## API接口

### 授权书接口
- `GET /api/auth-letters` - 查询授权书列表
- `GET /api/auth-letters/{id}` - 获取授权书详情
- `POST /api/auth-letters` - 创建授权书
- `PUT /api/auth-letters/{id}` - 更新授权书
- `DELETE /api/auth-letters/{id}` - 删除授权书
- `PUT /api/auth-letters/{id}/publish` - 发布授权书
- `PUT /api/auth-letters/{id}/expire` - 失效授权书

### 规则参数接口
- `GET /api/rule-params` - 查询规则参数列表
- `GET /api/rule-params/{id}` - 获取规则参数详情
- `POST /api/rule-params` - 创建规则参数
- `PUT /api/rule-params/{id}` - 更新规则参数

### Lookup接口
- `GET /api/lookup/{code}` - 获取下拉列表项
- `GET /api/lookup/org/tree` - 获取组织树