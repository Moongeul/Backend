package com.core.book.api.article.entity;

import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "REVIEW_ARTICLE")
@AllArgsConstructor
public class ReviewArticle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_article_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @Enumerated(EnumType.STRING)
    private ArticleType type; // 게시글 타입

    private String oneLineReview; //한줄평 리뷰

    private long likeCnt; // 좋아요 수
    private long quoCnt; // 인용 수
    private long commentCnt; // 댓글 수
    private float starRating; // 평점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reviewarticle_tag_id")
    private ReviewArticleTag reviewArticleTag;

}
