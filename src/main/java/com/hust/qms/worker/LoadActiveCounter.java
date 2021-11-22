package com.hust.qms.worker;

import com.hust.qms.entity.Counter;
import com.hust.qms.entity.ServiceQMS;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.UserServiceQMSRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

import static com.hust.qms.common.Const.StatusUserService.*;

@Slf4j
@Component
public class LoadActiveCounter {
    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private UserServiceQMSRepository userServiceQMSRepository;

    @Scheduled(fixedRate = 30000) // 30 giây load lại 1 lần
    protected void LoadActiveCounter() {

        log.info("Chuyển người đợi vào các quầy đã active");

        List<UserServiceQMS> serviceQMSList = userServiceQMSRepository.findUserServiceQMSByStatus(RESERVE);

        List<Counter> counterList = counterRepository.findAllByStatus(ACTIVE);

        if (serviceQMSList != null && counterList != null && counterList.size() != 0 && serviceQMSList.size() != 0) {
            int index = 0; //Chỉ số counter có ít hàng đợi nhất

            for (UserServiceQMS userServiceQMS: serviceQMSList) {
                int indexOrderNull = -1;
                for (int i = 0; i < counterList.size(); i++) {
                    Counter counter = counterList.get(i);
                    if (StringUtils.isBlank(counter.getOrderNumber())) {
                        index = i;
                        break;
                    }
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
                    Counter counter = counterList.get(i);
                    if (StringUtils.isBlank(counter.getOrderNumber())) {
                        indexOrderNull = i;
                        break;
                    }
                }

                index = indexOrderNull > -1 ? indexOrderNull : index;

                Counter counter = counterList.get(index);

                if (StringUtils.isBlank(counter.getOrderNumber())) {
                    counter.setServiceName(userServiceQMS.getServiceName());
                    counter.setServiceId(userServiceQMS.getServiceId());

                    counter.setOrderNumber(userServiceQMS.getNumber());
                    counter.setFirstNameCustomer(userServiceQMS.getFirstNameCustomer());
                    counter.setLastNameCustomer(userServiceQMS.getLastNameCustomer());
                    counter.setFullNameCustomer(userServiceQMS.getFullNameCustomer());
                    counter.setCustomerId(userServiceQMS.getCustomerId());

                    userServiceQMS.setMemberId(counter.getMemberId());
                    userServiceQMS.setFirstNameMember(counter.getFirstNameMember());
                    userServiceQMS.setLastNameMember(counter.getLastNameMember());
                    userServiceQMS.setFullNameMember(counter.getFullNameMember());

                    userServiceQMS.setStatus(ACTIVE);

                } else {
                    String waitingIds = StringUtils.isBlank(counter.getWaitingCustomerIds()) ? userServiceQMS.getNumber() : counter.getWaitingCustomerIds() + "," + userServiceQMS.getNumber();
                    counter.setWaitingCustomerIds(waitingIds);
                    userServiceQMS.setStatus(WAITING);
                }

                userServiceQMS.setCounterId(counter.getId());
                userServiceQMS.setCounterName(counter.getName());
                userServiceQMS.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

                userServiceQMSRepository.save(userServiceQMS);
                counterRepository.save(counter);

            }


        }

    }
}
