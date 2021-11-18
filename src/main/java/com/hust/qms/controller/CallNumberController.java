package com.hust.qms.controller;

import com.hust.qms.dto.CounterDTO;
import com.hust.qms.service.CallNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CallNumberController {
    @Autowired
    private CallNumberService callNumberService;

    @PostMapping("/active-counter")
    public ResponseEntity activeCounter(@RequestBody CounterDTO counterDTO) {
        return ResponseEntity.ok(callNumberService.activeCounter(counterDTO));
    }
}
