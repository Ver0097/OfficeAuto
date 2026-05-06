<template>
  <div class="config-container">
    <!-- 搜索栏 -->
    <div class="search-section">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="参数键名">
          <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable />
        </el-form-item>
        <el-form-item label="参数名称">
          <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable />
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
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增参数</el-button>
      </div>

      <div class="table-wrapper">
        <el-table :data="configList" v-loading="loading" border stripe height="100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="configKey" label="参数键名" width="150" />
          <el-table-column prop="configName" label="参数名称" width="150" />
          <el-table-column prop="configValue" label="参数键值" min-width="200" show-overflow-tooltip />
          <el-table-column prop="configType" label="参数类型" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" fixed="right" width="150">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="formData.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="formData.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="formData.configValue" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="参数类型" prop="configType">
          <el-select v-model="formData.configType" placeholder="请选择参数类型">
            <el-option label="文本" value="text" />
            <el-option label="数字" value="number" />
            <el-option label="布尔" value="boolean" />
            <el-option label="JSON" value="json" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigList, createConfig, updateConfig, deleteConfig } from '@/api/config'

const queryParams = reactive({ pageNum: 1, pageSize: 10, configKey: '', configName: '', status: null })
const configList = ref([])
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const isAdd = ref(true)
const dialogTitle = computed(() => isAdd.value ? '新增参数' : '编辑参数')

const formRef = ref()
const formData = reactive({ id: null, configKey: '', configName: '', configValue: '', configType: 'text', remark: '', status: 1 })
const formRules = {
  configKey: [{ required: true, message: '请输入参数键名', trigger: 'blur' }],
  configName: [{ required: true, message: '请输入参数名称', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入参数键值', trigger: 'blur' }]
}

const loadConfigList = async () => {
  loading.value = true
  try {
    const res = await getConfigList(queryParams)
    configList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('加载参数列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { queryParams.pageNum = 1; loadConfigList() }
const handleReset = () => { queryParams.configKey = ''; queryParams.configName = ''; queryParams.status = null; queryParams.pageNum = 1; loadConfigList() }
const handleSizeChange = (val) => { queryParams.pageSize = val; loadConfigList() }
const handlePageChange = (val) => { queryParams.pageNum = val; loadConfigList() }

const handleAdd = () => { isAdd.value = true; resetForm(); dialogVisible.value = true }
const handleEdit = (row) => {
  isAdd.value = false; resetForm()
  formData.id = row.id; formData.configKey = row.configKey; formData.configName = row.configName
  formData.configValue = row.configValue; formData.configType = row.configType || 'text'
  formData.remark = row.remark; formData.status = row.status
  dialogVisible.value = true
}
const resetForm = () => { formData.id = null; formData.configKey = ''; formData.configName = ''; formData.configValue = ''; formData.configType = 'text'; formData.remark = ''; formData.status = 1 }
const handleDialogClose = () => { formRef.value?.resetFields() }

const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isAdd.value) { await createConfig(formData); ElMessage.success('新增成功') }
    else { await updateConfig(formData); ElMessage.success('编辑成功') }
    dialogVisible.value = false; loadConfigList()
  } catch (error) { ElMessage.error(error.message || '操作失败') }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该参数吗？', '提示', { type: 'warning' })
    .then(async () => { await deleteConfig(row.id); ElMessage.success('删除成功'); loadConfigList() })
    .catch(() => {})
}

loadConfigList()
</script>

<style scoped>
.config-container { display: flex; flex-direction: column; height: 100%; padding: 10px; background-color: #f5f5f5; }
.search-section { background-color: #fff; padding: 15px 20px; margin-bottom: 10px; flex-shrink: 0; border-radius: 4px; }
.search-form { display: flex; flex-wrap: wrap; }
.table-section { flex: 1; display: flex; flex-direction: column; background-color: #fff; padding: 15px 20px; min-height: 0; border-radius: 4px; }
.toolbar { flex-shrink: 0; margin-bottom: 15px; }
.table-wrapper { flex: 1; min-height: 0; }
.pagination-wrapper { flex-shrink: 0; padding-top: 15px; display: flex; justify-content: flex-end; }
</style>