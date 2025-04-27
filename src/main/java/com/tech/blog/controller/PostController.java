package com.tech.blog.controller;

import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public ModelAndView home(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "ALL") String type,
                             @RequestParam(defaultValue = "") String keyword){
        int pageSize = 10;
        PostType postType = PostType.valueOf(StringUtils.capitalize(type));
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Post> postByPaging = postService.getPostByPaging(pageRequest, postType, keyword);

        int totalPages = postByPaging.getTotalPages();
        int currentPage = postByPaging.getNumber(); // 0-based
        int groupSize = 10;

        int start = (currentPage / groupSize) * groupSize;
        int end = Math.min(start + groupSize, totalPages);

        List<Integer> pageNumbers = IntStream.range(start, end).boxed().collect(Collectors.toList());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("page", page);
        modelAndView.addObject("type", type);
        modelAndView.addObject("keyword", keyword);

        modelAndView.addObject("posts", postByPaging);
        modelAndView.addObject("pageNumbers", pageNumbers);
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("hasPrevGroup", start > 0);
        modelAndView.addObject("hasNextGroup", end < totalPages);
        modelAndView.setViewName("index");

        return modelAndView;
    }




}
