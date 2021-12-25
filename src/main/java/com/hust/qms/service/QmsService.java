package com.hust.qms.service;

import com.hust.qms.dto.ServiceDTO;
import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.FeedbackRepository;
import com.hust.qms.repository.ServiceQMSRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hust.qms.exception.ServiceResponse.BAD_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

@Service
public class QmsService {
    @Autowired
    private ServiceQMSRepository serviceQMSRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse createService(ServiceQMS serviceQMS) {
        Long userId = baseService.getCurrentId();
        serviceQMS.setServiceCode(serviceQMS.getServiceCode().toUpperCase());

        ServiceQMS service = serviceQMSRepository.findServiceQMSByServiceCode(serviceQMS.getServiceCode()).orElse(null);

        if (service != null) {
            return BAD_RESPONSE("Service code already in use, please enter another code");
        }

        if (StringUtils.isBlank(serviceQMS.getImage())) service.setImage("https://res.cloudinary.com/litchitech/image/upload/v1640368460/PROJECT3/d8vtpdh8d67pdjj6np71.png");

        serviceQMS.setManualAdd(0);
        serviceQMS.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        serviceQMS.setCreatedBy(userId);

        ServiceQMS success = serviceQMSRepository.save(serviceQMS);

        return SUCCESS_RESPONSE("Thêm dịch vụ thành công!", success);
    }


    public ServiceResponse getListService() {
        List<ServiceQMS> list = serviceQMSRepository.findAll();
        return SUCCESS_RESPONSE("", list);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse updateService (ServiceDTO input) {
        ServiceQMS serviceQMS = serviceQMSRepository.findServiceQMSById(input.getServiceId());

        serviceQMS.setServiceName(input.getServiceName());
        serviceQMS.setServiceCode(input.getServiceCode());
        serviceQMS.setImage(input.getImage());
        serviceQMS.setPrice(input.getPrice());
        serviceQMS.setStatus(input.getStatus());
        serviceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        serviceQMS.setUpdatedBy(baseService.getCurrentId());


        userServiceQMSRepository.updateServiceQms(serviceQMS.getServiceName(), serviceQMS.getServiceCode(), serviceQMS.getId());

        counterRepository.updateServiceName(serviceQMS.getServiceName(), serviceQMS.getId());

        serviceQMSRepository.save(serviceQMS);

        return SUCCESS_RESPONSE("Thay đổi thông tin dịch vụ thành công!", serviceQMS);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse removeService(Long serviceId) {
        userServiceQMSRepository.deleteById(serviceId);
        return SUCCESS_RESPONSE("Xóa dịch vụ thành công", null);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse setStatusService(ServiceQMS input) {
        int count = serviceQMSRepository.updateStatusService(input.getStatus(), baseService.getCurrentId(), input.getId());
        return SUCCESS_RESPONSE("Num of change: "+count, serviceQMSRepository.findServiceQMSById(input.getId()));
    }

    public ServiceResponse listServiceBothScore() {
        List<ServiceQMS> serviceQMSList = serviceQMSRepository.findAll();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<ServiceDTO> dto = new ArrayList<>();
        for (ServiceQMS s: serviceQMSList) {
            List<Map<String, Object>> listScore = feedbackRepository.statisticScoreService(s.getId());
            int totalScore = 0;
            int totalFeedback = 0;
            Map<String, Integer> map = new HashMap<>();
            ServiceDTO serviceDTO = ServiceDTO.builder()
                    .serviceId(s.getId())
                    .serviceCode(s.getServiceCode())
                    .serviceName(s.getServiceName())
                    .status(s.getStatus())
                    .price(s.getPrice())
                    .createdAt(s.getCreatedAt() == null ? null :simpleDateFormat.format(s.getCreatedAt()))
                    .updatedAt(s.getUpdatedAt() == null ? null : simpleDateFormat.format(s.getUpdatedAt()))
                    .image(s.getImage())
                    .manualAdd(s.getManualAdd())
                    .createdBy(s.getCreatedBy())
                    .updatedBy(s.getUpdatedBy())
                    .build();
            for (int i = 1; i <=5 ; i++) {
                int check = 0;
                for (Map<String, Object> m : listScore) {
                    if (Integer.parseInt(m.get("score").toString()) == i) {
                        check = 1;
                        Integer total = Integer.parseInt(m.get("totalScore").toString());
                        map.put("score"+i,total);
                        totalScore = totalScore + total *i;
                        totalFeedback = totalFeedback + total;
                    }
                }
                if (check == 0) map.put("score"+i, 0);
            }
            float avg = (float)totalScore/totalFeedback;
            serviceDTO.setScore(map);
            serviceDTO.setScoreAverage((float)Math.round(avg*10)/10);
            dto.add(serviceDTO);
        }

        return SUCCESS_RESPONSE("SUCCESS", dto);
    }
}
