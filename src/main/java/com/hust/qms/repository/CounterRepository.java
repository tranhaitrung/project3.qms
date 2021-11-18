package com.hust.qms.repository;

import com.hust.qms.entity.Counter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Integer> {
    List<Counter> findAllByStatus(String status);

    Counter findCounterById(Integer id);

    Counter findCounterByIdAndStatus(Integer id, String status);
}
