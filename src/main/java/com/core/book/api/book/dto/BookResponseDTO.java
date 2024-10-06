package com.core.book.api.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookResponseDTO {

    private int totalSize; // 검색된 책 전체 개수
    private int page; // 현재 페이지 번호
    private List<BookDTO> bookList; // 검색된 책 List
    private boolean isLast; // 마지막 페이지 여부

    @Builder
    @Getter
    public static class BookDTO{

        private String isbn; // isbn
        private String title; // 책 제목
        private String image; // 책 이미지
        private String author; // 저자
        private String publisher; // 출판사
        private String pubdate; // 출판 날짜
    }
}
