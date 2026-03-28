import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 查询授权书列表
export function getAuthLetterList(params) {
  return request({
    url: `${baseURL}/auth-letters`,
    method: 'get',
    params
  })
}

// 获取授权书详情
export function getAuthLetterDetail(id) {
  return request({
    url: `${baseURL}/auth-letters/${id}`,
    method: 'get'
  })
}

// 创建授权书
export function createAuthLetter(data) {
  return request({
    url: `${baseURL}/auth-letters`,
    method: 'post',
    data
  })
}

// 更新授权书
export function updateAuthLetter(id, data) {
  return request({
    url: `${baseURL}/auth-letters/${id}`,
    method: 'put',
    data
  })
}

// 发布授权书
export function publishAuthLetter(id) {
  return request({
    url: `${baseURL}/auth-letters/${id}/publish`,
    method: 'post'
  })
}

// 失效授权书
export function invalidateAuthLetter(id) {
  return request({
    url: `${baseURL}/auth-letters/${id}/invalidate`,
    method: 'post'
  })
}

// 删除授权书
export function deleteAuthLetter(id) {
  return request({
    url: `${baseURL}/auth-letters/${id}`,
    method: 'delete'
  })
}

// 批量操作授权书
export function batchAuthLetter(data) {
  return request({
    url: `${baseURL}/auth-letters/batch`,
    method: 'post',
    data
  })
}

// 查询操作日志
export function getAuthLetterLogs(authLetterId, params) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/logs`,
    method: 'get',
    params
  })
}