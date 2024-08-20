package com.core.book.api.member.repository;

import com.core.book.api.member.entity.Member;
import com.core.book.api.member.entity.UserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTagRepository extends JpaRepository<UserTag, Long> {

    Optional<UserTag> findByMember(Member member);

}