package com.tech.blog.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = -1358875895L;

    public static final QPost post = new QPost("post");

    public final DateTimePath<java.util.Date> collected_at = createDateTime("collected_at", java.util.Date.class);

    public final DateTimePath<java.util.Date> created_at = createDateTime("created_at", java.util.Date.class);

    public final NumberPath<Integer> idx = createNumber("idx", Integer.class);

    public final StringPath title = createString("title");

    public final EnumPath<PostType> type = createEnum("type", PostType.class);

    public final StringPath url = createString("url");

    public QPost(String variable) {
        super(Post.class, forVariable(variable));
    }

    public QPost(Path<? extends Post> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPost(PathMetadata metadata) {
        super(Post.class, metadata);
    }

}

