package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
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
    private final float rating;
    private final List<ReviewArticleTagDTO> reviewArticleTagList;
    private final String nickname;
    private final String profileImage;
    private final long followerCount;
    private final String date;
    private final boolean myLike;

    @Builder
    @Getter
    public static class ReviewArticleTagDTO {

        private int tagId;
        private String tag;
    }
}