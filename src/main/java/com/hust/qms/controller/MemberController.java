package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/list")
    public ResponseEntity<?> listCustomer(@RequestParam(required = false) String search,
                                          @RequestParam(required = false) String status,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
                                          @RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        ServiceResponse response = memberService.listMember(search, status, fromDate, toDate, pageNo, pageSize);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
