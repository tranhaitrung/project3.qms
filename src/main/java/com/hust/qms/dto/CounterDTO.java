package com.hust.qms.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounterDTO {
    private Integer counterId;

    private String counterName;

    private String status;

    private Long customerId;

    private String firstNameCustomer;

    private String lastNameCustomer;

    private String fullNameCustomer;

    private Long memberId;

    private String firstNameMember;

    private String lastNameMember;

    private String fullNameMember;

    private String orderNumber;

    private String waitingCustomerIds;

    private Long serviceId;

    private String serviceName;
}
