package com.tja.bh.unsplash.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    private int total;
    @JsonProperty("total_pages")
    private int totalPages;
    private ArrayList<Photo> results;

    public SearchResult() {
        this.total = 0;
        this.totalPages = 0;
        this.results = new ArrayList<>();
    }

    public SearchResult(int total, int totalPages, ArrayList<Photo> results) {
        this.total = total;
        this.totalPages = totalPages;
        this.results = results;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setResults(ArrayList<Photo> results) {
        this.results = results;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public ArrayList<Photo> getResults() {
        return results;
    }
}
