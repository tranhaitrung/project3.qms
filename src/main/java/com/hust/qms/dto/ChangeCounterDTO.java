package com.hust.qms.dto;

import lombok.Data;

@Data
public class ChangeCounterDTO {
    private Integer counterIdFrom;
    private Integer counterIdTo;
    private String number;
}
