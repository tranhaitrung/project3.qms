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
public class UserDTO {

    private Long userId;

    private String username;

    private String password;

    private String status;

    private String firstName;

    private String lastName;

    private String fullName;

    private String roleCode;

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
