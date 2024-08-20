package com.core.book.api.member.controller;

import com.core.book.api.member.dto.UserTagRequestDTO;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.service.MemberService;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    // 사용자 토큰 인증 확인용 임시 API 입니다.
    @Operation(
            summary = "[임시]")
    @GetMapping("/check")
    public ApiResponse<Member> getMemberDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberService.getMemberDetails(userDetails.getUsername());
        return ApiResponse.success(SuccessStatus.SEND_USERDETAIL_SUCCESS, member);
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
    public ApiResponse<Void> registerInitialTags(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserTagRequestDTO userTagRequest, HttpServletResponse response) {

        memberService.registerInitialTags(userDetails.getUsername(), userTagRequest, response);
        return ApiResponse.success_only(SuccessStatus.CREATE_USERTAG_SUCCESS);
    }

    // 최초 회원가입 사용자 마케팅 동의 여부 등록 API (approve = ok : 승인 / approve = no : 미승인)
    @Operation(
            summary = "처음 사용자용 마케팅 정보 동의 등록 API",
            description = "처음 사용자 추가 정보 등록페이지 에서 마케팅 동의 여부를 확인 후 동의 여부 등록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 마케팅 동의 여부 설정 성공"),
    })
    @GetMapping("/initial-marketing")
    public ApiResponse<Void> registerInitialMarketing(@AuthenticationPrincipal UserDetails userDetails,
                                                      @Parameter(
                                                              description = "사용자의 마케팅 동의 여부를 나타내는 파라미터로, 'ok'는 승인, 'no'는 미승인을 의미합니다.",
                                                              example = "ok",
                                                              in = ParameterIn.QUERY)
                                                      @RequestParam String approve) {

        memberService.registerInitialMarketing(userDetails.getUsername(), approve);
        return ApiResponse.success_only(SuccessStatus.SET_USER_MARKETING_SUCCESS);
    }
}
