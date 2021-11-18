package com.hust.qms.repository;

import com.hust.qms.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findMemberByUserId(Long userId);
}
