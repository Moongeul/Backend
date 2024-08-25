package com.core.book.api.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InfoOpenRequestDTO {
    private Boolean follow_open;
    private Boolean content_open;
    private Boolean comment_open;
    private Boolean like_open;
}
