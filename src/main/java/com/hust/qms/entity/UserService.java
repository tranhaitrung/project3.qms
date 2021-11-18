package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user_services")
public class UserService {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}
