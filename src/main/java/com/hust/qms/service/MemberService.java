package com.hust.qms.service;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.Customer;
import com.hust.qms.entity.Member;
import com.hust.qms.entity.PermissionUserRole;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.MemberRepository;
import com.hust.qms.repository.PermissionUserRoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PermissionUserRoleRepository permissionUserRoleRepository;

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
}
