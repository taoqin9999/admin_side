import { createRouter, createWebHistory } from 'vue-router'
/**
 * Vue Router 路由配置
 * - 登录页 /login
 * - 管理页（Layout 布局 + 子页面）
 * - 路由守卫：未登录自动跳转登录页
 */
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import UserList from '../views/user/UserList.vue'
import RoleList from '../views/role/RoleList.vue'
import PermissionList from '../views/permission/PermissionList.vue'

const routes = [
  { path: '/login', name: 'Login', component: Login },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard, meta: { title: '首页' } },
      { path: 'user', name: 'User', component: UserList, meta: { title: '用户管理', perm: 'user:list' } },
      { path: 'role', name: 'Role', component: RoleList, meta: { title: '角色管理', perm: 'role:list' } },
      { path: 'permission', name: 'Permission', component: PermissionList, meta: { title: '权限管理', perm: 'permission:list' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('user')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
