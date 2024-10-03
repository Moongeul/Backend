package com.core.book.api.bookshelf.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WishBookshelfResponseDTO {

    private long totalBookCnt; // 전체 책 개수
    private List<wishBookDTO> wishBookList; // 읽고 싶은 책 리스트
    private boolean isLast; // 마지막 페이지 여부 (true: 마지막 페이지가 맞음 / false : 마지막 페이지가 아님)

    @Builder
    @Getter
    public static class wishBookDTO {
        
        private String isbn; //isbn
        private String bookImage; // 책 이미지
        private String bookTitle; //책 제목
        private String author; // 저자
        private String reason; // 읽고 싶은 이유
    }
}
