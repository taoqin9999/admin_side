package com.example.demo.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable
/**
 * 用户实体类
 * 对应数据库 t_user 表
 */
 {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String nickname;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // 非数据库字段
    private List<Long> roleIds;
    private String roleNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public List<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }

    public String getRoleNames() { return roleNames; }
    public void setRoleNames(String roleNames) { this.roleNames = roleNames; }
}
