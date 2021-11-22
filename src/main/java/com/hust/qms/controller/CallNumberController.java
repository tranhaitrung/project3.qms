package com.hust.qms.controller;

import com.hust.qms.dto.CounterDTO;
import com.hust.qms.service.CallNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CallNumberController {
    @Autowired
    private CallNumberService callNumberService;

    @PostMapping("/active-counter")
    public ResponseEntity activeCounter(@RequestBody CounterDTO counterDTO) {
        return ResponseEntity.ok(callNumberService.activeCounter(counterDTO));
    }

    @GetMapping("/next-number")
    public ResponseEntity nextNumber(@RequestParam("counterId") Integer counterId) {
        return ResponseEntity.ok(callNumberService.nextCustomer(counterId));
    }

    @GetMapping("/make-miss-number")
    public ResponseEntity missNumber(@RequestParam("counterId")Integer counterId) {
        return ResponseEntity.ok(callNumberService.missCustomer(counterId));
    }
}
