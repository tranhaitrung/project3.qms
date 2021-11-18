package com.hust.qms.repository;

import com.hust.qms.entity.ServiceQMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceQMSRepository extends JpaRepository<ServiceQMS, Long> {
    ServiceQMS findByServiceCodeAndStatus(String code, String status);
}
