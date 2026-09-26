package com.example.demo.pojo;

import java.io.Serializable;
import java.util.Date;

public class SysConfig implements Serializable
/**
 * 系统参数实体类
 * 对应数据库 t_sys_config 表
 */
 {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String paramKey;
    private String paramValue;
    private String description;
    private String regex;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getParamKey() { return paramKey; }
    public void setParamKey(String paramKey) { this.paramKey = paramKey; }

    public String getParamValue() { return paramValue; }
    public void setParamValue(String paramValue) { this.paramValue = paramValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRegex() { return regex; }
    public void setRegex(String regex) { this.regex = regex; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}