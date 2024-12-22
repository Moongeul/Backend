package com.core.book.api.article.service;

import com.core.book.api.article.dto.PhraseArticleCreateDTO;
import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.article.dto.ReviewArticleTagDTO;
import com.core.book.api.article.entity.ArticleType;
import com.core.book.api.article.entity.PhraseArticle;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.entity.ReviewArticleTag;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.api.member.entity.Member;
import com.core.book.api.member.repository.MemberRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleCreateService {

    private final PhraseArticleRepository phraseArticleRepository;
    private final ReviewArticleRepository reviewArticleRepository;
    private final MemberRepository memberRepository;
    private final ReadBooksRepository readBooksRepository;
    private final BookRepository bookRepository;

    // 감상평 게시글 생성
    public void createReviewArticle(ReviewArticleCreateDTO reviewArticleCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 사용자 읽은 책장에서 해당 ISBN이 있는지 확인
        String isbn = reviewArticleCreateDTO.getIsbn();
        boolean hasReadBook = readBooksRepository.existsByBookIsbnAndMemberId(isbn, userId);

        if (!hasReadBook) {
            throw new NotFoundException(ErrorStatus.READBOOK_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 책 정보 가져오기
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

        // ReviewArticleTag 생성
        ReviewArticleTag reviewArticleTag = null;
        ReviewArticleTagDTO tagDTO = reviewArticleCreateDTO.getReviewArticleTagDTO();

        if (tagDTO != null) {
            reviewArticleTag = tagDTO.toEntity();
            // CascadeType.ALL 설정으로 인해 별도의 저장 없이도 ReviewArticle 저장 시 함께 저장
        }

        ReviewArticle reviewArticle = ReviewArticle.builder()
                .content(reviewArticleCreateDTO.getContent())
                .oneLineReview(reviewArticleCreateDTO.getOneLineReview())
                .starRating(reviewArticleCreateDTO.getStarRating())
                .likeCnt(0)  // 처음 생성 시 좋아요, 인용, 댓글 수는 0
                .quoCnt(0)
                .commentCnt(0)
                .type(ArticleType.REVIEW)
                .member(member)
                .book(book)
                .reviewArticleTag(reviewArticleTag)
                .build();

        reviewArticleRepository.save(reviewArticle);
    }

    // 인상깊은구절 게시글 생성
    public void createPhraseArticle(PhraseArticleCreateDTO phraseArticleCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 사용자 읽은 책장에서 해당 ISBN이 있는지 확인
        String isbn = phraseArticleCreateDTO.getIsbn();
        boolean hasReadBook = readBooksRepository.existsByBookIsbnAndMemberId(isbn, userId);

        if (!hasReadBook) {
            throw new NotFoundException(ErrorStatus.READBOOK_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 책 정보 가져오기
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

        PhraseArticle phraseArticle = PhraseArticle.builder()
                .content(phraseArticleCreateDTO.getContent())
                .pageNum(phraseArticleCreateDTO.getPageNum())
                .phraseContent(phraseArticleCreateDTO.getPhraseContent())
                .likeCnt(0)  // 처음 생성 시 좋아요, 인용, 댓글 수는 0
                .quoCnt(0)
                .commentCnt(0)
                .type(ArticleType.PHRASE)
                .member(member)
                .book(book)
                .build();

        phraseArticleRepository.save(phraseArticle);
    }
}