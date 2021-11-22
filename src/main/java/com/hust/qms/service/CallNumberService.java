package com.hust.qms.service;

import com.hust.qms.dto.CounterDTO;

import com.hust.qms.entity.Counter;
import com.hust.qms.entity.Member;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
    private CustomerRepository customerRepository;

    @Autowired
    private OrderNumberRepository orderNumberRepository;

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse activeCounter(CounterDTO counterDTO) {
        Counter counter = counterRepository.findCounterById(counterDTO.getCounterId());
        if (counter == null) return BAD_RESPONSE("Mã quầy không đúng!");

        Member member = memberRepository.findMemberByUserId(baseService.getCurrentId());
        counter.setStatus(ACTIVE);
        counter.setFirstNameMember(member.getFirstName());
        counter.setLastNameMember(member.getLastName());
        counter.setMemberId(member.getId());
        counter.setFullNameMember(member.getFullName());
        Counter success = counterRepository.save(counter);

        return SUCCESS_RESPONSE("Kích hoạt quầy thành công", success);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse nextCustomer(Integer counterId) {
        Counter counter = counterRepository.findCounterByIdAndStatus(counterId, ACTIVE);

        if (counter == null) return BAD_RESPONSE("Quầy không tồn tại");

        String waitingCustomerIds = counter.getWaitingCustomerIds(); //Danh sách số đợi của khách hàng

        CounterDTO counterDTO = new CounterDTO();

        String missedCustomerIds = counter.getMissedCustomerIds(); //Danh sách số nhỡ

        List<UserServiceQMS> listCustomerMiss = new ArrayList<>();
        List<UserServiceQMS> listCustomerWaiting = new ArrayList<>();

        if (!StringUtils.isBlank(missedCustomerIds)) {
            List<String> missIds = Arrays.asList(missedCustomerIds.split(","));

            for (int i = 0; i < missIds.size(); i++) {
                UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(missIds.get(i), MISSED, counterId);
                //Customer customer = customerRepository.findCustomerByUsername(userServiceQMS.getUsername());
                listCustomerMiss.add(userServiceQMS);
            }

            counterDTO.setWaitingCustomerList(listCustomerMiss);
        }

        String currentNumer = counter.getOrderNumber();
        //Done khách hàng hiện tại
        if (!StringUtils.isBlank(currentNumer)) {
            UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(currentNumer, ACTIVE, counterId);
            userServiceQMS.setStatus(DONE);
            userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userServiceQMSRepository.save(userServiceQMS);
        }


        //Nếu hết khách hàng trong hàng đợi
        if (StringUtils.isBlank(waitingCustomerIds)) {
            counter.setCustomerId(null);
            counter.setFirstNameCustomer(null);
            counter.setLastNameCustomer(null);
            counter.setFullNameCustomer(null);
            counter.setServiceId(null);
            counter.setServiceName(null);
            counter.setOrderNumber(null);

            counterRepository.save(counter);
            return SUCCESS_RESPONSE("Đã hết khách hàng đợi", counter);
        } else {
            List<String> listNumberWaiting = Arrays.asList(waitingCustomerIds.split(","));
            String nextNumber = listNumberWaiting.get(0);
            UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(nextNumber, WAITING, counterId);

            userServiceQMS.setStatus(ACTIVE);
            userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userServiceQMSRepository.save(userServiceQMS);

            if (listNumberWaiting.size() == 1) counter.setWaitingCustomerIds(null);
            else counter.setWaitingCustomerIds(waitingCustomerIds.substring(7));

            counter.setCustomerId(userServiceQMS.getCustomerId());
            counter.setFirstNameCustomer(userServiceQMS.getFirstNameCustomer());
            counter.setLastNameCustomer(userServiceQMS.getLastNameCustomer());
            counter.setFullNameCustomer(userServiceQMS.getFullNameCustomer());
            counter.setServiceId(userServiceQMS.getServiceId());
            counter.setServiceName(userServiceQMS.getServiceName());
            counter.setOrderNumber(nextNumber);

            for (int i=1; i< listNumberWaiting.size(); i++) {
                UserServiceQMS userWaiting = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(listNumberWaiting.get(i), WAITING, counterId);
                //Customer customer = customerRepository.findCustomerByUsername(userServiceQMS.getUsername());
                listCustomerWaiting.add(userWaiting);
            }
        }
        if (waitingCustomerIds.length() > 6) counterDTO.setWaitingCustomerIds(waitingCustomerIds.substring(7));

        counterDTO.setFullNameMember(counter.getFullNameMember());
        counterDTO.setFirstNameMember(counter.getFirstNameMember());
        counterDTO.setLastNameMember(counter.getLastNameMember());
        counterDTO.setMemberId(counter.getMemberId());

        counterDTO.setLastNameCustomer(counter.getLastNameCustomer());
        counterDTO.setFirstNameCustomer(counter.getFirstNameCustomer());
        counterDTO.setFullNameCustomer(counter.getFullNameCustomer());

        counterDTO.setWaitingCustomerList(listCustomerWaiting);

        counterDTO.setCustomerId(counter.getCustomerId());

        counterDTO.setServiceId(counter.getServiceId());
        counterDTO.setServiceName(counter.getServiceName());

        counterDTO.setCounterId(counterId);
        counterDTO.setCounterName(counter.getName());
        counterDTO.setOrderNumber(counter.getOrderNumber());
        counterDTO.setStatus(counter.getStatus());





        counterRepository.save(counter);

        return SUCCESS_RESPONSE("SỐ TIẾP THEO LÀ "+counter.getOrderNumber(),counterDTO);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPLOYEE,MANAGER')")
    public ServiceResponse missCustomer(Integer counterId) {
        Counter counter = counterRepository.findCounterByIdAndStatus(counterId, ACTIVE);
        String missedNumbers = counter.getMissedCustomerIds();
        String waitingNumbers = counter.getWaitingCustomerIds();

        UserServiceQMS userServiceQMS = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(counter.getOrderNumber(), ACTIVE, counterId);

        userServiceQMS.setStatus(MISSED);
        userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userServiceQMSRepository.save(userServiceQMS);

        List<UserServiceQMS> missedCustomerList = new ArrayList<>();
        List<UserServiceQMS> waitingCustomerList = new ArrayList<>();

        CounterDTO counterDTO = new CounterDTO();

        if (StringUtils.isBlank(missedNumbers)) {
            missedNumbers = counter.getOrderNumber();
        }else {
            missedNumbers = missedNumbers +","+ counter.getOrderNumber();
        }

        counter.setMissedCustomerIds(missedNumbers);

        List<String> missedNumberList = Arrays.asList(missedNumbers.split(","));

        for (String number : missedNumberList) {
            UserServiceQMS missedCustomer = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(number,MISSED,counterId);
            missedCustomerList.add(missedCustomer);
        }

        //Kiểm tra xem có hàng đợi không
        if (StringUtils.isBlank(counter.getWaitingCustomerIds())) {
            counter.setCustomerId(null);
            counter.setFirstNameCustomer(null);
            counter.setLastNameCustomer(null);
            counter.setFullNameCustomer(null);
            counter.setServiceId(null);
            counter.setServiceName(null);
            counter.setOrderNumber(null);
            counterRepository.save(counter);
        } else {
            List<String> listNumberWaiting = Arrays.asList(waitingNumbers.split(","));
            String nextNumber = listNumberWaiting.get(0);
            UserServiceQMS userNext = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(nextNumber, WAITING, counterId);

            userNext.setStatus(ACTIVE);
            userNext.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userServiceQMSRepository.save(userServiceQMS);

            if (listNumberWaiting.size() == 1) counter.setWaitingCustomerIds(null);
            else counter.setWaitingCustomerIds(waitingNumbers.substring(7));

            counter.setCustomerId(userNext.getCustomerId());
            counter.setFirstNameCustomer(userNext.getFirstNameCustomer());
            counter.setLastNameCustomer(userNext.getLastNameCustomer());
            counter.setFullNameCustomer(userNext.getFullNameCustomer());
            counter.setServiceId(userNext.getServiceId());
            counter.setServiceName(userNext.getServiceName());
            counter.setOrderNumber(nextNumber);

            for (int i=1; i< listNumberWaiting.size(); i++) {
                UserServiceQMS userWaiting = userServiceQMSRepository.findUserServiceByNumberLastAndStatus(listNumberWaiting.get(i), WAITING, counterId);
                //Customer customer = customerRepository.findCustomerByUsername(userServiceQMS.getUsername());
                waitingCustomerList.add(userWaiting);
            }
        }

        counterDTO.setFullNameMember(counter.getFullNameMember());
        counterDTO.setFirstNameMember(counter.getFirstNameMember());
        counterDTO.setLastNameMember(counter.getLastNameMember());
        counterDTO.setMemberId(counter.getMemberId());

        counterDTO.setLastNameCustomer(counter.getLastNameCustomer());
        counterDTO.setFirstNameCustomer(counter.getFirstNameCustomer());
        counterDTO.setFullNameCustomer(counter.getFullNameCustomer());

        counterDTO.setWaitingCustomerList(waitingCustomerList);
        counterDTO.setMissedCustomerList(missedCustomerList);

        counterDTO.setCustomerId(counter.getCustomerId());

        counterDTO.setServiceId(counter.getServiceId());
        counterDTO.setServiceName(counter.getServiceName());

        counterDTO.setCounterId(counterId);
        counterDTO.setCounterName(counter.getName());
        counterDTO.setOrderNumber(counter.getOrderNumber());
        counterDTO.setStatus(counter.getStatus());

        counterRepository.save(counter);


        String message = StringUtils.isBlank(counter.getOrderNumber()) ? "Đã hết khách hàng đợi!" : "Khách hàng tiếp theo là: " +counter.getOrderNumber();

        return SUCCESS_RESPONSE(message, counterDTO);
    }

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPOYEE,MANAGER')")
    public ServiceResponse changeCounter() {
        return null;
    }


}
