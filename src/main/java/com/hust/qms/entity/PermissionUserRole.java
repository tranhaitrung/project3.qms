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
@Table(name = "permission_user_roles")
public class PermissionUserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;

    private String status;

    private Integer manualAdd;

    private String roleCode;

    private Long createdBy;

    private Long updatedBy;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
