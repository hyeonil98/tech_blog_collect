package com.tech.blog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.domain.QPost;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.tech.blog.domain.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public List<Post> getAllPosts() {
        return queryFactory.selectFrom(post).fetch();
    }

    public List<Post> getPostByType(PostType postType) {
        return queryFactory.selectFrom(post)
                .where(post.type.eq(postType))
                .fetch();
    }

    @Transactional
    public void insertPost(Post post) {
        em.persist(post);
    }

    @Transactional
    public void deleteAllPost() {
        queryFactory.delete(post)
                .execute();
    }

    @Transactional
    public void deletePostByType(PostType postType) {
        queryFactory.delete(post)
                .where(post.type.eq(postType))
                .execute();
    }
}
