<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">授权书列表</h1>
      <div class="page-actions">
        <el-button type="primary" @click="goToCreate">
          <el-icon><Plus /></el-icon>
          新建授权书
        </el-button>
      </div>
    </div>

    <el-table :data="authLetters" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishLevel" label="发布层级" width="100">
        <template #default="{ row }">
          {{ row.publishLevel ? getLevelText(row.publishLevel) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="authorizedLevel" label="被授权层级" width="100">
        <template #default="{ row }">
          {{ row.authorizedLevel ? getLevelText(row.authorizedLevel) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="validTo" label="有效期至" width="160">
        <template #default="{ row }">
          {{ row.validTo ? formatDate(row.validTo) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToDetail(row.id)">查看</el-button>
          <el-button link type="primary" @click="goToEdit(row.id)" :disabled="row.status === 'PUBLISHED'">编辑</el-button>
          <el-button link type="success" @click="handlePublish(row.id)" :disabled="row.status !== 'DRAFT'">发布</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)" :disabled="row.status === 'PUBLISHED'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthLetterStore } from '@/store/authLetter'

const router = useRouter()
const store = useAuthLetterStore()

const authLetters = ref([])
const loading = ref(false)

onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    await store.fetchAuthLetters()
    authLetters.value = store.authLetters
  } finally {
    loading.value = false
  }
}

function getStatusType(status) {
  const types = {
    'DRAFT': 'info',
    'PUBLISHED': 'success',
    'EXPIRED': 'danger'
  }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = {
    'DRAFT': '草稿',
    'PUBLISHED': '已发布',
    'EXPIRED': '已失效'
  }
  return texts[status] || status
}

function getLevelText(level) {
  const texts = {
    'ORGANIZATION': '机关',
    'REGIONAL_DEPT': '地区部',
    'REPRESENTATIVE_OFFICE': '代表处',
    'OFFICE': '办事处'
  }
  return texts[level] || level
}

function formatDate(dateStr) {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

function goToCreate() {
  router.push('/auth-letters/create')
}

function goToEdit(id) {
  router.push(`/auth-letters/${id}/edit`)
}

function goToDetail(id) {
  router.push(`/auth-letters/${id}`)
}

async function handlePublish(id) {
  try {
    await ElMessageBox.confirm('确定要发布此授权书吗？发布后不可修改和删除。', '提示', {
      type: 'warning'
    })
    await store.publishAuthLetter(id)
    ElMessage.success('发布成功')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除此授权书吗？', '提示', {
      type: 'warning'
    })
    await store.deleteAuthLetter(id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>