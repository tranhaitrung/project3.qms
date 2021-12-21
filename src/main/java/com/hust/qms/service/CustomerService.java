package com.hust.qms.service;

import com.hust.qms.entity.Customer;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CustomerRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public ServiceResponse listCustomer(String search, String status, Integer pageNo, Integer pageSize) {
        int page = pageNo > 0 ? pageNo - 1 : pageNo;

        Pageable pageable = PageRequest.of(page, pageSize);

        status = StringUtils.isBlank(status) ? null : status;
        search = StringUtils.isBlank(search) ? null : search;

        Page<Customer> customerPage = customerRepository.listCustomer(search, status, pageable);
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", customerPage);
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
