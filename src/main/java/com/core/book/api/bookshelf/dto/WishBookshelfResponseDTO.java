package com.core.book.api.bookshelf.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WishBookshelfResponseDTO {

    private String isbn; //isbn
    private String bookImage; // 책 이미지
    private String bookTitle; //책 제목
    private String author; // 저자
    private String reason; // 읽고 싶은 이유
}
