package com.hust.qms.repository;

import com.hust.qms.entity.PermissionUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionUserRoleRepository extends JpaRepository<PermissionUserRole, Long> {

    List<PermissionUserRole> findAllByUserIdAndStatus(Long userId, String status);
    List<PermissionUserRole> findAllByUserId(Long userId);
}
