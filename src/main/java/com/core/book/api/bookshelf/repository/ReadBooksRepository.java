package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {

    Page<ReadBooks> findByMemberId(Long memberId, Pageable pageable);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);

    @Query("SELECT rb FROM ReadBooks rb JOIN FETCH rb.book WHERE rb.member.id = :memberId ORDER BY rb.readDate DESC")
    List<ReadBooks> findReadBooksByMemberId(@Param("memberId") Long memberId);
}
