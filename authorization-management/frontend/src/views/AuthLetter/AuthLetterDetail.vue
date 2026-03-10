<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">授权书详情</h1>
      <div class="page-actions">
        <el-button @click="goBack">返回</el-button>
        <el-button type="primary" @click="goToEdit" :disabled="authLetter?.status === 'PUBLISHED'">编辑</el-button>
        <el-button type="success" @click="handlePublish" :disabled="authLetter?.status !== 'DRAFT'">发布</el-button>
        <el-button type="primary" @click="showAddSceneDialog = true">添加场景</el-button>
      </div>
    </div>

    <el-descriptions :column="2" border style="margin-bottom: 24px;">
      <el-descriptions-item label="名称">{{ authLetter?.name }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(authLetter?.status)">
          {{ getStatusText(authLetter?.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="发布层级">{{ getLevelText(authLetter?.publishLevel) }}</el-descriptions-item>
      <el-descriptions-item label="被授权层级">{{ getLevelText(authLetter?.authorizedLevel) }}</el-descriptions-item>
      <el-descriptions-item label="有效期开始">{{ formatDate(authLetter?.validFrom) }}</el-descriptions-item>
      <el-descriptions-item label="有效期结束">{{ formatDate(authLetter?.validTo) }}</el-descriptions-item>
      <el-descriptions-item label="描述" :span="2">{{ authLetter?.description || '-' }}</el-descriptions-item>
    </el-descriptions>

    <h3 style="margin-bottom: 16px;">场景列表</h3>
    <el-empty v-if="!scenes.length" description="暂无场景" />
    <div v-else>
      <el-collapse v-model="activeSceneIds">
        <el-collapse-item v-for="scene in scenes" :key="scene.id" :name="scene.id">
          <template #title>
            <div style="display: flex; align-items: center; gap: 12px; width: 100%;">
              <span><strong>{{ scene.name }}</strong></span>
              <el-tag size="small">决策层级: {{ scene.decisionLevel || '-' }}</el-tag>
              <div style="margin-left: auto; margin-right: 16px;">
                <el-button link type="primary" size="small" @click.stop="editScene(scene)">编辑</el-button>
                <el-button link type="danger" size="small" @click.stop="deleteScene(scene.id)">删除</el-button>
                <el-button link type="primary" size="small" @click.stop="openAddRuleDialog(scene.id)">添加规则</el-button>
              </div>
            </div>
          </template>
          <div style="padding: 12px;">
            <p style="margin-bottom: 12px; color: #606266;">{{ scene.description || '暂无描述' }}</p>
            <h4 style="margin-bottom: 12px;">规则列表</h4>
            <el-empty v-if="!scene.rules?.length" description="暂无规则" :image-size="60" />
            <el-card v-for="rule in scene.rules" :key="rule.id" shadow="never" style="margin-bottom: 12px;">
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                  <strong>{{ rule.name }}</strong>
                  <p style="color: #909399; font-size: 13px; margin-top: 4px;">{{ rule.description }}</p>
                </div>
                <div>
                  <el-button link type="primary" size="small" @click="editRule(scene.id, rule)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="deleteRule(scene.id, rule.id)">删除</el-button>
                </div>
              </div>
              <div v-if="rule.conditions?.length" style="margin-top: 12px;">
                <ConditionViewer :conditions="rule.conditions" />
              </div>
            </el-card>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>

    <!-- Add Scene Dialog -->
    <el-dialog v-model="showAddSceneDialog" title="添加场景" width="500px">
      <el-form ref="sceneFormRef" :model="sceneForm" :rules="sceneRules" label-width="100px">
        <el-form-item label="场景名称" prop="name">
          <el-input v-model="sceneForm.name" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="sceneForm.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="决策层级" prop="decisionLevel">
          <el-input-number v-model="sceneForm.decisionLevel" :min="1" :max="10" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddSceneDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddScene">确定</el-button>
      </template>
    </el-dialog>

    <!-- Add Rule Dialog -->
    <el-dialog v-model="showAddRuleDialog" title="添加规则" width="600px">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="ruleRules" label-width="100px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="ruleForm.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddRuleDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAddRule">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthLetterStore } from '@/store/authLetter'
import { useSceneStore } from '@/store/scene'
import { useRuleStore } from '@/store/rule'
import ConditionViewer from '@/components/ConditionViewer.vue'

const router = useRouter()
const route = useRoute()
const authLetterStore = useAuthLetterStore()
const sceneStore = useSceneStore()
const ruleStore = useRuleStore()

const authLetter = ref(null)
const scenes = ref([])
const activeSceneIds = ref([])

const showAddSceneDialog = ref(false)
const showAddRuleDialog = ref(false)
const currentSceneId = ref(null)

const sceneForm = ref({
  name: '',
  description: '',
  decisionLevel: 1
})

const ruleForm = ref({
  name: '',
  description: ''
})

const sceneRules = {
  name: [{ required: true, message: '请输入场景名称', trigger: 'blur' }]
}

const ruleRules = {
  name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }]
}

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    authLetter.value = await authLetterStore.fetchAuthLetter(route.params.id)
    await sceneStore.fetchScenes(route.params.id)
    scenes.value = sceneStore.scenes
  } catch (e) {
    ElMessage.error('加载失败')
    router.back()
  }
}

function getStatusType(status) {
  const types = { 'DRAFT': 'info', 'PUBLISHED': 'success', 'EXPIRED': 'danger' }
  return types[status] || 'info'
}

function getStatusText(status) {
  const texts = { 'DRAFT': '草稿', 'PUBLISHED': '已发布', 'EXPIRED': '已失效' }
  return texts[status] || status
}

function getLevelText(level) {
  const texts = { 'ORGANIZATION': '机关', 'REGIONAL_DEPT': '地区部', 'REPRESENTATIVE_OFFICE': '代表处', 'OFFICE': '办事处' }
  return texts[level] || '-'
}

function formatDate(dateStr) {
  return dateStr ? new Date(dateStr).toLocaleString('zh-CN') : '-'
}

function goBack() {
  router.back()
}

function goToEdit() {
  router.push(`/auth-letters/${route.params.id}/edit`)
}

async function handlePublish() {
  try {
    await ElMessageBox.confirm('确定要发布此授权书吗？发布后不可修改和删除。', '提示', { type: 'warning' })
    await authLetterStore.publishAuthLetter(route.params.id)
    ElMessage.success('发布成功')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('发布失败')
  }
}

async function handleAddScene() {
  try {
    await sceneStore.createScene(route.params.id, sceneForm.value)
    ElMessage.success('添加成功')
    showAddSceneDialog.value = false
    sceneForm.value = { name: '', description: '', decisionLevel: 1 }
    await loadData()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

function editScene(scene) {
  ElMessage.info('编辑场景功能开发中')
}

async function deleteScene(sceneId) {
  try {
    await ElMessageBox.confirm('确定要删除此场景吗？', '提示', { type: 'warning' })
    await sceneStore.deleteScene(route.params.id, sceneId)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

function openAddRuleDialog(sceneId) {
  currentSceneId.value = sceneId
  showAddRuleDialog.value = true
}

async function handleAddRule() {
  try {
    await ruleStore.createRule(currentSceneId.value, ruleForm.value)
    ElMessage.success('添加成功')
    showAddRuleDialog.value = false
    ruleForm.value = { name: '', description: '' }
    await loadData()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

function editRule(sceneId, rule) {
  ElMessage.info('编辑规则功能开发中')
}

async function deleteRule(sceneId, ruleId) {
  try {
    await ElMessageBox.confirm('确定要删除此规则吗？', '提示', { type: 'warning' })
    await ruleStore.deleteRule(sceneId, ruleId)
    ElMessage.success('删除成功')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}
</script>