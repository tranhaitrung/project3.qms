package com.hust.qms.service;

import com.hust.qms.dto.CounterDTO;
import com.hust.qms.entity.Counter;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.common.Const.Status.INACTIVE;
import static com.hust.qms.common.Const.StatusUserService.*;
import static com.hust.qms.common.Const.StatusUserService.WAITING;
import static com.hust.qms.exception.ServiceResponse.BAD_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

@Service
public class CounterService {
    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    public ServiceResponse getCounterAll(){
        List<Counter> list = counterRepository.findAll();
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", list);
    }

    public ServiceResponse counterDetail (Integer counterId) {



        Counter counter = counterRepository.findCounterByIdAndStatus(counterId, ACTIVE);

        if (counter == null) {
            Counter counterI = counterRepository.findCounterByIdAndStatus(counterId, INACTIVE);
            return SUCCESS_RESPONSE("INACTIVE", counterI);
        }

        String waitingCustomerIds = counter.getWaitingCustomerIds(); //Danh sách số đợi của khách hàng

        CounterDTO counterDTO = new CounterDTO();

        String missedCustomerIds = counter.getMissedCustomerIds(); //Danh sách số nhỡ

        List<UserServiceQMS> listCustomerMiss = new ArrayList<>();
        List<UserServiceQMS> listCustomerWaiting = new ArrayList<>();

        if (!StringUtils.isBlank(missedCustomerIds)) {
            List<String> missIds = Arrays.asList(missedCustomerIds.split(","));

            for (int i = 0; i < missIds.size(); i++) {
                UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(missIds.get(i), MISSED, counterId);
                //Customer customer = customerRepository.findCustomerByUsername(userServiceQMS.getUsername());
                listCustomerMiss.add(userServiceQMS);
            }
        }

        //Nếu không khách hàng trong hàng đợi
        if (!StringUtils.isBlank(waitingCustomerIds)) {
            List<String> listNumberWaiting = Arrays.asList(waitingCustomerIds.split(","));
            for (int i=0; i< listNumberWaiting.size(); i++) {
                UserServiceQMS userWaiting = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(listNumberWaiting.get(i), WAITING, counterId);
                //Customer customer = customerRepository.findCustomerByUsername(userServiceQMS.getUsername());
                listCustomerWaiting.add(userWaiting);
            }
        }

        counterDTO.setWaitingCustomerIds(counter.getWaitingCustomerIds());
        counterDTO.setFullNameMember(counter.getFullNameMember());
        counterDTO.setFirstNameMember(counter.getFirstNameMember());
        counterDTO.setLastNameMember(counter.getLastNameMember());
        counterDTO.setMemberId(counter.getMemberId());
        counterDTO.setLastNameCustomer(counter.getLastNameCustomer());
        counterDTO.setFirstNameCustomer(counter.getFirstNameCustomer());
        counterDTO.setFullNameCustomer(counter.getFullNameCustomer());
        counterDTO.setWaitingCustomerList(listCustomerWaiting);
        counterDTO.setCustomerId(counter.getCustomerId());
        counterDTO.setServiceId(counter.getServiceId());
        counterDTO.setServiceName(counter.getServiceName());
        counterDTO.setCounterId(counterId);
        counterDTO.setCounterName(counter.getName());
        counterDTO.setOrderNumber(counter.getOrderNumber());
        counterDTO.setStatus(counter.getStatus());
        counterDTO.setMissedCustomerList(listCustomerMiss);

        return SUCCESS_RESPONSE("Successful",counterDTO);
    }
}
