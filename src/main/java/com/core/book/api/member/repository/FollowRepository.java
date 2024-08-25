package com.core.book.api.member.repository;

import com.core.book.api.member.entity.Follow;
import com.core.book.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

}
