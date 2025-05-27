package com.tech.blog.repository;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

import static com.tech.blog.domain.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepository{
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

    public Page<Post> getPostByPaging(Pageable pageable, PostType postType, String keyword) {
        List<Post> content = queryFactory
                .selectFrom(post)
                .where(
                        postType != PostType.ALL ? post.type.eq(postType) : null,
                        post.title.like("%" + keyword + "%")
                )
                .orderBy(postSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        postType != PostType.ALL ? post.type.eq(postType) : null,
                        post.title.like("%" + keyword + "%")
                );


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public boolean existsByTitleAndUrl(String title, String url) {
        Post findPost = queryFactory.selectFrom(post)
                .where(post.title.eq(title)
                        .and(post.url.eq(url))).fetchFirst();
        return findPost != null;
    }

    private OrderSpecifier<?> postSort(Pageable page) {
        if (!page.getSort().isEmpty()) {
            for (Sort.Order order : page.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                return new OrderSpecifier(direction, post.created_at);
            }
        }
        return new OrderSpecifier(Order.DESC, post.created_at);
    }


    public void save(Post post) {
        em.persist(post);
    }

    public void saveAll(List<Post> posts) {
        int batchSize = 100;
        for (int i = 0; i < posts.size(); i++) {
            em.persist(posts.get(i));
            if (i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    public void deleteAllPost() {
        queryFactory.delete(post)
                .execute();
    }

    public void deletePostByType(PostType postType) {
        queryFactory.delete(post)
                .where(post.type.eq(postType))
                .execute();
    }
}
