package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewArticleDetailDTO {
    private final Long memberId;
    private final String isbn;
    private final String title;
    private final String author;
    private final String bookImage;
    private final String content;
    private final String oneLineReview;
    private final long likeCnt;
    private final long quoCnt;
    private final long commentCnt;
    private final float starRating;
    private final ReviewArticleTagDTO reviewArticleTagDTO;
    private final String nickname;
    private final String profileImage;
    private final long followerCount;
    private final String date;
    private final boolean myLike;

    @Builder
    public ReviewArticleDetailDTO(Long memberId, String isbn, String title, String author, String bookImage, String content, String date,
                                  String oneLineReview, long likeCnt, long quoCnt, long commentCnt, float starRating, ReviewArticleTagDTO reviewArticleTagDTO,
                                  String nickname, String profileImage, long followerCount, boolean myLike) {
        this.memberId = memberId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookImage = bookImage;
        this.content = content;
        this.oneLineReview = oneLineReview;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.starRating = starRating;
        this.reviewArticleTagDTO = reviewArticleTagDTO;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.followerCount = followerCount;
        this.date = date;
        this.myLike = myLike;
    }
}