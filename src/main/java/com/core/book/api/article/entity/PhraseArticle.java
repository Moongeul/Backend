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
@Table(name = "PHRASE_ARTICLE")
public class PhraseArticle extends Article {

    @Column(columnDefinition = "TEXT")
    private String content; // 구절에 대한 자세한 설명

    private long likeCnt; // 좋아요 수
    private long quoCnt; // 인용 수
    private long commentCnt; // 댓글 수
    private int pageNum; // 페이지 번호

    @Column
    private String phraseContent; // 인용 할 구절

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    // 댓글 수 증가
    @Override
    public PhraseArticle increaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() + 1)
                .build();
    }

    // 댓글 수 감소
    @Override
    public PhraseArticle decreaseCommentCount() {
        return this.toBuilder()
                .commentCnt(this.getCommentCnt() - 1)
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