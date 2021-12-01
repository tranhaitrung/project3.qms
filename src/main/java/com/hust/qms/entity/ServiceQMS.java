package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "services")
public class ServiceQMS {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String serviceCode;

    private String serviceName;

    private String status;

    private Integer manualAdd;

    private Long price;

    private String image;

    private Long createdBy;

    private Long updatedBy;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
