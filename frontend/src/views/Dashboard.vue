<!--
  首页仪表盘 Dashboard
  展示系统功能卡片
-->
<template>
  <div>
    <el-card>
      <h2>欢迎使用管理系统</h2>
      <p style="margin-top:20px; color:#666">请选择左侧菜单进行管理操作。</p>
      <el-row :gutter="20" style="margin-top:30px">
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header><span>用户管理</span></template>
            <p>管理所有系统用户，支持增删改查、分配角色。</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header><span>角色管理</span></template>
            <p>管理系统角色，支持增删改查、分配权限。</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header><span>权限管理</span></template>
            <p>管理系统权限和菜单，控制页面和按钮显示。</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>
