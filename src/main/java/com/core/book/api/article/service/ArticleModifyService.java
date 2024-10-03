package com.core.book.api.article.service;

import com.core.book.api.article.dto.ReviewArticleCreateDTO;
import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.article.repository.ReviewArticleRepository;
import com.core.book.api.book.entity.Book;
import com.core.book.api.book.repository.BookRepository;
import com.core.book.api.bookshelf.repository.ReadBooksRepository;
import com.core.book.common.exception.NotFoundException;
import com.core.book.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleModifyService {

    private final ReviewArticleRepository reviewArticleRepository;
    private final ReadBooksRepository readBooksRepository;
    private final BookRepository bookRepository;


    // 감상평 게시글 수정
    public void modifyReviewArticle(Long articleId, ReviewArticleCreateDTO reviewArticleCreateDTO, Long userId) {

        // 게시글 조회
        ReviewArticle reviewArticle = reviewArticleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ARTICLE_NOT_FOUND_EXCEPTION.getMessage()));

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
    }
}
