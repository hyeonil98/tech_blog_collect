package com.tech.blog.batch;

import com.tech.blog.domain.Post;
import com.tech.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostWriter implements ItemWriter<Post>{
    private final PostService postService;
    @Override
    public void write(Chunk<? extends Post> posts) throws Exception {
        postService.saveAll((List<Post>) posts);
    }

}
