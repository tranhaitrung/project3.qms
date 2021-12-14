package com.hust.qms.dto;

import com.hust.qms.entity.Counter;
import com.hust.qms.entity.UserServiceQMS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterDTO {
    private Integer counterId;

    private String counterName;

    private String status;

    private Long customerId;

    private String firstNameCustomer;

    private String lastNameCustomer;

    private String fullNameCustomer;

    private Long serviceId;

    private String serviceName;

    private String serviceCode;

    private Long memberId;

    private String firstNameMember;

    private String lastNameMember;

    private String fullNameMember;

    private String orderNumber;

    private String waitingCustomerIds;

    private String missedCustomerIds;

    private List<UserServiceQMS> waitingCustomerList;

    private List<UserServiceQMS> missedCustomerList;

    public CounterDTO (Counter counter) {
        setFullNameMember(counter.getFullNameMember());
        setFirstNameMember(counter.getFirstNameMember());
        setLastNameMember(counter.getLastNameMember());
        setMemberId(counter.getMemberId());

        setLastNameCustomer(counter.getLastNameCustomer());
        setFirstNameCustomer(counter.getFirstNameCustomer());
        setFullNameCustomer(counter.getFullNameCustomer());

        setCustomerId(counter.getCustomerId());

        setServiceId(counter.getServiceId());
        setServiceName(counter.getServiceName());

        setCounterId(counter.getId());
        setCounterName(counter.getName());
        setOrderNumber(counter.getOrderNumber());
        setStatus(counter.getStatus());
    }
}
