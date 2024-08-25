package com.core.book.api.member.repository;

import com.core.book.api.member.entity.InfoOpen;
import com.core.book.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfoOpenRepository extends JpaRepository<InfoOpen, Long> {

    Optional<InfoOpen> findByMember(Member member);
}