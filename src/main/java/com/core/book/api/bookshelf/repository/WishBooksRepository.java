package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {
    List<WishBooks> findByMemberId(Long memberId);
}
