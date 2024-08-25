package com.core.book.api.member.dto;

import com.core.book.api.member.entity.InfoOpen;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.entity.UserTag;
import lombok.Getter;

@Getter
public class UserInfoResponseDTO {
    private final Long id;
    private final String nickname;
    private final String imageUrl;
    private final Boolean marketingAllow;
    private final UserTagDTO userTag;
    private final InfoOpenDTO infoOpen;
    private final int followedCount;

    public UserInfoResponseDTO(Member member, UserTag userTag, InfoOpen infoOpen, int followedCount) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.imageUrl = member.getImageUrl();
        this.marketingAllow = member.getMarketing_allow();
        this.followedCount = followedCount;
        this.userTag = userTag != null ? new UserTagDTO(userTag) : null;
        this.infoOpen = infoOpen != null ? new InfoOpenDTO(infoOpen) : null;
    }

    @Getter
    public static class UserTagDTO {
        private final String tag1;
        private final String tag2;
        private final String tag3;
        private final String tag4;
        private final String tag5;

        public UserTagDTO(UserTag userTag) {
            this.tag1 = userTag.getTag1();
            this.tag2 = userTag.getTag2();
            this.tag3 = userTag.getTag3();
            this.tag4 = userTag.getTag4();
            this.tag5 = userTag.getTag5();
        }
    }

    @Getter
    public static class InfoOpenDTO {
        private final Boolean followOpen;
        private final Boolean contentOpen;
        private final Boolean commentOpen;
        private final Boolean likeOpen;

        public InfoOpenDTO(InfoOpen infoOpen) {
            this.followOpen = infoOpen.getFollow_open();
            this.contentOpen = infoOpen.getContent_open();
            this.commentOpen = infoOpen.getComment_open();
            this.likeOpen = infoOpen.getLike_open();
        }
    }
}
