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
    public List<Post> getAllPosts();


    public List<Post> getPostByType(PostType postType);

    public Page<Post> getPostByPaging(Pageable pageable);


    @Transactional
    public void insertPost(Post post);

    @Transactional
    public void deleteAllPost();

    @Transactional
    public void deletePostByType(PostType postType);
}
