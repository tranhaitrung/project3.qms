package com.hust.qms.controller;

import com.hust.qms.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/counter")
public class CounterController {
    @Autowired
    private CounterService counterService;

    @GetMapping("/get-all")
    public ResponseEntity getAll(){
        return ResponseEntity.ok(counterService.getCounterAll());
    }
}
