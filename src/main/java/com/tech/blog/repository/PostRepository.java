package com.tech.blog.repository;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tech.blog.domain.Post;
import com.tech.blog.domain.PostType;
import com.tech.blog.domain.QPost;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

import static com.tech.blog.domain.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostRepository implements PostRepositoryImpl{
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

    private OrderSpecifier<?> postSort(Pageable page) {
        if (!page.getSort().isEmpty()) {
            for (Sort.Order order : page.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                return new OrderSpecifier(direction, post.created_at);
            }
        }
        return new OrderSpecifier(Order.DESC, post.created_at);
    }


    public void insertPost(Post post) {
        em.persist(post);
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
