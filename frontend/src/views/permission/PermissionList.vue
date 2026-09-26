<!--
  权限管理页面
  展示权限/菜单列表，支持分页查询、权限标识/名称模糊搜索、新增/编辑/删除权限
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>权限管理</span>
          <el-button v-if="hasPerm('permission:add')" type="primary" @click="openDialog(null)">新增权限</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap; align-items:center">
        <el-input v-model="searchForm.code" placeholder="权限标识" clearable style="width:160px" @clear="handleSearch" />
        <el-input v-model="searchForm.name" placeholder="名称" clearable style="width:160px" @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="flatPermList" border stripe v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="code" label="权限标识" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="url" label="URL/路径" />
        <el-table-column prop="permType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.permType === 1 ? 'primary' : 'success'" size="small">
              {{ row.permType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('permission:edit')" type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="hasPerm('permission:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="550px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="permTreeData"
            :props="{ label: 'name', value: 'id' }"
            placeholder="选择上级菜单（留空为顶级）"
            check-strictly
            clearable
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="权限标识" prop="code">
          <el-input v-model="form.code" placeholder="如 user:list" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="URL路径">
          <el-input v-model="form.url" placeholder="菜单路径如 /user，按钮API路径如 /api/user" />
        </el-form-item>
        <el-form-item label="类型" prop="permType">
          <el-radio-group v-model="form.permType">
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮/功能</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="菜单图标名称（仅菜单类型）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 组件逻辑：数据加载、搜索、增删改操作
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPermissionList, addPermission, updatePermission, deletePermission } from '../../api/permission'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const permList = ref([])
// 加载中状态
const loading = ref(false)
const saving = ref(false)
// 当前页码
const page = ref(1)
// 每页条数
const pageSize = ref(10)
// 数据总数
const total = ref(0)
const searchForm = ref({
  code: '',
  name: ''
})
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = ref({
  parentId: null,
  code: '',
  name: '',
  url: '',
  permType: 2,
  icon: '',
  sort: 0,
  status: 1
})

const rules = {
  code: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
}

const hasPerm = (perm) => userStore.hasPerm(perm)

// 扁平化权限列表用于表格展示
const flatPermList = computed(() => {
  const flatten = (list, result = []) => {
    list.forEach(item => {
      result.push(item)
      if (item.children && item.children.length) {
        flatten(item.children, result)
      }
    })
    return result
  }
  return flatten(permList.value)
})

// 树形结构用于选择器
const permTreeData = computed(() => {
  const buildTree = (list, parentId = 0) => {
    return list
      .filter(p => (p.parentId || 0) === parentId)
      .map(p => ({
        ...p,
        children: buildTree(list, p.id)
      }))
  }
  return buildTree(permList.value, 0)
})

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.code) params.code = searchForm.value.code
    if (searchForm.value.name) params.name = searchForm.value.name
    const res = await getPermissionList(params)
    permList.value = res.data?.list || []
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
  searchForm.value = { code: '', name: '' }
  page.value = 1
  loadData()
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) {
    form.value = {
      parentId: row.parentId || null,
      code: row.code,
      name: row.name,
      url: row.url || '',
      permType: row.permType,
      icon: row.icon || '',
      sort: row.sort || 0,
      status: row.status
    }
  } else {
    form.value = { parentId: null, code: '', name: '', url: '', permType: 2, icon: '', sort: 0, status: 1 }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      const row = permList.value.find(p => p.code === form.value.code)
      if (row) await updatePermission(row.id, form.value)
    } else {
      await addPermission(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除权限 "${row.name}"?`, '提示', { type: 'warning' }).then(async () => {
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => { loadData() })
</script>
