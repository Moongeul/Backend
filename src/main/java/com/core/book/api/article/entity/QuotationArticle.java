package com.core.book.api.article.entity;

import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "QUOTATION_ARTICLE")
public class QuotationArticle extends Article{

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_article_id")
    private ReviewArticle reviewArticle;

    // 댓글 수 증가
    @Override
    public QuotationArticle increaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() + 1)
                .build();
    }

    // 댓글 수 감소
    @Override
    public QuotationArticle decreaseCommentCount() {
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
