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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final FollowRepository followRepository;

    public ReviewArticleListResponseDTO getAllArticles(String articleType, int page, int size) {
        if ("all".equalsIgnoreCase(articleType)) {
            // 각 게시글 타입별로 가져올 게시글 수 계산
            ArticleType[] articleTypes = ArticleType.values();
            int typesCount = articleTypes.length;
            int articlesPerType = (int) Math.ceil((double) size / typesCount);

            // 각 타입별로 게시글 가져오기
            Map<ArticleType, List<ReviewArticle>> articlesByType = new HashMap<>();
            for (ArticleType type : articleTypes) {
                Pageable pageable = PageRequest.of(0, articlesPerType, Sort.by("createdAt").descending());
                List<ReviewArticle> articles = reviewArticleRepository.findByType(type, pageable).getContent();
                articlesByType.put(type, articles);
            }

            // 가져온 게시글을 교대로 합치기
            List<ReviewArticle> interleavedArticles = new ArrayList<>();
            boolean hasMore = true;
            int index = 0;
            while (hasMore && interleavedArticles.size() < size) {
                hasMore = false;
                for (ArticleType type : articleTypes) {
                    List<ReviewArticle> typeArticles = articlesByType.get(type);
                    if (typeArticles.size() > index) {
                        interleavedArticles.add(typeArticles.get(index));
                        if (interleavedArticles.size() == size) {
                            break;
                        }
                        hasMore = true;
                    }
                }
                index++;
            }

            List<ReviewArticleListDTO> articles = interleavedArticles.stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());

            // 마지막 페이지 여부 판단
            boolean isLast = interleavedArticles.size() < size;

            return new ReviewArticleListResponseDTO(articles, isLast);
        } else {
            // 특정 타입의 게시글 가져오기
            return getArticlesByType(articleType, page, size);
        }
    }

    private ReviewArticleListResponseDTO getArticlesByType(String articleType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ArticleType type;
        try {
            type = ArticleType.valueOf(articleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(ErrorStatus.ARTICLE_TYPE_NOT_FOUND_EXCEPTION.getMessage());
        }
        Page<ReviewArticle> reviewArticlePage = reviewArticleRepository.findByType(type, pageable);

        List<ReviewArticleListDTO> articles = reviewArticlePage.getContent().stream()
                .map(this::convertToListDTO)
                .collect(Collectors.toList());

        boolean isLast = reviewArticlePage.isLast();

        return new ReviewArticleListResponseDTO(articles, isLast);
    }

    private ReviewArticleListDTO convertToListDTO(ReviewArticle reviewArticle) {
        Member member = reviewArticle.getMember();
        Book book = reviewArticle.getBook();

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