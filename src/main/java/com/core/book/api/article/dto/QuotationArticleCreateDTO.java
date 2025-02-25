package com.core.book.api.article.dto;

import lombok.Data;

@Data
public class QuotationArticleCreateDTO {
    private Long reviewArticleId; // 인용할 감상평 게시글의 ID
    private String content;       // 인용 내용
}
