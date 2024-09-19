package com.core.book.api.member.entity;

import com.core.book.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder(toBuilder = true)
@Table(name = "INFO_OPEN")
@AllArgsConstructor
public class InfoOpen extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "info_open_id")
    private Long id;

    private Boolean followOpen; // 팔로우 공개 여부
    private Boolean contentOpen; // 글 공개 여부
    private Boolean commentOpen; // 댓글 공개 여부
    private Boolean likeOpen; // 좋아요 공개 여부

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    public InfoOpen updateInfoOpen(Boolean followOpen, Boolean contentOpen, Boolean commentOpen, Boolean likeOpen) {
        return this.toBuilder()
                .followOpen(followOpen)
                .contentOpen(contentOpen)
                .commentOpen(commentOpen)
                .likeOpen(likeOpen)
                .build();
    }

}
