package com.core.book.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FollowedUserDTO {
    private Long id;
    private String nickname;
    private String imageUrl;
}
