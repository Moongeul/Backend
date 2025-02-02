package com.core.book.api.article.dto;

import com.core.book.api.book.dto.UserBookTagDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReviewArticleCreateDTO {

    private String isbn;
    private final String content;
    private final String oneLineReview;
    private final float rating;
    private List<UserBookTagDTO> userBookTagList; //태그 (리스트)
}
