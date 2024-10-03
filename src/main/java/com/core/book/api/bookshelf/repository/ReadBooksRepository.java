package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {

    Page<ReadBooks> findByMemberId(Long memberId, Pageable pageable);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);
}
