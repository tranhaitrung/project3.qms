package com.hust.qms.service;

import com.hust.qms.config.JwtUtils;
import com.hust.qms.dto.ChangePassDTO;
import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.Customer;
import com.hust.qms.entity.Member;
import com.hust.qms.entity.PermissionUserRole;
import com.hust.qms.entity.User;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CustomerRepository;
import com.hust.qms.repository.MemberRepository;
import com.hust.qms.repository.PermissionUserRoleRepository;
import com.hust.qms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.hust.qms.common.Const.Role.*;
import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.exception.ServiceResponse.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private CheckRolesService checkRolesService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PermissionUserRoleRepository permissionUserRoleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    public ServiceResponse updateInfoUser(UserDTO userDTO) {
        Long userId = baseService.getCurrentId();

        User user = userRepository.findByIdAndStatus(userId, ACTIVE);

        List<String> roles = baseService.getCurrentRoles();

//        user.setDisplayName(userDTO.getDisplayName());
//        user.setCountryCode(userDTO.getCountryCode());
//        user.setCountry(userDTO.getCountry());
//        user.setCity(userDTO.getCity());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
//        user.setDistrict(userDTO.getDistrict());
//        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setFullName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedBy(userId);
        user.setPhone(userDTO.getPhone());
        userDTO.setAvatar(user.getAvatar());
        user.setDisplayName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));

        if (roles.contains(CUSTOMER)) {
            Customer customer = customerRepository.findCustomerByUsername(user.getUsername());
            customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            customer.setCity(user.getCity());
            customer.setDistrict(user.getDistrict());
            customer.setFirstName(user.getFirstName());
            customer.setLastName(user.getLastName());
            customer.setBirthday(user.getBirthday());
            customer.setFullName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));
            customer.setUpdatedBy(userId);
            customer.setDisplayName(user.getFullName());
            customer.setEmail(user.getEmail());
            customer.setPhone(user.getPhone());

            customerRepository.save(customer);
        }

        if (roles.contains(MANAGER) || roles.contains(EMPLOYEE)) {
            Member member = memberRepository.findMemberByUserId(user.getId());
            member.setCity(user.getCity());
            member.setDistrict(user.getDistrict());
            member.setFirstName(user.getFirstName());
            member.setLastName(user.getLastName());
            member.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
            member.setUpdatedBy(userId);
            member.setDisplayName(user.getFullName());
            member.setEmail(user.getEmail());
            member.setPhone(user.getPhone());
            member.setBirthday(user.getBirthday());
            member.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            memberRepository.save(member);
        }

        userRepository.save(user);

        userDTO.setFullName(user.getFullName());

        return SUCCESS_RESPONSE("Cập nhật thông tin thành công!", userDTO);
    }


    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse setStatusUser(UserDTO input) {
        User user = userRepository.getUserByUsername(input.getUsername());
        if (user == null) {
            return NOT_FOUND_RESPONSE("Not found user", null);
        }
        List<PermissionUserRole> permissionUserRoles = permissionUserRoleRepository.findAllByUserId(user.getId());
        if (permissionUserRoles.get(0).getRoleCode().equals(CUSTOMER)) {
            Customer customer = customerRepository.findCustomerByUsername(user.getUsername());
            customer.setStatus(input.getStatus());
            customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            customer.setUpdatedBy(baseService.getCurrentId());
            customerRepository.save(customer);
        }
        else {
            Member member = memberRepository.findMemberByUserId(user.getId());
            member.setStatus(input.getStatus());
            member.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            member.setUpdatedBy(baseService.getCurrentId());
            memberRepository.save(member);
        }
        user.setStatus(input.getStatus());
        user.setUpdatedBy(baseService.getCurrentId());
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return SUCCESS_RESPONSE("Update status successful", null);
    }

    public ServiceResponse changePass(ChangePassDTO input) {
        Long userId = baseService.getCurrentId();
        User user = userRepository.getById(userId);

        boolean isCheck = encoder.matches(input.getPassword(), user.getPassword());
        if (!isCheck) return BAD_RESPONSE("Change pass fail");

        user.setPassword(encoder.encode(input.getNewPass()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return SUCCESS_RESPONSE("SUCCESS", null);
    }

    public ServiceResponse getUserInfoByToken(Long userId) {
        Long id = userId == null ? baseService.getCurrentId() : userId;
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return NOT_FOUND_RESPONSE("Not found user by id = "+id,null);
        }
        UserDTO userDTO = UserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .country(user.getCountry())
                .avatar(user.getAvatar())
                .birthday(user.getBirthday())
                .address(user.getAddress())
                .displayName(user.getDisplayName())
                .status(user.getStatus())
                .build();
        return SUCCESS_RESPONSE("SUCCESS", userDTO);
    }

}
