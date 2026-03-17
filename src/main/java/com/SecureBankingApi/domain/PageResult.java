package com.SecureBankingApi.domain;

import java.util.List;

public class PageResult<T> {
    private final int page;
    private final int size;
    private final long totalElements;
    private final List<T> content;

    public PageResult(int page, int size, long totalElements, List<T> content) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    public boolean isFirst() {
        return page == 0;
    }

    public boolean isLast() {
        return page >= getTotalPages() - 1;
    }
}
