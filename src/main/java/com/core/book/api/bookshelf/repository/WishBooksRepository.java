package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {

    Page<WishBooks> findByMemberId(Long memberId, Pageable pageable)

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);
}
