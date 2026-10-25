package com.example.demo.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统操作日志实体类
 * 对应数据库 t_sys_log 表
 */
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 用户操作描述 */
    private String operation;
    /** 响应时间(毫秒) */
    private Long time;
    /** 请求方法：类名.方法名() */
    private String method;
    /** 请求参数 */
    private String params;
    /** 客户端 IP */
    private String ip;
    /** 创建时间 */
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Long getTime() { return time; }
    public void setTime(Long time) { this.time = time; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
