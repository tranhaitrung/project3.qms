package com.hust.qms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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

    private Long createdAtLong;

    private String createdAtStr;

    private String birthdayDisplay;

    private Timestamp updatedAt;

    private Long updatedBy;

    private String updatedAtStr;

    private Long updatedAtLong;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp birthday;

    private Integer verify_phone;

    private Integer verify_email;
}
