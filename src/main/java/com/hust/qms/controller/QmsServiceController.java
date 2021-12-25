package com.hust.qms.controller;

import com.hust.qms.dto.ServiceDTO;
import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.QmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/service")
public class QmsServiceController {
    @Autowired
    private QmsService qmsService;

    @PostMapping("/create")
    public ResponseEntity<?> createService(@RequestBody ServiceQMS serviceQMS) {
        ServiceResponse response = qmsService.createService(serviceQMS);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/get-list")
    public ResponseEntity<?> getListService () {
        ServiceResponse response = qmsService.getListService();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateService(@RequestBody ServiceDTO serviceQMS) {
        ServiceResponse response = qmsService.updateService(serviceQMS);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PutMapping("/update-status")
    public ResponseEntity<?> setStatusService(@RequestBody ServiceQMS serviceQMS) {
        ServiceResponse response = qmsService.setStatusService(serviceQMS);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/list-both-score")
    public ResponseEntity<?> listBothScore(){
        ServiceResponse response = qmsService.listServiceBothScore();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteService(@RequestBody ServiceDTO serviceDTO){
        ServiceResponse response = qmsService.removeService(serviceDTO.getServiceId());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

}
