package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {

    // findByMemberIdOrderByReadDateDesc : memberId로 조회하면서 readDate를 기준으로 내림차순 정렬
    List<ReadBooks> findByMemberIdOrderByReadDateDesc(Long memberId);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);

    @Query("SELECT rb FROM ReadBooks rb JOIN FETCH rb.book WHERE rb.member.id = :memberId ORDER BY rb.readDate DESC")
    List<ReadBooks> findReadBooksByMemberId(@Param("memberId") Long memberId);
}
