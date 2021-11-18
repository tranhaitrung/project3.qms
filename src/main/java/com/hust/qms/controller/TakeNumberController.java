package com.hust.qms.controller;

import com.hust.qms.service.TakeNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TakeNumberController {
    @Autowired
    private TakeNumberService takeNumberService;

    @GetMapping("/take-number")
    public ResponseEntity takeNumber(@RequestParam(value = "serviceCode", required = true) String code) {
        return ResponseEntity.ok(takeNumberService.takeNumber(code.toUpperCase()));
    }
}
