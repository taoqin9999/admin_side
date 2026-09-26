<!--
  角色管理页面
  展示角色列表，支持分页查询、角色标识/描述模糊搜索、新增/编辑/删除角色、分配权限
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>角色管理</span>
          <el-button v-if="hasPerm('role:add')" type="primary" @click="openDialog(null)">新增角色</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap; align-items:center">
        <el-input v-model="searchForm.name" placeholder="角色标识" clearable style="width:160px" @clear="handleSearch" />
        <el-input v-model="searchForm.memo" placeholder="角色描述" clearable style="width:160px" @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="roleList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色标识" />
        <el-table-column prop="memo" label="角色描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('role:edit')" type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="hasPerm('role:assignPerm')" type="warning" link size="small" @click="openPermDialog(row)">分配权限</el-button>
            <el-button v-if="hasPerm('role:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色标识" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色描述" prop="memo">
          <el-input v-model="form.memo" type="textarea" />
        </el-form-item>
        <el-form-item label="权限">
          <el-tree
            ref="permTree"
            :data="permTreeData"
            show-checkbox
            node-key="id"
            :props="{ label: 'name', children: 'children' }"
            :default-checked-keys="form.permissionIds"
            check-strictly
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px">
      <el-tree
        ref="permTree2"
        :data="permTreeData"
        show-checkbox
        node-key="id"
        :props="{ label: 'name', children: 'children' }"
        :default-checked-keys="currentPermIds"
        check-strictly
      />
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSavePerm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 组件逻辑：数据加载、搜索、增删改操作
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, assignRolePermissions } from '../../api/role'
import { getPermissionList } from '../../api/permission'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const roleList = ref([])
const permTreeData = ref([])
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
  name: '',
  memo: ''
})
const dialogVisible = ref(false)
const permDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const permTree = ref()
const permTree2 = ref()
const currentRoleId = ref(null)
const currentPermIds = ref([])

const form = ref({
  name: '',
  memo: '',
  permissionIds: []
})

const rules = {
  name: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const hasPerm = (perm) => userStore.hasPerm(perm)

const buildPermTree = (list) => {
  const map = {}
  const trees = []
  list.forEach(p => { map[p.id] = { ...p, children: [] } })
  list.forEach(p => {
    if (p.parentId && p.parentId !== 0 && map[p.parentId]) {
      map[p.parentId].children.push(map[p.id])
    } else if (!p.parentId || p.parentId === 0) {
      trees.push(map[p.id])
    }
  })
  return trees
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.name) params.name = searchForm.value.name
    if (searchForm.value.memo) params.memo = searchForm.value.memo
    const [roleRes, permRes] = await Promise.all([
      getRoleList(params),
      getPermissionList()
    ])
    roleList.value = roleRes.data?.list || []
    total.value = roleRes.data?.total || 0
    permTreeData.value = buildPermTree(permRes.data?.list || permRes.data || [])
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.value = { name: '', memo: '' }
  page.value = 1
  loadData()
}

const openDialog = (row) => {
  isEdit.value = !!row
  form.value = {
    name: row?.name || '',
    memo: row?.memo || '',
    permissionIds: row?.permissionIds || []
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return

  form.value.permissionIds = permTree.value?.getCheckedKeys() || []

  saving.value = true
  try {
    if (isEdit.value) {
      const row = roleList.value.find(r => r.name === form.value.name)
      if (row) await updateRole(row.id, form.value)
    } else {
      await addRole(form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const openPermDialog = (row) => {
  currentRoleId.value = row.id
  currentPermIds.value = row.permissionIds || []
  permDialogVisible.value = true
}

const handleSavePerm = async () => {
  const checkedKeys = permTree2.value?.getCheckedKeys() || []
  saving.value = true
  try {
    await assignRolePermissions(currentRoleId.value, checkedKeys)
    ElMessage.success('分配权限成功')
    permDialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除角色 "${row.name}"?`, '提示', { type: 'warning' }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => { loadData() })
</script>
