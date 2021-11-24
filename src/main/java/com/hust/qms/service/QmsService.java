package com.hust.qms.service;

import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.ServiceQMSRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

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

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,MANAGER')")
    public ServiceResponse createService(ServiceQMS serviceQMS) {
        Long userId = baseService.getCurrentId();

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
    public ServiceResponse updateService (ServiceQMS input) {
        ServiceQMS serviceQMS = serviceQMSRepository.findServiceQMSById(input.getId());

        serviceQMS.setServiceName(input.getServiceName());
        serviceQMS.setServiceCode(input.getServiceCode());
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
}
