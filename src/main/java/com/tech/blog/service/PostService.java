package com.tech.blog.service;

import com.tech.blog.controller.PostController;
import com.tech.blog.domain.Post;
import com.tech.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostController postController;
    private final PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    public void insertPost(Post post) {
        postRepository.insertPost(post);
    }
}
