<!--
  操作日志查询页面
  仅支持查询（只读）与 CSV 导出，不提供新增/修改/删除
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>操作日志查询</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap; align-items:center">
        <el-input v-model="searchForm.username" placeholder="用户名" clearable style="width:150px" @clear="handleSearch" />
        <el-input v-model="searchForm.operation" placeholder="操作描述" clearable style="width:170px" @clear="handleSearch" />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          style="width:340px"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" :loading="exporting" @click="handleExport">导出 CSV</el-button>
      </div>

      <el-table :data="logList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="operation" label="操作" width="140" />
        <el-table-column prop="method" label="请求方法" width="250" show-overflow-tooltip />
        <el-table-column prop="params" label="请求参数" min-width="240" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="120" />
        <el-table-column prop="time" label="耗时(ms)" width="90" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
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
import { getLogList, getLogExport } from '../../api/log'

const logList = ref([])
const loading = ref(false)
const exporting = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchForm = ref({ username: '', operation: '' })
const dateRange = ref(null)

// 组装当前筛选条件（查询与导出共用）
const buildParams = () => {
  const params = { page: page.value, pageSize: pageSize.value }
  if (searchForm.value.username) params.username = searchForm.value.username
  if (searchForm.value.operation) params.operation = searchForm.value.operation
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  return params
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLogList(buildParams())
    logList.value = res.data?.list || []
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
  searchForm.value = { username: '', operation: '' }
  dateRange.value = null
  page.value = 1
  loadData()
}

// 导出 CSV：按当前筛选条件导出全部匹配记录
const handleExport = async () => {
  exporting.value = true
  try {
    const { page, pageSize, ...filter } = buildParams()
    const res = await getLogExport(filter)
    const csv = res.data || ''
    if (!csv) {
      ElMessage.warning('没有可导出的日志')
      return
    }
    // 加 BOM，确保 Excel 打开 UTF-8 中文不乱码
    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    const ts = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
    a.download = `操作日志_${ts}.csv`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    ElMessage.success(`已导出 ${csv.split('\n').length - 1} 条日志`)
  } catch (e) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>
