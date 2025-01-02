package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class QnaArticleDetailDTO {

    private final Long articleId;
    private final Long memberId;
    private final String nickname;
    private final String profileImage;
    private final long followerCount;
    private final String date;
    private final boolean myLike;

    private final long likeCnt;
    private final long quoCnt;
    private final long commentCnt;

    // 책 정보
    private final String isbn;
    private final String title;
    private final String author;
    private final String bookImage;

    private final List<QnaArticleContentDetailDTO> qnaContents;

    @Builder
    public QnaArticleDetailDTO(Long articleId,
                                  Long memberId,
                                  String nickname,
                                  String profileImage,
                                  String date,
                                  String isbn,
                                  String title,
                                  String author,
                                  String bookImage,
                                  long likeCnt,
                                  long quoCnt,
                                  long commentCnt,
                                  long followerCount,
                                  List<QnaArticleContentDetailDTO> qnaContents,
                                  boolean myLike) {
        this.articleId = articleId;
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.qnaContents = qnaContents;
        this.followerCount = followerCount;
        this.date = date;
        this.myLike = myLike;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookImage = bookImage;
    }
}
