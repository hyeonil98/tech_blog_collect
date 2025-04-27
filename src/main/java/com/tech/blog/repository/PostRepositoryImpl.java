package com.tech.blog.repository;


import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.tech.blog.domain.QPost.post;

public interface PostRepositoryImpl {
    List<Post> getAllPosts();


    List<Post> getPostByType(PostType postType);

    Page<Post> getPostByPaging(Pageable pageable, PostType postType, String keyword);


    @Transactional
    void insertPost(Post post);

    @Transactional
    void deleteAllPost();

    @Transactional
    void deletePostByType(PostType postType);
}
