<template>
  <el-config-provider :locale="zhCn">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="logo">
          <el-icon><Document /></el-icon>
          <span>授权书管理系统</span>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="currentRoute"
          router
          class="nav-menu"
        >
          <el-menu-item index="/auth-letters">
            <el-icon><Document /></el-icon>
            授权书管理
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

const route = useRoute()
const currentRoute = computed(() => route.path)
</script>

<style scoped>
.app-container {
  min-height: 100vh;
}

.app-header {
  display: flex;
  align-items: center;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-right: 40px;
}

.logo .el-icon {
  font-size: 28px;
  margin-right: 8px;
}

.nav-menu {
  flex: 1;
  border-bottom: none;
}

.app-main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>