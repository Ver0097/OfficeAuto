<template>
  <div class="dict-container">
    <!-- 左侧：字典类型 -->
    <div class="dict-type-panel">
      <div class="panel-header">
        <span>字典类型</span>
        <el-button type="primary" size="small" @click="handleAddType">新增</el-button>
      </div>
      <div class="panel-search">
        <el-input v-model="typeQueryParams.dictName" placeholder="搜索字典名称" clearable @keyup.enter="loadDictTypeList" />
      </div>
      <div class="panel-content">
        <el-table :data="dictTypeList" v-loading="typeLoading" border stripe height="100%" highlight-current-row @current-change="handleTypeSelect">
          <el-table-column prop="dictType" label="类型编码" width="120" />
          <el-table-column prop="dictName" label="类型名称" min-width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditType(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteType(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="panel-pagination">
        <el-pagination v-model:current-page="typeQueryParams.pageNum" v-model:page-size="typeQueryParams.pageSize" :total="typeTotal" layout="total, prev, pager, next" :page-sizes="[10]" @current-change="loadDictTypeList" />
      </div>
    </div>

    <!-- 右侧：字典数据 -->
    <div class="dict-data-panel">
      <div class="panel-header">
        <span>字典数据 - {{ selectedTypeName || '请选择字典类型' }}</span>
        <el-button type="primary" size="small" @click="handleAddData" :disabled="!selectedType">新增</el-button>
      </div>
      <div class="panel-content">
        <el-table :data="dictDataList" v-loading="dataLoading" border stripe height="100%">
          <el-table-column prop="dictLabel" label="字典标签" width="120" />
          <el-table-column prop="dictValue" label="字典键值" width="120" />
          <el-table-column prop="dictSort" label="排序" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditData(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteData(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="panel-pagination">
        <el-pagination v-model:current-page="dataQueryParams.pageNum" v-model:page-size="dataQueryParams.pageSize" :total="dataTotal" layout="total, prev, pager, next" :page-sizes="[10]" @current-change="loadDictDataList" />
      </div>
    </div>

    <!-- 字典类型对话框 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="400px" @close="typeDialogClose">
      <el-form ref="typeFormRef" :model="typeFormData" :rules="typeFormRules" label-width="100px">
        <el-form-item label="类型编码" prop="dictType"><el-input v-model="typeFormData.dictType" placeholder="请输入类型编码" /></el-form-item>
        <el-form-item label="类型名称" prop="dictName"><el-input v-model="typeFormData.dictName" placeholder="请输入类型名称" /></el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="typeFormData.remark" type="textarea" placeholder="请输入备注" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeFormData.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="typeDialogVisible = false">取消</el-button><el-button type="primary" @click="handleTypeSubmit">确定</el-button></template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="400px" @close="dataDialogClose">
      <el-form ref="dataFormRef" :model="dataFormData" :rules="dataFormRules" label-width="100px">
        <el-form-item label="字典标签" prop="dictLabel"><el-input v-model="dataFormData.dictLabel" placeholder="请输入字典标签" /></el-form-item>
        <el-form-item label="字典键值" prop="dictValue"><el-input v-model="dataFormData.dictValue" placeholder="请输入字典键值" /></el-form-item>
        <el-form-item label="排序" prop="dictSort"><el-input-number v-model="dataFormData.dictSort" :min="0" /></el-form-item>
        <el-form-item label="备注" prop="remark"><el-input v-model="dataFormData.remark" type="textarea" placeholder="请输入备注" /></el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataFormData.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dataDialogVisible = false">取消</el-button><el-button type="primary" @click="handleDataSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDictTypeList, createDictType, updateDictType, deleteDictType, getDictDataList, createDictData, updateDictData, deleteDictData } from '@/api/dict'

const typeQueryParams = reactive({ pageNum: 1, pageSize: 10, dictName: '', status: null })
const dictTypeList = ref([])
const typeTotal = ref(0)
const typeLoading = ref(false)
const selectedType = ref(null)
const selectedTypeName = ref('')

const typeDialogVisible = ref(false)
const isAddType = ref(true)
const typeDialogTitle = computed(() => isAddType.value ? '新增字典类型' : '编辑字典类型')
const typeFormRef = ref()
const typeFormData = reactive({ id: null, dictType: '', dictName: '', remark: '', status: 1 })
const typeFormRules = { dictType: [{ required: true, message: '请输入类型编码', trigger: 'blur' }], dictName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }] }

const dataQueryParams = reactive({ pageNum: 1, pageSize: 10, dictType: '', dictLabel: '', status: null })
const dictDataList = ref([])
const dataTotal = ref(0)
const dataLoading = ref(false)

const dataDialogVisible = ref(false)
const isAddData = ref(true)
const dataDialogTitle = computed(() => isAddData.value ? '新增字典数据' : '编辑字典数据')
const dataFormRef = ref()
const dataFormData = reactive({ id: null, dictType: '', dictLabel: '', dictValue: '', dictSort: 0, remark: '', status: 1 })
const dataFormRules = { dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }], dictValue: [{ required: true, message: '请输入字典键值', trigger: 'blur' }] }

const loadDictTypeList = async () => {
  typeLoading.value = true
  try { const res = await getDictTypeList(typeQueryParams); dictTypeList.value = res.data.list; typeTotal.value = res.data.total }
  catch (error) { ElMessage.error('加载字典类型失败') } finally { typeLoading.value = false }
}

const loadDictDataList = async () => {
  if (!selectedType.value) return
  dataLoading.value = true; dataQueryParams.dictType = selectedType.value
  try { const res = await getDictDataList(dataQueryParams); dictDataList.value = res.data.list; dataTotal.value = res.data.total }
  catch (error) { ElMessage.error('加载字典数据失败') } finally { dataLoading.value = false }
}

const handleTypeSelect = (row) => { selectedType.value = row?.dictType; selectedTypeName.value = row?.dictName || ''; dataQueryParams.pageNum = 1; loadDictDataList() }

const handleAddType = () => { isAddType.value = true; resetTypeForm(); typeDialogVisible.value = true }
const handleEditType = (row) => { isAddType.value = false; resetTypeForm(); typeFormData.id = row.id; typeFormData.dictType = row.dictType; typeFormData.dictName = row.dictName; typeFormData.remark = row.remark; typeFormData.status = row.status; typeDialogVisible.value = true }
const resetTypeForm = () => { typeFormData.id = null; typeFormData.dictType = ''; typeFormData.dictName = ''; typeFormData.remark = ''; typeFormData.status = 1 }
const typeDialogClose = () => { typeFormRef.value?.resetFields() }
const handleTypeSubmit = async () => {
  await typeFormRef.value.validate()
  try { if (isAddType.value) { await createDictType(typeFormData); ElMessage.success('新增成功') } else { await updateDictType(typeFormData); ElMessage.success('编辑成功') }; typeDialogVisible.value = false; loadDictTypeList() }
  catch (error) { ElMessage.error(error.message || '操作失败') }
}
const handleDeleteType = (row) => {
  ElMessageBox.confirm('确定要删除该字典类型吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteDictType(row.id); ElMessage.success('删除成功')
    if (selectedType.value === row.dictType) { selectedType.value = null; selectedTypeName.value = ''; dictDataList.value = []; dataTotal.value = 0 }
    loadDictTypeList()
  }).catch(() => {})
}

const handleAddData = () => { isAddData.value = true; resetDataForm(); dataFormData.dictType = selectedType.value; dataDialogVisible.value = true }
const handleEditData = (row) => { isAddData.value = false; resetDataForm(); dataFormData.id = row.id; dataFormData.dictType = row.dictType; dataFormData.dictLabel = row.dictLabel; dataFormData.dictValue = row.dictValue; dataFormData.dictSort = row.dictSort; dataFormData.remark = row.remark; dataFormData.status = row.status; dataDialogVisible.value = true }
const resetDataForm = () => { dataFormData.id = null; dataFormData.dictType = ''; dataFormData.dictLabel = ''; dataFormData.dictValue = ''; dataFormData.dictSort = 0; dataFormData.remark = ''; dataFormData.status = 1 }
const dataDialogClose = () => { dataFormRef.value?.resetFields() }
const handleDataSubmit = async () => {
  await dataFormRef.value.validate()
  try { if (isAddData.value) { await createDictData(dataFormData); ElMessage.success('新增成功') } else { await updateDictData(dataFormData); ElMessage.success('编辑成功') }; dataDialogVisible.value = false; loadDictDataList() }
  catch (error) { ElMessage.error(error.message || '操作失败') }
}
const handleDeleteData = (row) => {
  ElMessageBox.confirm('确定要删除该字典数据吗？', '提示', { type: 'warning' }).then(async () => { await deleteDictData(row.id); ElMessage.success('删除成功'); loadDictDataList() }).catch(() => {})
}

loadDictTypeList()
</script>

<style scoped>
.dict-container { display: flex; height: 100%; padding: 10px; background-color: #f5f5f5; gap: 10px; }
.dict-type-panel, .dict-data-panel { flex: 1; display: flex; flex-direction: column; background-color: #fff; border-radius: 4px; min-width: 0; }
.panel-header { padding: 15px 20px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; flex-shrink: 0; }
.panel-header span { font-weight: 500; }
.panel-search { padding: 10px 20px; flex-shrink: 0; }
.panel-content { flex: 1; padding: 10px 20px; min-height: 0; }
.panel-pagination { padding: 10px 20px; flex-shrink: 0; display: flex; justify-content: flex-end; }
</style>