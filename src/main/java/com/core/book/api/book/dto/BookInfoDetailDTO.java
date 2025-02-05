package com.core.book.api.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookInfoDetailDTO {
    /* 책 정보 자세히 보기 정보 */

    private String isbn; // isbn
    private String image; // 책 이미지
    private String title; // 책 제목
    private String author; // 저자
    private String publisher; // 출판사
    private String pubdate; // 출판 날짜
    private String description; // 책 소개

    private float ratingAverage; // 평점 (전체 평균)
    private List<String> tagList; // 태그 Best5
    List<ReviewPreviewDTO> reviewPreviewList; // 리뷰 (미리보기) - 5개
}
