package com.hust.qms.controller;

import com.hust.qms.service.TakeNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(value = "*", maxAge = 3600)
public class TakeNumberController {
    @Autowired
    private TakeNumberService takeNumberService;

    @GetMapping("/take-number")
    public ResponseEntity takeNumber(@RequestParam(value = "serviceCode", required = true) String code) {
        return ResponseEntity.ok(takeNumberService.takeNumber(code.toUpperCase()));
    }
}
