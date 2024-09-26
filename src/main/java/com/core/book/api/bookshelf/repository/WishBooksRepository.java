package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {
    List<WishBooks> findByMemberId(Long memberId);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);

    @Query("SELECT wb FROM WishBooks wb JOIN FETCH wb.book WHERE wb.member.id = :memberId")
    List<WishBooks> findWishBooksByMemberId(@Param("memberId") Long memberId);
}
