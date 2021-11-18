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
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String status;

    private String firstName;

    private String lastName;

    private String fullName;

    private String countryCode;

    private String country;

    private String address;

    private String district;

    private String city;

    private String avatar;

    private String displayName;

    private String phone;

    private String email;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Long updatedBy;

    private Timestamp birthday;
}
