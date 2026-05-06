<template>
  <div class="home-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <span class="title">OA管理系统</span>
          <div class="user-info">
            <span>{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            <el-button type="primary" link @click="handleLogout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu
            :default-active="activeMenu"
            class="side-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-sub-menu index="system">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/system/user">
                <el-icon><User /></el-icon>
                <span>用户管理</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
        <el-container class="main-container">
          <div class="tabs-bar">
            <el-tag
              v-for="tab in tabsStore.tabs"
              :key="tab.path"
              :type="tabsStore.activePath === tab.path ? '' : 'info'"
              :closable="tab.closable"
              @click="handleTabClick(tab.path)"
              @close="handleTabClose(tab.path)"
              class="tab-item"
            >
              {{ tab.title }}
            </el-tag>
          </div>
          <el-main>
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useTabsStore } from '@/store/tabs'
import { useRouter, useRoute } from 'vue-router'
import { HomeFilled, Setting, User } from '@element-plus/icons-vue'
import { computed, watch } from 'vue'

const userStore = useUserStore()
const tabsStore = useTabsStore()
const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

// 监听路由变化，自动添加标签
watch(
  () => route.path,
  (path) => {
    const title = route.meta?.title || '未命名'
    tabsStore.addTab(path, title)
  },
  { immediate: true }
)

// 菜单选择
const handleMenuSelect = (index) => {
  const menuItem = findMenuItem(index)
  const title = menuItem?.title || route.meta?.title || '未命名'
  tabsStore.addTab(index, title)
  router.push(index)
}

// 查找菜单项标题
const findMenuItem = (path) => {
  if (path === '/') return { title: '首页' }
  if (path === '/system/user') return { title: '用户管理' }
  return null
}

// 标签点击
const handleTabClick = (path) => {
  tabsStore.switchTab(path, router)
}

// 标签关闭
const handleTabClose = (path) => {
  tabsStore.closeTab(path, router)
}

const handleLogout = async () => {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh;
}

.el-header {
  background-color: #409EFF;
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.title {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}

.side-menu {
  border-right: none;
  height: 100%;
}

.main-container {
  flex-direction: column;
}

.tabs-bar {
  background-color: #fff;
  padding: 8px 20px;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  height: 40px;
  line-height: 24px;
}

.tab-item {
  cursor: pointer;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
  flex: 1;
  overflow: auto;
}
</style>