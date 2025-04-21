package com.tech.blog.controller;

import com.tech.blog.crawling.carrot.CarrotCrawling;
import com.tech.blog.domain.Post;
import com.tech.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlingRestController {
    @Autowired
    private PostService postService;

    @GetMapping(value = "/crawling")
    public ResponseEntity<String> crawling() throws Exception {
        CarrotCrawling crawling = new CarrotCrawling();
        String carrotData = crawling.getCarrotData();
        return ResponseEntity.ok(carrotData);
    }

    @GetMapping(value = "/v1/posts")
    public Page<Post> searchPost(Pageable pageable) throws Exception {
        return postService.getPostByPaging(pageable);
    }
}
