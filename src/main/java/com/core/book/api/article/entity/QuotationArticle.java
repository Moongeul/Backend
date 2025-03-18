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
    @JoinColumn(name = "quoted_article_id")
    private Article quotedArticle;

    // 댓글 수 증가
    @Override
    public void increaseCommentCount() {
        this.commentCnt++;
    }

    // 댓글 수 감소
    @Override
    public void decreaseCommentCount() {
        this.commentCnt--;
    }

    // 좋아요 증가
    @Override
    public void increaseLikeCount() {
        this.likeCnt++;
    }

    // 좋아요 감소
    @Override
    public void decreaseLikeCount() {
        this.likeCnt--;
    }

    // 인용 수 증가
    @Override
    public void increaseQuoCount() {
        this.quoCnt++;
    }

    // 인용 수 감소
    @Override
    public void decreaseQuoCount() {
        this.quoCnt--;
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
