import request from '@/utils/request'

// baseURL - 联调时可临时修改
const baseURL = '/api/v1'

// 查询问卷题目列表
export function getQuestionList(params) {
  return request({
    url: `${baseURL}/questionnaire-questions`,
    method: 'get',
    params
  })
}

// 获取问卷题目详情
export function getQuestionDetail(id) {
  return request({
    url: `${baseURL}/questionnaire-questions/${id}`,
    method: 'get'
  })
}

// 创建问卷题目
export function createQuestion(data) {
  return request({
    url: `${baseURL}/questionnaire-questions`,
    method: 'post',
    data
  })
}

// 更新问卷题目
export function updateQuestion(id, data) {
  return request({
    url: `${baseURL}/questionnaire-questions/${id}`,
    method: 'put',
    data
  })
}

// 删除问卷题目
export function deleteQuestion(id) {
  return request({
    url: `${baseURL}/questionnaire-questions/${id}`,
    method: 'delete'
  })
}

// 批量删除问卷题目
export function batchDeleteQuestions(ids) {
  return request({
    url: `${baseURL}/questionnaire-questions/batch-delete`,
    method: 'post',
    data: { ids }
  })
}

// 获取问卷题目下拉列表
export function getQuestionOptions(language = 'ZH') {
  return request({
    url: `${baseURL}/questionnaire-questions/options`,
    method: 'get',
    params: { language }
  })
}