package com.tech.blog.batch;

import com.tech.blog.domain.Post;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostDeduplicationProcessor implements ItemProcessor<Post, Post> {
    private final PostService postService;
    @Override
    public Post process(Post post) throws Exception {
        return postService.existsByTitleAndUrl(post.getTitle(), post.getUrl()) ? null : post;
    }
}
