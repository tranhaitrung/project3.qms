package com.hust.qms.repository;

import com.hust.qms.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findMemberByUserId(Long userId);
    @Query(value = "select * from members where (:search is null or LOCATE(:search, CONCAT_WS(', ', full_name, username, email))) " +
            " AND (:status is null or FIND_IN_SET(status,:status)) " +
            " and (:fromDate is null or created_at >= :fromDate ) and (:toDate is null or created_at <= :toDate) " +
            " order by created_at desc", nativeQuery = true)
    Page<Member> listMember(@Param("search") String search, @Param("status") String status, @Param("fromDate") String fromDate, @Param("toDate") String toDate, Pageable pageable);
}
