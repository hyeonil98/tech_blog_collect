package com.tech.blog.controller;

import com.tech.blog.domain.Post;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        List<Post> allPost = postService.getAllPosts();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("posts", allPost);
        modelAndView.setViewName("index");
        return modelAndView;
    }
}
