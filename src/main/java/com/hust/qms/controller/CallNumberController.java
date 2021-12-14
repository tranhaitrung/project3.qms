package com.hust.qms.controller;

import com.hust.qms.dto.CounterDTO;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.CallNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(value = "*", maxAge = 3600)
public class CallNumberController {
    @Autowired
    private CallNumberService callNumberService;

    @PostMapping("/active-counter")
    public ResponseEntity activeCounter(@RequestBody CounterDTO counterDTO) {
        int counterId = counterDTO.getCounterId();
        String serviceCode = counterDTO.getServiceCode();
        ServiceResponse response = callNumberService.activeCounter(counterId, serviceCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/next-number")
    public ResponseEntity nextNumber(@RequestParam("counterId") Integer counterId) {
        ServiceResponse response = callNumberService.nextCustomer(counterId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/make-miss-number")
    public ResponseEntity missNumber(@RequestParam("counterId")Integer counterId) {
        ServiceResponse response = callNumberService.missCustomer(counterId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/change-counter")
    public ResponseEntity<?> changeCounter(@RequestParam Integer counterIdFrom,
                                           @RequestParam Integer counterIdTo,
                                           @RequestParam String number) {
        ServiceResponse response = callNumberService.changeCounter(number, counterIdFrom, counterIdTo);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
