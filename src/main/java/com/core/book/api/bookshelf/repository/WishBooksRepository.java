package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {

    Page<WishBooks> findByMemberId(Long memberId, Pageable pageable);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);

    @Query("SELECT wb FROM WishBooks wb JOIN FETCH wb.book WHERE wb.member.id = :memberId")
    List<WishBooks> findWishBooksByMemberId(@Param("memberId") Long memberId);
}
