package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public ResponseEntity<?> listCustomer(@RequestParam(required = false) String search,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        ServiceResponse response = customerService.listCustomer(search, status, pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/statistic-round-seven-day")
    public ResponseEntity<?> customerStatisticRoundSevenDay() {
        ServiceResponse response = customerService.customerStatisticRoundSevenDay();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
