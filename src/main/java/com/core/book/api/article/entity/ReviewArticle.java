package com.core.book.api.article.entity;

import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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

    // 추후 감상평 게시글에서 자신을 인용한 인용 게시글 목록을 관리할 수 있도록 코드 추가
    @OneToMany(mappedBy = "reviewArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationArticle> quotationArticles = new ArrayList<>();

    public void addQuotationArticle(QuotationArticle quotationArticle) {
        quotationArticles.add(quotationArticle);
    }


    public ReviewArticle update(ReviewArticleCreateDTO dto, Book newBook) {
        return this.toBuilder()
                .content(dto.getContent())
                .oneLineReview(dto.getOneLineReview())
                .rating(dto.getRating())
                .book(newBook)
                .build();
    }

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
