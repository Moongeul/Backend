package com.core.book.api.bookshelf.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class ReadBookshelfResponseDTO {

    private long totalBookCnt; // 전체 책 개수
    private List<MonthlyInfoDTO> monthlyInfoList; // 책장 내 월 별로 요구되는 데이터들의 리스트
    private int page; // 현재 페이지 번호
    private boolean isLast; // 마지막 페이지 여부 (true: 마지막 페이지가 맞음 / false : 마지막 페이지가 아님)

    @Builder
    @Getter
    public static class MonthlyInfoDTO{
        private String date; // 읽은 날짜(월별) (YYYY-M)
        private int monthlyBookCnt; // 월별 읽은 책 개수
        private List<MonthlyReadBookDTO> monthlyReadBookList; // 월별 읽은 책 정보 리스트

        @Builder
        @Getter
        public static class MonthlyReadBookDTO {

            private Long id;  // '읽은 책' 책장 데이터의 id
            private String isbn; // isbn
            private String bookImage; // 책 이미지
            private double rating; // 평점
            private String title; // 책 제목
            private LocalDate readDate; // 읽은 날짜
        }
    }

}
