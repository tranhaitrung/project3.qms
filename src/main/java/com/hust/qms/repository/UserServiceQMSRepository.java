package com.hust.qms.repository;

import com.hust.qms.entity.UserServiceQMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserServiceQMSRepository extends JpaRepository<UserServiceQMS, Long> {

    @Query(value = "SELECT * FROM user_services where number = :number and status = :status and counter_id = :counter_id order by created_at desc limit 1", nativeQuery = true)
    UserServiceQMS findUserServiceByNumberLastAndStatus(@Param("number") String number, @Param("status") String status, @Param("counter_id") Integer counterId);

    @Query(value = "SELECT * FROM user_services where status = :status order by created_at asc ", nativeQuery = true)
    List<UserServiceQMS> findUserServiceQMSByStatus(@Param("status") String status);
}
