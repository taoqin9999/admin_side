package com.example.demo.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Permission implements Serializable
/**
 * 权限/菜单实体类
 * 对应数据库 t_permission 表
 */
 {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String url;
    private Long parentId;
    private Integer permType;
    private String icon;
    private Integer sort;
    private Integer status;
    private Date createTime;

    // 非数据库字段
    private List<Permission> children;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Integer getPermType() { return permType; }
    public void setPermType(Integer permType) { this.permType = permType; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public List<Permission> getChildren() { return children; }
    public void setChildren(List<Permission> children) { this.children = children; }
}
