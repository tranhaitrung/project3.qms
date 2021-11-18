package com.hust.qms.repository;

import com.hust.qms.entity.OrderNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderNumberRepository extends JpaRepository<OrderNumber, Long> {

    @Query(value = "SELECT * FROM order_numbers ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    OrderNumber getLastOrderNumber();

    OrderNumber findOrderNumberByNumber(Long number);

}
