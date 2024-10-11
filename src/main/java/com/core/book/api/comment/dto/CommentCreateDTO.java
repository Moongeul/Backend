package com.core.book.api.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreateDTO {

    private String comment;
    private Long articleId;
    private Long parentId;
}
