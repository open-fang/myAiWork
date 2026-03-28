# 授权书管理系统 V7 - API 接口清单

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

**错误响应格式**:
```json
{
  "code": 1001,
  "message": "授权书名称已存在",
  "data": null
}
```

---

## 2. API 接口清单

### 2.1 授权书管理接口

#### 2.1.1 查询授权书列表

**接口说明**: 分页查询授权书列表，支持多条件筛选

**请求方式**: `GET`

**接口路径**: `/auth-letters`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 授权书名称（模糊查询） |
| authObjectLevel | String | 否 | 授权对象层级（多选，逗号分隔编码） |
| applicableRegion | String | 否 | 适用区域（多选，逗号分隔编码） |
| authPublishLevel | String | 否 | 授权发布层级（多选，逗号分隔编码） |
| authPublishOrg | String | 否 | 授权发布组织（多选，逗号分隔编码） |
| publishYear | Integer | 否 | 授权书发布年份 |
| status | String | 否 | 状态（DRAFT/PUBLISHED/INVALID） |
| pageNum | Integer | 是 | 页码，从1开始 |
| pageSize | Integer | 是 | 每页条数（10/30/50） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "销售授权书2024",
        "status": "DRAFT",
        "statusName": "草稿",
        "authObjectLevel": "层级1,层级2",
        "applicableRegion": "机关-地区部1-代表处1",
        "authPublishLevel": "层级1",
        "authPublishOrg": "机关-地区部1",
        "publishYear": 2024,
        "createdBy": "admin",
        "createdByName": "管理员",
        "createdTime": "2024-01-01 12:00:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

#### 2.1.2 获取授权书详情

**接口说明**: 根据ID获取授权书完整信息

**请求方式**: `GET`

**接口路径**: `/auth-letters/{id}`

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
    "name": "销售授权书2024",
    "authObjectLevel": ["LEVEL1", "LEVEL2"],
    "applicableRegion": ["1", "1-1", "1-1-1"],
    "authPublishLevel": ["LEVEL1"],
    "authPublishOrg": ["1", "1-1"],
    "publishYear": 2024,
    "summary": "授权书内容摘要...",
    "status": "DRAFT",
    "createdBy": "admin",
    "createdTime": "2024-01-01 12:00:00",
    "updatedBy": "admin",
    "updatedTime": "2024-01-02 12:00:00"
  }
}
```

---

#### 2.1.3 创建授权书

**接口说明**: 创建新的授权书，状态默认为草稿

**请求方式**: `POST`

**接口路径**: `/auth-letters`

**请求体**:
```json
{
  "name": "销售授权书2024",
  "authObjectLevel": ["LEVEL1", "LEVEL2"],
  "applicableRegion": ["1", "1-1", "1-1-1"],
  "authPublishLevel": ["LEVEL1"],
  "authPublishOrg": ["1", "1-1"],
  "publishYear": 2024,
  "summary": "授权书内容摘要..."
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

**错误码**:
- 1001: 授权书名称已存在

---

#### 2.1.4 更新授权书

**接口说明**: 更新授权书基本信息（仅草稿状态可更新）

**请求方式**: `PUT`

**接口路径**: `/auth-letters/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**请求体**: 同创建授权书

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

**错误码**:
- 1002: 授权书状态不允许此操作

---

#### 2.1.5 发布授权书

**接口说明**: 将草稿状态的授权书发布

**请求方式**: `POST`

**接口路径**: `/auth-letters/{id}/publish`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

**错误码**:
- 1002: 授权书状态不允许此操作
- 1003: 授权书信息不完整，无法发布

---

#### 2.1.6 失效授权书

**接口说明**: 将已发布状态的授权书失效

**请求方式**: `POST`

**接口路径**: `/auth-letters/{id}/invalidate`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

**错误码**:
- 1002: 授权书状态不允许此操作

---

#### 2.1.7 删除授权书

**接口说明**: 删除授权书（所有状态均可删除）

**请求方式**: `DELETE`

**接口路径**: `/auth-letters/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.1.8 批量操作授权书

**接口说明**: 批量执行发布、失效、删除操作

**请求方式**: `POST`

**接口路径**: `/auth-letters/batch`

**请求体**:
```json
{
  "ids": [1, 2, 3],
  "operation": "PUBLISH"
}
```

**operation 可选值**:
- PUBLISH: 发布（仅草稿状态）
- INVALIDATE: 失效（仅已发布状态）
- DELETE: 删除（所有状态）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 2,
    "failCount": 1,
    "failDetails": [
      {"id": 3, "reason": "状态不允许此操作"}
    ]
  }
}
```

---

### 2.2 附件管理接口

#### 2.2.1 查询附件列表

**接口说明**: 分页查询授权书的附件列表

**请求方式**: `GET`

**接口路径**: `/auth-letters/{authLetterId}/attachments`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

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
        "docTypeName": "PDF文档",
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

---

#### 2.2.2 上传附件

**接口说明**: 上传附件到授权书（记录docId等信息，文件上传逻辑暂不实现）

**请求方式**: `POST`

**接口路径**: `/auth-letters/{authLetterId}/attachments`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

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

---

#### 2.2.3 下载附件

**接口说明**: 下载指定附件文件

**请求方式**: `GET`

**接口路径**: `/auth-letters/{authLetterId}/attachments/{id}/download`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 附件ID |

**响应**: 文件流

---

#### 2.2.4 删除附件

**接口说明**: 删除指定附件

**请求方式**: `DELETE`

**接口路径**: `/auth-letters/{authLetterId}/attachments/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 附件ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.2.5 批量删除附件

**接口说明**: 批量删除附件

**请求方式**: `POST`

**接口路径**: `/auth-letters/{authLetterId}/attachments/batch-delete`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

### 2.3 场景管理接口

#### 2.3.1 查询场景列表

**接口说明**: 分页查询授权书的场景列表

**请求方式**: `GET`

**接口路径**: `/auth-letters/{authLetterId}/scenes`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

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
        "sceneName": "销售场景1",
        "industry": ["产业1", "产业1-1"],
        "industryName": "产业1-产业1-1",
        "businessScene": "BS001",
        "businessSceneName": "业务场景1",
        "decisionLevel": "DL001",
        "decisionLevelName": "决策层级1",
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

---

#### 2.3.2 获取场景详情

**接口说明**: 获取场景完整信息，包含规则配置

**请求方式**: `GET`

**接口路径**: `/auth-letters/{authLetterId}/scenes/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 场景ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "sceneName": "销售场景1",
    "industry": ["产业1", "产业1-1"],
    "businessScene": "BS001",
    "decisionLevel": "DL001",
    "specificRule": "具体规则描述",
    "rules": [
      {
        "id": 1,
        "ruleName": "规则组1",
        "logicType": "AND",
        "conditions": [
          {
            "id": 1,
            "fieldCode": "AMOUNT",
            "fieldName": "金额",
            "operator": ">=",
            "compareType": "NUMBER",
            "compareValue": "1000000",
            "compareUnit": "CNY",
            "logicType": "AND",
            "isGroup": false,
            "sortOrder": 1
          },
          {
            "id": 2,
            "fieldCode": "REGION",
            "fieldName": "区域",
            "operator": "=",
            "compareType": "FIELD",
            "compareFieldCode": "APPLICABLE_REGION",
            "logicType": "AND",
            "isGroup": false,
            "sortOrder": 2
          },
          {
            "id": 3,
            "isGroup": true,
            "groupLogicType": "OR",
            "logicType": "AND",
            "sortOrder": 3,
            "children": [
              {
                "id": 4,
                "fieldCode": "INDUSTRY",
                "fieldName": "产业",
                "operator": "=",
                "compareType": "TEXT",
                "compareValue": "产业1",
                "logicType": "OR",
                "sortOrder": 1
              }
            ]
          }
        ]
      }
    ],
    "questionnaires": [
      {
        "id": 1,
        "questionId": 1,
        "questionCode": "QT001",
        "questionText": "请选择授权类型",
        "correctAnswerId": 1,
        "correctAnswerCode": "ANS001",
        "correctAnswerText": "全额授权",
        "sortOrder": 1
      }
    ]
  }
}
```

---

#### 2.3.3 创建场景

**接口说明**: 创建新场景

**请求方式**: `POST`

**接口路径**: `/auth-letters/{authLetterId}/scenes`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体**:
```json
{
  "sceneName": "销售场景1",
  "industry": ["产业1", "产业1-1"],
  "businessScene": "BS001",
  "decisionLevel": "DL001",
  "specificRule": "具体规则描述",
  "rules": [
    {
      "ruleName": "规则组1",
      "logicType": "AND",
      "conditions": [
        {
          "fieldCode": "AMOUNT",
          "operator": ">=",
          "compareType": "NUMBER",
          "compareValue": "1000000",
          "compareUnit": "CNY",
          "logicType": "AND",
          "isGroup": false,
          "sortOrder": 1
        }
      ]
    }
  ],
  "questionnaires": [
    {
      "questionId": 1,
      "correctAnswerId": 1,
      "sortOrder": 1
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

**错误码**:
- 1004: 场景名称已存在
- 1005: 规则和问卷至少需要配置一项

---

#### 2.3.4 更新场景

**接口说明**: 更新场景配置

**请求方式**: `PUT`

**接口路径**: `/auth-letters/{authLetterId}/scenes/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 场景ID |

**请求体**: 同创建场景

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.3.5 删除场景

**接口说明**: 删除指定场景

**请求方式**: `DELETE`

**接口路径**: `/auth-letters/{authLetterId}/scenes/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 场景ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.3.6 批量删除场景

**接口说明**: 批量删除场景

**请求方式**: `POST`

**接口路径**: `/auth-letters/{authLetterId}/scenes/batch-delete`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

### 2.4 规则参数配置接口

#### 2.4.1 查询规则参数列表

**接口说明**: 分页查询规则参数配置

**请求方式**: `GET`

**接口路径**: `/rule-params`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 名称（模糊查询） |
| nameEn | String | 否 | 名称英文（模糊查询） |
| status | String | 否 | 状态（ACTIVE/INACTIVE） |
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
        "businessObjects": [
          {"businessObject": "订单", "valueLogic": "$.order.amount"},
          {"businessObject": "合同", "valueLogic": "$.contract.amount"}
        ],
        "status": "ACTIVE",
        "statusName": "生效",
        "dataType": "NUMBER",
        "dataTypeName": "数值",
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

---

#### 2.4.2 获取规则参数详情

**接口说明**: 获取规则参数完整信息

**请求方式**: `GET`

**接口路径**: `/rule-params/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 规则参数ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "金额",
    "nameEn": "amount",
    "businessObjects": [
      {"businessObject": "订单", "valueLogic": "$.order.amount"},
      {"businessObject": "合同", "valueLogic": "$.contract.amount"}
    ],
    "status": "ACTIVE",
    "dataType": "NUMBER",
    "referenceFieldId": null,
    "createdBy": "admin",
    "createdTime": "2024-01-01 12:00:00"
  }
}
```

---

#### 2.4.3 创建规则参数

**接口说明**: 创建新的规则参数配置

**请求方式**: `POST`

**接口路径**: `/rule-params`

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
  "dataType": "NUMBER",
  "referenceFieldId": null
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

**错误码**:
- 1006: 规则参数名称已存在

---

#### 2.4.4 更新规则参数

**接口说明**: 更新规则参数配置

**请求方式**: `PUT`

**接口路径**: `/rule-params/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 规则参数ID |

**请求体**: 同创建规则参数

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.4.5 批量更新规则参数状态

**接口说明**: 批量更新规则参数状态（生效/失效）

**请求方式**: `POST`

**接口路径**: `/rule-params/batch-status`

**请求体**:
```json
{
  "ids": [1, 2, 3],
  "status": "ACTIVE"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.4.6 获取规则参数下拉列表

**接口说明**: 获取规则参数的下拉列表数据（用于规则条件配置）

**请求方式**: `GET`

**接口路径**: `/rule-params/options`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | String | 否 | 状态过滤（默认ACTIVE） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"code": "AMOUNT", "name": "金额", "dataType": "NUMBER"},
    {"code": "REGION", "name": "区域", "dataType": "TEXT"},
    {"code": "INDUSTRY", "name": "产业", "dataType": "TEXT"}
  ]
}
```

---

### 2.5 问卷管理接口

#### 2.5.1 查询问卷题目列表

**接口说明**: 分页查询问卷题目配置

**请求方式**: `GET`

**接口路径**: `/questionnaire-questions`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| questionCode | String | 否 | 题目编号（模糊查询） |
| questionText | String | 否 | 题目内容（模糊查询） |
| language | String | 否 | 语言（ZH/EN） |
| createdBy | String | 否 | 创建人（模糊查询） |
| createdTimeFrom | String | 否 | 创建时间起始（按天） |
| createdTimeTo | String | 否 | 创建时间截止（按天） |
| updatedBy | String | 否 | 更新人（模糊查询） |
| updatedTimeFrom | String | 否 | 更新时间起始（按天） |
| updatedTimeTo | String | 否 | 更新时间截止（按天） |
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
        "questionCode": "QT20240101001",
        "questionText": "请选择授权类型",
        "language": "ZH",
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

---

#### 2.5.2 获取问卷题目详情

**接口说明**: 获取问卷题目完整信息，包含多语言题目内容和可选答案

**请求方式**: `GET`

**接口路径**: `/questionnaire-questions/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 题目ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "questionCode": "QT20240101001",
    "questionTexts": [
      {
        "id": 1,
        "questionText": "请选择授权类型",
        "language": "ZH",
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00"
      },
      {
        "id": 2,
        "questionText": "Please select authorization type",
        "language": "EN",
        "createdBy": "admin",
        "createdTime": "2024-01-01 12:00:00"
      }
    ],
    "answers": [
      {
        "id": 1,
        "answerCode": "ANS20240101001",
        "answerTexts": [
          {"answerText": "全额授权", "language": "ZH"},
          {"answerText": "Full authorization", "language": "EN"}
        ],
        "sortOrder": 1
      },
      {
        "id": 2,
        "answerCode": "ANS20240101002",
        "answerTexts": [
          {"answerText": "部分授权", "language": "ZH"},
          {"answerText": "Partial authorization", "language": "EN"}
        ],
        "sortOrder": 2
      }
    ]
  }
}
```

---

#### 2.5.3 创建问卷题目

**接口说明**: 创建新的问卷题目

**请求方式**: `POST`

**接口路径**: `/questionnaire-questions`

**请求体**:
```json
{
  "questionTexts": [
    {"questionText": "请选择授权类型", "language": "ZH"},
    {"questionText": "Please select authorization type", "language": "EN"}
  ],
  "answers": [
    {
      "answerTexts": [
        {"answerText": "全额授权", "language": "ZH"},
        {"answerText": "Full authorization", "language": "EN"}
      ],
      "sortOrder": 1
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
    "id": 1,
    "questionCode": "QT20240101001"
  }
}
```

---

#### 2.5.4 更新问卷题目

**接口说明**: 更新问卷题目配置

**请求方式**: `PUT`

**接口路径**: `/questionnaire-questions/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 题目ID |

**请求体**: 同创建问卷题目

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.5.5 删除问卷题目

**接口说明**: 删除问卷题目

**请求方式**: `DELETE`

**接口路径**: `/questionnaire-questions/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 题目ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.5.6 批量删除问卷题目

**接口说明**: 批量删除问卷题目

**请求方式**: `POST`

**接口路径**: `/questionnaire-questions/batch-delete`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

---

#### 2.5.7 获取问卷题目下拉列表

**接口说明**: 获取问卷题目下拉列表（用于场景配置时选择题目）

**请求方式**: `GET`

**接口路径**: `/questionnaire-questions/options`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| language | String | 否 | 语言（默认ZH） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "questionId": 1,
      "questionCode": "QT20240101001",
      "questionText": "请选择授权类型",
      "answers": [
        {"answerId": 1, "answerCode": "ANS001", "answerText": "全额授权"},
        {"answerId": 2, "answerCode": "ANS002", "answerText": "部分授权"}
      ]
    }
  ]
}
```

---

### 2.6 下拉列表数据接口

#### 2.6.1 获取下拉列表数据

**接口说明**: 根据类型编码获取下拉列表数据

**请求方式**: `GET`

**接口路径**: `/lookup-values/{typeCode}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| typeCode | String | 是 | 类型编码 |

**typeCode 可选值**:
- AUTH_OBJECT_LEVEL: 授权对象层级
- AUTH_PUBLISH_LEVEL: 授权发布层级
- APPLICABLE_REGION: 适用区域
- AUTH_PUBLISH_ORG: 授权发布组织
- INDUSTRY: 产业
- BUSINESS_SCENE: 业务场景
- DECISION_LEVEL: 决策层级
- DOC_TYPE: 文档类型
- OPERATOR: 运算符
- COMPARE_UNIT: 计量单位
- LANGUAGE: 语言
- DATA_TYPE: 数据类型

**响应示例（平铺列表 - 层级）**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"code": "COMPANY", "name": "公司"},
    {"code": "BU", "name": "BU"},
    {"code": "MKT_SRV", "name": "营销服"},
    {"code": "REGION", "name": "地区部"},
    {"code": "REP_OFFICE", "name": "代表处"}
  ]
}
```

**响应示例（树形列表 - 四层）**:
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
            {
              "code": "1-1-1",
              "name": "代表处1",
              "children": [
                {"code": "1-1-1-1", "name": "办事处1"},
                {"code": "1-1-1-2", "name": "办事处2"}
              ]
            },
            {
              "code": "1-1-2",
              "name": "代表处2",
              "children": [
                {"code": "1-1-2-1", "name": "办事处3"}
              ]
            }
          ]
        }
      ]
    }
  ]
}
```

---

### 2.7 操作日志接口

#### 2.7.1 查询操作日志

**接口说明**: 分页查询授权书的操作日志

**请求方式**: `GET`

**接口路径**: `/auth-letters/{authLetterId}/logs`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

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
        "operationName": "发布",
        "operator": "admin",
        "operatorName": "管理员",
        "operationTime": "2024-01-01 12:00:00"
      },
      {
        "id": 2,
        "operation": "UPDATE",
        "operationName": "更新",
        "operator": "admin",
        "operatorName": "管理员",
        "operationTime": "2024-01-01 10:00:00"
      },
      {
        "id": 3,
        "operation": "CREATE",
        "operationName": "创建",
        "operator": "admin",
        "operatorName": "管理员",
        "operationTime": "2024-01-01 08:00:00"
      }
    ],
    "total": 3,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 2.8 场景匹配接口

#### 2.8.1 场景匹配

**接口说明**: 根据规则匹配授权场景，返回所有命中的场景

**请求方式**: `POST`

**接口路径**: `/scene-match`

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

**说明**:
- `data` 字段包含规则匹配所需的参数数据
- 参数名对应规则参数的 `nameEn` 字段
- 参数值由调用方根据业务上下文提供

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "matchedScenes": [
      {
        "sceneId": 1,
        "sceneName": "销售场景1",
        "decisionLevel": "DL001",
        "decisionLevelName": "决策层级1"
      },
      {
        "sceneId": 2,
        "sceneName": "销售场景2",
        "decisionLevel": "DL002",
        "decisionLevelName": "决策层级2"
      }
    ]
  }
}
```

**说明**:
- 命中几个场景返回几个
- 返回命中场景的决策层级
- 不考虑场景之间的关系

---

## 3. 错误码定义

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 200 | 成功 | - |
| 400 | 请求参数错误 | 检查请求参数格式 |
| 401 | 未授权 | 需要登录认证 |
| 403 | 无权限 | 检查用户权限 |
| 404 | 资源不存在 | 检查资源ID是否正确 |
| 500 | 服务器内部错误 | 联系系统管理员 |
| 1001 | 授权书名称已存在 | 使用不同的名称 |
| 1002 | 授权书状态不允许此操作 | 检查授权书当前状态 |
| 1003 | 授权书信息不完整，无法发布 | 补充完整基本信息和场景配置 |
| 1004 | 场景名称已存在 | 使用不同的场景名称 |
| 1005 | 规则和问卷至少需要配置一项 | 配置规则或问卷 |
| 1006 | 规则参数名称已存在 | 使用不同的名称 |
| 1007 | 相同语言只能维护一个题目 | 删除已有同语言题目后再添加 |
| 1008 | 相同语言只能维护一个答案 | 删除已有同语言答案后再添加 |

---

## 4. API 接口汇总

| 模块 | 接口数量 | 说明 |
|------|----------|------|
| 授权书管理 | 8 | 列表查询、详情、创建、更新、发布、失效、删除、批量操作 |
| 附件管理 | 5 | 列表查询、上传、下载、删除、批量删除 |
| 场景管理 | 6 | 列表查询、详情、创建、更新、删除、批量删除 |
| 规则参数配置 | 6 | 列表查询、详情、创建、更新、批量状态更新、下拉列表 |
| 问卷管理 | 7 | 列表查询、详情、创建、更新、删除、批量删除、下拉列表 |
| 下拉列表数据 | 1 | 根据类型编码获取下拉数据 |
| 操作日志 | 1 | 查询操作日志 |
| 场景匹配 | 1 | 场景匹配服务接口 |
| **总计** | **34** | |

---

## 5. 前端 baseURL 配置

每个页面文件需定义 `baseURL` 变量，方便联调时临时修改：

```javascript
// 开发环境
const baseURL = '/api/v1';

// 或联调环境
// const baseURL = 'http://192.168.1.100:8080/api/v1';
```

---

**文档版本**: v1.1
**创建日期**: 2026-03-28
**创建人**: BA Agent
**最后更新**: 2026-03-28
**更新说明**: 根据用户澄清内容更新层级定义、树形结构层级等