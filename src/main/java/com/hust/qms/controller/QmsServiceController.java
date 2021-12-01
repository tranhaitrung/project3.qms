package com.hust.qms.controller;

import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.service.QmsService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity createService(@RequestBody ServiceQMS serviceQMS) {
        return ResponseEntity.ok(qmsService.createService(serviceQMS));
    }

    @GetMapping("/get-list")
    public ResponseEntity getListService () {
        return ResponseEntity.ok(qmsService.getListService());
    }

    @PutMapping("/update")
    public ResponseEntity updateService(@RequestBody ServiceQMS serviceQMS) {
        return ResponseEntity.ok(qmsService.updateService(serviceQMS));
    }

    @PutMapping("/update-status")
    public ResponseEntity setStatusService(@RequestBody ServiceQMS serviceQMS) {
        return ResponseEntity.ok(qmsService.setStatusService(serviceQMS));
    }

}
