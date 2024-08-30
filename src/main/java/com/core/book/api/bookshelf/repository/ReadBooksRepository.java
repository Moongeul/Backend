package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {
}
