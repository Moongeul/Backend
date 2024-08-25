package com.core.book.api.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtherUserInfoResponseDTO {
    private final Long id;
    private final String nickname;
    private final String imageUrl;
    private final int followedCount;

    public OtherUserInfoResponseDTO(Long id, String nickname, String imageUrl, int followedCount) {
        this.id = id;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.followedCount = followedCount;
    }
}
