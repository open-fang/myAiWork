# 授权书管理系统 - API 接口设计

## 1. 概述

本文档定义授权书管理系统的 RESTful API 接口契约。

**基础路径**: `/api/v1`

**通用响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**分页响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

## 2. API 列表

### 2.1 授权书管理接口

#### 2.1.1 查询授权书列表

**GET** `/auth-letters`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 授权书名称（模糊查询） |
| authObjectLevel | String | 否 | 授权对象层级（多选，逗号分隔） |
| applicableRegion | String | 否 | 适用区域（多选，逗号分隔） |
| authPublishLevel | String | 否 | 授权发布层级（多选，逗号分隔） |
| authPublishOrg | String | 否 | 授权发布组织（多选，逗号分隔） |
| publishYear | Integer | 否 | 授权书发布年份 |
| status | String | 否 | 状态 |
| pageNum | Integer | 是 | 页码，从1开始 |
| pageSize | Integer | 是 | 每页条数 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "授权书名称",
        "status": "DRAFT",
        "authObjectLevel": "层级1,层级2",
        "applicableRegion": "机关-地区部-代表处",
        "authPublishLevel": "层级1",
        "authPublishOrg": "机关-地区部",
        "publishYear": 2024,
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 2.1.2 获取授权书详情

**GET** `/auth-letters/{id}`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "授权书名称",
    "authObjectLevel": ["层级1", "层级2"],
    "applicableRegion": [
      {"code": "1", "name": "机关", "children": [
        {"code": "1-1", "name": "地区部1", "children": [
          {"code": "1-1-1", "name": "代表处1"}
        ]}
      ]}
    ],
    "authPublishLevel": ["层级1"],
    "authPublishOrg": [],
    "publishYear": 2024,
    "summary": "授权书内容摘要",
    "status": "DRAFT",
    "createdBy": "admin",
    "createdTime": "2024-01-01 12:00:00",
    "updatedBy": "admin",
    "updatedTime": "2024-01-02 12:00:00"
  }
}
```

#### 2.1.3 创建授权书

**POST** `/auth-letters`

**请求体**:
```json
{
  "name": "授权书名称",
  "authObjectLevel": ["层级1", "层级2"],
  "applicableRegion": ["1", "1-1", "1-1-1"],
  "authPublishLevel": ["层级1"],
  "authPublishOrg": ["1", "1-1"],
  "publishYear": 2024,
  "summary": "授权书内容摘要"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

#### 2.1.4 更新授权书

**PUT** `/auth-letters/{id}`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**请求体**:
```json
{
  "name": "授权书名称",
  "authObjectLevel": ["层级1", "层级2"],
  "applicableRegion": ["1", "1-1", "1-1-1"],
  "authPublishLevel": ["层级1"],
  "authPublishOrg": ["1", "1-1"],
  "publishYear": 2024,
  "summary": "授权书内容摘要"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

#### 2.1.5 发布授权书

**POST** `/auth-letters/{id}/publish`

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

#### 2.1.6 失效授权书

**POST** `/auth-letters/{id}/invalidate`

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

#### 2.1.7 删除授权书

**DELETE** `/auth-letters/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

#### 2.1.8 批量操作授权书

**POST** `/auth-letters/batch`

**请求体**:
```json
{
  "ids": [1, 2, 3],
  "operation": "PUBLISH"
}
```

operation 可选值：PUBLISH（发布）、INVALIDATE（失效）、DELETE（删除）

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

### 2.2 附件管理接口

#### 2.2.1 查询附件列表

**GET** `/auth-letters/{authLetterId}/attachments`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页条数 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "docId": "DOC001",
        "docName": "授权书附件.pdf",
        "docType": "PDF",
        "isEncrypted": 0,
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00",
        "updatedBy": "admin",
        "updatedTime": "2024-01-02 12:00:00"
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 2.2.2 上传附件

**POST** `/auth-letters/{authLetterId}/attachments`

**请求体**:
```json
{
  "docId": "DOC001",
  "docName": "授权书附件.pdf",
  "docType": "PDF"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

#### 2.2.3 下载附件

**GET** `/auth-letters/{authLetterId}/attachments/{id}/download`

**响应**: 文件流

#### 2.2.4 删除附件

**DELETE** `/auth-letters/{authLetterId}/attachments/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

#### 2.2.5 批量删除附件

**POST** `/auth-letters/{authLetterId}/attachments/batch-delete`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

### 2.3 场景管理接口

#### 2.3.1 查询场景列表

**GET** `/auth-letters/{authLetterId}/scenes`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页条数 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "sceneName": "场景名称",
        "industry": ["产业1", "产业2"],
        "businessScene": "业务场景1",
        "decisionLevel": "决策层级1",
        "specificRule": "具体规则描述",
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00",
        "updatedBy": "admin",
        "updatedTime": "2024-01-02 12:00:00"
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 2.3.2 创建场景

**POST** `/auth-letters/{authLetterId}/scenes`

**请求体**:
```json
{
  "sceneName": "场景名称",
  "industry": ["产业1", "产业1-1"],
  "businessScene": "业务场景1",
  "decisionLevel": "决策层级1",
  "specificRule": "具体规则描述",
  "rules": [
    {
      "logicType": "AND",
      "conditions": [
        {
          "fieldCode": "AMOUNT",
          "operator": ">=",
          "compareType": "NUMBER",
          "compareValue": "1000000",
          "compareUnit": "CNY",
          "logicType": "AND"
        }
      ]
    }
  ],
  "questionnaire": [
    {
      "question": "问题1",
      "options": ["选项A", "选项B"],
      "answer": "选项A"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1
  }
}
```

#### 2.3.3 更新场景

**PUT** `/auth-letters/{authLetterId}/scenes/{id}`

**请求体**: 同创建场景

#### 2.3.4 删除场景

**DELETE** `/auth-letters/{authLetterId}/scenes/{id}`

#### 2.3.5 批量删除场景

**POST** `/auth-letters/{authLetterId}/scenes/batch-delete`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

### 2.4 规则参数配置接口

#### 2.4.1 查询规则参数列表

**GET** `/rule-params`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 名称（模糊查询） |
| nameEn | String | 否 | 名称英文（模糊查询） |
| status | String | 否 | 状态 |
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页条数 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "金额",
        "nameEn": "amount",
        "businessObject": "订单",
        "valueLogic": "$.order.amount",
        "status": "ACTIVE",
        "dataType": "NUMBER",
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00",
        "updatedBy": "admin",
        "updatedTime": "2024-01-02 12:00:00"
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

#### 2.4.2 获取规则参数详情

**GET** `/rule-params/{id}`

#### 2.4.3 创建规则参数

**POST** `/rule-params`

**请求体**:
```json
{
  "name": "金额",
  "nameEn": "amount",
  "businessObjects": [
    {"businessObject": "订单", "valueLogic": "$.order.amount"},
    {"businessObject": "合同", "valueLogic": "$.contract.amount"}
  ],
  "status": "ACTIVE",
  "dataType": "NUMBER"
}
```

#### 2.4.4 更新规则参数

**PUT** `/rule-params/{id}`

**请求体**: 同创建规则参数

#### 2.4.5 批量更新状态

**POST** `/rule-params/batch-status`

**请求体**:
```json
{
  "ids": [1, 2, 3],
  "status": "ACTIVE"
}
```

### 2.5 下拉列表数据接口

#### 2.5.1 获取下拉列表数据

**GET** `/lookup-values/{typeCode}`

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| typeCode | String | 是 | 类型编码 |

**响应示例（平铺列表）**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"code": "LEVEL1", "name": "层级1"},
    {"code": "LEVEL2", "name": "层级2"}
  ]
}
```

**响应示例（树形列表）**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "code": "1",
      "name": "机关",
      "children": [
        {
          "code": "1-1",
          "name": "地区部1",
          "children": [
            {"code": "1-1-1", "name": "代表处1"},
            {"code": "1-1-2", "name": "代表处2"}
          ]
        }
      ]
    }
  ]
}
```

### 2.6 操作日志接口

#### 2.6.1 查询操作日志

**GET** `/auth-letters/{authLetterId}/logs`

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 是 | 页码 |
| pageSize | Integer | 是 | 每页条数 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "operation": "PUBLISH",
        "operator": "admin",
        "operationTime": "2024-01-01 12:00:00"
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2.7 场景匹配接口

#### 2.7.1 场景匹配

**POST** `/scene-match`

**请求体**:
```json
{
  "authLetterId": 1,
  "data": {
    "amount": 1000000,
    "currency": "CNY",
    "region": "1-1-1",
    "industry": "产业1"
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "matchedScenes": [
      {
        "sceneId": 1,
        "sceneName": "场景名称",
        "decisionLevel": "决策层级1"
      }
    ]
  }
}
```

## 3. 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 1001 | 授权书名称已存在 |
| 1002 | 授权书状态不允许此操作 |
| 1003 | 场景规则配置不完整 |
| 1004 | 规则参数名称已存在 |