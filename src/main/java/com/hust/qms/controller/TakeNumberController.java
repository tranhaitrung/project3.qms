package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.QmsService;
import com.hust.qms.service.TakeNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(value = "*", maxAge = 3600)
public class TakeNumberController {
    @Autowired
    private TakeNumberService takeNumberService;

    @GetMapping("/take-number")
    public ResponseEntity takeNumber(@RequestParam(value = "serviceCode", required = true) String code) {
        ServiceResponse response = takeNumberService.takeNumber(code.toUpperCase());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/my-number")
    public ResponseEntity myNumber() {
        ServiceResponse response = takeNumberService.myNumber();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/my-list-number")
    public ResponseEntity<?> myListNumber(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) String serviceCode,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")Date fromDate,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")Date toDate,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "5") Integer pageSize) {
        ServiceResponse response = takeNumberService.listUserNumber(search,userId, serviceCode, fromDate, toDate, status, pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
