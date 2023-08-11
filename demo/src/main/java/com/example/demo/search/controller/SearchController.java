package com.example.demo.search.controller;

import com.example.demo.common.dto.MessageResponse;
import com.example.demo.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<MessageResponse> search(@RequestParam("keyword") String keyword, @RequestParam("checkinDate") LocalDate checkinDate,
                                                  @RequestParam("checkoutDate") LocalDate checkoutDate, @RequestParam("day") Long day) {
        return ResponseEntity.ok().body(searchService.search(keyword, checkinDate, checkoutDate, day));
    }

    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<MessageResponse> detail(@RequestParam("keyword") String keyword, @RequestParam("checkinDate") LocalDate checkinDate,
                       @RequestParam("checkoutDate") LocalDate checkoutDate, @RequestParam("day") Long day){
        return ResponseEntity.ok().body(searchService.detail(keyword, checkinDate, checkoutDate, day));
    }
}
