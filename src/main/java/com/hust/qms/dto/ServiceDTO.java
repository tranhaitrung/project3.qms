package com.hust.qms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Long serviceId;

    private String serviceCode;

    private String serviceName;

    private String status;

    private Integer manualAdd;

    private Long price;

    private String image;

    private Long createdBy;

    private Long updatedBy;

    private String createdAt;

    private String updatedAt;

    private Float scoreAverage;

    private Map<String,Integer> score;

}
