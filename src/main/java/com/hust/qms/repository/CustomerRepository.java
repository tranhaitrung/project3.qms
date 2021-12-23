package com.hust.qms.repository;

import com.hust.qms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findCustomerByUsername(String userName);

    @Query(value = "select * from customers where (:search is null or LOCATE(:search, CONCAT_WS(', ', full_name, username, email))) " +
            " AND (:status is null or FIND_IN_SET(status,:status)) " +
            " and (:fromDate is null or created_at >= :fromDate ) and (:toDate is null or created_at <= :toDate) " +
            " order by created_at desc", nativeQuery = true)
    Page<Customer> listCustomer(@Param("search") String search, @Param("status") String status, @Param("fromDate") String fromDate, @Param("toDate") String toDate, Pageable pageable);

    @Query(value = "SELECT date(created_at) as createdAt, count(id) as total FROM customers group by Date(created_at) order by created_at desc limit 7", nativeQuery = true)
    List<Map<String, Object>> customerStatistic();
}
