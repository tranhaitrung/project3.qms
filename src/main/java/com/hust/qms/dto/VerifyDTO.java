package com.hust.qms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class VerifyDTO {
    private Long id;

    private Long userId;

    private String type;

    private String username;

    private String email;

    private String phone;

    private String verifyCode;

    private String status;

    private Timestamp createdAt;

    private Timestamp expiredAt;
}
