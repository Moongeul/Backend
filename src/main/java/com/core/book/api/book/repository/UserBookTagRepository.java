package com.core.book.api.book.repository;

import com.core.book.api.article.entity.ReviewArticle;
import com.core.book.api.book.entity.UserBookTag;
import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookTagRepository extends JpaRepository<UserBookTag, Integer> {

    // 'ReadBooks(읽은 책)' ID에 해당하는 태그 리스트 가져오기
    List<UserBookTag> findByReadBooks(ReadBooks readBooks);

    // 'ReviewArticle(감상평 게시글)' ID에 해당하는 태그 리스트 가져오기
    List<UserBookTag> findByReviewArticle(ReviewArticle reviewArticle);

    @Query("""
    SELECT u.tag 
    FROM UserBookTag u 
    WHERE u.book.isbn = :isbn
    GROUP BY u.tag 
    ORDER BY COUNT(u.tag) DESC
""")
    List<Integer> findTop5TagsByIsbn(String isbn, Pageable pageable);


}
