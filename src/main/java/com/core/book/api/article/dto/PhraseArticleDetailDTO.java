package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PhraseArticleDetailDTO {

    private final Long memberId;
    private final String isbn;
    private final String title;
    private final String author;
    private final String bookImage;
    private final String content;
    private final String phraseContent;
    private final int pageNum;
    private final long likeCnt;
    private final long quoCnt;
    private final long commentCnt;
    private final String nickname;
    private final String profileImage;
    private final long followerCount;

    @Builder
    public PhraseArticleDetailDTO(Long memberId, String isbn, String title, String author, String bookImage, String content,
                                  String phraseContent, long likeCnt, long quoCnt, long commentCnt, int pageNum,
                                  String nickname, String profileImage, long followerCount) {
        this.memberId = memberId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookImage = bookImage;
        this.content = content;
        this.pageNum = pageNum;
        this.phraseContent = phraseContent;
        this.likeCnt = likeCnt;
        this.quoCnt = quoCnt;
        this.commentCnt = commentCnt;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.followerCount = followerCount;
    }
}
