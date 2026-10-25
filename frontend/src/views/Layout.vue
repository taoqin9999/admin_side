<!--
  后台管理布局 Layout
  侧边栏菜单 + 顶部导航栏 + 主体内容区
-->
<template>
  <el-container style="height:100vh">
    <el-aside :width="isCollapse ? '64px' : '220px'" style="background:#304156; transition: width 0.3s">
      <div style="height:60px; display:flex; align-items:center; justify-content:center; color:#fff; font-size:18px; font-weight:bold; border-bottom:1px solid rgba(255,255,255,0.1)">
        {{ isCollapse ? '管' : '管理系统' }}
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
        style="border-right:none"
      >
        <template v-for="menu in userStore.menus" :key="menu.id">
          <el-menu-item v-if="!menu.children || menu.children.length === 0" :index="menu.url">
            <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
            <template #title>{{ menu.name }}</template>
          </el-menu-item>
          <el-sub-menu v-else :index="menu.url">
            <template #title>
              <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.url">
              {{ child.name }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff; border-bottom:1px solid #e6e6e6; display:flex; align-items:center; justify-content:space-between; height:60px; padding:0 20px">
        <div>
          <el-button :icon="isCollapse ? 'Expand' : 'Fold'" text @click="isCollapse = !isCollapse" />
          <span style="margin-left:10px; color:#666">欢迎您，{{ userStore.user?.nickname || userStore.user?.username }}</span>
        </div>
        <div>
          <el-button type="primary" text @click="openPwdDialog">修改密码</el-button>
          <el-button type="danger" text @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main style="background:#f0f2f5; padding:20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码弹窗 -->
  <el-dialog v-model="pwdDialogVisible" title="修改密码" width="440px" :close-on-click-modal="false" @closed="resetPwdForm">
    <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-50 位" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="pwdDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="pwdLoading" @click="submitPwd">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
// 布局组件逻辑
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store'
import { updatePassword } from '../api/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
// 侧边栏是否折叠
const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// ---------- 修改密码 ----------
const pwdDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 50, message: '新密码长度需在 6-50 位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const openPwdDialog = () => {
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  pwdDialogVisible.value = true
}

const resetPwdForm = () => {
  pwdFormRef.value?.resetFields()
}

const submitPwd = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await updatePassword({
        oldPassword: pwdForm.value.oldPassword,
        newPassword: pwdForm.value.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      pwdDialogVisible.value = false
      handleLogout()
    } catch (e) {
      // 错误提示已由 request 拦截器统一处理
    } finally {
      pwdLoading.value = false
    }
  })
}

onMounted(() => {
  if (!userStore.user) {
    userStore.initFromSession()
  }
  if (!userStore.user) {
    router.push('/login')
  }
})
</script>
