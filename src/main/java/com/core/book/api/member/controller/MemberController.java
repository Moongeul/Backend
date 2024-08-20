package com.core.book.api.member.controller;

import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ApiResponse;
import com.core.book.common.response.ErrorStatus;
import com.core.book.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberRepository memberRepository;

    // 사용자 토큰 인증 확인용 임시 API 입니다.
    @GetMapping("/check")
    public ApiResponse<Member> getMemberDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
       return ApiResponse.success(SuccessStatus.SEND_USERDETAIL_SUCCESS, member);
    }
}
