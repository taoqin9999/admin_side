<!--
  系统参数管理页面
  仅支持查询和修改参数值，正则校验在提交时由后端完成
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>系统参数管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap; align-items:center">
        <el-input v-model="searchForm.paramKey" placeholder="参数键" clearable style="width:160px" @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="configList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="paramKey" label="参数键" />
        <el-table-column prop="paramValue" label="参数值">
          <template #default="{ row }">
            <el-tag v-if="editingId !== row.id" :type="hasPerm('sysConfig:edit') ? 'warning' : 'plain'" @click="hasPerm('sysConfig:edit') && startEdit(row)">{{ row.paramValue }}</el-tag>
            <el-input
              v-else
              v-model="editValue"
              size="small"
              style="width:120px"
              :placeholder="'regex: ' + (row.regex || '\u65e0')"
              @keyup.enter="hasPerm('sysConfig:edit') && confirmEdit(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="regex" label="正则校验规则">
          <template #default="{ row }">
            <el-tag v-if="row.regex" size="small">{{ row.regex }}</el-tag>
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <template v-if="editingId === row.id && hasPerm('sysConfig:edit')">
              <el-button type="primary" link size="small" :loading="saving" @click="confirmEdit(row)">保存</el-button>
              <el-button link size="small" @click="cancelEdit">取消</el-button>
            </template>
            <el-button v-else-if="hasPerm('sysConfig:edit')" type="primary" link size="small" @click="startEdit(row)">修改值</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex; justify-content:flex-end; margin-top:16px">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSysConfigList, updateSysConfig } from '../../api/sysConfig'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const configList = ref([])
const loading = ref(false)
const saving = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({ paramKey: '' })
const editingId = ref(null)
const editValue = ref('')

const hasPerm = (perm) => userStore.hasPerm(perm)

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.paramKey) params.paramKey = searchForm.value.paramKey
    const res = await getSysConfigList(params)
    configList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { paramKey: '' }
  page.value = 1
  loadData()
}

const startEdit = (row) => {
  editingId.value = row.id
  editValue.value = row.paramValue
}

const cancelEdit = () => {
  editingId.value = null
  editValue.value = ''
}

const confirmEdit = async (row) => {
  saving.value = true
  try {
    await updateSysConfig(row.id, { paramValue: editValue.value })
    ElMessage.success('更新成功')
    editingId.value = null
    loadData()
  } catch (e) {
    cancelEdit()
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
