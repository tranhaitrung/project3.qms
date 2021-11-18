package com.hust.qms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "order_numbers")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long customerId;

    private Long number;

    private Timestamp createdAt;
}
