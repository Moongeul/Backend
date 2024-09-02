package com.core.book.api.bookshelf.repository;

import com.core.book.api.bookshelf.entity.ReadBooks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadBooksRepository extends JpaRepository<ReadBooks, Long> {

    // findByMemberIdOrderByReadDateDesc : memberId로 조회하면서 readDate를 기준으로 내림차순 정렬
    List<ReadBooks> findByMemberIdOrderByReadDateDesc(Long memberId);
}
