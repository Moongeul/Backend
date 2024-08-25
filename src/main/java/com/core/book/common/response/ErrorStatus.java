package com.core.book.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public enum ErrorStatus {
    /**
     * 400 BAD_REQUEST
     */
    VALIDATION_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청값이 입력되지 않았습니다."),
    USERTAG_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "최소 한개 이상의 USERTAG가 발송되지 않았습니다."),
    ALREADY_ADD_USERTAG_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 유저는 이미 태그가 등록되었습니다."),

    /**
     * 404 NOT_FOUND
     */

    USER_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    BOOK_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 도서를 찾을 수 없습니다."),
    BOOK_NO_MORE_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "더 이상 검색 결과가 없습니다."),

    /**
     * 500 SERVER_ERROR
     */
    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
