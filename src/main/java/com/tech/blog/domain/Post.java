package com.tech.blog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    private String title;

    @Column(length = 500)
    private String url;

    private Date created_at;
    private Date collected_at;

    @Enumerated(EnumType.STRING)
    private PostType type;
}
