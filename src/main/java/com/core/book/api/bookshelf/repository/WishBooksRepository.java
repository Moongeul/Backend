package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {
}
