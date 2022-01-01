package com.hust.qms.repository;

import com.hust.qms.entity.UserServiceQMS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.hust.qms.common.Const.Procedures.LIST_CUSTOMER_ORDER_NUMBER;

@Repository
public interface UserServiceQMSRepository extends JpaRepository<UserServiceQMS, Long> {

    @Query(value = "SELECT * FROM user_services where number = :number and status = :status and counter_id = :counter_id order by created_at desc limit 1", nativeQuery = true)
    UserServiceQMS findUserServiceByNumberLastAndStatus(@Param("number") String number, @Param("status") String status, @Param("counter_id") Integer counterId);

    @Query(value = "SELECT * FROM user_services where status = :status order by created_at asc ", nativeQuery = true)
    List<UserServiceQMS> findUserServiceQMSByStatus(@Param("status") String status);

    @Query(value = "UPDATE user_services SET service_name = :serviceName, service_code = :code WHERE service_id = :id", nativeQuery = true)
    @Transactional
    @Modifying
    Integer updateServiceQms(@Param("serviceName") String serviceName, @Param("code") String code, @Param("id") Long id);

    @Query(value = "SELECT * FROM user_services WHERE customer_id = :userId and created_at >= :today order by created_at desc", nativeQuery = true)
    List<UserServiceQMS> getUserServiceQMSByCustomerId(@Param("userId") Long userId, @Param("today") String today);

    @Query(value = "SELECT * FROM user_services WHERE customer_id = :userId and created_at >= :today and status != :status", nativeQuery = true)
    List<UserServiceQMS> getUserServiceQMSByCustomerIdAndNotStatus(@Param("userId") Long userId, @Param("today") String today, @Param("status") String status);

    UserServiceQMS getUserServiceQMSById(Long ticketId);

    @Query(value = "SELECT * FROM user_services WHERE customer_id = :userId and (:serviceCode is null or FIND_IN_SET(service_code,:serviceCode)) and (:fromDate is null or created_at >= :fromDate) and (:toDate is null or created_at <= :toDate) and (:status is null or FIND_IN_SET(status,:status)) order by created_at desc", nativeQuery = true)
    Page<UserServiceQMS> findUserServiceQMSByCustomerIdAndSearch(@Param("userId") Long userId, @Param("serviceCode") String serviceCode, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate, @Param("status") String status, Pageable pageable);

    @Query(value = LIST_CUSTOMER_ORDER_NUMBER, nativeQuery = true)
    List<Map<String,Object>> searchListOrderNumberCustomer(String typeQuery, String search, Long userId, String serviceCode, Date fromDate, Date toDate, String status, int pageNo, int pageSize);

    @Query(value = LIST_CUSTOMER_ORDER_NUMBER, nativeQuery = true)
    Integer countListOrderNumberCustomer(String typeQuery, String search, Long userId, String serviceCode, Date fromDate, Date toDate, String status, int pageNo, int pageSize);

    @Query(value = "SELECT count(id) as total, Date(created_at) as createdAt from user_services group by Date(created_at) order by created_at desc limit 7", nativeQuery = true)
    List<Map<String,Object>> ticketStatisticAroundSevenDate();

    @Query(value = "SELECT count(id) as total, date(created_at) as createdAt FROM user_services WHERE service_code = :serviceCode group by Date(created_at) order by created_at limit 7", nativeQuery = true)
    List<Map<String, Object>> eachTicketStatisticAroundSevenDate(String serviceCode);

    @Query(value = "SELECT * FROM user_services WHERE " +
            "(:search is null or LOCATE(:search, CONCAT_WS(', ', counter_name, full_name_customer, full_name_member, service_code, service_name))) " +
            "and (:serviceCode is null or FIND_IN_SET(service_code,:serviceCode)) " +
            "and (:fromDate is null or created_at >= :fromDate ) and (:toDate is null or created_at <= :toDate) " +
            "and (:status is null or FIND_IN_SET(status,:status)) order by created_at desc", nativeQuery = true)
    Page<UserServiceQMS> listTicket(@Param("search") String search, @Param("serviceCode") String serviceCode, @Param("status") String status, @Param("fromDate") String fromDate, @Param("toDate") String toDate, Pageable pageable);
}
