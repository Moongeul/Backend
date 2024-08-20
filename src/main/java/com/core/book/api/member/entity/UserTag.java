package com.core.book.api.member.entity;

import com.core.book.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USER_TAG")
@AllArgsConstructor
public class UserTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long id;

    private String tag1; // 유저가 선택한 태그
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

}
