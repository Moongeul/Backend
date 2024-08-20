package com.core.book.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    /**
     * 200
     */
    SEND_USERDETAIL_SUCCESS(HttpStatus.OK, "유저 정보 발송 성공"),
    SET_USER_MARKETING_SUCCESS(HttpStatus.OK, "유저 마케팅 동의 여부 설정 성공"),

    /**
     * 201
     */
    CREATE_ARTICLE_SUCCESS(HttpStatus.CREATED, "게시판 등록 성공"),
    CREATE_USERTAG_SUCCESS(HttpStatus.CREATED, "USERTAG 등록 성공")

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}