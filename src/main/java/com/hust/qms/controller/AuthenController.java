package com.hust.qms.controller;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.dto.VerifyDTO;
import com.hust.qms.entity.User;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.AuthenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin(value = "*", maxAge = 3600)
public class AuthenController {

    @Autowired
    private AuthenService authenService;

    @PostMapping("/sendCode")
    public ResponseEntity<?> sendVerifyCode(@RequestBody UserDTO userDTO) {
        ServiceResponse response = authenService.resendVerifyCode(userDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody UserDTO userDTO) {
        ServiceResponse response = authenService.resisterCustomer(userDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateActive(@RequestBody UserDTO userDTO) {
        ServiceResponse response = authenService.validateAccountActive(userDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        ServiceResponse response = authenService.login(userDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/create-member")
    public ResponseEntity<?> createMember(@RequestBody UserDTO userDTO) {
        ServiceResponse response = authenService.createMemberAccount(userDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping("/active-account")
    public ResponseEntity<?> activeAccountCustomer(@RequestBody VerifyDTO verifyDTO) {
        ServiceResponse response = authenService.activeAccount(verifyDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
