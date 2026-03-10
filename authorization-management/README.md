# 授权书管理系统 (Authorization Management System)

一个基于 Spring Boot + Vue 3 构建的授权书管理系统，支持授权书的创建、发布、场景管理和规则配置。

## 项目结构

```
authorization-management/
├── backend/          # 后端 - Java Spring Boot
│   ├── src/main/java/com/auth/letter/
│   │   ├── entity/        # 实体类
│   │   ├── repository/    # 数据访问层
│   │   ├── service/       # 业务逻辑层
│   │   ├── controller/    # REST API控制器
│   │   ├── dto/           # 数据传输对象
│   │   ├── enums/         # 枚举类
│   │   └── exception/     # 异常处理
│   └── src/test/          # 测试代码
│
├── frontend/         # 前端 - Vue.js
│   └── src/
│       ├── api/           # API接口
│       ├── views/         # 页面组件
│       ├── components/    # 通用组件
│       ├── router/        # 路由配置
│       └── store/         # 状态管理
│
└── README.md
```

## 功能特性

### 核心功能
- **授权书生命周期管理**：创建 → 发布 → 失效（超过有效期自动失效）
- **场景管理**：在授权书下维护多个场景，每个场景可配置决策层级
- **规则管理**：在场景下维护规则，支持动态条件配置
- **条件匹配**：支持与/或逻辑，支持嵌套条件（如 [A与(B或C)]）

### 业务层级
- 机关 > 地区部 > 代表处 > 办事处

### 授权书属性
- 发布层级：授权书是由哪个层级发布的
- 被授权层级：授权书最终在哪个层级生效
- 决策层级：场景中的独立字段

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (开发环境)
- Maven

### 前端
- Vue 3 + Composition API
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios

## 快速开始

### 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## API 接口

### 授权书管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/auth-letters | 获取所有授权书 |
| GET | /api/auth-letters/{id} | 获取授权书详情 |
| POST | /api/auth-letters | 创建授权书 |
| PUT | /api/auth-letters/{id} | 更新授权书 |
| DELETE | /api/auth-letters/{id} | 删除授权书 |
| POST | /api/auth-letters/{id}/publish | 发布授权书 |
| POST | /api/auth-letters/match | 匹配场景 |

### 场景管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/auth-letters/{id}/scenes | 获取授权书下的场景 |
| POST | /api/auth-letters/{id}/scenes | 创建场景 |
| PUT | /api/auth-letters/{id}/scenes/{sceneId} | 更新场景 |
| DELETE | /api/auth-letters/{id}/scenes/{sceneId} | 删除场景 |

### 规则管理
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/scenes/{id}/rules | 获取场景下的规则 |
| POST | /api/scenes/{id}/rules | 创建规则 |
| PUT | /api/scenes/{id}/rules/{ruleId} | 更新规则 |
| DELETE | /api/scenes/{id}/rules/{ruleId} | 删除规则 |
| POST | /api/scenes/{id}/rules/{ruleId}/conditions | 添加条件 |

## 测试

### 后端测试
```bash
cd backend
mvn test
```

### 前端测试
```bash
cd frontend
npm run test
```

## License

MIT