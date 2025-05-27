package com.tech.blog.crawling;

import com.tech.blog.domain.Post;

import java.text.ParseException;
import java.util.List;

public interface TechBlogCrawler {
    List<Post> crawling() throws ParseException;
    String getSiteKey();
}
