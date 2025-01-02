package com.core.book.api.comment.dto;

import com.core.book.api.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class CommentResponseDTO {

    private Long id;
    private String comment;
    private String nickname;
    private String profileImageUrl;
    private Long parentId;
    private String createAt;

    public static CommentResponseDTO fromEntity(Comment comment) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .nickname(comment.getMember().getNickname())
                .profileImageUrl(comment.getMember().getImageUrl())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createAt(comment.getCreatedAt().format(dateTimeFormatter))
                .build();
    }
}