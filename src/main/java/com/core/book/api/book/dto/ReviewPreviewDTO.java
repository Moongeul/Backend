package com.core.book.api.book.dto;

import com.core.book.api.article.entity.ArticleType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewPreviewDTO {
    /* 책 정보 자세히보기 - 리뷰 미리보기 */

    private String profileImage;
    private String nickname;
    private ArticleType articleType; // 게시글 타입 구분을 위한 필드
    private String content;
}
