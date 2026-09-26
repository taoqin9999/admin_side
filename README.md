# 管理系统（Admin Management System）

基于 **Spring Boot + Shiro + Vue 3 + Element Plus** 的通用后台权限管理系统，实现用户、角色、权限的细粒度管理与控制。

---

## 目录

- [项目结构](#项目结构)
- [技术栈](#技术栈)
- [功能概述](#功能概述)
- [快速开始（后台）](#快速开始后台)
- [快速开始（前台）](#快速开始前台)
- [访问系统](#访问系统)
- [默认账号](#默认账号)
- [API 文档](#api-文档)
- [数据库说明](#数据库说明)

---

## 项目结构

`
E:\code\admin_side\
├── backend/                  # 后台（Spring Boot）
│   ├── src/main/java/        # Java 源码
│   ├── src/main/resources/   # 配置文件和 MyBatis Mapper
│   └── pom.xml               # Maven 依赖配置
├── frontend/                 # 前台（Vue 3）
│   ├── src/                  # Vue 源码
│   ├── package.json          # npm 依赖配置
│   └── vite.config.js        # Vite 构建配置
└── sql/
    └── init.sql              # 数据库初始化脚本
`

---

## 技术栈

### 后台（backend）

| 技术                | 说明                     |
|---------------------|--------------------------|
| Spring Boot 2.3.12  | 应用框架                 |
| Apache Shiro 1.7.1  | 身份认证与权限授权       |
| MyBatis + Spring Boot 2.1.4 | ORM 框架          |
| MySQL               | 数据库                   |
| Druid 1.2.6         | 数据库连接池 & 监控      |
| Lombok              | 代码简化                 |
| Maven               | 项目构建                 |

### 前台（frontend）

| 技术                  | 说明                 |
|-----------------------|----------------------|
| Vue 3                 | 前端框架             |
| Vite 4                | 构建工具             |
| Vue Router 4          | 前端路由             |
| Pinia 2               | 状态管理             |
| Element Plus 2.4      | 组件库               |
| Axios                 | HTTP 请求封装        |

---

## 功能概述

### 后台功能

后台提供完整的 **RESTful API**，所有接口以 /api 为前缀，统一返回 { code, msg, data } 格式：

| 模块         | 功能                                                   | 接口路径                     |
|--------------|--------------------------------------------------------|------------------------------|
| 登录认证     | 用户登录（MD5 加密）、获取用户信息、退出登录            | POST /api/login 等         |
| 用户管理     | 用户列表分页查询、新增、编辑、删除、分配角色            | /api/user/**               |
| 角色管理     | 角色列表分页查询、新增、编辑、删除、分配权限            | /api/role/**               |
| 权限管理     | 权限/菜单列表分页查询、新增、编辑、删除、获取菜单树    | /api/permission/**         |

**权限控制**：基于 Shiro 注解 @RequiresPermissions 实现细粒度的接口权限校验，如 user:add、ole:delete 等。

### 前台功能

| 页面               | 功能说明                                                                 |
|--------------------|--------------------------------------------------------------------------|
| 登录页             | 用户名/密码登录，表单验证                                                 |
| 首页仪表盘         | 展示系统功能卡片，快速导航                                                 |
| 用户管理           | 用户列表分页展示、用户名/昵称模糊搜索、新增/编辑/删除用户、分配角色       |
| 角色管理           | 角色列表分页展示、角色标识/描述模糊搜索、新增/编辑/删除角色、分配权限     |
| 权限管理           | 权限列表分页展示、权限标识/名称模糊搜索、新增/编辑/删除权限               |

**权限控制**：前端根据后端返回的权限标识列表，通过 -if="hasPerm('xxx')" 控制按钮和功能的显示隐藏。

---

## 快速开始（后台）

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+ / 8.0

### 步骤

1. **创建数据库**

   连接 MySQL 并执行以下命令创建数据库：
   `sql
   CREATE DATABASE IF NOT EXISTS server DEFAULT CHARSET utf8mb4;
   `

2. **导入表结构和初始数据**

   使用 MySQL 客户端（命令行、Navicat、DBeaver 等）执行初始化脚本：
   `
   E:\code\admin_side\sql\init.sql
   `
   脚本会自动创建 	_user、	_role、	_permission 等 5 张表，并插入初始管理员账号和权限数据。

3. **修改数据库连接配置**

   编辑 E:\code\admin_side\backend\src\main\resources\application.yml，将数据库连接信息改为你的配置：
   `yaml
   spring:
     datasource:
       druid:
         url: jdbc:mysql://你的IP:3306/server?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
         username: 你的数据库用户名
         password: 你的数据库密码
   `

4. **启动后台服务**

   打开命令行进入 backend 目录，执行：
   `ash
   cd E:\code\admin_side\backend
   mvn spring-boot:run
   `
   或者先打包再运行：
   `ash
   mvn clean package -DskipTests
   java -jar target\shiro-admin-1.0.0.jar
   `

   启动成功后，控制台会输出类似 Started Application in X.XXX seconds 的日志。

5. **验证后台是否启动成功**

   浏览器访问：http://localhost:8080/api/login
   应返回 {"code":401,"msg":"未登录或登录已过期"}

6. **（可选）访问 Druid 监控**

   - 地址：http://localhost:8080/druid/
   - 用户名：druid
   - 密码：druid123

---

## 快速开始（前台）

### 环境要求

- Node.js 16+
- npm 8+（或 yarn / pnpm）

### 步骤

1. **安装依赖**

   打开命令行进入 frontend 目录：
   `ash
   cd E:\code\admin_side\frontend
   npm install
   `

2. **启动开发服务器**

   `ash
   npm run dev
   `

   启动成功后，控制台会显示：http://localhost:3000

3. **（可选）构建生产包**

   `ash
   npm run build
   `
   构建产物在 rontend/dist/ 目录下。

---

## 访问系统

1. 确保**后台**（端口 8080）和**前台**（端口 3000）均已启动
2. 浏览器打开：http://localhost:3000
3. 进入登录页，使用默认账号登录（见下方）

> **注意**：前台开发服务器已配置代理（ite.config.js），将以 /api 开头的请求转发到 http://localhost:8080，因此无需单独配置跨域。

---

## 默认账号

| 用户名   | 密码     | 角色     | 权限说明                         |
|----------|----------|----------|----------------------------------|
| dmin  | 123456 | 超级管理员 | 拥有所有菜单和操作权限           |
| 	est   | 123456 | 测试账户   | 仅有查看权限，无新增/编辑/删除权限 |

> 登录页默认填充了 dmin / 123456，可直接点击登录。

---

## API 文档

### 统一响应格式

所有接口返回 JSON 格式：
`json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
`

| code | 说明         |
|------|--------------|
| 200  | 成功         |
| 401  | 未登录/认证失败 |
| 403  | 无权限       |
| 500  | 服务器内部错误 |

### 核心接口清单

| 方法     | 路径                          | 权限              | 说明                     |
|----------|-------------------------------|-------------------|--------------------------|
| POST     | /api/login                  | 匿名              | 用户登录                 |
| GET      | /api/login                  | 匿名              | 检测未登录状态（返回401） |
| GET      | /api/user/info              | 需认证            | 获取当前用户信息         |
| POST     | /api/logout                 | 需认证            | 退出登录                 |
| GET      | /api/user                   | user:list       | 用户列表（分页）         |
| POST     | /api/user                   | user:add        | 新增用户                 |
| PUT      | /api/user/{id}              | user:edit       | 编辑用户                 |
| DELETE   | /api/user/{id}              | user:delete     | 删除用户                 |
| POST     | /api/user/{id}/roles        | user:assignRole | 分配用户角色             |
| GET      | /api/role                   | ole:list       | 角色列表（分页）         |
| POST     | /api/role                   | ole:add        | 新增角色                 |
| PUT      | /api/role/{id}              | ole:edit       | 编辑角色                 |
| DELETE   | /api/role/{id}              | ole:delete     | 删除角色                 |
| POST     | /api/role/{id}/permissions  | ole:assignPerm | 分配角色权限             |
| GET      | /api/permission             | permission:list | 权限列表（分页）         |
| GET      | /api/permission/tree        | permission:list | 获取菜单树               |
| POST     | /api/permission             | permission:add  | 新增权限                 |
| PUT      | /api/permission/{id}        | permission:edit | 编辑权限                 |
| DELETE   | /api/permission/{id}        | permission:delete | 删除权限               |

---

## 数据库说明

系统包含 5 张表：

| 表名                | 说明                 |
|---------------------|----------------------|
| 	_user            | 用户表               |
| 	_role            | 角色表               |
| 	_permission      | 权限/菜单表          |
| 	_user_role       | 用户-角色关联表      |
| 	_role_permission | 角色-权限关联表      |

权限模型中：
- **菜单**（perm_type=1）：侧边栏导航菜单项，有图标和路由路径
- **按钮/功能**（perm_type=2）：页面中的操作按钮，如新增、编辑、删除等

> 密码使用 **MD5** 加密，加密方式为 MD5(用户名 + 密码)。初始密码 123456 的 MD5 值为 e10adc3949ba59abbe56e057f20f883e。
