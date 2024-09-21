package com.core.book.api.bookshelf.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class ReadBookshelfResponseDTO {

    private String date; // 읽은 날짜(월별) (YYYY-M)
    private int monthlyBookCnt; // 월별 읽은 책 개수
    private List<MonthlyReadBooksDTO> monthlyReadBooks; // 월별 읽은 책 정보 리스트


    @Builder
    @Getter
    public static class MonthlyReadBooksDTO{

        private String isbn; //isbn
        private String bookImage; // 책 이미지
        private double starRating; // 평점
        private String title; // 책 제목
        private LocalDate readDate; // 읽은 날짜
    }
}
