package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @GetMapping("/list-ticket")
    public ResponseEntity<?> listTicket(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) String serviceCode,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")Date fromDate,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                        @RequestParam(defaultValue = "1") int pageNo,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        ServiceResponse response = ticketService.getListTicket(search, serviceCode, status, fromDate, toDate, pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
