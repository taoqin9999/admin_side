<!--
  用户管理页面
  展示用户列表，支持分页查询、用户名/昵称模糊搜索、新增/编辑/删除用户、分配角色
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>用户管理</span>
          <el-button v-if="hasPerm('user:add')" type="primary" @click="openDialog(null)">新增用户</el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <div style="display:flex; gap:12px; margin-bottom:16px; flex-wrap:wrap; align-items:center">
        <el-input v-model="searchForm.username" placeholder="用户名" clearable style="width:160px" @clear="handleSearch" />
        <el-input v-model="searchForm.nickname" placeholder="昵称" clearable style="width:160px" @clear="handleSearch" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="userList" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="roleNames" label="角色" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '锁定' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('user:edit')" type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="hasPerm('user:assignRole')" type="warning" link size="small" @click="openRoleDialog(row)">分配角色</el-button>
            <el-button v-if="hasPerm('user:delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" type="password" show-password :placeholder="isEdit ? '留空则不修改' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">锁定</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width:100%">
            <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-checkbox-group v-model="currentRoleIds">
        <el-checkbox v-for="r in roleList" :key="r.id" :label="r.id" style="margin-bottom:10px">
          {{ r.name }} - {{ r.memo }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 组件逻辑：数据加载、搜索、增删改操作
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser, assignUserRoles } from '../../api/user'
import { getRoleList } from '../../api/role'
import { useUserStore } from '../../store'

const userStore = useUserStore()
const userList = ref([])
const roleList = ref([])
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
  username: '',
  nickname: ''
})
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentUserId = ref(null)
const currentRoleIds = ref([])

const form = ref({
  username: '',
  password: '',
  nickname: '',
  status: 1,
  roleIds: []
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const hasPerm = (perm) => userStore.hasPerm(perm)

const loadData = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (searchForm.value.username) params.username = searchForm.value.username
    if (searchForm.value.nickname) params.nickname = searchForm.value.nickname
    const res = await getUserList(params)
    userList.value = res.data?.list || []
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
  searchForm.value = { username: '', nickname: '' }
  page.value = 1
  loadData()
}

const loadRoles = async () => {
  const res = await getRoleList()
  roleList.value = res.data?.list || []
}

const openDialog = (row) => {
  isEdit.value = !!row
  if (row) {
    form.value = {
      username: row.username,
      password: '',
      nickname: row.nickname,
      status: row.status,
      roleIds: row.roleIds || []
    }
  } else {
    form.value = { username: '', password: '', nickname: '', status: 1, roleIds: [] }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateUser(userList.value.find(u => u.username === form.value.username)?.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addUser(form.value)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const openRoleDialog = (row) => {
  currentUserId.value = row.id
  currentRoleIds.value = row.roleIds ? [...row.roleIds] : []
  roleDialogVisible.value = true
}

const handleSaveRole = async () => {
  saving.value = true
  try {
    await assignUserRoles(currentUserId.value, currentRoleIds.value)
    ElMessage.success('分配角色成功')
    roleDialogVisible.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户 "${row.username}"?`, '提示', { type: 'warning' }).then(async () => {
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadRoles()
})
</script>
