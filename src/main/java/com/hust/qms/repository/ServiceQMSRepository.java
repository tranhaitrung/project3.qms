package com.hust.qms.repository;

import com.hust.qms.entity.ServiceQMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ServiceQMSRepository extends JpaRepository<ServiceQMS, Long> {
    ServiceQMS findByServiceCodeAndStatus(String code, String status);

    ServiceQMS findServiceQMSByServiceCode(String code);

    ServiceQMS findServiceQMSById(Long id);

    @Transactional
    @Query(value = "UPDATE services SET status = :status, updated_at = now(), updated_by = :updatedBy where id = :id", nativeQuery = true)
    int updateStatusService(@Param("status") String status, @Param("updatedBy") Long userId, @Param("id") Long id);

}
