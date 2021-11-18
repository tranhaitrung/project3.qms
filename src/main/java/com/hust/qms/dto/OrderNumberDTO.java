package com.hust.qms.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class OrderNumberDTO {
    private String fullNameCustomer;

    private String fullNameMember;

    private String orderNumber;

    private Integer counterId;

    private String counterName;

    private Timestamp createdAt;
}
