package com.core.book.api.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTagRequestDTO {

    private final String tag1;
    private final String tag2;
    private final String tag3;
    private final String tag4;
    private final String tag5;
}
