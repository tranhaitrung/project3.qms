package com.hust.qms.service;

import com.hust.qms.dto.CounterDTO;

import com.hust.qms.entity.*;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.mail.EmailService;
import com.hust.qms.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.common.Const.StatusUserService.*;
import static com.hust.qms.exception.ServiceResponse.BAD_RESPONSE;
import static com.hust.qms.exception.ServiceResponse.SUCCESS_RESPONSE;

@Service
public class CallNumberService {

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private ServiceQMSRepository serviceQMSRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse activeCounter(Integer counterId, String serviceCode) {
        Counter counter = counterRepository.findCounterById(counterId);
        if (counter == null) return BAD_RESPONSE("Mã quầy không đúng!");

        ServiceQMS serviceQMS = serviceQMSRepository.findByServiceCodeAndStatus(serviceCode, ACTIVE);
        Member member = memberRepository.findMemberByUserId(baseService.getCurrentId());
        counter.setStatus(ACTIVE);
        counter.setFirstNameMember(member.getFirstName());
        counter.setLastNameMember(member.getLastName());
        counter.setMemberId(member.getId());
        counter.setFullNameMember(member.getFullName());
        counter.setServiceId(serviceQMS.getId());
        counter.setServiceName(serviceQMS.getServiceName());
        Counter success = counterRepository.save(counter);

        return SUCCESS_RESPONSE("Kích hoạt quầy thành công", success);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse nextCustomer(Integer counterId) {
        Counter counter = counterRepository.findCounterByIdAndStatus(counterId, ACTIVE);

        if (counter == null) return BAD_RESPONSE("Quầy không tồn tại");

        String currentNumber = counter.getOrderNumber();
        //Done khách hàng hiện tại
        if (!StringUtils.isBlank(currentNumber)) {
            UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(currentNumber, ACTIVE, counterId);
            userServiceQMS.setStatus(DONE);
            userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userServiceQMSRepository.save(userServiceQMS);

            Feedback feedback = feedbackRepository.getFeedbackByTicketId(userServiceQMS.getId());
            feedback.setMemberId(counter.getMemberId());
            feedback.setMemberFullname(counter.getFullNameMember());
            feedbackRepository.save(feedback);
        }

        counter = nextNumberCounter(counter);
        List<UserServiceQMS> listCustomerMiss = getListCustomerByStringNumber(counter.getMissedCustomerIds(), MISSED, counterId);
        List<UserServiceQMS> listCustomerWaiting = getListCustomerByStringNumber(counter.getWaitingCustomerIds(), WAITING, counterId);

        CounterDTO counterDTO = new CounterDTO(counter);

        counterDTO.setWaitingCustomerList(listCustomerWaiting);
        counterDTO.setMissedCustomerList(listCustomerMiss);

        counterRepository.save(counter);

        return SUCCESS_RESPONSE("SỐ TIẾP THEO LÀ "+counter.getOrderNumber(),counterDTO);
    }

    /**
     * Đặt nhỡ số hiện tại
     * @param counterId id quầy đặt nhỡ số
     * @return
     */
    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse missCustomer(Integer counterId) {
        Counter counter = counterRepository.findCounterByIdAndStatus(counterId, ACTIVE);
        String missedNumbers = counter.getMissedCustomerIds();

        UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(counter.getOrderNumber(), ACTIVE, counterId);

        userServiceQMS.setStatus(MISSED);
        userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userServiceQMSRepository.save(userServiceQMS);

        if (StringUtils.isBlank(missedNumbers)) {
            missedNumbers = counter.getOrderNumber();
        }else {
            missedNumbers = missedNumbers +","+ counter.getOrderNumber();
        }

        counter.setMissedCustomerIds(missedNumbers);

        counter = nextNumberCounter(counter);
        counterRepository.save(counter);

        CounterDTO counterDTO = new CounterDTO(counter);
        List<UserServiceQMS> missedCustomerList = getListCustomerByStringNumber(counter.getMissedCustomerIds(), MISSED, counter.getId());
        List<UserServiceQMS> waitingCustomerList = getListCustomerByStringNumber(counter.getWaitingCustomerIds(), WAITING, counter.getId());
        counterDTO.setMissedCustomerList(missedCustomerList);
        counterDTO.setWaitingCustomerList(waitingCustomerList);

        String message = StringUtils.isBlank(counter.getOrderNumber()) ? "Đã hết khách hàng đợi!" : "Khách hàng tiếp theo là: " +counter.getOrderNumber();

        User user = userRepository.findById(userServiceQMS.getCustomerId()).orElse(null);
        emailService.sendSimpleMessage(user.getEmail(), "ĐẶT NHỠ SỐ", "Số của bạn đã được đặt vào danh sách số nhỡ do khi gọi tới bạn không có mặt");

        return SUCCESS_RESPONSE(message, counterDTO);
    }

    /**
     * Chuyển số hiện tại sang 1 quầy chỉ chịnh
     * @param number số hiện tại
     * @param counterIdTo id quầy cần chuyển tới
     * @param counterIdFrom id quầy gốc
     * @return
     */
    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPOYEE,MANAGER')")
    public ServiceResponse changeCounter(String number, Integer counterIdFrom, Integer counterIdTo) {
        UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(number, ACTIVE, counterIdFrom);

        if (userServiceQMS == null) return BAD_RESPONSE("Số quầy hoặc số gọi không hợp lệ!");
        if (userServiceQMS.getCounterId() == counterIdTo) return SUCCESS_RESPONSE("Chuyển quầy thành công!", null);

        Counter counterFrom = counterRepository.findCounterById(userServiceQMS.getCounterId());

        counterFrom = nextNumberCounter(counterFrom);

        Counter counterTo = counterRepository.findCounterById(counterIdTo);
        if (StringUtils.isBlank(counterTo.getOrderNumber())) {
            counterTo.setOrderNumber(userServiceQMS.getNumber());
            counterTo.setCustomerId(userServiceQMS.getCustomerId());
            counterTo.setFirstNameCustomer(userServiceQMS.getFirstNameCustomer());
            counterTo.setLastNameCustomer(userServiceQMS.getLastNameCustomer());
            counterTo.setFullNameCustomer(userServiceQMS.getFullNameCustomer());
            userServiceQMS.setStatus(ACTIVE);
        }else {
            if (StringUtils.isBlank(counterTo.getWaitingCustomerIds())) {
                counterTo.setWaitingCustomerIds(userServiceQMS.getNumber());
            }else {
                String listWaiting = counterTo.getWaitingCustomerIds();
                counterTo.setWaitingCustomerIds(listWaiting+","+userServiceQMS.getNumber());
            }
        }

        userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userServiceQMS.setCounterId(counterIdTo);
        userServiceQMS.setFirstNameMember(counterTo.getFirstNameMember());
        userServiceQMS.setLastNameMember(counterTo.getLastNameMember());
        userServiceQMS.setFullNameMember(counterTo.getFullNameMember());
        userServiceQMS.setMemberId(counterTo.getMemberId());
        userServiceQMS.setCounterName(counterTo.getName());

        userServiceQMSRepository.save(userServiceQMS);
        counterRepository.save(counterFrom);
        counterRepository.save(counterTo);

        Feedback feedback = feedbackRepository.getFeedbackByTicketId(userServiceQMS.getId());
        feedback.setCounterId(counterIdTo);
        feedback.setMemberFullname(counterTo.getFullNameMember());
        feedbackRepository.save(feedback);

        List<UserServiceQMS> listWaitingCustomer = getListCustomerByStringNumber(counterFrom.getWaitingCustomerIds(), WAITING, counterFrom.getId());
        List<UserServiceQMS> listMissedCustomer = getListCustomerByStringNumber(counterFrom.getMissedCustomerIds(), MISSED, counterFrom.getId());

        CounterDTO counterDTO = new CounterDTO(counterFrom);
        counterDTO.setWaitingCustomerList(listWaitingCustomer);
        counterDTO.setMissedCustomerList(listMissedCustomer);

        return SUCCESS_RESPONSE("SUCCESS", counterDTO);
    }

    /**
     * Chuyển tới số đầu tiên trong hàng đợi
     * @author Trần Hải Trung
     * @param counter: quầy chuyển số
     * @return coutner
     */
    Counter nextNumberCounter(Counter counter) {
        String listStringWaitingNumber = counter.getWaitingCustomerIds();
        if (StringUtils.isBlank(counter.getWaitingCustomerIds())) {
            counter.setCustomerId(null);
            counter.setFirstNameCustomer(null);
            counter.setLastNameCustomer(null);
            counter.setFullNameCustomer(null);
            counter.setOrderNumber(null);
        } else {
            List<String> listNumberWaiting = Arrays.asList(listStringWaitingNumber.split(","));
            String nextNumber = listNumberWaiting.get(0);
            UserServiceQMS userNext = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(nextNumber, WAITING, counter.getId());

            userNext.setStatus(ACTIVE);
            userNext.setCounterName(counter.getName());
            userNext.setCounterId(counter.getId());
            userNext.setMemberId(counter.getMemberId());
            userNext.setFullNameMember(counter.getFullNameMember());
            userNext.setLastNameMember(counter.getLastNameMember());
            userNext.setFirstNameMember(counter.getFirstNameMember());
            userNext.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userServiceQMSRepository.save(userNext);

            if (listNumberWaiting.size() == 1) counter.setWaitingCustomerIds(null);
            else counter.setWaitingCustomerIds(listStringWaitingNumber.substring(7));

            counter.setCustomerId(userNext.getCustomerId());
            counter.setFirstNameCustomer(userNext.getFirstNameCustomer());
            counter.setLastNameCustomer(userNext.getLastNameCustomer());
            counter.setFullNameCustomer(userNext.getFullNameCustomer());
            counter.setOrderNumber(nextNumber);
        }

        return counter;
    }

    List<UserServiceQMS> getListCustomerByStringNumber(String orders, String status, int counterId) {
        if (StringUtils.isBlank(orders)) return null;
        List<UserServiceQMS> listCustomer = new ArrayList<>();
        List<String> numberList = Arrays.asList(orders.split(","));
        for (int i=0; i< numberList.size(); i++) {
            UserServiceQMS user = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(numberList.get(i), status, counterId);
            listCustomer.add(user);
        }
        return listCustomer;
    }

    public ServiceResponse callBack(Integer counterId) {
        Counter counter = counterRepository.findCounterById(counterId);
        Long userId = counter.getCustomerId();
        if (userId == null) return BAD_RESPONSE("No customer is active");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return BAD_RESPONSE("No customer is activce");
        emailService.sendSimpleMessage(user.getEmail(), "GỌI LẠI SỐ", "Số của bạn đã tới lượt, Vui lòng di chuyển tới "+ counter.getName());
        return SUCCESS_RESPONSE("Success", null);
    }


}
