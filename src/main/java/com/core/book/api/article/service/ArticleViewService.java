package com.core.book.api.article.service;

import com.core.book.api.article.dto.*;
import com.core.book.api.article.entity.*;
import com.core.book.api.article.repository.ArticleLikeRepository;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.member.service.MemberService;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.FollowRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final PhraseArticleRepository phraseArticleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final FollowRepository followRepository;
    private final MemberService memberService;

    // 전체 게시글을 가져오는 메서드
    public ArticleListResponseDTO getAllArticles(String articleType, int page, int size, UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if ("all".equalsIgnoreCase(articleType)) {
            // 전체 게시글 타입 설정: REVIEW, PHRASE, QNA
            List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

            // 지정된 타입의 게시글을 페이징하여 조회
            Page<Article> articlePage = articleRepository.findByTypeIn(targetTypes, pageable);

            // 조회된 게시글을 DTO로 변환하여 리스트에 추가
            List<ArticleListDTO> articles = articlePage.getContent().stream()
                    .map(article -> convertToListDTO(article, userDetails))
                    .collect(Collectors.toList());

            // 응답 DTO 생성 및 반환
            return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
        } else {
            // 특정 게시글 타입이 요청된 경우 해당 메서드로 처리
            return getArticlesByType(articleType, page, size, userDetails);
        }
    }

    private ArticleListResponseDTO getArticlesByType(String articleType, int page, int size, UserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ArticleType type;

        try {
            // 문자열로 받은 articleType을 ArticleType enum으로 변환
            type = ArticleType.valueOf(articleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(ErrorStatus.ARTICLE_TYPE_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 지정된 타입의 게시글을 페이징하여 조회
        Page<Article> articlePage = articleRepository.findByType(type, pageable);

        List<ArticleListDTO> articles = articlePage.getContent().stream()
                .map(article -> convertToListDTO(article, userDetails))
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
    }

    private ArticleListDTO convertToListDTO(Article article, UserDetails userDetails) {

        Member member = article.getMember();

        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = article.getCreatedAt().format(formatter);

        // 좋아요 여부 체크
        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(article.getId(), userId).isPresent();
        }

        Book representativeBook = null;
        if (article instanceof ReviewArticle) {
            representativeBook = ((ReviewArticle) article).getBook();
        } else if (article instanceof PhraseArticle) {
            PhraseArticle phraseArticle = (PhraseArticle) article;
            if (!phraseArticle.getPhraseArticleContents().isEmpty()) {
                representativeBook = phraseArticle.getPhraseArticleContents()
                        .get(0)
                        .getBook();
            }
        }

        String bookImage = (representativeBook != null) ? representativeBook.getBookImage() : null;
        String title     = (representativeBook != null) ? representativeBook.getTitle()     : null;
        String author    = (representativeBook != null) ? representativeBook.getAuthor()    : null;

        return ArticleListDTO.builder()
                .articleId(article.getId())
                .memberId(member.getId())
                .profileImage(member.getImageUrl())
                .nickname(member.getNickname())
                .content(article.getContent())
                .likeCnt(article.getLikeCnt())
                .commentCnt(article.getCommentCnt())
                .quoCnt(article.getQuoCnt())
                .bookImage(bookImage)
                .title(title)
                .author(author)
                .articleType(article.getType())
                .date(formattedDate)
                .myLike(myLike)
                .build();
    }


    // 감상평 게시글 상세 조회 메서드
    public ReviewArticleDetailDTO getReviewArticleDetail(Long id, UserDetails userDetails) {
        // 게시글 조회
        ReviewArticle reviewArticle = reviewArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 책 정보 가져오기
        Book book = reviewArticle.getBook();

        // 작성자 정보 가져오기
        Member member = reviewArticle.getMember();

        // 작성자의 팔로워 수 조회
        long followerCount = followRepository.countByFollowingId(member.getId());

        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = reviewArticle.getCreatedAt().format(formatter);

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

        // 좋아요 여부 체크
        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(reviewArticle.getId(), userId).isPresent();
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
                .rating(reviewArticle.getRating())
                .reviewArticleTagDTO(reviewArticleTagDTO)
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .followerCount(followerCount)
                .date(formattedDate)
                .myLike(myLike)
                .build();
    }

    // 인상깊은구절 게시글 상세 조회 메서드
    public PhraseArticleDetailDTO getPhraseArticleDetail(Long id, UserDetails userDetails) {
        // 게시글 조회
        PhraseArticle phraseArticle = phraseArticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 작성자 정보 가져오기
        Member member = phraseArticle.getMember();

        // 작성자의 팔로워 수 조회
        long followerCount = followRepository.countByFollowingId(member.getId());

        // 날짜 포맷팅
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = phraseArticle.getCreatedAt().format(formatter);

        // 좋아요 여부 체크
        boolean myLike = false;
        if (userDetails != null) {
            Long userId = memberService.getUserIdByEmail(userDetails.getUsername());
            myLike = articleLikeRepository.findByArticleIdAndMemberId(phraseArticle.getId(), userId).isPresent();
        }

        List<PhraseArticleContentDetailDTO> contentDetailList = phraseArticle.getPhraseArticleContents().stream()
                .map(child -> {
                    // 자식이 참조하는 Book
                    Book book = child.getBook();
                    return PhraseArticleContentDetailDTO.builder()
                            .content(child.getContent())
                            .pageNum(child.getPageNum())
                            .phraseContent(child.getPhraseContent())
                            .isbn(book != null ? book.getIsbn() : null)
                            .title(book != null ? book.getTitle() : null)
                            .author(book != null ? book.getAuthor() : null)
                            .bookImage(book != null ? book.getBookImage() : null)
                            .build();
                })
                .collect(Collectors.toList());

        return PhraseArticleDetailDTO.builder()
                .articleId(phraseArticle.getId())
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImage(member.getImageUrl())
                .likeCnt(phraseArticle.getLikeCnt())
                .quoCnt(phraseArticle.getQuoCnt())
                .commentCnt(phraseArticle.getCommentCnt())
                .phraseContents(contentDetailList)
                .followerCount(followerCount)
                .date(formattedDate)
                .myLike(myLike)
                .build();
    }
}
