package com.hust.qms.repository;

import com.hust.qms.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Integer> {
    List<Counter> findAllByStatus(String status);

    Counter findCounterById(Integer id);

    Counter findCounterByIdAndStatus(Integer id, String status);

    @Query(value = "UPDATE counters SET service_name = :serviceName WHERE service_id = :serviceId", nativeQuery = true)
    @Transactional
    Integer updateServiceName(@Param("serviceName") String serviceName, @Param("serviceId") Long serviceId);
}
