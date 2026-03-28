import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 查询场景列表
export function getSceneList(authLetterId, params) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes`,
    method: 'get',
    params
  })
}

// 获取场景详情
export function getSceneDetail(authLetterId, id) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`,
    method: 'get'
  })
}

// 创建场景
export function createScene(authLetterId, data) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes`,
    method: 'post',
    data
  })
}

// 更新场景
export function updateScene(authLetterId, id, data) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`,
    method: 'put',
    data
  })
}

// 删除场景
export function deleteScene(authLetterId, id) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes/${id}`,
    method: 'delete'
  })
}

// 批量删除场景
export function batchDeleteScenes(authLetterId, ids) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/scenes/batch-delete`,
    method: 'post',
    data: { ids }
  })
}