package com.hust.qms.controller;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.Customer;
import com.hust.qms.entity.Member;
import com.hust.qms.entity.User;
import com.hust.qms.repository.CustomerRepository;
import com.hust.qms.repository.MemberRepository;
import com.hust.qms.repository.UserRepository;
import com.hust.qms.service.BaseService;
import com.hust.qms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.TabularType;
import java.sql.Timestamp;
import java.util.List;

import static com.hust.qms.common.Const.Role.*;
import static com.hust.qms.common.Const.Status.ACTIVE;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/update-info")
    public ResponseEntity updateInfo(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateInfoUser(userDTO));
    }

    @PostMapping("/update-avatar")
    public ResponseEntity updateAvatar() {
        return null;
    }

    @PostMapping("/block-user")
    public ResponseEntity blockUser(UserDTO userDTO) {
        return ResponseEntity.ok(userService.setStatusUser(userDTO));
    }
}
