package com.hust.qms.service;

import com.hust.qms.entity.Counter;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterService {
    @Autowired
    private CounterRepository counterRepository;

    public ServiceResponse getCounterAll(){
        List<Counter> list = counterRepository.findAll();
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", list);
    }
}
