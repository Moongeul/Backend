package com.core.book.api.article.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuotationArticleListResponseDTO {
    private List<QuotationArticleResponseDTO> quotations;  // 인용 게시글 목록
    private boolean isLast;                                    // 마지막 페이지 여부
    private int page;                                        // 현재 페이지 번호
}
