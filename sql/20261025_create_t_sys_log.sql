-- ============================================================
-- 系统操作日志表（AOP + 自定义注解 @Log 用户操作监控）
-- 归档日期：2026-10-25
-- 数据库：MySQL 5.7+ / 8.0，字符集 utf8mb4
-- 对应实体：com.example.demo.pojo.SysLog
-- 对应 Mapper：SysLogMapper / mapper/SysLogMapper.xml
-- 写入方：com.example.demo.aspect.LogAspect
-- 说明：仅建 t_sys_log 表；完整初始化请执行同目录 20261025_init.sql
-- ============================================================
DROP TABLE IF EXISTS t_sys_log;

CREATE TABLE t_sys_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  DEFAULT NULL COMMENT '操作人用户名',
    operation   VARCHAR(100) DEFAULT NULL COMMENT '用户操作描述（@Log 注解 value）',
    time        BIGINT       DEFAULT NULL COMMENT '接口响应时间(毫秒)',
    method      VARCHAR(200) DEFAULT NULL COMMENT '请求方法：类名.方法名()',
    params      TEXT         COMMENT '请求参数（参数名: 参数值，敏感字段已脱敏为***）',
    ip          VARCHAR(64)  DEFAULT NULL COMMENT '客户端IP地址',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_create_time (create_time),
    KEY idx_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统操作日志表';
