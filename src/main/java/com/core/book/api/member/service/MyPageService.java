package com.core.book.api.member.service;

import com.core.book.api.article.dto.ArticleListDTO;
import com.core.book.api.article.dto.ArticleListResponseDTO;
import com.core.book.api.article.entity.Article;
import com.core.book.api.article.entity.ArticleLike;
import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.article.repository.ArticleLikeRepository;
import com.core.book.api.article.repository.ArticleRepository;
import com.core.book.api.article.service.ArticleViewService;
import com.core.book.api.comment.repository.CommentRepository;
import com.core.book.api.comment.repository.QnaCommentRepository;
import com.core.book.api.member.entity.InfoOpen;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.InfoOpenRepository;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.BadRequestException;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;
    private final ArticleViewService articleViewService;
    private final InfoOpenRepository infoOpenRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final CommentRepository commentRepository;
    private final QnaCommentRepository qnaCommentRepository;

    // 타인 사용자 게시글 목록 조회
    public ArticleListResponseDTO getUserArticles(Long targetUserId, int page, int size, UserDetails currentUser) {
        // 타인 사용자 존재 여부 체크
        Member targetMember = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // InfoOpen에서 contentOpen 값 확인
        InfoOpen infoOpen = infoOpenRepository.findByMember(targetMember).orElse(null);

        // contentOpen이 false면 예외처리
        if (infoOpen == null || !Boolean.TRUE.equals(infoOpen.getContentOpen())) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_GET_OTHER_USER_INFO.getMessage());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

        // 타인 사용자의 게시글 조회
        Page<Article> articlePage = articleRepository.findByUserIdAndTypes(targetUserId, targetTypes, pageable);

        List<ArticleListDTO> articles = articlePage.getContent().stream()
                .map(article -> articleViewService.convertToListDTO(article, currentUser))
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
    }

    // 타인 사용자가 좋아요한 게시글 목록 조회
    public ArticleListResponseDTO getUserLikedArticles(Long targetUserId, int page, int size, UserDetails currentUser) {
        // 타인 사용자 존재 여부 체크
        Member targetMember = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // InfoOpen에서 likeOpen 값 확인
        InfoOpen infoOpen = infoOpenRepository.findByMember(targetMember).orElse(null);

        // likeOpen이 false면 예외처리
        if (infoOpen == null || !Boolean.TRUE.equals(infoOpen.getLikeOpen())) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_GET_OTHER_USER_INFO.getMessage());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("article.createdAt").descending());
        List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

        // 타인 사용자의 좋아요 누른 게시글 조회
        Page<ArticleLike> likedPage = articleLikeRepository.findByMemberAndArticleTypeIn(targetMember, targetTypes, pageable);

        List<ArticleListDTO> articles = likedPage.getContent().stream()
                .map(like -> {
                    Article article = like.getArticle();
                    return articleViewService.convertToListDTO(article, currentUser);
                })
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, likedPage.isLast(), page);
    }

    // 타인 사용자가 댓글 단 게시글 목록 조회
    public ArticleListResponseDTO getUserCommentedArticles(Long targetUserId, int page, int size, UserDetails currentUser) {
        // 타인 사용자 존재 여부 체크
        Member targetMember = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // InfoOpen에서 commentOpen 값 확인
        InfoOpen infoOpen = infoOpenRepository.findByMember(targetMember).orElse(null);

        // commentOpen이 false면 예외처리
        if (infoOpen == null || !Boolean.TRUE.equals(infoOpen.getCommentOpen())) {
            throw new BadRequestException(ErrorStatus.NOT_ALLOW_GET_OTHER_USER_INFO.getMessage());
        }

        // Review와 Phrase 게시글은 Comment 엔티티 사용
        List<ArticleType> reviewAndPhraseTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE);
        List<Article> articlesFromComment = commentRepository.findDistinctArticleByMemberIdAndArticleTypeIn(targetUserId, reviewAndPhraseTypes);

        // QnA 게시글은 QnaComment 엔티티 사용
        List<Article> articlesFromQnaComment = qnaCommentRepository.findDistinctQnaArticleByMemberId(targetUserId);

        // 두 리스트를 union 처리 (중복 제거)
        Set<Article> unionSet = new HashSet<>();
        unionSet.addAll(articlesFromComment);
        unionSet.addAll(articlesFromQnaComment);

        // unionSet을 List로 변환 후, Article의 생성일(createdAt) 내림차순 정렬
        List<Article> combinedArticles = new ArrayList<>(unionSet);
        combinedArticles.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // 수동 페이징 처리
        int total = combinedArticles.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        List<Article> paginatedArticles = (start < total) ? combinedArticles.subList(start, end) : Collections.emptyList();

        List<ArticleListDTO> dtos = new ArrayList<>();
        for (Article article : paginatedArticles) {
            dtos.add(articleViewService.convertToListDTO(article, currentUser));
        }

        boolean isLast = end >= total;
        return new ArticleListResponseDTO(dtos, isLast, page);
    }

    // 내가 작성한 게시글 조회
    public ArticleListResponseDTO getMyArticles(int page, int size, UserDetails userDetails) {

        // 사용자 정보 조회
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
        Long userId = member.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

        // 게시글 조회
        Page<Article> articlePage = articleRepository.findByUserIdAndTypes(userId, targetTypes, pageable);

        List<ArticleListDTO> articles = articlePage.getContent().stream()
                .map(a -> articleViewService.convertToListDTO(a, userDetails))
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, articlePage.isLast(), page);
    }

    // 내가 좋아요 누른 게시글 조회
    public ArticleListResponseDTO getMyLikedArticles(int page, int size, UserDetails userDetails) {

        // 사용자 정보 조회
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        Pageable pageable = PageRequest.of(page, size, Sort.by("article.createdAt").descending());
        List<ArticleType> targetTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE, ArticleType.QNA);

        // 내 좋아요 목록
        Page<ArticleLike> likedPage = articleLikeRepository.findByMemberAndArticleTypeIn(member, targetTypes, pageable);

        List<ArticleListDTO> articles = likedPage.getContent().stream()
                .map(like -> articleViewService.convertToListDTO(like.getArticle(), userDetails))
                .collect(Collectors.toList());

        return new ArticleListResponseDTO(articles, likedPage.isLast(), page);
    }

    // 내가 댓글 단 게시글 조회
    public ArticleListResponseDTO getMyCommentedArticles(int page, int size, UserDetails userDetails) {

        // 사용자 정보 조회
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));
        Long userId = member.getId();

        // Review와 Phrase 게시글은 Comment 엔티티 사용
        List<ArticleType> reviewAndPhraseTypes = Arrays.asList(ArticleType.REVIEW, ArticleType.PHRASE);
        List<Article> articlesFromComment =
                commentRepository.findDistinctArticleByMemberIdAndArticleTypeIn(userId, reviewAndPhraseTypes);

        // QnA 게시글은 QnaComment 엔티티 사용
        List<Article> articlesFromQnaComment =
                qnaCommentRepository.findDistinctQnaArticleByMemberId(userId);

        // 두 리스트를 union 처리 (중복 제거)
        Set<Article> unionSet = new HashSet<>();
        unionSet.addAll(articlesFromComment);
        unionSet.addAll(articlesFromQnaComment);

        // unionSet을 List로 변환 후, Article의 생성일(createdAt) 내림차순 정렬
        List<Article> combined = new ArrayList<>(unionSet);
        combined.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // 수동 페이징 처리
        int start = page * size;
        int end = Math.min(start + size, combined.size());
        List<Article> subList = (start < combined.size()) ? combined.subList(start, end) : Collections.emptyList();

        List<ArticleListDTO> dtos = subList.stream()
                .map(a -> articleViewService.convertToListDTO(a, userDetails))
                .collect(Collectors.toList());

        boolean isLast = (end >= combined.size());
        return new ArticleListResponseDTO(dtos, isLast, page);
    }

}
