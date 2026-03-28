import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 查询规则参数列表
export function getRuleParamList(params) {
  return request({
    url: `${baseURL}/rule-params`,
    method: 'get',
    params
  })
}

// 获取规则参数详情
export function getRuleParamDetail(id) {
  return request({
    url: `${baseURL}/rule-params/${id}`,
    method: 'get'
  })
}

// 创建规则参数
export function createRuleParam(data) {
  return request({
    url: `${baseURL}/rule-params`,
    method: 'post',
    data
  })
}

// 更新规则参数
export function updateRuleParam(id, data) {
  return request({
    url: `${baseURL}/rule-params/${id}`,
    method: 'put',
    data
  })
}

// 批量更新状态
export function batchUpdateStatus(ids, status) {
  return request({
    url: `${baseURL}/rule-params/batch-status`,
    method: 'post',
    data: { ids, status }
  })
}

// 获取规则参数下拉列表
export function getRuleParamOptions(status = 'ACTIVE') {
  return request({
    url: `${baseURL}/rule-params/options`,
    method: 'get',
    params: { status }
  })
}