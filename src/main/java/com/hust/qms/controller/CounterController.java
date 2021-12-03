package com.hust.qms.controller;

import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/counter")
public class CounterController {
    @Autowired
    private CounterService counterService;

    @GetMapping("/get-all")
    public ResponseEntity getAll(){
        ServiceResponse response = counterService.getCounterAll();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/detail")
    public ResponseEntity counterDetail(@RequestParam("counterId") Integer counterId) {
        ServiceResponse response = counterService.counterDetail(counterId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
