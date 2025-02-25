package com.core.book.api.article.dto;

import com.core.book.api.article.entity.ArticleType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuotationArticleResponseDTO {
    // 인용 게시글(QuotationArticle) 정보
    private Long quotationArticleId;       // 인용 게시글 id
    private Long memberId;                 // 인용 게시글 작성자 id
    private String profileImage;           // 인용 게시글 작성자 프로필 사진
    private String nickname;               // 인용 게시글 작성자 닉네임
    private long likeCnt;                // 좋아요 수
    private long commentCnt;             // 댓글 수
    private long quoCnt;                    // 인용 수 (quoCnt)
    private String date;                   // 작성일 (포맷팅된 날짜)
    private String content;                // 인용 게시글 내용

    // 인용한 감상평 게시글(ReviewArticle) 정보
    private Long originalArticleId;         // 인용한 게시글 id
    private Long originalMemberId;         // 감상평 게시글 작성자 id
    private String originalMemberProfileImage; // 감상평 게시글 작성자 프로필 사진
    private ArticleType originalArticleType;     // 감상평 게시글 타입 (일반적으로 REVIEW)
    private String bookId;                 // 감상평에 등록된 책의 id (ISBN)
    private String bookImage;              // 책 이미지
    private String bookTitle;              // 책 이름
    private String bookAuthor;             // 책 저자
    private String originalContent;        // 감상평 게시글 내용
}
