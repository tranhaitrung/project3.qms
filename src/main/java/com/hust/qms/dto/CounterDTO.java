package com.hust.qms.dto;

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

    private Long memberId;

    private String firstNameMember;

    private String lastNameMember;

    private String fullNameMember;

    private String orderNumber;

    private String waitingCustomerIds;

    private String missedCustomerIds;

    private List<UserServiceQMS> waitingCustomerList;

    private List<UserServiceQMS> missedCustomerList;


}
