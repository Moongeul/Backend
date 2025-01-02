package com.core.book.api.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QnaCommentCreateDTO {
    private String comment;
    private Long qnaCommentId;
    private Long parentId;
}
