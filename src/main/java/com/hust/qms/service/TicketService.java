package com.hust.qms.service;

import com.hust.qms.dto.TicketDTO;
import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.ServiceQMSRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TicketService {
    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    @Autowired
    private ServiceQMSRepository serviceQMSRepository;

    public ServiceResponse serviceStatisticAroundSevenDay() {
        List<Map<String, Object>> mapList = userServiceQMSRepository.ticketStatisticAroundSevenDate();
        int mapSize = mapList.size();
        Map<String, Object> map = new HashMap<>();
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

    public ServiceResponse eachTicketStatisticAroundSevenDay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<ServiceQMS> list = serviceQMSRepository.findAll();
        List<Map<String,Object>> mapTicket = new ArrayList<>();
        for (ServiceQMS s : list) {
            List<Map<String, Object>> mapList = userServiceQMSRepository.eachTicketStatisticAroundSevenDate(s.getServiceCode());
            Map<String, Object> service = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                String date = dateFormat.format(new Date(System.currentTimeMillis() - i*1000*60*60*24));
                String keyDate = "date"+(i+1);
                String keyTotal = "total"+(i+1);
                service.put(keyDate, date);
                for (Map<String, Object> m : mapList) {
                    if (m.get("createdAt").toString().equals(date)) {
                        service.put(keyTotal, m.get("total"));
                    }
                }
                if (!service.containsKey(keyTotal)) service.put(keyTotal, 0);
            }
            service.put("serviceCode", s.getServiceCode());
            service.put("serviceName", s.getServiceName());
            mapTicket.add(service);
        }

        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", mapTicket);
    }

    public ServiceResponse getListTicket(String search, String serviceCode, String status, Date fromDate, Date toDate, int pageNo, int pageSize) {
        pageNo = pageNo > 0 ? pageNo - 1 : pageNo;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        search = StringUtils.isBlank(search) ? null : search;
        status = StringUtils.isBlank(status) ? null : status;
        serviceCode = StringUtils.isBlank(serviceCode) ? null : serviceCode;
        String fromDateStr = null;
        String toDateStr = null;
        if (fromDate != null) {
            fromDateStr = dateFormat.format(fromDate)+ " 00:00:00";
        }
        if (toDate != null) {
            toDateStr = dateFormat.format(toDate) + " 23:59:59";
        }
        Page<UserServiceQMS> page = userServiceQMSRepository.listTicket(search, serviceCode, status, fromDateStr, toDateStr, pageable);
        List<UserServiceQMS> list = page.getContent();
        List<TicketDTO> listDTO = new ArrayList<>();
        for (UserServiceQMS u: list) {
            TicketDTO ticketDTO = TicketDTO.builder()
                    .id(u.getId())
                    .counterId(u.getCounterId())
                    .counterName(u.getCounterName())
                    .serviceId(u.getServiceId())
                    .serviceCode(u.getServiceCode())
                    .serviceName(u.getServiceName())
                    .customerId(u.getCustomerId())
                    .firstNameCustomer(u.getFirstNameCustomer())
                    .lastNameCustomer(u.getLastNameCustomer())
                    .fullNameCustomer(u.getFullNameCustomer())
                    .memberId(u.getMemberId())
                    .firstNameMember(u.getFirstNameMember())
                    .lastNameMember(u.getLastNameMember())
                    .fullNameMember(u.getFullNameMember())
                    .username(u.getUsername())
                    .number(u.getNumber())
                    .createdAt(u.getCreatedAt())
                    .updatedAt(u.getUpdatedAt())
                    .createdDisplay(dateFormat.format(u.getCreatedAt()))
                    .status(u.getStatus())
                    .build();
            listDTO.add(ticketDTO);
        }
        Page<TicketDTO> dtoPage = new PageImpl<>(listDTO,pageable, page.getTotalElements());
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", dtoPage);
    }

}
