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
        User user = User.builder().username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
        return ResponseEntity.ok(authenService.sendCode(userDTO));
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody UserDTO userDTO) {

        return ResponseEntity.ok(authenService.resisterCustomer(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authenService.login(userDTO));
    }

    @PostMapping("/create-member")
    public ResponseEntity<?> createMember(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authenService.createMemberAccount(userDTO));
    }

    @PostMapping("/active-account")
    public ResponseEntity<?> activeAccountCustomer(@RequestBody VerifyDTO verifyDTO) {
        ServiceResponse response = authenService.activeAccount(verifyDTO);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
