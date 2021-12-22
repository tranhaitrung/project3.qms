package com.hust.qms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long id;

    private Long customerId;

    private String username;

    private Long serviceId;

    private String serviceCode;

    private String serviceName;

    private String status;

    private String lastNameCustomer;

    private String firstNameCustomer;

    private String fullNameCustomer;

    private Long memberId;

    private String firstNameMember;

    private String lastNameMember;

    private String fullNameMember;

    private int counterId;

    private String counterName;

    private String number;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String createdDisplay;
}
