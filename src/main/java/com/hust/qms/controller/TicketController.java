package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticket")
@CrossOrigin(value = "*", maxAge = 3600)
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/statistic-around-seven-day")
    public ResponseEntity<?> ticketStatisticAroundSevenDay() {
        ServiceResponse response = ticketService.serviceStatisticAroundSevenDay();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/statistic")
    public ResponseEntity<?> statistic() {
        ServiceResponse response = ticketService.eachTicketStatisticAroundSevenDay();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
