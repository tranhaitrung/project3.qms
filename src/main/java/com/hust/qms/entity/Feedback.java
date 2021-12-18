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
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long customerId;
    private Integer counterId;
    private Long memberId;
    private Long serviceId;
    private String customerFullname;
    private String memberFullname;
    private String serviceName;
    private Integer score;
    private String comment;
    private Long ticketId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
