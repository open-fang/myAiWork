import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 查询附件列表
export function getAttachmentList(authLetterId, params) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/attachments`,
    method: 'get',
    params
  })
}

// 上传附件
export function uploadAttachment(authLetterId, data) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/attachments`,
    method: 'post',
    data
  })
}

// 下载附件
export function downloadAttachment(authLetterId, id) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/attachments/${id}/download`,
    method: 'get',
    responseType: 'blob'
  })
}

// 删除附件
export function deleteAttachment(authLetterId, id) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/attachments/${id}`,
    method: 'delete'
  })
}

// 批量删除附件
export function batchDeleteAttachments(authLetterId, ids) {
  return request({
    url: `${baseURL}/auth-letters/${authLetterId}/attachments/batch-delete`,
    method: 'post',
    data: { ids }
  })
}