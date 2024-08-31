package com.core.book.api.member.controller;

import com.core.book.api.member.dto.*;
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
import java.util.List;

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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<UserInfoResponseDTO>> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        UserInfoResponseDTO userInfo = memberService.getUserInfo(userId);
        return ApiResponse.success(SuccessStatus.GET_USERINFO_SUCCESS, userInfo);
    }

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
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        // 사용자 태그가 입력되지 않았을 경우 예외 처리
        if (userTagRequest == null) {
            throw new BadRequestException(ErrorStatus.MISSING_USERTAG.getMessage());
        }

        memberService.registerInitialTags(userId, userTagRequest, response);
        return ApiResponse.success_only(SuccessStatus.CREATE_USERTAG_SUCCESS);
    }

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
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        // 마케팅 동의 정보가 입력되지 않았을 경우 예외 처리
        if (approve == null || approve.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_MARKETING_APPROVE.getMessage());
        }

        memberService.registerInitialMarketing(userId, approve);
        return ApiResponse.success_only(SuccessStatus.SET_USER_MARKETING_SUCCESS);
    }

    @Operation(
            summary = "프로필 사진 변경 API",
            description = "사용자의 프로필 사진을 변경합니다. with MultipartFile"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 사진 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "수정할 프로필 이미지파일이 업로드 되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "프로필 사진이 변경되지 않았습니다.")
    })
    @PutMapping(value = "/change-profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> changeProfileImage(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestParam("image") MultipartFile image) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        // 이미지가 첨부되지 않았을 경우 예외 처리
        if (image == null || image.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_UPLOAD_IMAGE.getMessage());
        }

        try {
            memberService.updateProfileImage(userId, image);
            return ApiResponse.success_only(SuccessStatus.UPDATE_PROFILE_IMAGE_SUCCESS);
        } catch (IOException e) {
            throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_PROFILE_IMAGE.getMessage());
        }
    }

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
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        // 닉네임이 입력되지 않았을 경우 예외 처리
        if (nickname == null || nickname.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_NICKNAME.getMessage());
        }

        memberService.changeNickname(userId, nickname);
        return ApiResponse.success_only(SuccessStatus.UPDATE_NICKNAME_SUCCESS);
    }

    @Operation(
            summary = "정보 공개 여부 수정 API",
            description = "사용자의 정보 공개 여부를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정보 공개 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "정보 공개 여부 값이 입력되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @PutMapping("/change-info-open")
    public ResponseEntity<ApiResponse<Void>> changeInfoOpen(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestBody InfoOpenRequestDTO infoOpenRequestDTO) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        // 정보 공개 여부 값이 입력되지 않았을 경우 예외 처리
        if (infoOpenRequestDTO == null) {
            throw new BadRequestException(ErrorStatus.MISSING_INFOOPEN.getMessage());
        }

        memberService.updateInfoOpen(userId, infoOpenRequestDTO);
        return ApiResponse.success_only(SuccessStatus.UPDATE_INFO_OPEN_SUCCESS);
    }

    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴 시 연관된 모든 데이터를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @DeleteMapping("/quit")
    public ResponseEntity<ApiResponse<Void>> quitMember(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());

        memberService.quitMember(userId);
        return ApiResponse.success_only(SuccessStatus.DELETE_MEMBER_SUCCESS);
    }

    @Operation(summary = "사용자 팔로우, 언팔로우 API", description = "특정 사용자를 팔로우하거나, 이미 팔로우한 경우 해지합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로우 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @PostMapping("/follow")
    public ResponseEntity<ApiResponse<Void>> followMember(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestBody FollowRequestDTO followRequestDTO) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        boolean isFollowed = memberService.followOrUnfollowMember(userId, followRequestDTO.getFollowingId());

        if(isFollowed){
            return ApiResponse.success_only(SuccessStatus.USER_FOLLOW_SUCCESS);
        }else{
            return ApiResponse.success_only(SuccessStatus.USER_UNFOLLOW_SUCCESS);
        }
    }

    @Operation(
            summary = "팔로우 중인 사용자 목록 조회 API",
            description = "현재 사용자가 팔로우하고 있는 사용자 목록을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로우 중인 사용자 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @GetMapping("/follow")
    public ResponseEntity<ApiResponse<List<FollowedUserDTO>>> getFollowedUsers(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        List<FollowedUserDTO> followedUsers = memberService.getFollowedUsers(userId);
        return ApiResponse.success(SuccessStatus.GET_FOLLOWED_USERS_SUCCESS, followedUsers);
    }

    @Operation(summary = "팔로워 목록 조회 API", description = "현재 사용자를 팔로우하는 사람들의 목록을 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로워 사용자 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @GetMapping("/follower")
    public ResponseEntity<ApiResponse<List<FollowerUserDTO>>> getFollowers(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
        List<FollowerUserDTO> followers = memberService.getFollowers(userId);
        return ApiResponse.success(SuccessStatus.GET_FOLLOWER_USERS_SUCCESS, followers);
    }

    @Operation(
            summary = "타인 사용자 정보 조회 API",
            description = "사용자의 ID를 통해 해당 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "타인 사용자 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @GetMapping("/user-info/{id}")
    public ResponseEntity<ApiResponse<OtherUserInfoResponseDTO>> getOtherUserInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "조회할 사용자의 ID", required = true)
            @PathVariable Long id) {

        // 상대방 ID 미입력인 경우 예외 처리
        if (id == null) {
            throw new BadRequestException(ErrorStatus.VALIDATION_REQUEST_MISSING_EXCEPTION.getMessage());
        }

        // 조회 사용자 미인증인 경우 예외 처리
        if (userDetails.getUsername() == null) {
            throw new BadRequestException(ErrorStatus.USER_UNAUTHORIZED.getMessage());
        }

        OtherUserInfoResponseDTO userInfo = memberService.getOtherUserInfo(id);
        return ApiResponse.success(SuccessStatus.GET_USERINFO_SUCCESS, userInfo);
    }

}
