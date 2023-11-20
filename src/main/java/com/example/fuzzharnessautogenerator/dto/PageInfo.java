package com.example.fuzzharnessautogenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int totalPages;
    private int currentPage;
    private long totalRows;
    private int pageSize;
}
