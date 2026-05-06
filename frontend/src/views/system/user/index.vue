<template>
  <div class="user-container">
    <!-- 搜索栏 -->
    <div class="search-section">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="queryParams.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <!-- 操作栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>

      <!-- 用户表格 -->
      <div class="table-wrapper">
        <el-table :data="userList" v-loading="loading" border stripe height="100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="realName" label="真实姓名" width="120" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="deptName" label="部门" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" fixed="right" width="200">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
              <el-button
                :type="row.status === 1 ? 'danger' : 'success'"
                link
                @click="handleStatus(row)"
              >
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
              <el-button type="danger" link @click="handleDelete(row)" v-if="row.username !== 'admin'">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username" v-if="isAdd">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="isAdd">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="formData.deptId" placeholder="请选择部门" clearable>
            <!-- 后续接入部门接口 -->
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="400px">
      <el-form ref="resetPwdRef" :model="resetPwdData" :rules="resetPwdRules" label-width="100px">
        <el-form-item label="新密码" prop="password">
          <el-input v-model="resetPwdData.password" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwdSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, resetPassword, changeStatus } from '@/api/user'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  status: null
})

// 用户列表
const userList = ref([])
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const isAdd = ref(true)
const dialogTitle = computed(() => isAdd.value ? '新增用户' : '编辑用户')

// 表单数据
const formRef = ref()
const formData = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  deptId: null
})

// 表单校验规则
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '长度3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }]
}

// 重置密码
const resetPwdVisible = ref(false)
const resetPwdRef = ref()
const resetPwdData = reactive({
  id: null,
  password: ''
})
const resetPwdRules = {
  password: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }]
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.status = null
  queryParams.pageNum = 1
  loadUserList()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  loadUserList()
}

const handlePageChange = (val) => {
  queryParams.pageNum = val
  loadUserList()
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isAdd.value = false
  resetForm()
  formData.id = row.id
  formData.realName = row.realName
  formData.email = row.email
  formData.phone = row.phone
  formData.deptId = row.deptId
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.email = ''
  formData.phone = ''
  formData.deptId = null
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 提交
const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isAdd.value) {
      await createUser(formData)
      ElMessage.success('新增成功')
    } else {
      await updateUser(formData)
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    loadUserList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadUserList()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

// 重置密码
const handleResetPwd = (row) => {
  resetPwdData.id = row.id
  resetPwdData.password = ''
  resetPwdVisible.value = true
}

const handleResetPwdSubmit = async () => {
  await resetPwdRef.value.validate()
  try {
    await resetPassword(resetPwdData.id, resetPwdData.password)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '重置失败')
  }
}

// 切换状态
const handleStatus = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const text = newStatus === 1 ? '启用' : '禁用'
  ElMessageBox.confirm(`确定要${text}该用户吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await changeStatus(row.id, newStatus)
      ElMessage.success(`${text}成功`)
      loadUserList()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    }
  }).catch(() => {})
}

// 初始化
loadUserList()
</script>

<style scoped>
.user-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 10px;
  background-color: #f5f5f5;
}

.search-section {
  background-color: #fff;
  padding: 15px 20px;
  margin-bottom: 10px;
  flex-shrink: 0;
  border-radius: 4px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.table-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  padding: 15px 20px;
  min-height: 0;
  border-radius: 4px;
}

.toolbar {
  flex-shrink: 0;
  margin-bottom: 15px;
}

.table-wrapper {
  flex: 1;
  min-height: 0;
}

.pagination-wrapper {
  flex-shrink: 0;
  padding-top: 15px;
  display: flex;
  justify-content: flex-end;
}
</style>