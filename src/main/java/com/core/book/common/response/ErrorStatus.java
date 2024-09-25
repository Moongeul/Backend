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
    VALIDATION_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값이 입력되지 않았습니다."),
    VALIDATION_CONTENT_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "필수 정보가 입력되지 않았습니다."),
    USERTAG_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "최소 한개 이상의 USERTAG가 발송되지 않았습니다."),
    ALREADY_ADD_USERTAG_EXCEPTION(HttpStatus.BAD_REQUEST, "해당 유저는 이미 태그가 등록되었습니다."),
    MISSING_UPLOAD_IMAGE(HttpStatus.BAD_REQUEST, "수정할 프로필 이미지파일이 업로드 되지 않았습니다."),
    MISSING_NICKNAME(HttpStatus.BAD_REQUEST,"닉네임이 입력되지 않았습니다."),
    MISSING_USERTAG(HttpStatus.BAD_REQUEST,"사용자 태그가 입력되지 않았습니다."),
    MISSING_MARKETING_APPROVE(HttpStatus.BAD_REQUEST, "사용자의 마케팅 동의 정보가 입력되지 않았습니다."),
    MISSING_KAKAO_ACCESSTOKEN(HttpStatus.BAD_REQUEST, "카카오 엑세스토큰이 입력되지 않았습니다."),
    MISSING_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "리프레시토큰이 입력되지 않았습니다."),
    NOT_ALLOW_NICKNAME_FILTER_UNDER_10(HttpStatus.BAD_REQUEST, "닉네임은 10자 이하로 설정해야 합니다."),
    NOT_ALLOW_USERTAG_FILTER_ROLE(HttpStatus.BAD_REQUEST, "닉네임은 영문, 숫자, 한글만 사용할 수 있습니다."),
    NOT_ALLOW_USERTAG_FILTER_LIST(HttpStatus.BAD_REQUEST, "부적절한 닉네임입니다."),
    NOT_ALLOW_IMG_MIME(HttpStatus.BAD_REQUEST,"이미지 파일(jpg, jpeg, png, bmp, webp) 만 업로드할 수 있습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST,"중복된 닉네임입니다."),
    MISSING_INFOOPEN(HttpStatus.BAD_REQUEST,"정보 공개 여부 값이 입력되지 않았습니다."),
    MISSING_BOOKSHELF_DATE(HttpStatus.BAD_REQUEST, "책장 등록 날짜가 입력되지 않았습니다."),
    MISSING_BOOKSHELF_MEMBER(HttpStatus.BAD_REQUEST, "책장 등록을 요청하는 유저가 입력되지 않았습니다."),
    NOT_ALLOW_DUPLICATE_BOOKSHELF(HttpStatus.BAD_REQUEST, "이미 책장에 등록된 책입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    INVALID_KAKAO_ACCESSTOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 엑세스토큰입니다."),
    INVALID_REFRESHTOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시토큰입니다."),

    /**
     * 404 NOT_FOUND
     */

    USER_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    BOOK_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 도서를 찾을 수 없습니다."),
    BOOK_NO_MORE_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "더 이상 검색 결과가 없습니다."),
    BOOKSHELF_INFO_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 책장에서 선택된 도서를 찾을 수 없습니다."),
    BOOKSHELF_NOTFOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 책장에서 도서를 찾을 수 없습니다."),
    INFOOPEN_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "사용자 정보 공개 동의 여부가 등록되어 있지 않습니다."),
    READBOOK_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 도서를 읽은 기록이 없습니다."),

    /**
     * 500 SERVER_ERROR
     */

    FAIL_UPLOAD_PROFILE_IMAGE(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 사진이 변경되지 않았습니다."),
    FAIL_REQUEST_KAKAO_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보 요청 중 오류가 발생했습니다."),
    FAIL_PARSE_KAKAO_USER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보를 가져오지 못했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
