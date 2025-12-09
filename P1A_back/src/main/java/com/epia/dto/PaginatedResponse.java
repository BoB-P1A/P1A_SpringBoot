package com.epia.dto;

import java.util.List;

public class PaginatedResponse<T> {
    public List<T> items;
    public int page;
    public int pageSize;
    public long total;
    public int totalPages;

    public PaginatedResponse() {}

    public PaginatedResponse(List<T> items, int page, int pageSize, long total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}