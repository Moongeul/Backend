package com.core.book.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreateDTO {

    private String comment;
    private Long articleId;
    private Long parentId;
}
