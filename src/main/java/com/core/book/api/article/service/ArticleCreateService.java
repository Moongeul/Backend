package com.core.book.api.article.service;

import com.core.book.api.article.dto.*;
import com.core.book.api.article.entity.*;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.repository.QnaArticleRepository;
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
    private final QnaArticleRepository qnaArticleRepository;

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
                .rating(reviewArticleCreateDTO.getRating())
                .likeCnt(0)  // 처음 생성 시 좋아요, 인용, 댓글 수는 0
                .quoCnt(0)
                .commentCnt(0)
                .type(ArticleType.REVIEW)
                .member(member)
                .book(book)
                .reviewArticleTag(reviewArticleTag)
                .build();

        reviewArticleRepository.save(reviewArticle);

        // BOOK rating_average 갱신
        updateRatingAverage(book, reviewArticleCreateDTO.getRating());
    }

    // BOOK rating_average 갱신 메서드
    public void updateRatingAverage(Book book, double new_rating){

        // 해당 책의 평균 평점(rating_average) 새로 계산 및 rating_count(평점 개수) + 1
        // 계산 공식: new_rating_average = (rating_average * rating_count + rating) / rating_count + 1 )
        double new_rating_average = (book.getRatingAverage() * book.getRatingCount() + new_rating) / (book.getRatingCount() + 1);
        new_rating_average = Math.round(new_rating_average * 100) / 100.0;

        Book updatedBook = book.toBuilder()
                .ratingAverage(new_rating_average)
                .ratingCount(book.getRatingCount() + 1)
                .build();

        bookRepository.save(updatedBook);
    }

    // 인상깊은구절 게시글 생성
    public void createPhraseArticle(PhraseArticleCreateDTO phraseArticleCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        PhraseArticle phraseArticle = PhraseArticle.builder()
                .likeCnt(0)
                .quoCnt(0)
                .commentCnt(0)
                .type(ArticleType.PHRASE)
                .member(member)
                .build();

        // 자식 구절 리스트 추가
        if (phraseArticleCreateDTO.getPhraseContents() != null) {
            for (PhraseArticleContentDTO contentDTO : phraseArticleCreateDTO.getPhraseContents()) {

                // 사용자가 읽은 책인지 확인
                boolean hasReadBook = readBooksRepository.existsByBookIsbnAndMemberId(contentDTO.getIsbn(), userId);
                if (!hasReadBook) {
                    throw new NotFoundException(ErrorStatus.READBOOK_NOT_FOUND_EXCEPTION.getMessage());
                }

                // 책 엔티티 조회
                Book book = bookRepository.findById(contentDTO.getIsbn())
                        .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

                PhraseArticleContent phraseArticleContent = PhraseArticleContent.builder()
                        .content(contentDTO.getContent())
                        .pageNum(contentDTO.getPageNum())
                        .phraseContent(contentDTO.getPhraseContent())
                        .book(book)
                        .build();

                // PhraseArticle 연결
                phraseArticle.addPhraseArticleContent(phraseArticleContent);
            }
        }

        phraseArticleRepository.save(phraseArticle);
    }

    // QnA 게시글 생성
    public void createQnaArticle(QnaArticleCreateDTO qnaArticleCreateDTO, Long userId) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOTFOUND_EXCEPTION.getMessage()));

        // 사용자 읽은 책장에서 해당 ISBN이 있는지 확인
        String isbn = qnaArticleCreateDTO.getIsbn();
        boolean hasReadBook = readBooksRepository.existsByBookIsbnAndMemberId(isbn, userId);

        if (!hasReadBook) {
            throw new NotFoundException(ErrorStatus.READBOOK_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 책 정보 가져오기
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

        QnaArticle qnaArticle = QnaArticle.builder()
                .likeCnt(0)
                .quoCnt(0)
                .commentCnt(0)
                .type(ArticleType.QNA)
                .book(book)
                .member(member)
                .build();

        // 질문 리스트 추가
        if (qnaArticleCreateDTO.getQnaContents() != null) {
            for (QnaArticleContentDTO contentDTO : qnaArticleCreateDTO.getQnaContents()) {

                QnaArticleContent qnaArticleContent = QnaArticleContent.builder()
                        .content(contentDTO.getContent())
                        .build();

                // QnaArticle 연결
                qnaArticle.addQnaArticleContent(qnaArticleContent);
            }
        }

        qnaArticleRepository.save(qnaArticle);
    }
}