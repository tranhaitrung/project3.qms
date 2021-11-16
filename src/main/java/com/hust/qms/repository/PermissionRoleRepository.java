package com.hust.qms.repository;

import com.hust.qms.entity.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRoleRepository extends JpaRepository<PermissionRole, Long> {
    PermissionRole findByCode(String code);
}
