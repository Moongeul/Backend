package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.WishBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishBooksRepository extends JpaRepository<WishBooks, Long> {

    @Query(value =
            "SELECT * " +
            "FROM WishBooks " +
            "WHERE user_id = :memberId " +
            "ORDER BY wishbooks_id DESC " +   // read_date로 우선 정렬, 그다음 id로 정렬
            "LIMIT :listLimit OFFSET :startPage",
            nativeQuery = true)
    List<WishBooks> findByMemberId(@Param("memberId") Long memberId,
                                   @Param("startPage") int startPage,
                                   @Param("listLimit") int listLimit);

    boolean existsByBookIsbnAndMemberId(String bookIsbn, Long memberId);
}
