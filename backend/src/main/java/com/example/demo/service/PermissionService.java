package com.example.demo.service;

import com.example.demo.dao.PermissionMapper;
import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * 权限业务逻辑层
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    public List<Permission> findAll() {
        return permissionMapper.findAll();
    }

    public PageResult<Permission> findPage(int page, int pageSize) {
        return findPage(page, pageSize, null, null);
    }

    public PageResult<Permission> findPage(int page, int pageSize, String code, String name) {
        int offset = (page - 1) * pageSize;
        List<Permission> list = permissionMapper.findPage(offset, pageSize, code, name);
        long total = permissionMapper.countByCondition(code, name);
        return new PageResult<>(list, total, page, pageSize);
    }

    /**
     * 获取树形菜单 (仅菜单类型)
     */
    public List<Permission> findMenuTree() {
        List<Permission> all = permissionMapper.findAll();
        List<Permission> menus = all.stream()
                .filter(p -> p.getPermType() == 1 && p.getStatus() == 1)
                .collect(Collectors.toList());
        return buildTree(menus);
    }

    /**
     * 根据用户名获取菜单树
     */
    public List<Permission> findMenuTreeByUsername(String username) {
        List<Permission> all = permissionMapper.findByUsername(username);
        List<Permission> menus = all.stream()
                .filter(p -> p.getPermType() == 1)
                .collect(Collectors.toList());
        return buildTree(menus);
    }

    /**
     * 根据用户名获取权限 code 列表
     */
    public List<String> findPermCodesByUsername(String username) {
        List<Permission> all = permissionMapper.findByUsername(username);
        return all.stream().map(Permission::getCode).collect(Collectors.toList());
    }

    private List<Permission> buildTree(List<Permission> list) {
        List<Permission> trees = new ArrayList<>();
        for (Permission p : list) {
            if (p.getParentId() == null || p.getParentId() == 0) {
                p.setChildren(getChildren(p.getId(), list));
                trees.add(p);
            }
        }
        trees.sort((a, b) -> Integer.compare(a.getSort(), b.getSort()));
        return trees;
    }

    private List<Permission> getChildren(Long parentId, List<Permission> list) {
        List<Permission> children = new ArrayList<>();
        for (Permission p : list) {
            if (parentId.equals(p.getParentId())) {
                p.setChildren(getChildren(p.getId(), list));
                children.add(p);
            }
        }
        children.sort((a, b) -> Integer.compare(a.getSort(), b.getSort()));
        return children;
    }

    public Permission findById(Long id) {
        return permissionMapper.findById(id);
    }

    public void add(Permission permission) {
        permissionMapper.insert(permission);
    }

    public void update(Permission permission) {
        permissionMapper.update(permission);
    }

    public void delete(Long id) {
        permissionMapper.deleteById(id);
    }
}