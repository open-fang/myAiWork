# 授权书管理系统 V7 - API 契约设计文档

## 1. API 设计规范

### 1.1 基础规范

| 项目 | 规范 |
|------|------|
| 协议 | HTTP/HTTPS |
| 数据格式 | JSON |
| 字符集 | UTF-8 |
| 日期格式 | yyyy-MM-dd HH:mm:ss |
| 基础路径 | /api/v1 |
| 版本策略 | URL 路径版本 |

### 1.2 请求规范

| 项目 | 规范 |
|------|------|
| GET 参数 | Query String |
| POST/PUT/DELETE 参数 | JSON Body |
| 必填参数 | 文档标注，后端校验 |
| 数组参数 | JSON 数组格式 |

### 1.3 响应规范

**通用响应结构**:
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**分页响应结构**:
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

**错误响应结构**:
```json
{
  "code": 1001,
  "message": "授权书名称已存在",
  "data": null
}
```

### 1.4 HTTP 状态码使用

| 状态码 | 使用场景 |
|--------|----------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 2. 授权书管理 API

### 2.1 查询授权书列表

**接口路径**: `GET /api/v1/auth-letters`

**功能说明**: 分页查询授权书列表，支持多条件筛选

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| name | String | 否 | Query | 授权书名称（模糊查询） |
| authObjectLevel | String | 否 | Query | 授权对象层级（逗号分隔编码） |
| applicableRegion | String | 否 | Query | 适用区域（逗号分隔编码） |
| authPublishLevel | String | 否 | Query | 授权发布层级（逗号分隔编码） |
| authPublishOrg | String | 否 | Query | 授权发布组织（逗号分隔编码） |
| publishYear | Integer | 否 | Query | 发布年份 |
| status | String | 否 | Query | 状态：DRAFT/PUBLISHED/INVALID |
| pageNum | Integer | 是 | Query | 页码，从1开始 |
| pageSize | Integer | 是 | Query | 每页条数：10/30/50 |

**请求示例**:
```
GET /api/v1/auth-letters?name=销售&status=DRAFT&pageNum=1&pageSize=10
```

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 授权书列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 授权书ID |
| name | String | 授权书名称 |
| status | String | 状态编码 |
| statusName | String | 状态名称 |
| authObjectLevel | String | 授权对象层级（逗号分隔名称） |
| applicableRegion | String | 适用区域（路径拼接名称） |
| authPublishLevel | String | 授权发布层级（逗号分隔名称） |
| authPublishOrg | String | 授权发布组织（路径拼接名称） |
| publishYear | Integer | 发布年份 |
| createdBy | String | 创建人账号 |
| createdByName | String | 创建人姓名 |
| createdTime | String | 创建时间 |

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
        "authObjectLevel": "公司,BU",
        "applicableRegion": "机关-地区部1-代表处1",
        "authPublishLevel": "公司",
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

### 2.2 获取授权书详情

**接口路径**: `GET /api/v1/auth-letters/{id}`

**功能说明**: 根据ID获取授权书完整信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**请求示例**:
```
GET /api/v1/auth-letters/1
```

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 授权书ID |
| name | String | 授权书名称 |
| authObjectLevel | Array[String] | 授权对象层级编码数组 |
| authObjectLevelNames | Array[String] | 授权对象层级名称数组 |
| applicableRegion | Array[String] | 适用区域编码数组 |
| applicableRegionNames | Array[String] | 适用区域路径名称数组 |
| authPublishLevel | Array[String] | 授权发布层级编码数组 |
| authPublishLevelNames | Array[String] | 授权发布层级名称数组 |
| authPublishOrg | Array[String] | 授权发布组织编码数组 |
| authPublishOrgNames | Array[String] | 授权发布组织路径名称数组 |
| publishYear | Integer | 发布年份 |
| summary | String | 内容摘要 |
| status | String | 状态编码 |
| statusName | String | 状态名称 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |
| updatedBy | String | 更新人 |
| updatedTime | String | 更新时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "销售授权书2024",
    "authObjectLevel": ["LEVEL1", "LEVEL2"],
    "authObjectLevelNames": ["公司", "BU"],
    "applicableRegion": ["1", "1-1", "1-1-1"],
    "applicableRegionNames": ["机关", "地区部1", "代表处1"],
    "authPublishLevel": ["LEVEL1"],
    "authPublishLevelNames": ["公司"],
    "authPublishOrg": ["1", "1-1"],
    "authPublishOrgNames": ["机关", "地区部1"],
    "publishYear": 2024,
    "summary": "授权书内容摘要...",
    "status": "DRAFT",
    "statusName": "草稿",
    "createdBy": "admin",
    "createdTime": "2024-01-01 12:00:00",
    "updatedBy": "admin",
    "updatedTime": "2024-01-02 12:00:00"
  }
}
```

---

### 2.3 创建授权书

**接口路径**: `POST /api/v1/auth-letters`

**功能说明**: 创建新的授权书，状态默认为草稿

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 授权书名称，最大200字符 |
| authObjectLevel | Array[String] | 是 | 授权对象层级编码数组 |
| applicableRegion | Array[String] | 是 | 适用区域编码数组 |
| authPublishLevel | Array[String] | 是 | 授权发布层级编码数组 |
| authPublishOrg | Array[String] | 是 | 授权发布组织编码数组 |
| publishYear | Integer | 是 | 发布年份 |
| summary | String | 是 | 内容摘要，最大4000字符 |

**请求示例**:
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

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 新创建的授权书ID |

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
| 错误码 | 说明 |
|--------|------|
| 1001 | 授权书名称已存在 |

---

### 2.4 更新授权书

**接口路径**: `PUT /api/v1/auth-letters/{id}`

**功能说明**: 更新授权书基本信息（仅草稿状态可更新）

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
| 错误码 | 说明 |
|--------|------|
| 1002 | 授权书状态不允许此操作 |
| 1001 | 授权书名称已存在（修改名称时） |

---

### 2.5 发布授权书

**接口路径**: `POST /api/v1/auth-letters/{id}/publish`

**功能说明**: 将草稿状态的授权书发布

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**请求示例**:
```
POST /api/v1/auth-letters/1/publish
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success"
}
```

**错误码**:
| 错误码 | 说明 |
|--------|------|
| 1002 | 授权书状态不允许此操作（非草稿状态） |
| 1003 | 授权书信息不完整，无法发布 |

---

### 2.6 失效授权书

**接口路径**: `POST /api/v1/auth-letters/{id}/invalidate`

**功能说明**: 将已发布状态的授权书失效

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
| 错误码 | 说明 |
|--------|------|
| 1002 | 授权书状态不允许此操作（非已发布状态） |

---

### 2.7 删除授权书

**接口路径**: `DELETE /api/v1/auth-letters/{id}`

**功能说明**: 删除授权书（逻辑删除，所有状态均可删除）

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

### 2.8 批量操作授权书

**接口路径**: `POST /api/v1/auth-letters/batch`

**功能说明**: 批量执行发布、失效、删除操作

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Array[Long] | 是 | 授权书ID数组 |
| operation | String | 是 | 操作类型：PUBLISH/INVALIDATE/DELETE |

**请求示例**:
```json
{
  "ids": [1, 2, 3],
  "operation": "PUBLISH"
}
```

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| successCount | Integer | 成功数量 |
| failCount | Integer | 失败数量 |
| failDetails | Array | 失败详情列表 |

**failDetails 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 失败的授权书ID |
| reason | String | 失败原因 |

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

## 3. 附件管理 API

### 3.1 查询附件列表

**接口路径**: `GET /api/v1/auth-letters/{authLetterId}/attachments`

**功能说明**: 分页查询授权书的附件列表

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| pageNum | Integer | 是 | Query | 页码 |
| pageSize | Integer | 是 | Query | 每页条数 |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 附件列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 附件ID |
| docId | String | 文档ID |
| docName | String | 文档名称 |
| docType | String | 文档类型编码 |
| docTypeName | String | 文档类型名称 |
| isEncrypted | Integer | 是否加密：0-否，1-是 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |
| updatedBy | String | 更新人 |
| updatedTime | String | 更新时间 |

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

### 3.2 上传附件

**接口路径**: `POST /api/v1/auth-letters/{authLetterId}/attachments`

**功能说明**: 上传附件到授权书（仅记录docId等数据）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| docId | String | 是 | 文档ID |
| docName | String | 是 | 文档名称 |
| docType | String | 是 | 文档类型编码 |

**请求示例**:
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

### 3.3 下载附件

**接口路径**: `GET /api/v1/auth-letters/{authLetterId}/attachments/{id}/download`

**功能说明**: 下载指定附件文件

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 附件ID |

**响应**: 文件流（Content-Disposition 响应头）

---

### 3.4 删除附件

**接口路径**: `DELETE /api/v1/auth-letters/{authLetterId}/attachments/{id}`

**功能说明**: 删除指定附件

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

### 3.5 批量删除附件

**接口路径**: `POST /api/v1/auth-letters/{authLetterId}/attachments/batch-delete`

**功能说明**: 批量删除附件

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Array[Long] | 是 | 附件ID数组 |

**请求示例**:
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

## 4. 场景管理 API

### 4.1 查询场景列表

**接口路径**: `GET /api/v1/auth-letters/{authLetterId}/scenes`

**功能说明**: 分页查询授权书的场景列表

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| pageNum | Integer | 是 | Query | 页码 |
| pageSize | Integer | 是 | Query | 每页条数 |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 场景列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 场景ID |
| sceneName | String | 场景名称 |
| industry | Array[String] | 产业编码数组 |
| industryName | String | 产业路径名称 |
| businessScene | String | 业务场景编码 |
| businessSceneName | String | 业务场景名称 |
| decisionLevel | String | 决策层级编码 |
| decisionLevelName | String | 决策层级名称 |
| specificRule | String | 具体规则描述 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |
| updatedBy | String | 更新人 |
| updatedTime | String | 更新时间 |

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
        "industry": ["LV1", "LV1-LV2"],
        "industryName": "产业1-产业2",
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

### 4.2 获取场景详情

**接口路径**: `GET /api/v1/auth-letters/{authLetterId}/scenes/{id}`

**功能说明**: 获取场景完整信息，包含规则配置和问卷配置

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| id | Long | 是 | 场景ID |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 场景ID |
| sceneName | String | 场景名称 |
| industry | Array[String] | 产业编码数组 |
| businessScene | String | 业务场景编码 |
| decisionLevel | String | 决策层级编码 |
| specificRule | String | 具体规则描述 |
| rules | Array | 规则组配置列表 |
| questionnaires | Array | 问卷配置列表 |

**rules 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 规则组ID |
| ruleName | String | 规则名称 |
| logicType | String | 逻辑类型：AND/OR |
| conditions | Array | 条件列表（含嵌套） |

**conditions 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 条件ID |
| fieldCode | String | 规则字段编码 |
| fieldName | String | 规则字段名称 |
| operator | String | 运算符编码 |
| operatorSymbol | String | 运算符符号 |
| compareType | String | 对比类型编码 |
| compareTypeDesc | String | 对比类型描述 |
| compareFieldCode | String | 对比字段编码（FIELD类型时） |
| compareFieldName | String | 对比字段名称 |
| compareValue | String | 对比值 |
| compareUnit | String | 计量单位编码 |
| compareUnitName | String | 计量单位名称 |
| logicType | String | 与上一条件连接逻辑 |
| isGroup | Boolean | 是否为条件组 |
| groupLogicType | String | 条件组内部逻辑（条件组时） |
| sortOrder | Integer | 排序号 |
| children | Array | 子条件列表（条件组时） |

**questionnaires 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 关联ID |
| questionId | Long | 题目ID |
| questionCode | String | 题目编号 |
| questionText | String | 题目内容 |
| correctAnswerId | Long | 正确答案ID |
| correctAnswerCode | String | 正确答案编号 |
| correctAnswerText | String | 正确答案内容 |
| sortOrder | Integer | 排序号 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "sceneName": "销售场景1",
    "industry": ["LV1", "LV1-LV2"],
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
            "operator": "GE",
            "operatorSymbol": ">=",
            "compareType": "NUMBER",
            "compareValue": "1000000",
            "compareUnit": "CNY",
            "compareUnitName": "人民币",
            "logicType": "AND",
            "isGroup": false,
            "sortOrder": 1
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
                "operator": "EQ",
                "operatorSymbol": "=",
                "compareType": "TEXT",
                "compareValue": "产业1",
                "logicType": "OR",
                "isGroup": false,
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

### 4.3 创建场景

**接口路径**: `POST /api/v1/auth-letters/{authLetterId}/scenes`

**功能说明**: 创建新场景

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| sceneName | String | 是 | 场景名称 |
| industry | Array[String] | 是 | 产业编码数组 |
| businessScene | String | 是 | 业务场景编码 |
| decisionLevel | String | 是 | 决策层级编码 |
| specificRule | String | 否 | 具体规则描述 |
| rules | Array | 否 | 规则组配置列表 |
| questionnaires | Array | 否 | 问卷配置列表 |

**rules 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ruleName | String | 否 | 规则名称 |
| logicType | String | 是 | 逻辑类型：AND/OR |
| conditions | Array | 是 | 条件列表 |

**conditions 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fieldCode | String | 条件时必填 | 规则字段编码 |
| operator | String | 条件时必填 | 运算符编码 |
| compareType | String | 条件时必填 | 对比类型编码 |
| compareFieldCode | String | FIELD时必填 | 对比字段编码 |
| compareValue | String | NUMBER/TEXT时必填 | 对比值 |
| compareUnit | String | 否 | 计量单位编码 |
| logicType | String | 是 | 与上一条件连接逻辑 |
| isGroup | Boolean | 是 | 是否为条件组 |
| groupLogicType | String | 条件组时必填 | 条件组内部逻辑 |
| sortOrder | Integer | 是 | 排序号 |
| children | Array | 条件组时必填 | 子条件列表 |

**questionnaires 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| questionId | Long | 是 | 题目ID |
| correctAnswerId | Long | 是 | 正确答案ID |
| sortOrder | Integer | 是 | 排序号 |

**请求示例**:
```json
{
  "sceneName": "销售场景1",
  "industry": ["LV1", "LV1-LV2"],
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
          "operator": "GE",
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
| 错误码 | 说明 |
|--------|------|
| 1004 | 场景名称已存在 |
| 1005 | 规则和问卷至少需要配置一项 |

---

### 4.4 更新场景

**接口路径**: `PUT /api/v1/auth-letters/{authLetterId}/scenes/{id}`

**功能说明**: 更新场景配置

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

### 4.5 删除场景

**接口路径**: `DELETE /api/v1/auth-letters/{authLetterId}/scenes/{id}`

**功能说明**: 删除指定场景

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

### 4.6 批量删除场景

**接口路径**: `POST /api/v1/auth-letters/{authLetterId}/scenes/batch-delete`

**功能说明**: 批量删除场景

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Array[Long] | 是 | 场景ID数组 |

**请求示例**:
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

## 5. 规则参数配置 API

### 5.1 查询规则参数列表

**接口路径**: `GET /api/v1/rule-params`

**功能说明**: 分页查询规则参数配置

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| name | String | 否 | Query | 名称（模糊查询） |
| nameEn | String | 否 | Query | 英文名（模糊查询） |
| status | String | 否 | Query | 状态：ACTIVE/INACTIVE |
| pageNum | Integer | 是 | Query | 页码 |
| pageSize | Integer | 是 | Query | 每页条数 |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 规则参数列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 规则参数ID |
| name | String | 名称 |
| nameEn | String | 英文名 |
| businessObjects | Array | 业务对象配置列表 |
| status | String | 状态编码 |
| statusName | String | 状态名称 |
| dataType | String | 数据类型编码 |
| dataTypeName | String | 数据类型名称 |
| referenceFieldId | Long | 关联字段ID |
| referenceFieldName | String | 关联字段名称 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |
| updatedBy | String | 更新人 |
| updatedTime | String | 更新时间 |

**businessObjects 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| businessObject | String | 业务对象名称 |
| valueLogic | String | 取值逻辑（JSONPath） |

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
        "referenceFieldId": null,
        "referenceFieldName": null,
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

### 5.2 获取规则参数详情

**接口路径**: `GET /api/v1/rule-params/{id}`

**功能说明**: 获取规则参数完整信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 规则参数ID |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 规则参数ID |
| name | String | 名称 |
| nameEn | String | 英文名 |
| businessObjects | Array | 业务对象配置列表 |
| status | String | 状态编码 |
| dataType | String | 数据类型编码 |
| referenceFieldId | Long | 关联字段ID（FIELD类型时） |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |

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

### 5.3 创建规则参数

**接口路径**: `POST /api/v1/rule-params`

**功能说明**: 创建新的规则参数配置

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 名称，最大100字符 |
| nameEn | String | 是 | 英文名，最大100字符 |
| businessObjects | Array | 是 | 业务对象配置列表 |
| status | String | 是 | 状态：ACTIVE/INACTIVE |
| dataType | String | 是 | 数据类型：TEXT/NUMBER/FIELD/RATIO |
| referenceFieldId | Long | FIELD时必填 | 关联字段ID |

**请求示例**:
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
| 错误码 | 说明 |
|--------|------|
| 1006 | 规则参数名称已存在 |

---

### 5.4 更新规则参数

**接口路径**: `PUT /api/v1/rule-params/{id}`

**功能说明**: 更新规则参数配置

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

### 5.5 批量更新规则参数状态

**接口路径**: `POST /api/v1/rule-params/batch-status`

**功能说明**: 批量更新规则参数状态（生效/失效）

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Array[Long] | 是 | 规则参数ID数组 |
| status | String | 是 | 目标状态：ACTIVE/INACTIVE |

**请求示例**:
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

### 5.6 获取规则参数下拉列表

**接口路径**: `GET /api/v1/rule-params/options`

**功能说明**: 获取规则参数下拉列表（用于规则条件配置）

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| status | String | 否 | Query | 状态过滤，默认ACTIVE |

**响应数据结构**: Array

**元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String | 字段编码（nameEn） |
| name | String | 字段名称 |
| dataType | String | 数据类型 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"code": "amount", "name": "金额", "dataType": "NUMBER"},
    {"code": "region", "name": "区域", "dataType": "TEXT"},
    {"code": "industry", "name": "产业", "dataType": "TEXT"}
  ]
}
```

---

## 6. 问卷管理 API

### 6.1 查询问卷题目列表

**接口路径**: `GET /api/v1/questionnaire-questions`

**功能说明**: 分页查询问卷题目配置

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| questionCode | String | 否 | Query | 题目编号（模糊查询） |
| questionText | String | 否 | Query | 题目内容（模糊查询） |
| language | String | 否 | Query | 语言：ZH/EN |
| createdBy | String | 否 | Query | 创建人（模糊查询） |
| createdTimeFrom | String | 否 | Query | 创建时间起始 |
| createdTimeTo | String | 否 | Query | 创建时间截止 |
| updatedBy | String | 否 | Query | 更新人（模糊查询） |
| updatedTimeFrom | String | 否 | Query | 更新时间起始 |
| updatedTimeTo | String | 否 | Query | 更新时间截止 |
| pageNum | Integer | 是 | Query | 页码 |
| pageSize | Integer | 是 | Query | 每页条数 |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 题目列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 题目ID |
| questionCode | String | 题目编号 |
| questionText | String | 题目内容 |
| language | String | 语言 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |
| updatedBy | String | 更新人 |
| updatedTime | String | 更新时间 |

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

### 6.2 获取问卷题目详情

**接口路径**: `GET /api/v1/questionnaire-questions/{id}`

**功能说明**: 获取问卷题目完整信息，包含多语言题目内容和可选答案

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 题目ID |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 题目ID |
| questionCode | String | 题目编号 |
| questionTexts | Array | 多语言题目内容列表 |
| answers | Array | 可选答案列表 |

**questionTexts 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 文本ID |
| questionText | String | 题目内容 |
| language | String | 语言 |
| createdBy | String | 创建人 |
| createdTime | String | 创建时间 |

**answers 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 答案ID |
| answerCode | String | 答案编号 |
| answerTexts | Array | 多语言答案内容列表 |
| sortOrder | Integer | 排序号 |

**answerTexts 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| answerText | String | 答案内容 |
| language | String | 语言 |

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

### 6.3 创建问卷题目

**接口路径**: `POST /api/v1/questionnaire-questions`

**功能说明**: 创建新的问卷题目，题目编号自动生成

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| questionTexts | Array | 是 | 多语言题目内容列表 |
| answers | Array | 是 | 可选答案列表 |

**questionTexts 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| questionText | String | 是 | 题目内容，最大500字符 |
| language | String | 是 | 语言：ZH/EN |

**answers 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| answerTexts | Array | 是 | 多语言答案内容列表 |
| sortOrder | Integer | 是 | 排序号，支持题目排序 |

**answerTexts 元素结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| answerText | String | 是 | 答案内容，最大200字符 |
| language | String | 是 | 语言：ZH/EN |

**请求示例**:
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

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 新创建的题目ID |
| questionCode | String | 自动生成的题目编号 |

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

**错误码**:
| 错误码 | 说明 |
|--------|------|
| 1007 | 相同语言只能维护一个题目 |

---

### 6.4 更新问卷题目

**接口路径**: `PUT /api/v1/questionnaire-questions/{id}`

**功能说明**: 更新问卷题目配置

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

### 6.5 删除问卷题目

**接口路径**: `DELETE /api/v1/questionnaire-questions/{id}`

**功能说明**: 删除问卷题目

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

### 6.6 批量删除问卷题目

**接口路径**: `POST /api/v1/questionnaire-questions/batch-delete`

**功能说明**: 批量删除问卷题目

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Array[Long] | 是 | 题目ID数组 |

**请求示例**:
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

### 6.7 获取问卷题目下拉列表

**接口路径**: `GET /api/v1/questionnaire-questions/options`

**功能说明**: 获取问卷题目下拉列表（用于场景配置时选择题目）

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| language | String | 否 | Query | 语言，默认ZH |

**响应数据结构**: Array

**元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| questionId | Long | 题目ID |
| questionCode | String | 题目编号 |
| questionText | String | 题目内容 |
| answers | Array | 答案列表 |

**answers 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| answerId | Long | 答案ID |
| answerCode | String | 答案编号 |
| answerText | String | 答案内容 |

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

## 7. 下拉列表数据 API

### 7.1 获取下拉列表数据

**接口路径**: `GET /api/v1/lookup-values/{typeCode}`

**功能说明**: 根据类型编码获取下拉列表数据

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| typeCode | String | 是 | 类型编码 |

**typeCode 可选值**:

| 编码 | 说明 | 结构 |
|------|------|------|
| AUTH_OBJECT_LEVEL | 授权对象层级 | 平铺列表，多选 |
| AUTH_PUBLISH_LEVEL | 授权发布层级 | 平铺列表，多选 |
| APPLICABLE_REGION | 适用区域 | 三层树形，多选 |
| AUTH_PUBLISH_ORG | 授权发布组织 | 三层树形，多选 |
| INDUSTRY | 产业 | 三层树形（LV1/LV2/LV3），多选 |
| BUSINESS_SCENE | 业务场景 | 平铺列表，单选 |
| DECISION_LEVEL | 决策层级 | 平铺列表，单选 |
| DOC_TYPE | 文档类型 | 平铺列表，单选 |
| OPERATOR | 运算符 | 平铺列表，单选 |
| COMPARE_UNIT | 计量单位 | 平铺列表，单选 |
| LANGUAGE | 语言 | 平铺列表，单选 |
| DATA_TYPE | 数据类型 | 平铺列表，单选 |

**平铺列表响应结构**: Array

**元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String | 值编码 |
| name | String | 值名称 |

**平铺列表响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {"code": "LEVEL1", "name": "公司"},
    {"code": "LEVEL2", "name": "BU"}
  ]
}
```

**树形列表响应结构**: Array

**元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| code | String | 值编码 |
| name | String | 值名称 |
| children | Array | 子节点列表（树形时） |

**树形列表响应示例**:
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
        },
        {
          "code": "1-2",
          "name": "地区部2",
          "children": [
            {"code": "1-2-1", "name": "代表处3"}
          ]
        }
      ]
    }
  ]
}
```

---

## 8. 操作日志 API

### 8.1 查询操作日志

**接口路径**: `GET /api/v1/auth-letters/{authLetterId}/logs`

**功能说明**: 分页查询授权书的操作日志

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |

**请求参数**:

| 参数名 | 类型 | 必填 | 位置 | 说明 |
|--------|------|------|------|------|
| pageNum | Integer | 是 | Query | 页码 |
| pageSize | Integer | 是 | Query | 每页条数 |

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| list | Array | 日志列表 |
| total | Integer | 总记录数 |
| pageNum | Integer | 当前页码 |
| pageSize | Integer | 每页条数 |

**list 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 日志ID |
| operation | String | 操作类型编码 |
| operationName | String | 操作类型名称 |
| operator | String | 操作人账号 |
| operatorName | String | 操作人姓名 |
| operationTime | String | 操作时间 |

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

## 9. 场景匹配 API

### 9.1 场景匹配

**接口路径**: `POST /api/v1/scene-match`

**功能说明**: 根据规则匹配授权场景，返回所有命中的场景

**请求体结构**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| authLetterId | Long | 是 | 授权书ID |
| data | Object | 是 | 规则匹配所需的参数数据 |

**data 字段说明**:
- 参数名对应规则参数的 `nameEn` 字段
- 参数值由调用方根据业务上下文提供
- 参数类型需与规则参数的 `dataType` 匹配

**请求示例**:
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

**响应数据结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| matchedScenes | Array | 命中场景列表 |

**matchedScenes 元素结构**:

| 字段 | 类型 | 说明 |
|------|------|------|
| sceneId | Long | 场景ID |
| sceneName | String | 场景名称 |
| decisionLevel | String | 决策层级编码 |
| decisionLevelName | String | 决策层级名称 |

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

**匹配规则说明**:
1. 命中几个场景返回几个（多场景命中）
2. 返回命中场景的决策层级
3. 不考虑场景之间的关系
4. 无场景命中时返回空数组

---

## 10. 错误码定义

### 10.1 HTTP 错误码

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 10.2 业务错误码

| 错误码 | 说明 | 触发场景 |
|--------|------|----------|
| 1001 | 授权书名称已存在 | 创建/更新授权书时名称重复 |
| 1002 | 授权书状态不允许此操作 | 非草稿状态尝试编辑，非已发布状态尝试失效 |
| 1003 | 授权书信息不完整，无法发布 | 发布时缺少必填信息或场景配置 |
| 1004 | 场景名称已存在 | 同一授权书下场景名称重复 |
| 1005 | 规则和问卷至少需要配置一项 | 创建/更新场景时未配置规则和问卷 |
| 1006 | 规则参数名称已存在 | 规则参数名称或英文名重复 |
| 1007 | 相同语言只能维护一个题目 | 问卷题目同语言重复 |
| 1008 | 相同语言只能维护一个答案 | 问卷答案同语言重复 |

---

## 11. API 接口汇总

| 模块 | 接口路径 | 方法 | 说明 |
|------|----------|------|------|
| 授权书管理 | /api/v1/auth-letters | GET | 查询授权书列表 |
| 授权书管理 | /api/v1/auth-letters/{id} | GET | 获取授权书详情 |
| 授权书管理 | /api/v1/auth-letters | POST | 创建授权书 |
| 授权书管理 | /api/v1/auth-letters/{id} | PUT | 更新授权书 |
| 授权书管理 | /api/v1/auth-letters/{id}/publish | POST | 发布授权书 |
| 授权书管理 | /api/v1/auth-letters/{id}/invalidate | POST | 失效授权书 |
| 授权书管理 | /api/v1/auth-letters/{id} | DELETE | 删除授权书 |
| 授权书管理 | /api/v1/auth-letters/batch | POST | 批量操作授权书 |
| 附件管理 | /api/v1/auth-letters/{id}/attachments | GET | 查询附件列表 |
| 附件管理 | /api/v1/auth-letters/{id}/attachments | POST | 上传附件 |
| 附件管理 | /api/v1/auth-letters/{id}/attachments/{attId}/download | GET | 下载附件 |
| 附件管理 | /api/v1/auth-letters/{id}/attachments/{attId} | DELETE | 删除附件 |
| 附件管理 | /api/v1/auth-letters/{id}/attachments/batch-delete | POST | 批量删除附件 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes | GET | 查询场景列表 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes/{sceneId} | GET | 获取场景详情 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes | POST | 创建场景 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes/{sceneId} | PUT | 更新场景 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes/{sceneId} | DELETE | 删除场景 |
| 场景管理 | /api/v1/auth-letters/{id}/scenes/batch-delete | POST | 批量删除场景 |
| 规则参数 | /api/v1/rule-params | GET | 查询规则参数列表 |
| 规则参数 | /api/v1/rule-params/{id} | GET | 获取规则参数详情 |
| 规则参数 | /api/v1/rule-params | POST | 创建规则参数 |
| 规则参数 | /api/v1/rule-params/{id} | PUT | 更新规则参数 |
| 规则参数 | /api/v1/rule-params/batch-status | POST | 批量更新状态 |
| 规则参数 | /api/v1/rule-params/options | GET | 获取规则参数下拉列表 |
| 问卷管理 | /api/v1/questionnaire-questions | GET | 查询问卷题目列表 |
| 问卷管理 | /api/v1/questionnaire-questions/{id} | GET | 获取问卷题目详情 |
| 问卷管理 | /api/v1/questionnaire-questions | POST | 创建问卷题目 |
| 问卷管理 | /api/v1/questionnaire-questions/{id} | PUT | 更新问卷题目 |
| 问卷管理 | /api/v1/questionnaire-questions/{id} | DELETE | 删除问卷题目 |
| 问卷管理 | /api/v1/questionnaire-questions/batch-delete | POST | 批量删除题目 |
| 问卷管理 | /api/v1/questionnaire-questions/options | GET | 获取题目下拉列表 |
| 下拉数据 | /api/v1/lookup-values/{typeCode} | GET | 获取下拉列表数据 |
| 操作日志 | /api/v1/auth-letters/{id}/logs | GET | 查询操作日志 |
| 场景匹配 | /api/v1/scene-match | POST | 场景匹配 |

**总计**: 34 个接口

---

**文档版本**: v1.0
**创建日期**: 2026-03-28
**创建人**: Architect Agent
**最后更新**: 2026-03-28