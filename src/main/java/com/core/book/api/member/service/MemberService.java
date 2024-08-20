package com.core.book.api.member.service;

import com.core.book.api.member.dto.UserTagRequestDTO;
import com.core.book.api.member.entity.InfoOpen;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.entity.UserTag;
import com.core.book.api.member.jwt.service.JwtService;
import com.core.book.api.member.repository.InfoOpenRepository;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.api.member.oauth2.CustomOAuth2User;
import com.core.book.api.member.oauth2.OAuthAttributes;
import com.core.book.api.member.repository.UserTagRepository;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final UserTagRepository userTagRepository;
    private final InfoOpenRepository infoOpenRepository;
    private final JwtService jwtService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(userNameAttributeName, attributes);

        Member createdUser = getUser(extractAttributes);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdUser.getEmail(),
                createdUser.getRole()
        );
    }

    private Member getUser(OAuthAttributes attributes) {
        Member findUser = memberRepository.findBySocialId(attributes.getOauth2UserInfo().getId()).orElse(null);

        if (findUser == null) {
            return saveUser(attributes);
        }
        return findUser;
    }

    private Member saveUser(OAuthAttributes attributes) {
        Member createdUser = attributes.toEntity(attributes.getOauth2UserInfo());

        // 초기 사용자 모든 정보 공개 TRUE 설정
        InfoOpen infoOpen = InfoOpen.builder()
                .follow_open(true)
                .content_open(true)
                .comment_open(true)
                .like_open(true)
                .member(createdUser)
                .build();

        Member savedMember = memberRepository.save(createdUser);

        infoOpenRepository.save(infoOpen);

        return savedMember;
    }

    @Transactional(readOnly = true)
    public Member getMemberDetails(String email) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
    }

    @Transactional
    public void registerInitialTags(String email, UserTagRequestDTO userTagRequest, HttpServletResponse response) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 이미 사용자가 초기 유저 태그 등록을 했을 경우 예외처리
        if (userTagRepository.findByMember(member).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_ADD_USERTAG_EXCEPTION.getMessage());
        }

        validateTagRequest(userTagRequest);

        UserTag userTag = UserTag.builder()
                .member(member)
                .tag1(sanitizeTag(userTagRequest.getTag1()))
                .tag2(sanitizeTag(userTagRequest.getTag2()))
                .tag3(sanitizeTag(userTagRequest.getTag3()))
                .tag4(sanitizeTag(userTagRequest.getTag4()))
                .tag5(sanitizeTag(userTagRequest.getTag5()))
                .build();

        userTagRepository.save(userTag);

        // 유저 권한 설정 GUEST -> USER
        Member updatedMember = member.authorizeUser();
        memberRepository.save(updatedMember);

        // 엑세스 토큰 및 리프레시 토큰 발급 후 쿠키로 전송
        String accessToken = jwtService.createAccessToken(member.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        // 리프레시 토큰 업데이트
        jwtService.updateRefreshToken(member.getEmail(), refreshToken);
    }

    private void validateTagRequest(UserTagRequestDTO userTagRequest) {
        if (isAllTagsEmpty(userTagRequest)) {
            throw new BadRequestException(ErrorStatus.USERTAG_REQUEST_MISSING_EXCEPTION.getMessage());
        }
    }

    private boolean isAllTagsEmpty(UserTagRequestDTO userTagRequest) {
        return !StringUtils.hasText(userTagRequest.getTag1()) &&
                !StringUtils.hasText(userTagRequest.getTag2()) &&
                !StringUtils.hasText(userTagRequest.getTag3()) &&
                !StringUtils.hasText(userTagRequest.getTag4()) &&
                !StringUtils.hasText(userTagRequest.getTag5());
    }

    private String sanitizeTag(String tag) {
        return StringUtils.hasText(tag) ? tag : null;
    }

    @Transactional
    public void registerInitialMarketing(String email, String approve) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // OK 일 경우 마케팅 동의 여부 TRUE 변경, 그 외는 FALSE
        Member updatedMember;
        if ("ok".equalsIgnoreCase(approve)) {
            updatedMember = member.updateMarketingAllow(true);
        } else {
            updatedMember = member.updateMarketingAllow(false);
        }

        memberRepository.save(updatedMember);
    }
}
