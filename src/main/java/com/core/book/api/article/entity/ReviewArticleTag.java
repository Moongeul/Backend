package com.core.book.api.article.entity;

import com.core.book.api.article.dto.ReviewArticleTagDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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

    public ReviewArticleTag update(ReviewArticleTagDTO dto) {
        return this.toBuilder()
                .tag1(dto.getTag1())
                .tag2(dto.getTag2())
                .tag3(dto.getTag3())
                .tag4(dto.getTag4())
                .tag5(dto.getTag5())
                .build();
    }
}
