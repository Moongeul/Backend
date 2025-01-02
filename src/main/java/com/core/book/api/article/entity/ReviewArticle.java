package com.core.book.api.article.entity;

import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "REVIEW_ARTICLE")
public class ReviewArticle extends Article {

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    private String oneLineReview; //한줄평 리뷰
    private float rating; // 평점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reviewarticle_tag_id")
    private ReviewArticleTag reviewArticleTag;

    public ReviewArticle update(ReviewArticleCreateDTO dto, Book newBook) {
        return this.toBuilder()
                .content(dto.getContent())
                .oneLineReview(dto.getOneLineReview())
                .rating(dto.getRating())
                .book(newBook)
                .reviewArticleTag(
                        dto.getReviewArticleTagDTO() != null
                                ? (this.reviewArticleTag != null
                                ? this.reviewArticleTag.update(dto.getReviewArticleTagDTO())
                                : dto.getReviewArticleTagDTO().toEntity())
                                : this.reviewArticleTag)
                .build();
    }

    // 댓글 수 증가
    @Override
    public ReviewArticle increaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() + 1)
                .build();
    }

    // 댓글 수 감소
    @Override
    public ReviewArticle decreaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() - 1)
                .build();
    }

    // 좋아요 증가
    public Article increaseLikeCount() {
        return this.toBuilder()
                .likeCnt(this.getLikeCnt() + 1)
                .build();
    }

    // 좋아요 감소
    public Article decreaseLikeCount() {
        return this.toBuilder()
                .likeCnt(this.getLikeCnt() - 1)
                .build();
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public Member getMember() {
        return this.member;
    }

}
