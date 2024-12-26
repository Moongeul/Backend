package com.core.book.api.article.service;

import com.core.book.api.article.dto.PhraseArticleContentDTO;
import com.core.book.api.article.dto.PhraseArticleCreateDTO;
import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.article.entity.PhraseArticle;
import com.core.book.api.article.entity.PhraseArticleContent;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.repository.PhraseArticleRepository;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleModifyService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final PhraseArticleRepository phraseArticleRepository;
    private final ReadBooksRepository readBooksRepository;
    private final BookRepository bookRepository;

    // 감상평 게시글 수정
    public void modifyReviewArticle(Long articleId, ReviewArticleCreateDTO reviewArticleCreateDTO, Long userId) {

        // 게시글 조회
        ReviewArticle reviewArticle = reviewArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        double old_rating = reviewArticle.getRating();

        // 게시글 작성자와 수정 요청자가 다를 경우 예외 처리
        if (!reviewArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_MODIFY_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        // 사용자 읽은 책장에서 해당 ISBN이 있는지 확인
        String isbn = reviewArticleCreateDTO.getIsbn();
        boolean hasReadBook = readBooksRepository.existsByBookIsbnAndMemberId(isbn, userId);

        if (!hasReadBook) {
            throw new NotFoundException(ErrorStatus.READBOOK_NOT_FOUND_EXCEPTION.getMessage());
        }

        // 새로운 ISBN 처리
        Book newBook = reviewArticle.getBook();
        if (reviewArticleCreateDTO.getIsbn() != null
                && !reviewArticleCreateDTO.getIsbn().equals(reviewArticle.getBook().getIsbn())) {
            newBook = bookRepository.findById(isbn)
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));
        }

        // 업데이트된 엔티티 생성
        ReviewArticle updatedArticle = reviewArticle.update(reviewArticleCreateDTO, newBook);

        // 엔티티 저장
        reviewArticleRepository.save(updatedArticle);

        // 평균 평점 수정 (단, rating_count 는 오르지 않음)
        Book book = bookRepository.findById(reviewArticleCreateDTO.getIsbn()).
                orElseThrow(() -> new NotFoundException(ErrorStatus.BOOK_NOTFOUND_EXCEPTION.getMessage()));

        modifyRatingAverage(book, reviewArticleCreateDTO.getRating(), old_rating);
    }

    // BOOK rating_average 갱신 메서드
    public void modifyRatingAverage(Book book, double new_rating, double old_rating){

        // 새 점수들의 합 = (기존 점수들의 합) - (바뀌기 이전 평점) + (바뀐 평점)
        double new_rating_sum = (book.getRatingAverage() * book.getRatingCount()) - old_rating + new_rating;
        double new_rating_average = new_rating_sum / book.getRatingCount();
        new_rating_average = Math.round(new_rating_average * 100) / 100.0;

        Book updatedBook = book.toBuilder()
                .ratingAverage(new_rating_average)
                .build();

        bookRepository.save(updatedBook);
    }

    // 인상깊은구절 게시글 수정
    public void modifyPhraseArticle(Long articleId, PhraseArticleCreateDTO phraseArticleCreateDTO, Long userId) {

        // 게시글 조회
        PhraseArticle phraseArticle = phraseArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

        // 게시글 작성자와 수정 요청자가 다를 경우 예외 처리
        if (!phraseArticle.getMember().getId().equals(userId)) {
            throw new NotFoundException(ErrorStatus.ARTICLE_MODIFY_NOT_SAME_USER_EXCEPTION.getMessage());
        }

        // 기존 구절들 전부 삭제
        phraseArticle.getPhraseArticleContents().clear();

        // 새 구절 목록 생성
        List<PhraseArticleContent> newContents = new ArrayList<>();
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

            newContents.add(phraseArticleContent);
        }

        phraseArticleRepository.save(phraseArticle);
    }
}
