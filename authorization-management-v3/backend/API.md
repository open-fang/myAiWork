# 授权书管理系统 API 文档

## 基础信息

- **Base URL**: `/api`
- **Content-Type**: `application/json`
- **响应格式**: JSON

## 通用响应结构

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

错误响应:
```json
{
  "success": false,
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

---

## 1. 授权书管理 API

### 1.1 分页查询授权书列表

**GET** `/api/auth-letters`

**请求参数 (Query String)**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 授权书名称（模糊查询） |
| authTargetLevel | List<String> | 否 | 授权对象层级编码列表 |
| applicableRegion | List<String> | 否 | 适用区域编码列表 |
| authPublishLevel | List<String> | 否 | 授权发布层级编码列表 |
| authPublishOrg | List<String> | 否 | 授权发布组织编码列表 |
| publishYear | Integer | 否 | 发布年份 |
| status | String | 否 | 状态: DRAFT/PUBLISHED/EXPIRED |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "code": "AUTH20240310001",
        "name": "2024年华东区销售授权书",
        "status": "PUBLISHED",
        "statusText": "已发布",
        "authTargetLevel": ["ORGANIZATION", "REGIONAL_DEPT"],
        "authTargetLevelText": ["机关", "地区部"],
        "applicableRegion": ["EAST"],
        "applicableRegionText": ["华东"],
        "authPublishLevel": ["REGIONAL_DEPT"],
        "authPublishLevelText": ["地区部"],
        "authPublishOrg": ["ORG002"],
        "authPublishOrgText": ["华东区"],
        "publishYear": 2024,
        "createdBy": "admin",
        "createdAt": "2024-03-10T10:30:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 1.2 获取授权书详情

**GET** `/api/auth-letters/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": {
    "id": 1,
    "code": "AUTH20240310001",
    "name": "2024年华东区销售授权书",
    "status": "PUBLISHED",
    "statusText": "已发布",
    "authPublishLevel": ["REGIONAL_DEPT"],
    "authPublishLevelText": ["地区部"],
    "authPublishOrg": ["ORG002"],
    "authPublishOrgText": ["华东区"],
    "authTargetLevel": ["ORGANIZATION", "REGIONAL_DEPT"],
    "authTargetLevelText": ["机关", "地区部"],
    "applicableRegion": ["EAST"],
    "applicableRegionText": ["华东"],
    "publishYear": 2024,
    "contentSummary": "授权书内容摘要...",
    "createdBy": "admin",
    "createdAt": "2024-03-10 10:30:00",
    "updatedBy": null,
    "updatedAt": null,
    "publishedAt": "2024-03-10 11:00:00",
    "scenes": [
      {
        "id": 1,
        "sceneName": "销售场景",
        "industry": ["IND001", "IND002"],
        "industryText": "ICT、消费者",
        "businessScenario": "设备销售",
        "businessScenarioCode": "BS001",
        "decisionLevel": "地区部",
        "decisionLevelCode": "REGIONAL_DEPT",
        "ruleDetail": "单笔金额不超过500万的销售订单可自主决策",
        "createdBy": "admin",
        "createdAt": "2024-03-10 10:30:00",
        "updatedBy": null,
        "updatedAt": null,
        "conditions": [],
        "conditionLogics": []
      }
    ]
  }
}
```

---

### 1.3 创建授权书

**POST** `/api/auth-letters`

**请求体**:
```json
{
  "name": "2024年华东区销售授权书",
  "authPublishLevel": ["REGIONAL_DEPT"],
  "authPublishOrg": ["ORG002"],
  "authTargetLevel": ["ORGANIZATION", "REGIONAL_DEPT"],
  "applicableRegion": ["EAST"],
  "publishYear": 2024,
  "contentSummary": "授权书内容摘要...",
  "scenes": []
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "创建成功",
  "data": 1
}
```

---

### 1.4 更新授权书

**PUT** `/api/auth-letters/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**请求体**:
```json
{
  "name": "2024年华东区销售授权书（修改版）",
  "authPublishLevel": ["REGIONAL_DEPT"],
  "authPublishOrg": ["ORG002"],
  "authTargetLevel": ["ORGANIZATION", "REGIONAL_DEPT"],
  "applicableRegion": ["EAST"],
  "publishYear": 2024,
  "contentSummary": "更新后的内容摘要...",
  "scenes": []
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功"
}
```

---

### 1.5 删除授权书

**DELETE** `/api/auth-letters/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "删除成功"
}
```

**注意事项**: 只能删除草稿状态(DRAFT)的授权书

---

### 1.6 发布授权书

**PUT** `/api/auth-letters/{id}/publish`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 授权书ID |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "发布成功"
}
```

**注意事项**: 只能发布草稿状态(DRAFT)的授权书

---

### 1.7 批量发布授权书

**PUT** `/api/auth-letters/batch/publish`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "成功发布3条数据",
  "data": 3
}
```

---

### 1.8 批量失效授权书

**PUT** `/api/auth-letters/batch/expire`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "成功失效3条数据",
  "data": 3
}
```

---

### 1.9 批量删除授权书

**DELETE** `/api/auth-letters/batch`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "成功删除3条数据",
  "data": 3
}
```

---

## 2. 规则参数配置 API

### 2.1 分页查询规则参数列表

**GET** `/api/rule-params`

**请求参数 (Query String)**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 否 | 名称（模糊查询） |
| nameEn | String | 否 | 名称英文（模糊查询） |
| status | String | 否 | 状态: ACTIVE/INACTIVE |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": {
    "list": [
      {
        "id": 1,
        "name": "金额",
        "nameEn": "amount",
        "status": "ACTIVE",
        "dataType": "NUMBER",
        "businessMappings": "[{\"businessObject\":\"CONTRACT\",\"fieldPath\":\"contractAmount\"}]",
        "createdBy": "admin",
        "createdAt": "2024-03-10T10:30:00",
        "updatedBy": null,
        "updatedAt": null
      }
    ],
    "total": 10,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 2.2 获取规则参数详情

**GET** `/api/rule-params/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 规则参数ID |

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": {
    "id": 1,
    "name": "金额",
    "nameEn": "amount",
    "status": "ACTIVE",
    "dataType": "NUMBER",
    "businessMappings": "[{\"businessObject\":\"CONTRACT\",\"fieldPath\":\"contractAmount\"}]",
    "createdBy": "admin",
    "createdAt": "2024-03-10T10:30:00",
    "updatedBy": null,
    "updatedAt": null
  }
}
```

---

### 2.3 创建规则参数

**POST** `/api/rule-params`

**请求体**:
```json
{
  "name": "金额",
  "nameEn": "amount",
  "status": "ACTIVE",
  "dataType": "NUMBER",
  "businessMappings": "[{\"businessObject\":\"CONTRACT\",\"fieldPath\":\"contractAmount\"}]"
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "创建成功",
  "data": 1
}
```

---

### 2.4 更新规则参数

**PUT** `/api/rule-params/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 规则参数ID |

**请求体**:
```json
{
  "name": "金额",
  "nameEn": "amount",
  "status": "ACTIVE",
  "dataType": "NUMBER",
  "businessMappings": "[{\"businessObject\":\"CONTRACT\",\"fieldPath\":\"contractAmount\"}]"
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "更新成功"
}
```

---

### 2.5 批量激活规则参数

**PUT** `/api/rule-params/batch/activate`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "成功激活3条数据",
  "data": 3
}
```

---

### 2.6 批量失效规则参数

**PUT** `/api/rule-params/batch/deactivate`

**请求体**:
```json
{
  "ids": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "message": "成功失效3条数据",
  "data": 3
}
```

---

## 3. Lookup API（下拉选项）

### 3.1 获取通用下拉选项

**GET** `/api/lookup/{code}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| code | String | 是 | 配置项编码 |

**支持的配置项编码**:
- `authTargetLevel` - 授权对象层级
- `authPublishLevel` - 授权发布层级
- `applicableRegion` - 适用区域

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    { "code": "ORGANIZATION", "name": "机关" },
    { "code": "REGIONAL_DEPT", "name": "地区部" },
    { "code": "REPRESENTATIVE_OFFICE", "name": "代表处" },
    { "code": "OFFICE", "name": "办事处" }
  ]
}
```

---

### 3.2 获取组织树

**GET** `/api/lookup/org/tree`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    {
      "code": "ORG001",
      "name": "总部",
      "level": 0,
      "children": [
        {
          "code": "ORG002",
          "name": "华东区",
          "level": 1,
          "children": [
            { "code": "ORG003", "name": "上海办事处", "level": 2, "children": [] },
            { "code": "ORG004", "name": "杭州办事处", "level": 2, "children": [] }
          ]
        }
      ]
    }
  ]
}
```

---

### 3.3 获取产业树

**GET** `/api/lookup/industry/tree`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    {
      "code": "IND001",
      "name": "ICT",
      "level": 0,
      "children": [
        { "code": "IND001_1", "name": "运营商", "level": 1, "children": [] },
        { "code": "IND001_2", "name": "企业", "level": 1, "children": [] }
      ]
    },
    {
      "code": "IND002",
      "name": "消费者",
      "level": 0,
      "children": [
        { "code": "IND002_1", "name": "手机", "level": 1, "children": [] },
        { "code": "IND002_2", "name": "PC", "level": 1, "children": [] }
      ]
    }
  ]
}
```

---

### 3.4 获取业务场景选项

**GET** `/api/lookup/business-scenarios`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    { "code": "BS001", "name": "设备销售" },
    { "code": "BS002", "name": "软件销售" },
    { "code": "BS003", "name": "服务销售" },
    { "code": "BS004", "name": "物料采购" }
  ]
}
```

---

### 3.5 获取决策层级选项

**GET** `/api/lookup/decision-levels`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    { "code": "ORGANIZATION", "name": "机关" },
    { "code": "REGIONAL_DEPT", "name": "地区部" },
    { "code": "REPRESENTATIVE_OFFICE", "name": "代表处" },
    { "code": "OFFICE", "name": "办事处" }
  ]
}
```

---

### 3.6 获取业务对象选项

**GET** `/api/lookup/business-objects`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    { "code": "CONTRACT", "name": "合同" },
    { "code": "ORDER", "name": "订单" },
    { "code": "PROJECT", "name": "项目" },
    { "code": "CUSTOMER", "name": "客户" },
    { "code": "PRODUCT", "name": "产品" }
  ]
}
```

---

### 3.7 获取规则字段选项

**GET** `/api/lookup/rule-fields`

**响应示例**:
```json
{
  "success": true,
  "code": 200,
  "data": [
    { "code": "amount", "name": "金额" },
    { "code": "quantity", "name": "数量" },
    { "code": "customer_level", "name": "客户等级" },
    { "code": "product_type", "name": "产品类型" }
  ]
}
```

---

## 4. 数据字典

### 4.1 授权书状态 (AuthLetterStatus)

| 编码 | 名称 | 说明 |
|------|------|------|
| DRAFT | 草稿 | 新建未发布状态 |
| PUBLISHED | 已发布 | 已发布生效状态 |
| EXPIRED | 已失效 | 过期失效状态 |

### 4.2 规则参数状态

| 编码 | 名称 | 说明 |
|------|------|------|
| ACTIVE | 生效 | 参数有效可用 |
| INACTIVE | 失效 | 参数无效不可用 |

### 4.3 规则参数数据类型 (DataType)

| 编码 | 名称 | 说明 |
|------|------|------|
| TEXT | 文本 | 文本类型 |
| NUMBER | 数值 | 数值类型 |
| COMPARE_FIELD | 比对字段 | 与其他字段比对 |
| RATIO | 比率 | 比率/百分比类型 |

### 4.4 条件操作符 (Operator)

| 编码 | 名称 |
|------|------|
| EQ | 等于 |
| NE | 不等于 |
| GT | 大于 |
| GE | 大于等于 |
| LT | 小于 |
| LE | 小于等于 |
| IN | 包含 |
| NOT_IN | 不包含 |

### 4.5 逻辑操作符 (LogicOperator)

| 编码 | 名称 |
|------|------|
| AND | 且 |
| OR | 或 |