package com.hust.qms.repository;

import com.hust.qms.entity.UserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserServiceRepository extends JpaRepository<UserService, Long> {
}
