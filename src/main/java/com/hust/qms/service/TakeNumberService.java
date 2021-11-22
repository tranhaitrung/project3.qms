package com.hust.qms.service;

import com.hust.qms.dto.OrderNumberDTO;
import com.hust.qms.entity.Counter;
import com.hust.qms.entity.OrderNumber;
import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.entity.User;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.common.Const.StatusUserService.RESERVE;
import static com.hust.qms.common.Const.StatusUserService.WAITING;
import static com.hust.qms.exception.ServiceResponse.BAD_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

@Service
public class TakeNumberService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderNumberRepository orderNumberRepository;

    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    @Autowired
    private ServiceQMSRepository serviceQMSRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private BaseService baseService;

    public ServiceResponse takeNumber(String serviceCode) {

        ServiceQMS serviceQMS = serviceQMSRepository.findByServiceCodeAndStatus(serviceCode, ACTIVE);

        if (serviceQMS == null) {
            return BAD_RESPONSE("Dịch vụ không tồn lại, vui lòng điền mã khác!");
        }

        OrderNumber orderNumber = orderNumberRepository.getLastOrderNumber();

        long no = orderNumber == null ? 1 : orderNumber.getNumber()+1;

        OrderNumber currentOrderNumber = OrderNumber.builder()
                .number(no)
                .customerId(baseService.getCurrentId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        User user = userRepository.findByIdAndStatus(baseService.getCurrentId(), ACTIVE);

        orderNumberRepository.save(currentOrderNumber);

        List<Counter>  counterList = counterRepository.findAllByStatus(ACTIVE);

        if (counterList.size() == 0) {
            UserServiceQMS userServiceQMS = UserServiceQMS.builder()
                    .firstNameCustomer(user.getFirstName())
                    .lastNameCustomer(user.getLastName())
                    .fullNameCustomer(user.getFullName())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .serviceCode(serviceCode)
                    .serviceId(serviceQMS.getId())
                    .serviceName(serviceQMS.getServiceName())
                    .username(user.getUsername())
                    .customerId(user.getId())
                    .status(RESERVE)
                    .number(String.format("%06d", no))
                    .build();

            userServiceQMSRepository.save(userServiceQMS);
            return SUCCESS_RESPONSE("Số thứ tự của bạn là : " + String.format("%06d", no), userServiceQMS);
        }

        int index = 0; //Chỉ số counter có ít hàng đợi nhất

        int indexNullCustomer = -1;

        for (int i = 0; i < counterList.size(); i++) {
            Counter counter = counterList.get(i);
            if (StringUtils.isBlank(counter.getWaitingCustomerIds())) {
                index = i;
                break;
            } else {
                if (counter.getWaitingCustomerIds().length() < counterList.get(index).getWaitingCustomerIds().length()) {
                    index = i;
                }
            }

        }

        for (int i = 0; i < counterList.size(); i++) {
            if (StringUtils.isBlank(counterList.get(i).getOrderNumber())) {
                indexNullCustomer = i;
                break;
            }
        }

        if (indexNullCustomer >= 0) index = indexNullCustomer;

        String oderNumberStr = String.format("%06d", no);
        String waitingIds = counterList.get(index).getWaitingCustomerIds();
        Counter counter = counterList.get(index);

        waitingIds = waitingIds == null ? oderNumberStr : waitingIds +","+ oderNumberStr;

        UserServiceQMS userServiceQMS = UserServiceQMS.builder()
                .counterId(counter.getId())
                .counterName(counter.getName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .customerId(user.getId())
                .firstNameCustomer(user.getFirstName())
                .lastNameCustomer(user.getLastName())
                .fullNameCustomer(user.getFullName())
                .firstNameMember(counter.getFirstNameMember())
                .lastNameMember(counter.getLastNameMember())
                .fullNameMember(counter.getFullNameMember())
                .memberId(counter.getMemberId())
                .number(oderNumberStr)
                .serviceCode(serviceCode)
                .serviceId(serviceQMS.getId())
                .serviceName(serviceQMS.getServiceName())
                .username(user.getUsername())
                .build();

        if (StringUtils.isBlank(counter.getOrderNumber())) {
            counter.setOrderNumber(oderNumberStr);
            counter.setCustomerId(baseService.getCurrentId());
            counter.setFirstNameCustomer(user.getFirstName());
            counter.setLastNameCustomer(user.getLastName());
            counter.setFullNameCustomer(user.getFullName());
            counter.setServiceId(serviceQMS.getId());
            counter.setServiceName(serviceQMS.getServiceName());
            userServiceQMS.setStatus(ACTIVE);
        }
        else {
            counter.setWaitingCustomerIds(waitingIds);
            userServiceQMS.setStatus(WAITING);
        }
        counterRepository.save(counter);
        userServiceQMSRepository.save(userServiceQMS);

        OrderNumberDTO orderNumberDTO = OrderNumberDTO.builder()
                .orderNumber(oderNumberStr)
                .counterId(counter.getId())
                .counterName(counter.getName())
                .fullNameCustomer(user.getFullName())
                .fullNameMember(counter.getFullNameMember())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return SUCCESS_RESPONSE("Bạn đã lấy số thành công!", orderNumberDTO);
    }
}
