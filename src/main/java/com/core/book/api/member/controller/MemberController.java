package com.core.book.api.member.controller;

import com.core.book.api.member.dto.InfoOpenRequestDTO;
import com.core.book.api.member.dto.UserInfoResponseDTO;
import com.core.book.api.member.dto.UserTagRequestDTO;
import com.core.book.api.member.service.MemberService;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.InternalServerException;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "사용자 정보 조회 API",
            description = "토큰을 통해 인증된 사용자의 정보를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<UserInfoResponseDTO>> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        UserInfoResponseDTO userInfo = memberService.getUserInfo(userDetails.getUsername());
        return ApiResponse.success(SuccessStatus.GET_USERINFO_SUCCESS, userInfo);
    }

    // 최초 회원가입 사용자 유저 태그 등록 API
    @Operation(
            summary = "처음 사용자용 TAG 등록 API",
            description = "처음 사용자 추가 정보 등록페이지 에서 TAG를 등록하고 기존사용자로 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "USERTAG 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최소 한개 이상의 USERTAG가 발송되지 않았습니다.")
    })
    @PostMapping("/initial-tags")
    public ResponseEntity<ApiResponse<Void>> registerInitialTags(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserTagRequestDTO userTagRequest, HttpServletResponse response) {

        // 사용자 태그가 입력되지 않았을 경우 예외 처리
        if (userTagRequest == null) {
            throw new BadRequestException(ErrorStatus.MISSING_USERTAG.getMessage());
        }

        memberService.registerInitialTags(userDetails.getUsername(), userTagRequest, response);
        return ApiResponse.success_only(SuccessStatus.CREATE_USERTAG_SUCCESS);
    }

    // 최초 회원가입 사용자 마케팅 동의 여부 등록 API
    @Operation(
            summary = "처음 사용자용 마케팅 정보 동의 등록 API",
            description = "처음 사용자 추가 정보 등록페이지 에서 마케팅 동의 여부를 확인 후 동의 여부 등록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 마케팅 동의 여부 설정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자의 마케팅 동의 정보가 입력되지 않았습니다."),
    })
    @GetMapping("/initial-marketing")
    public ResponseEntity<ApiResponse<Void>> registerInitialMarketing(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Parameter(
                                                              description = "사용자의 마케팅 동의 여부를 나타내는 파라미터로, 'ok'는 승인, 'no'는 미승인을 의미합니다.",
                                                              example = "ok",
                                                              in = ParameterIn.QUERY)
                                                      @RequestParam String approve) {

        // 마케팅 동의 정보가 입력되지 않았을 경우 예외 처리
        if (approve == null || approve.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_MARKETING_APPROVE.getMessage());
        }

        memberService.registerInitialMarketing(userDetails.getUsername(), approve);
        return ApiResponse.success_only(SuccessStatus.SET_USER_MARKETING_SUCCESS);
    }

    // 프로필 사진 변경 API
    @Operation(
            summary = "프로필 사진 변경 API",
            description = "사용자의 프로필 사진을 변경합니다. with MultipartFile"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 사진 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "수정할 프로필 이미지파일이 업로드 되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "프로팔 사진이 변경되지 않았습니다.")
    })
    @PutMapping(value = "/change-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> changeProfileImage(@AuthenticationPrincipal UserDetails userDetails,
                                                @RequestParam("image") MultipartFile image) {
        // 이미지가 첨부되지 않았을 경우 예외 처리
        if (image == null || image.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_UPLOAD_IMAGE.getMessage());
        }

        try {
            // 이미지 업로드 및 프로필 이미지 업데이트
            memberService.updateProfileImage(userDetails.getUsername(), image);
            return ApiResponse.success_only(SuccessStatus.UPDATE_PROFILE_IMAGE_SUCCESS);
        } catch (IOException e) {
            throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_PROFILE_IMAGE.getMessage());
        }
    }

    // 닉네임 변경 API
    @Operation(
            summary = "닉네임 변경 API",
            description = "사용자의 닉네임을 변경합니다. (닉네임 필터 조건 : 닉네임은 10자 이하로 설정, 닉네임은 영문, 숫자, 한글만 사용가능, 현재 다른 사용자가 사용중인 닉네임은 사용 불가)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "닉네임이 입력되지 않았습니다."),
    })
    @PutMapping("/change-nickname")
    public ResponseEntity<ApiResponse<Void>> changeNickname(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam("nickname") String nickname) {
        // 닉네임이 입력되지 않았을 경우 예외 처리
        if (nickname == null || nickname.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_NICKNAME.getMessage());
        }

        memberService.changeNickname(userDetails.getUsername(), nickname);
        return ApiResponse.success_only(SuccessStatus.UPDATE_NICKNAME_SUCCESS);
    }

    // 정보 공개 여부 변경 API
    @Operation(
            summary = "정보 공개 여부 수정 API",
            description = "사용자의 정보 공개 여부를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정보 공개 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "정보 공개 여부 값이 입력되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PutMapping("/change-info-open")
    public ResponseEntity<ApiResponse<Void>> changeInfoOpen(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody InfoOpenRequestDTO infoOpenRequestDTO) {
        // 정보 공개 여부 값이 입력되지 않았을 경우 예외 처리
        if (infoOpenRequestDTO == null) {
            throw new BadRequestException(ErrorStatus.MISSING_INFOOPEN.getMessage());
        }

        memberService.updateInfoOpen(userDetails.getUsername(), infoOpenRequestDTO);
        return ApiResponse.success_only(SuccessStatus.UPDATE_INFO_OPEN_SUCCESS);
    }

    // 회원 탈퇴 API
    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴 시 연관된 모든 데이터를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @DeleteMapping("/quit")
    public ResponseEntity<ApiResponse<Void>> quitMember(@AuthenticationPrincipal UserDetails userDetails) {

        memberService.quitMember(userDetails.getUsername());
        return ApiResponse.success_only(SuccessStatus.DELETE_MEMBER_SUCCESS);
    }

}
