import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 获取下拉列表数据
export function getLookupValues(typeCode) {
  return request({
    url: `${baseURL}/lookup-values/${typeCode}`,
    method: 'get'
  })
}

// 获取多个下拉列表数据（并发请求）
export function getMultipleLookupValues(typeCodes) {
  return Promise.all(
    typeCodes.map(code => getLookupValues(code))
  )
}

// 下拉类型编码常量
export const LOOKUP_TYPES = {
  AUTH_OBJECT_LEVEL: 'AUTH_OBJECT_LEVEL',
  AUTH_PUBLISH_LEVEL: 'AUTH_PUBLISH_LEVEL',
  APPLICABLE_REGION: 'APPLICABLE_REGION',
  AUTH_PUBLISH_ORG: 'AUTH_PUBLISH_ORG',
  INDUSTRY: 'INDUSTRY',
  BUSINESS_SCENE: 'BUSINESS_SCENE',
  DECISION_LEVEL: 'DECISION_LEVEL',
  DOC_TYPE: 'DOC_TYPE',
  OPERATOR: 'OPERATOR',
  COMPARE_UNIT: 'COMPARE_UNIT',
  COMPARE_TYPE: 'COMPARE_TYPE',
  LANGUAGE: 'LANGUAGE',
  DATA_TYPE: 'DATA_TYPE',
  STATUS: 'STATUS',
  IS_ACTIVE: 'IS_ACTIVE'
}