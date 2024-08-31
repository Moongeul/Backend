package com.core.book.api.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowerUserDTO {
    private Long id;
    private String nickname;
    private String imageUrl;
}

