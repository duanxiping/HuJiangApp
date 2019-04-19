package com.hujiang.hujiangapp.model;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int size;

    public List<T> getContent() {
        if (content == null) {
            return new ArrayList<T>();
        }
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
