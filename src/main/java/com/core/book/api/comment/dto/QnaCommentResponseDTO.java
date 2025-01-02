package com.core.book.api.comment.dto;

import com.core.book.api.comment.entity.QnaComment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class QnaCommentResponseDTO {
    private Long id;
    private String comment;
    private String nickname;
    private String profileImageUrl;
    private Long parentId;
    private String createdAt;

    public static QnaCommentResponseDTO fromEntity(QnaComment qnaComment) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

        return QnaCommentResponseDTO.builder()
                .id(qnaComment.getId())
                .comment(qnaComment.getComment())
                .nickname(qnaComment.getMember().getNickname())
                .profileImageUrl(qnaComment.getMember().getImageUrl())
                .parentId(qnaComment.getParentComment() != null ? qnaComment.getParentComment().getId() : null)
                .createdAt(qnaComment.getCreatedAt().format(dateTimeFormatter))
                .build();
    }
}
