package com.core.book.api.member.service;

import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.api.bookshelf.repository.WishBooksRepository;
import com.core.book.api.member.dto.*;
import com.core.book.api.member.entity.*;
import com.core.book.api.member.jwt.service.JwtService;
import com.core.book.api.member.repository.FollowRepository;
import com.core.book.api.member.repository.InfoOpenRepository;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.api.member.repository.UserTagRepository;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserTagRepository userTagRepository;
    private final InfoOpenRepository infoOpenRepository;
    private final FollowRepository followRepository;
    private final ReadBooksRepository readBooksRepository;
    private final WishBooksRepository wishBooksRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final OAuthService oAuthService;

    // 금지된 닉네임 리스트
    @Value("${member.prohibited-nicknames}")
    private List<String> prohibitedNicknames;

    @Transactional
    public Map<String, Object> loginWithKakao(String kakaoAccessToken) {
        // 카카오 Access Token을 이용해 사용자 정보 가져오기
        KakaoUserInfoDTO kakaoUserInfo = oAuthService.getKakaoUserInfo(kakaoAccessToken);

        // 사용자 정보를 저장
        Member member = registerOrLoginKakaoUser(kakaoUserInfo);

        // 엑세스,리프레시 토큰 생성
        Map<String, String> tokens = jwtService.createAccessAndRefreshToken(member.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("tokens", tokens);
        response.put("role", member.getRole());

        return response;
    }

    // 카카오 사용자 정보를 사용해 회원가입 또는 로그인 처리
    public Member registerOrLoginKakaoUser(KakaoUserInfoDTO kakaoUserInfo) {
        // 카카오 사용자 ID로 사용자 조회
        return memberRepository.findBySocialId(kakaoUserInfo.getId())
                .orElseGet(() -> registerNewKakaoUser(kakaoUserInfo));  // 없으면 새 사용자 등록
    }

    // 새로운 카카오 사용자 등록
    private Member registerNewKakaoUser(KakaoUserInfoDTO kakaoUserInfo) {
        Member member = Member.builder()
                .socialId(kakaoUserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .nickname(kakaoUserInfo.getNickname())
                .imageUrl(kakaoUserInfo.getProfileImage())
                .marketing_allow(Boolean.FALSE)
                .role(Role.GUEST)
                .build();

        // 초기 정보 공개 설정
        InfoOpen infoOpen = InfoOpen.builder()
                .followOpen(true)
                .contentOpen(true)
                .commentOpen(true)
                .likeOpen(true)
                .member(member)
                .build();

        memberRepository.save(member);
        infoOpenRepository.save(infoOpen);

        return member;
    }

    @Transactional
    public void registerInitialTags(Long userId, UserTagRequestDTO userTagRequest, HttpServletResponse response) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
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

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
        return member.getId();
    }

    @Transactional
    public void registerInitialMarketing(Long userId, String approve) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
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

    @Transactional
    public void updateProfileImage(Long userId, MultipartFile image) throws IOException {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 파일 타입 검사 (이미지 파일만 허용)
        if (!isImageFile(image)) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_IMG_MIME.getMessage());
        }

        // 기존 이미지가 S3에 있는 경우 삭제
        s3Service.deleteFile(member.getImageUrl());

        // 새로운 이미지 업로드
        String imageUrl = s3Service.uploadFile(member.getEmail(), image);

        Member updatedMember = member.updateImageUrl(imageUrl);
        memberRepository.save(updatedMember);
    }

    private boolean isImageFile(MultipartFile file) {
        // 허용되는 이미지 MIME 타입
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/bmp") ||
                        contentType.equals("image/webp")
        );
    }

    @Transactional
    public void changeNickname(Long userId, String nickname) {
        // 유저 조회 및 닉네임 변경 로직
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        validateNickname(nickname);

        Member updatedMember = member.updateNickname(nickname);
        memberRepository.save(updatedMember); // Member 객체 반환
    }

    @Transactional
    public void checkNickname(String nickname) {

        validateNickname(nickname);
    }

    private void validateNickname(String nickname) {
        //10자 이하 인지 체크
        if (nickname.length() > 10) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_NICKNAME_FILTER_UNDER_10.getMessage());
        }

        //한글, 영어, 숫자만 허용
        if (!nickname.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_USERTAG_FILTER_ROLE.getMessage());
        }

        // 부적절한 닉네임 체크
        for (String word : prohibitedNicknames) {
            if (nickname.toLowerCase().contains(word)) {
                throw new BadRequestException(ErrorStatus.NOT_ALLOW_USERTAG_FILTER_LIST.getMessage());
            }
        }

        // 중복된 닉네임 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new BadRequestException(ErrorStatus.DUPLICATE_NICKNAME.getMessage());
        }
    }

    @Transactional
    public void updateInfoOpen(Long userId, InfoOpenRequestDTO infoOpenRequestDTO) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        InfoOpen infoOpen = infoOpenRepository.findByMember(member)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.INFOOPEN_NOT_FOUND_EXCEPTION.getMessage()));

        // InfoOpen 정보 업데이트
        infoOpen = infoOpen.updateInfoOpen(
                infoOpenRequestDTO.getFollowOpen(),
                infoOpenRequestDTO.getContentOpen(),
                infoOpenRequestDTO.getCommentOpen(),
                infoOpenRequestDTO.getLikeOpen()
        );

        infoOpenRepository.save(infoOpen);
    }

    @Transactional
    public void quitMember(Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // InfoOpen 삭제
        infoOpenRepository.findByMember(member).ifPresent(infoOpenRepository::delete);

        // UserTag 삭제
        userTagRepository.findByMember(member).ifPresent(userTagRepository::delete);

        // Member 삭제
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo(Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        UserTag userTag = userTagRepository.findByMember(member).orElse(null);
        InfoOpen infoOpen = infoOpenRepository.findByMember(member).orElse(null);

        int followedCount = member.getFollowing().size(); //팔로잉 수 계산
        int followerCount = member.getFollowers().size(); //팔로워 수 계산
        int readBooksCount = readBooksRepository.findReadBooksByMemberId(userId).size(); // 읽은책 수 계산
        int wishBooksCount = wishBooksRepository.findWishBooksByMemberId(userId).size(); // 읽고싶은책 수 계산

        return new UserInfoResponseDTO(member, userTag, infoOpen, followedCount, followerCount, readBooksCount, wishBooksCount);
    }

    @Transactional
    @CacheEvict(value = {"followers", "following", "userInfo"}, key = "#userId")
    public boolean followOrUnfollowMember(Long userId, Long followingId) {
        // 팔로우 하는 유저를 찾을 수 없을 경우 예외처리
        Member follower = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 팔로우 할려는 유저를 찾을 수 없을 경우 예외처리
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 팔로우 상태인지 확인
        return followRepository.findByFollowerAndFollowing(follower, following)
                .map(follow -> {
                    followRepository.delete(follow);
                    return false; // 팔로우 해지됨
                })
                .orElseGet(() -> {
                    Follow newFollow = Follow.builder()
                            .follower(follower)
                            .following(following)
                            .build();
                    followRepository.save(newFollow);
                    return true; // 팔로우 추가됨
                });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "following", key = "#userId")
    public List<FollowedUserDTO> getFollowedUsers(Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        return member.getFollowing().stream()
                .map(follow -> new FollowedUserDTO(
                        follow.getFollowing().getId(),
                        follow.getFollowing().getNickname(),
                        follow.getFollowing().getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "followers", key = "#userId")
    public List<FollowerUserDTO> getFollowers(Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // N+1 문제 해결을 위한 해당 사용자의 모든 팔로워 리스트 불러오기
        List<Long> followingIds = followRepository.findFollowingIdsByFollowerId(userId);

        return member.getFollowers().stream()
                .map(follow -> {
                    boolean isFollowing = followingIds.contains(follow.getFollower().getId());
                    return FollowerUserDTO.builder()
                            .id(follow.getFollower().getId())
                            .nickname(follow.getFollower().getNickname())
                            .imageUrl(follow.getFollower().getImageUrl())
                            .isFollowing(isFollowing)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OtherUserInfoResponseDTO getOtherUserInfo(Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member targetMember = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        int followedCount = targetMember.getFollowing().size(); // 팔로잉 수 계산
        int followerCount = targetMember.getFollowers().size(); // 팔로워 수 계산
        int readBooksCount = readBooksRepository.findReadBooksByMemberId(userId).size(); // 읽은책 수 계산
        int wishBooksCount = wishBooksRepository.findWishBooksByMemberId(userId).size(); // 읽고싶은책 수 계산

        return OtherUserInfoResponseDTO.builder()
                .id(targetMember.getId())
                .nickname(targetMember.getNickname())
                .imageUrl(targetMember.getImageUrl())
                .followedCount(followedCount)
                .followerCount(followerCount)
                .readBooksCount(readBooksCount)
                .wishBooksCount(wishBooksCount)
                .build();
    }
}
