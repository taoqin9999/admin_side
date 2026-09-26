package com.example.demo.pojo;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable
/**
 * 分页查询结果封装
 */
 {
    private static final long serialVersionUID = 1L;

    private List<T> list;
    private long total;
    private int page;
    private int pageSize;
    private long totalPages;

    public PageResult() {}

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (total + pageSize - 1) / pageSize;
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public long getTotalPages() { return totalPages; }
    public void setTotalPages(long totalPages) { this.totalPages = totalPages; }
}