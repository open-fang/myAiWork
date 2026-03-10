<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">{{ isEdit ? '编辑授权书' : '创建授权书' }}</h1>
      <div class="page-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
      </div>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 800px;">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入授权书名称" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
      </el-form-item>

      <el-form-item label="发布层级" prop="publishLevel">
        <LevelSelector v-model="form.publishLevel" placeholder="请选择发布层级" />
      </el-form-item>

      <el-form-item label="被授权层级" prop="authorizedLevel">
        <LevelSelector v-model="form.authorizedLevel" placeholder="请选择被授权层级" />
      </el-form-item>

      <el-form-item label="有效期">
        <el-col :span="11">
          <el-date-picker
            v-model="form.validFrom"
            type="datetime"
            placeholder="开始时间"
            style="width: 100%;"
          />
        </el-col>
        <el-col :span="2" style="text-align: center;">-</el-col>
        <el-col :span="11">
          <el-date-picker
            v-model="form.validTo"
            type="datetime"
            placeholder="结束时间"
            style="width: 100%;"
          />
        </el-col>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthLetterStore } from '@/store/authLetter'
import LevelSelector from '@/components/LevelSelector.vue'

const router = useRouter()
const route = useRoute()
const store = useAuthLetterStore()

const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = ref({
  name: '',
  description: '',
  publishLevel: null,
  authorizedLevel: null,
  validFrom: null,
  validTo: null
})

const rules = {
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' }
  ]
}

onMounted(async () => {
  if (isEdit.value) {
    await loadAuthLetter()
  }
})

async function loadAuthLetter() {
  try {
    const data = await store.fetchAuthLetter(route.params.id)
    Object.assign(form.value, data)
  } catch (e) {
    ElMessage.error('加载失败')
    router.back()
  }
}

function goBack() {
  router.back()
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await store.updateAuthLetter(route.params.id, form.value)
      ElMessage.success('更新成功')
    } else {
      const result = await store.createAuthLetter(form.value)
      ElMessage.success('创建成功')
      router.push(`/auth-letters/${result.id}`)
    }
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}
</script>