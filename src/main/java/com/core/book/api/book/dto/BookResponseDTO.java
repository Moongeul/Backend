package com.core.book.api.book.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BookResponseDTO {

    private int totalSize; // 검색된 책 전체 개수
    private int page; // 현재 페이지 번호
    private List<BookInfoDTO> bookList; // 검색된 책 List
    private boolean isLast; // 마지막 페이지 여부
}
