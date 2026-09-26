-- ============================================================
-- 管理系统 初始化脚本  (MySQL 5.7+ / 8.0)
-- ============================================================
-- 数据库: 请先执行: CREATE DATABASE IF NOT EXISTS server DEFAULT CHARSET utf8mb4;

DROP TABLE IF EXISTS t_role_permission;
DROP TABLE IF EXISTS t_user_role;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_user;

CREATE TABLE t_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username    VARCHAR(50)  NOT NULL                COMMENT '用户名',
  password    VARCHAR(128) NOT NULL                COMMENT '密码(MD5)',
  nickname    VARCHAR(50)  DEFAULT NULL            COMMENT '昵称',
  status      TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '状态 1:启用 0:锁定',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE t_role (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  name        VARCHAR(50)  NOT NULL                COMMENT '角色标识',
  memo        VARCHAR(100) DEFAULT NULL            COMMENT '角色描述',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE t_permission (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  code        VARCHAR(100) NOT NULL                COMMENT '权限标识(user:add)',
  name        VARCHAR(100) DEFAULT NULL            COMMENT '中文描述',
  url         VARCHAR(256) DEFAULT NULL            COMMENT '接口URL/菜单路径',
  parent_id   BIGINT       DEFAULT 0               COMMENT '父级ID, 0表示顶级',
  perm_type   TINYINT(1)   DEFAULT 1               COMMENT '类型 1:菜单 2:按钮/功能',
  icon        VARCHAR(50)  DEFAULT NULL            COMMENT '菜单图标',
  sort        INT          DEFAULT 0               COMMENT '排序',
  status      TINYINT(1)   DEFAULT 1               COMMENT '状态 1:显示 0:隐藏',
  create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_perm_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表(含菜单和按钮)';

CREATE TABLE t_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色';

CREATE TABLE t_role_permission (
  role_id       BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限';

-- 初始密码: 123456  md5值: e10adc3949ba59abbe56e057f20f883e
INSERT INTO t_user (id, username, password, nickname, status) VALUES
  (1, 'admin', '1fedc5a36d03c185065dd2b323886aa5', '超级管理员', 1),
  (2, 'test',  '7a38c13ec5e9310aed731de58bbc4214', '测试账号',   1);

INSERT INTO t_role (id, name, memo) VALUES
  (1, 'admin', '超级管理员'),
  (2, 'test',  '测试账户');

-- 权限数据: parent_id=0 表示顶级菜单, perm_type=1菜单 perm_type=2按钮/功能
INSERT INTO t_permission (id, code, name, url, parent_id, perm_type, icon, sort, status) VALUES
  -- 顶级菜单
  (1,  'sys:user',         '用户管理', '/user',      0, 1, 'User',     1,  1),
  (2,  'sys:role',         '角色管理', '/role',      0, 1, 'Role',     2,  1),
  (3,  'sys:permission',   '权限管理', '/permission',0, 1, 'Lock',     3,  1),
  -- 用户管理 按钮/功能
  (10, 'user:list',        '查看用户列表',  '/api/user',        1, 2, NULL, 1, 1),
  (11, 'user:add',         '新增用户',      '/api/user',        1, 2, NULL, 2, 1),
  (12, 'user:edit',        '编辑用户',      '/api/user/*',      1, 2, NULL, 3, 1),
  (13, 'user:delete',      '删除用户',      '/api/user/*',      1, 2, NULL, 4, 1),
  (14, 'user:assignRole',  '分配用户角色',  '/api/user/*/roles',1, 2, NULL, 5, 1),
  -- 角色管理 按钮/功能
  (20, 'role:list',        '查看角色列表',       '/api/role',           2, 2, NULL, 1, 1),
  (21, 'role:add',         '新增角色',           '/api/role',           2, 2, NULL, 2, 1),
  (22, 'role:edit',        '编辑角色',           '/api/role/*',         2, 2, NULL, 3, 1),
  (23, 'role:delete',      '删除角色',           '/api/role/*',         2, 2, NULL, 4, 1),
  (24, 'role:assignPerm',  '分配角色权限',       '/api/role/*/permissions', 2, 2, NULL, 5, 1),
  -- 权限管理 按钮/功能
  (30, 'permission:list',  '查看权限列表',       '/api/permission',     3, 2, NULL, 1, 1),
  (31, 'permission:add',   '新增权限',           '/api/permission',     3, 2, NULL, 2, 1),
  (32, 'permission:edit',  '编辑权限',           '/api/permission/*',   3, 2, NULL, 3, 1),
  (33, 'permission:delete','删除权限',           '/api/permission/*',   3, 2, NULL, 4, 1);

INSERT INTO t_role_permission (role_id, permission_id) VALUES
  -- admin 拥有所有权限
  (1,1),(1,2),(1,3),(1,10),(1,11),(1,12),(1,13),(1,14),
  (1,20),(1,21),(1,22),(1,23),(1,24),
  (1,30),(1,31),(1,32),(1,33),
  -- test 只有查看权限
  (2,1),(2,2),(2,3),(2,10),(2,20),(2,30);

INSERT INTO t_user_role (user_id, role_id) VALUES (1,1),(2,2);
