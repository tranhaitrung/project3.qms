package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.hust.qms.common.Const.Status.INACTIVE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "counters")
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

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

    private String missedCustomerIds;

    private Long serviceId;

    private String serviceName;
}
