package com.core.book.api.article.service;

import com.core.book.api.article.dto.ReviewArticleDetailDTO;
import com.core.book.api.article.dto.ReviewArticleListDTO;
import com.core.book.api.article.dto.ReviewArticleListResponseDTO;
import com.core.book.api.article.dto.ReviewArticleTagDTO;
import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.entity.ReviewArticleTag;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.FollowRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final FollowRepository followRepository;

    public ReviewArticleListResponseDTO getAllArticles(String articleType, int page, int size) {
        if ("all".equalsIgnoreCase(articleType)) {
            // 게시글 타입 설정: REVIEW, PHRASE, QNA
            List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

            // 페이징 및 정렬 정보 설정 - 생성일 기준 내림차순
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

            // 데이터베이스에서 지정된 타입의 게시글을 페이징하여 조회
            Page<ReviewArticle> reviewArticlePage = reviewArticleRepository.findByTypeIn(
                    targetTypes, pageable);

            // 조회된 게시글을 DTO로 변환하여 리스트에 추가
            List<ReviewArticleListDTO> articles = reviewArticlePage.getContent().stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());

            // 마지막 페이지 여부 확인
            boolean isLast = reviewArticlePage.isLast();

            // 응답 DTO 생성 및 반환
            return new ReviewArticleListResponseDTO(articles, isLast, page);
        } else {
            // 특정 게시글 타입이 요청된 경우 해당 메서드로 처리
            return getArticlesByType(articleType, page, size);
        }
    }

    private ReviewArticleListResponseDTO getArticlesByType(String articleType, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ArticleType type;

        try {
            // 문자열로 받은 articleType을 ArticleType enum으로 변환
            type = ArticleType.valueOf(articleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(ErrorStatus.ARTICLE_TYPE_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 지정된 타입의 게시글을 페이징하여 조회
        Page<ReviewArticle> reviewArticlePage = reviewArticleRepository.findByType(type, pageable);

        List<ReviewArticleListDTO> articles = reviewArticlePage.getContent().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());

        // 마지막 페이지 여부 확인
        boolean isLast = reviewArticlePage.isLast();

        return new ReviewArticleListResponseDTO(articles, isLast, page);
    }

    private ReviewArticleListDTO convertToListDTO(ReviewArticle reviewArticle) {
        Member member = reviewArticle.getMember(); // 작성자 정보
        Book book = reviewArticle.getBook(); // 책 정보

        return ReviewArticleListDTO.builder()
                .articleId(reviewArticle.getId())
                .memberId(member.getId())
                .profileImage(member.getImageUrl())
                .nickname(member.getNickname())
                .content(reviewArticle.getContent())
                .likeCnt(reviewArticle.getLikeCnt())
                .commentCnt(reviewArticle.getCommentCnt())
                .quoCnt(reviewArticle.getQuoCnt())
                .bookImage(book.getBookImage())
                .title(book.getTitle())
                .author(book.getAuthor())
                .articleType(reviewArticle.getType())
                .build();
    }

    public ReviewArticleDetailDTO getReviewArticleDetail(Long id) {
        // 게시글 조회
        ReviewArticle reviewArticle = reviewArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 책 정보 가져오기
        Book book = reviewArticle.getBook();

        // 작성자 정보 가져오기
        Member member = reviewArticle.getMember();

        // 작성자의 팔로워 수 조회
        long followerCount = followRepository.countByFollowingId(member.getId());

        // 태그 정보 가져오기
        ReviewArticleTag reviewArticleTag = reviewArticle.getReviewArticleTag();
        ReviewArticleTagDTO reviewArticleTagDTO = null;
        if (reviewArticleTag != null) {
            reviewArticleTagDTO = ReviewArticleTagDTO.builder()
                    .tag1(reviewArticleTag.getTag1())
                    .tag2(reviewArticleTag.getTag2())
                    .tag3(reviewArticleTag.getTag3())
                    .tag4(reviewArticleTag.getTag4())
                    .tag5(reviewArticleTag.getTag5())
                    .build();
        }

        return ReviewArticleDetailDTO.builder()
                .memberId(member.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .bookImage(book.getBookImage())
                .content(reviewArticle.getContent())
                .oneLineReview(reviewArticle.getOneLineReview())
                .likeCnt(reviewArticle.getLikeCnt())
                .quoCnt(reviewArticle.getQuoCnt())
                .commentCnt(reviewArticle.getCommentCnt())
                .starRating(reviewArticle.getStarRating())
                .reviewArticleTagDTO(reviewArticleTagDTO)
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .followerCount(followerCount)
                .build();
    }
}
