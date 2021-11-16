package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "verify_codes")
public class VerifyCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
