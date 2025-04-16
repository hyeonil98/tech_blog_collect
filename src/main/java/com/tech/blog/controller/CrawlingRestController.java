package com.tech.blog.controller;

import com.tech.blog.crawling.carrot.CarrotCrawling;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlingRestController {
    @GetMapping(value = "/crawling")
    public ResponseEntity<String> crawling() throws Exception {
        CarrotCrawling crawling = new CarrotCrawling();
        String carrotData = crawling.getCarrotData();
        return ResponseEntity.ok(carrotData);
    }
}
