package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {

    @Query(value =
            "SELECT * " +
            "FROM WishBooks " +
            "WHERE user_id = :memberId " +
            "ORDER BY wishbooks_id DESC " +
            "LIMIT :size OFFSET :startPage", // 페이징 처리
            nativeQuery = true)
    List<WishBooks> findByMemberId(@Param("memberId") Long memberId,
                                   @Param("startPage") int startPage,
                                   @Param("size") int size);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);
}
