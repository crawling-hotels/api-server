package com.example.demo.search.dto;

import com.example.demo.search.vo.CrawlingType;
import lombok.Getter;

import java.time.LocalDate;
@Getter
public class CrawlingRequest {
    private String keyword;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Long day;
    private CrawlingType crawlingType;

    public CrawlingRequest(String keyword, LocalDate checkinDate, LocalDate checkoutDate, Long day, CrawlingType crawlingType) {
        this.keyword = keyword;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
        this.day = day;
        this.crawlingType = crawlingType;
    }
}


