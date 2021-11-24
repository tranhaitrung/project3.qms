package com.hust.qms.service;

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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.hust.qms.common.Const.Role.*;
import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.exception.ServiceResponse.FORBIDDEN_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

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

    public ServiceResponse updateInfoUser(UserDTO userDTO) {
        Long userId = baseService.getCurrentId();

        User user = userRepository.findByIdAndStatus(userId, ACTIVE);

        List<String> roles = baseService.getCurrentRoles();

        user.setDisplayName(userDTO.getDisplayName());
//        user.setCountryCode(userDTO.getCountryCode());
//        user.setCountry(userDTO.getCountry());
        user.setCity(userDTO.getCity());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setDistrict(userDTO.getDistrict());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setFullName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedBy(userId);
        user.setPhone(userDTO.getPhone());

        if (roles.contains(CUSTOMER)) {
            Customer customer = customerRepository.findCustomerByUsername(user.getUsername());
            customer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            customer.setCity(userDTO.getCity());
            customer.setDistrict(userDTO.getDistrict());
            customer.setFirstName(userDTO.getFirstName());
            customer.setLastName(userDTO.getLastName());
            customer.setFullName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));
            customer.setUpdatedBy(userId);
            customer.setDisplayName(userDTO.getDisplayName());
            customer.setEmail(userDTO.getEmail());
            customer.setPhone(userDTO.getPhone());

            customerRepository.save(customer);
        }

        if (roles.contains(MANAGER) || roles.contains(EMPLOYEE)) {
            Member member = memberRepository.findMemberByUserId(user.getId());
            member.setCity(userDTO.getCity());
            member.setDistrict(userDTO.getDistrict());
            member.setFirstName(userDTO.getFirstName());
            member.setLastName(userDTO.getLastName());
            member.setFullName(String.format("%s %s", userDTO.getFirstName(), userDTO.getLastName()));
            member.setUpdatedBy(userId);
            member.setDisplayName(userDTO.getDisplayName());
            member.setEmail(userDTO.getEmail());
            member.setPhone(userDTO.getPhone());
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

        user.setStatus(input.getStatus());
        user.setUpdatedBy(baseService.getCurrentId());
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        return null;

    }



}
