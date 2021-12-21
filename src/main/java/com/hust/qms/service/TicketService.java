package com.hust.qms.service;

import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.ServiceQMSRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
