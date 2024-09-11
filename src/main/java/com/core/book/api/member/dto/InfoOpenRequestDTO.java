package com.core.book.api.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InfoOpenRequestDTO {
    private Boolean followOpen;
    private Boolean contentOpen;
    private Boolean commentOpen;
    private Boolean likeOpen;
}
