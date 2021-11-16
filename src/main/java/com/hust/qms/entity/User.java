package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    private String password;

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

    private Integer verify_phone;

    private Integer verify_email;
}
