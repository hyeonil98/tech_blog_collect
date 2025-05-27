package com.tech.blog.repository;


import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository {
    List<Post> getAllPosts();


    List<Post> getPostByType(PostType postType);

    Page<Post> getPostByPaging(Pageable pageable, PostType postType, String keyword);

    boolean existsByTitleAndUrl(String title, String url);

    void save(Post post);

    void saveAll(List<Post> posts);

    void deleteAllPost();

    void deletePostByType(PostType postType);
}
