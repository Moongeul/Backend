package com.core.book.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowedUserDTO {
    private Long id;
    private String nickname;
    private String imageUrl;
}
