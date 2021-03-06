package com.hust.qms.service;

import com.hust.qms.dto.UserDTO;
import com.hust.qms.entity.Customer;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CustomerRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public ServiceResponse listCustomer(String search, String status,Date fromDate, Date toDate, Integer pageNo, Integer pageSize) {
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

        Page<Customer> customerPage = customerRepository.listCustomer(search, status, fromDateStr, toDateStr, pageable);
        List<Customer> list = customerPage.getContent();
        List<UserDTO> dtoList = new ArrayList<>();
        for (Customer c : list) {
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

        Page pageDTO = new PageImpl(dtoList, pageable, customerPage.getTotalElements());
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", pageDTO);
    }

    public ServiceResponse customerStatisticRoundSevenDay() {
        List<Map<String, Object>> mapList = customerRepository.customerStatistic();
        int mapSize = mapList.size();
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i < 8; i++) {
            String keyDate = "date"+i;
            String keyTotal = "total"+i;
            if (mapSize < i) {
                map.put(keyDate, 0);
                map.put(keyTotal, 0);
                continue;
            } else {
                map.put(keyTotal, mapList.get(i-1).get("total"));
                map.put(keyDate, mapList.get(i-1).get("createdAt"));
            }

        }
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", map);
    }
}
