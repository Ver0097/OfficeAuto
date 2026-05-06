<template>
  <div class="dashboard-container">
    <el-card>
      <h2>欢迎登录OA管理系统</h2>
      <p>当前用户：{{ userStore.userInfo?.username }}</p>
      <p>Token有效，系统运行正常。</p>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  height: 100%;
}

h2 {
  margin-bottom: 20px;
  color: #303133;
}

p {
  color: #606266;
  line-height: 2;
}
</style>