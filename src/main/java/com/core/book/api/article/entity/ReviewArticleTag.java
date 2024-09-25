package com.core.book.api.article.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "REVIEWARTICLE_TAG")
public class ReviewArticleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewarticle_tag_id")
    private Long id;

    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
}
