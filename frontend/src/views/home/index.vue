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
      <el-main>
        <el-card>
          <h2>欢迎登录OA管理系统</h2>
          <p>当前用户：{{ userStore.userInfo?.username }}</p>
          <p>Token有效，系统运行正常。</p>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

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

.el-main {
  background-color: #f5f5f5;
}
</style>