// API基础配置
const BASE_URL = '/api'

// 通用请求方法
function request(url, options = {}) {
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json'
    }
  }

  const config = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  }

  // 如果有body且是对象，转为JSON字符串
  if (config.body && typeof config.body === 'object') {
    config.body = JSON.stringify(config.body)
  }

  return fetch(BASE_URL + url, config).then(response => {
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    return response.json()
  })
}

// ==================== Lookup服务 ====================
export const lookupApi = {
  // 获取下拉选项（授权对象层级、适用区域、授权发布层级等）
  getOptions(code) {
    return request(`/lookup/${code}`)
  },

  // 获取组织树
  getOrgTree() {
    return request('/lookup/org/tree')
  },

  // 获取产业树
  getIndustryTree() {
    return request('/lookup/industry/tree')
  },

  // 获取业务场景选项
  getBusinessScenarios() {
    return request('/lookup/business-scenarios')
  },

  // 获取决策层级选项
  getDecisionLevels() {
    return request('/lookup/decision-levels')
  },

  // 获取业务对象选项
  getBusinessObjects() {
    return request('/lookup/business-objects')
  },

  // 获取规则字段选项
  getRuleFields() {
    return request('/lookup/rule-fields')
  }
}

// ==================== 授权书管理 ====================
export const authLetterApi = {
  // 分页查询授权书列表
  getList(params) {
    const queryStr = Object.entries(params)
      .filter(([, value]) => value !== null && value !== undefined && value !== '')
      .map(([key, value]) => {
        if (Array.isArray(value)) {
          return value.map(v => `${key}=${encodeURIComponent(v)}`).join('&')
        }
        return `${key}=${encodeURIComponent(value)}`
      })
      .join('&')
    return request(`/auth-letters?${queryStr}`)
  },

  // 获取授权书详情
  getDetail(id) {
    return request(`/auth-letters/${id}`)
  },

  // 创建授权书
  create(data) {
    return request('/auth-letters', {
      method: 'POST',
      body: data
    })
  },

  // 更新授权书
  update(id, data) {
    return request(`/auth-letters/${id}`, {
      method: 'PUT',
      body: data
    })
  },

  // 删除授权书
  delete(id) {
    return request(`/auth-letters/${id}`, {
      method: 'DELETE'
    })
  },

  // 发布授权书
  publish(id) {
    return request(`/auth-letters/${id}/publish`, {
      method: 'PUT'
    })
  },

  // 批量生效
  batchPublish(ids) {
    return request('/auth-letters/batch/publish', {
      method: 'PUT',
      body: { ids }
    })
  },

  // 批量失效
  batchExpire(ids) {
    return request('/auth-letters/batch/expire', {
      method: 'PUT',
      body: { ids }
    })
  },

  // 批量删除
  batchDelete(ids) {
    return request('/auth-letters/batch', {
      method: 'DELETE',
      body: { ids }
    })
  },

  // ==================== 附件管理 ====================
  // 获取附件列表
  getAttachments(authLetterId, params) {
    const queryStr = Object.entries(params)
      .filter(([, value]) => value !== null && value !== undefined)
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&')
    return request(`/auth-letters/${authLetterId}/attachments?${queryStr}`)
  },

  // 上传附件
  uploadAttachment(authLetterId, formData) {
    return fetch(`${BASE_URL}/auth-letters/${authLetterId}/attachments`, {
      method: 'POST',
      body: formData
      // 不设置Content-Type，让浏览器自动设置multipart/form-data
    }).then(response => response.json())
  },

  // 删除附件
  deleteAttachment(authLetterId, attachmentId) {
    return request(`/auth-letters/${authLetterId}/attachments/${attachmentId}`, {
      method: 'DELETE'
    })
  },

  // 批量删除附件
  batchDeleteAttachments(authLetterId, ids) {
    return request(`/auth-letters/${authLetterId}/attachments/batch`, {
      method: 'DELETE',
      body: { ids }
    })
  },

  // 下载附件
  downloadAttachment(authLetterId, attachmentId) {
    window.open(`${BASE_URL}/auth-letters/${authLetterId}/attachments/${attachmentId}/download`)
  },

  // ==================== 授权规则/场景管理 ====================
  // 获取场景列表
  getScenes(authLetterId, params) {
    const queryStr = Object.entries(params)
      .filter(([, value]) => value !== null && value !== undefined)
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&')
    return request(`/auth-letters/${authLetterId}/scenes?${queryStr}`)
  },

  // 创建场景
  createScene(authLetterId, data) {
    return request(`/auth-letters/${authLetterId}/scenes`, {
      method: 'POST',
      body: data
    })
  },

  // 更新场景
  updateScene(authLetterId, sceneId, data) {
    return request(`/auth-letters/${authLetterId}/scenes/${sceneId}`, {
      method: 'PUT',
      body: data
    })
  },

  // 删除场景
  deleteScene(authLetterId, sceneId) {
    return request(`/auth-letters/${authLetterId}/scenes/${sceneId}`, {
      method: 'DELETE'
    })
  },

  // 批量删除场景
  batchDeleteScenes(authLetterId, ids) {
    return request(`/auth-letters/${authLetterId}/scenes/batch`, {
      method: 'DELETE',
      body: { ids }
    })
  }
}

// ==================== 规则参数配置 ====================
export const ruleParamApi = {
  // 分页查询规则参数列表
  getList(params) {
    const queryStr = Object.entries(params)
      .filter(([, value]) => value !== null && value !== undefined && value !== '')
      .map(([key, value]) => `${key}=${encodeURIComponent(value)}`)
      .join('&')
    return request(`/rule-params?${queryStr}`)
  },

  // 获取规则参数详情
  getDetail(id) {
    return request(`/rule-params/${id}`)
  },

  // 创建规则参数
  create(data) {
    return request('/rule-params', {
      method: 'POST',
      body: data
    })
  },

  // 更新规则参数
  update(id, data) {
    return request(`/rule-params/${id}`, {
      method: 'PUT',
      body: data
    })
  },

  // 批量生效
  batchActivate(ids) {
    return request('/rule-params/batch/activate', {
      method: 'PUT',
      body: { ids }
    })
  },

  // 批量失效
  batchDeactivate(ids) {
    return request('/rule-params/batch/deactivate', {
      method: 'PUT',
      body: { ids }
    })
  }
}

export default {
  lookupApi,
  authLetterApi,
  ruleParamApi
}