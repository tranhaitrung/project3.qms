package com.hust.qms.service;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.*;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.MemberRepository;
import com.hust.qms.repository.PermissionRoleRepository;
import com.hust.qms.repository.PermissionUserRoleRepository;
import com.hust.qms.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.hust.qms.common.Const.Role.ADMIN;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PermissionUserRoleRepository permissionUserRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRoleRepository permissionRoleRepository;

    @Autowired
    private BaseService baseService;

    public ServiceResponse listMember(String search, String status, Date fromDate, Date toDate, Integer pageNo, Integer pageSize) {
        int page = pageNo > 0 ? pageNo - 1 : pageNo;

        Pageable pageable = PageRequest.of(page, pageSize);

        status = StringUtils.isBlank(status) ? null : status;
        search = StringUtils.isBlank(search) ? null : search;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateStr = null;
        String toDateStr = null;
        if (fromDate != null) {
            fromDateStr = dateFormat.format(fromDate)+ " 00:00:00";
        }
        if (toDate != null) {
            toDateStr = dateFormat.format(toDate) + " 23:59:59";
        }

        Page<Member> memberPage = memberRepository.listMember(search, status, fromDateStr, toDateStr, pageable);
        List<Member> list = memberPage.getContent();
        List<UserDTO> dtoList = new ArrayList<>();
        for (Member c : list) {
            PermissionUserRole permissionUserRole = permissionUserRoleRepository.findAllByUserId(c.getUserId()).get(0);
            UserDTO userDTO = UserDTO.builder()
                    .userId(c.getUserId())
                    .displayName(c.getDisplayName())
                    .address(c.getAddress())
                    .birthday(c.getBirthday())
                    .avatar(c.getAvatar())
                    .phone(c.getPhone())
                    .fullName(c.getFullName())
                    .lastName(c.getLastName())
                    .firstName(c.getFirstName())
                    .username(c.getUsername())
                    .email(c.getEmail())
                    .birthdayDisplay( c.getBirthday() == null ? null : dateFormat.format(c.getBirthday()))
                    .birthday(c.getBirthday())
                    .city(c.getCity())
                    .country(c.getCountry())
                    .countryCode(c.getCountryCode())
                    .district(c.getDistrict())
                    .roleCode(permissionUserRole.getRoleCode())
                    .status(c.getStatus())
                    .createdAt(c.getCreatedAt())
                    .createdAtLong(c.getCreatedAt().getTime())
                    .createdAtStr(dateFormat.format(c.getCreatedAt()))
                    .updatedAt(c.getUpdatedAt())
                    .updatedBy(c.getUpdatedBy())
                    .updatedAtLong(c.getUpdatedAt() == null ? null : c.getUpdatedAt().getTime())
                    .updatedAtStr(c.getUpdatedAt() == null ? null : dateFormat.format(c.getUpdatedAt()))
                    .build();
            dtoList.add(userDTO);
        }

        Page pageDTO = new PageImpl(dtoList, pageable, memberPage.getTotalElements());
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", pageDTO);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse updateInfoMember(UserDTO userDTO) {
        User user = null;
        if (userDTO.getUserId() != null) {
            user = userRepository.findById(userDTO.getUserId()).orElse(null);
        }else if (!StringUtils.isBlank(userDTO.getUsername())) {
            user = userRepository.findByUsername(userDTO.getUsername()).orElse(null);
        }

        if (user == null) {
            return ServiceResponse.NOT_FOUND_RESPONSE("Not found user", null);
        }
        if (StringUtils.isNotBlank(userDTO.getFirstName())) user.setFirstName(userDTO.getFirstName());
        if (StringUtils.isNotBlank(userDTO.getLastName())) user.setLastName(userDTO.getLastName());
        user.setFullName(String.format("%s %s", user.getFirstName(), user.getLastName()));
        if (userDTO.getBirthday() != null) user.setBirthday(userDTO.getBirthday());
        if (StringUtils.isNotBlank(userDTO.getCountry())) user.setCountry(userDTO.getCountry());
        if (StringUtils.isNotBlank(userDTO.getPhone())) user.setPhone(userDTO.getPhone());
        if (StringUtils.isNotBlank(userDTO.getEmail())) user.setEmail(userDTO.getEmail());
        if (StringUtils.isNotBlank(userDTO.getStatus())) user.setStatus(userDTO.getStatus());
        if (StringUtils.isNotBlank(userDTO.getRoleCode())) {

            if (ADMIN.equals(userDTO.getRoleCode())) {
                if (!ADMIN.equals(baseService.getCurrentRoles().get(0)))
                    return ServiceResponse.FORBIDDEN_RESPONSE("you don't have permission to update this permission. Only ADMIN can update this permission!");
            }

            PermissionUserRole permissionUserRole = permissionUserRoleRepository.findAllByUserId(user.getId()).get(0);
            PermissionRole permissionRole = permissionRoleRepository.findByCode(userDTO.getRoleCode());
            permissionUserRole.setRoleId(permissionRole.getId());
            permissionUserRole.setRoleCode(permissionRole.getCode());
            permissionUserRole.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            permissionUserRole.setUpdatedBy(baseService.getCurrentId());
            permissionUserRoleRepository.save(permissionUserRole);
        }
        userRepository.save(user);

        Member member = memberRepository.findMemberByUserId(user.getId());
        member.setFirstName(user.getFirstName());
        member.setLastName(user.getLastName());
        member.setBirthday(user.getBirthday());
        member.setCountry(user.getCountry());
        member.setPhone(user.getPhone());
        member.setFullName(user.getFullName());
        member.setEmail(user.getEmail());
        member.setStatus(user.getStatus());
        member.setUpdatedBy(user.getUpdatedBy());
        member.setUpdatedAt(user.getUpdatedAt());
        memberRepository.save(member);

        return ServiceResponse.SUCCESS_RESPONSE("Update infomation member successful", userDTO);

    }
}
