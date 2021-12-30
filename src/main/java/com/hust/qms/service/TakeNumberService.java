package com.hust.qms.service;

import com.hust.qms.dto.OrderNumberDTO;
import com.hust.qms.entity.*;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.common.Const.StatusUserService.*;
import static com.hust.qms.exception.ServiceResponse.*;

@Service
public class TakeNumberService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

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

        Long userId = baseService.getCurrentId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        List<UserServiceQMS> userServiceQMSList = userServiceQMSRepository.getUserServiceQMSByCustomerIdAndNotStatus(userId, today, DONE);

//        if (userServiceQMSList.size() > 0 ) {
//            return BAD_RESPONSE("Số của bạn đang được xử lý!");
//        }
        OrderNumber orderNumber = orderNumberRepository.getLastOrderNumber();

        long no = orderNumber == null ? 1 : orderNumber.getNumber()+1;

        OrderNumber currentOrderNumber = OrderNumber.builder()
                .number(no)
                .customerId(baseService.getCurrentId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        User user = userRepository.findByIdAndStatus(baseService.getCurrentId(), ACTIVE);

        orderNumberRepository.save(currentOrderNumber);

        List<Counter>  counterList = counterRepository.findAllByStatusAndAndServiceId(ACTIVE, serviceQMS.getId());

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

            UserServiceQMS u = userServiceQMSRepository.save(userServiceQMS);
            Feedback feedback = Feedback.builder()
                    .serviceName(userServiceQMS.getServiceName())
                    .serviceId(userServiceQMS.getServiceId())
                    .ticketId(u.getId())
                    .customerFullname(user.getFullName())
                    .customerId(userId)
                    .memberId(u.getMemberId())
                    .memberFullname(u.getFullNameMember())
                    .counterId(u.getCounterId())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            feedbackRepository.save(feedback);
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
        UserServiceQMS response = userServiceQMSRepository.save(userServiceQMS);

        Feedback feedback = Feedback.builder()
                .serviceName(userServiceQMS.getServiceName())
                .serviceId(userServiceQMS.getServiceId())
                .ticketId(response.getId())
                .customerFullname(user.getFullName())
                .customerId(userId)
                .memberId(response.getMemberId())
                .memberFullname(response.getFullNameMember())
                .counterId(response.getCounterId())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        feedbackRepository.save(feedback);

        OrderNumberDTO orderNumberDTO = OrderNumberDTO.builder()
                .serviceCode(userServiceQMS.getServiceCode())
                .serviceName(userServiceQMS.getServiceName())
                .orderNumber(oderNumberStr)
                .counterId(counter.getId())
                .counterName(counter.getName())
                .fullNameCustomer(user.getFullName())
                .fullNameMember(counter.getFullNameMember())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return SUCCESS_RESPONSE("Số thứ tự của bạn là : " + String.format("%06d", no), orderNumberDTO);
    }

    public ServiceResponse myNumber () {
        Long userId = baseService.getCurrentId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        List<UserServiceQMS> userServiceQMS = userServiceQMSRepository.getUserServiceQMSByCustomerId(userId, today);
        if (userServiceQMS.size() > 0) return SUCCESS_RESPONSE("SUCCESS",userServiceQMS.get(0));
        return SUCCESS_RESPONSE("SUCCESS", null);
    }

    public ServiceResponse listUserNumber(String search, Long userId, String serviceCode, Date fromDate, Date toDate, String status, Integer pageNo, Integer pageSize) {
        Long id = userId != null ? userId : baseService.getCurrentId();
        pageNo = pageNo > 0 ? pageNo - 1 : pageNo;
        int page = pageNo*pageSize;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        if (StringUtils.isBlank(serviceCode)) serviceCode = null;
//        if (StringUtils.isBlank(status)) status = null;
//        Page<UserServiceQMS> serviceQMSPage = userServiceQMSRepository.findUserServiceQMSByCustomerIdAndSearch(id, serviceCode, fromDate, toDate, status, pageable);

        List<Map<String,Object>> listTicket = userServiceQMSRepository.searchListOrderNumberCustomer("LIST", search, id, serviceCode, fromDate, toDate, status, page, pageSize);
        int totalTicket = userServiceQMSRepository.countListOrderNumberCustomer("COUNT", search, id, serviceCode, fromDate, toDate, status, page, pageSize);

        Page serviceQMSPage = new PageImpl(listTicket, pageable, totalTicket);

        return SUCCESS_RESPONSE("SUCCESS", serviceQMSPage);
    }
}
