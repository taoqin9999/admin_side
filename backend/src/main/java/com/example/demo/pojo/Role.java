package com.example.demo.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Role implements Serializable
/**
 * 角色实体类
 * 对应数据库 t_role 表
 */
 {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String memo;
    private Date createTime;

    // 非数据库字段
    private List<Long> permissionIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public List<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(List<Long> permissionIds) { this.permissionIds = permissionIds; }
}
