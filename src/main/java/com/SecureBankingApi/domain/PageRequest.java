package com.SecureBankingApi.domain;

public class PageRequest {
    private int page;
    private int size;
    private final String sortBy;
    private final String sortDirection;

    public PageRequest(String sortBy, String sortDirection, int size, int page) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        this.size = size;
        this.page = page;
    }



    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }
}
