package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {

    // findByMemberIdOrderByReadDateDesc : memberId로 조회하면서 readDate를 기준으로 내림차순 정렬
    @Query(value =
            "SELECT * " +
            "FROM ReadBooks " +
            "WHERE user_id = :memberId " +
            "ORDER BY read_date DESC, readbooks_id DESC " +   // read_date로 우선 정렬, 그다음 id로 정렬
            "LIMIT :size OFFSET :startPage", // 페이징 처리
            nativeQuery = true)
    List<ReadBooks> findByMemberIdOrderByReadDateDesc(@Param("memberId") Long memberId,
                                                      @Param("startPage") int startPage,
                                                      @Param("size") int size);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);

}
