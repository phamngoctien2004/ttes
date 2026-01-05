package com.demo.news.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String keyword;
    private String category;
    private String author;
    private String startDate;
    private String endDate;
    private int page = 0;
    private int size = 10;
}
