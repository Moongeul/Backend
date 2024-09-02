package com.core.book.api.bookshelf.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ReadBookshelfResponseDTO {
    
    private String bookImage; // 책 이미지
    private LocalDate readDate; // 읽은 날짜
    private double starRating; // 평점
}
