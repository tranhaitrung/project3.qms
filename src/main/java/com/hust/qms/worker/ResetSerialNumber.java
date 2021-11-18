package com.hust.qms.worker;

import com.hust.qms.entity.Counter;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.OrderNumberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.hust.qms.common.Const.Status.INACTIVE;

@Slf4j
@Component
public class ResetSerialNumber {
    @Autowired
    private OrderNumberRepository orderNumberRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Scheduled(cron = "0 0 * * * *") //00:00 load lại dữ liệu
    protected void resetDataSerialNumber() {
        orderNumberRepository.deleteAll();
        log.info("reset table order number");

        List<Counter> counterList = counterRepository.findAll();

        for (Counter counter : counterList) {
            counter.setStatus(INACTIVE);
            counter.setCustomerId(null);
            counter.setFirstNameCustomer(null);
            counter.setLastNameCustomer(null);
            counter.setFirstNameCustomer(null);
            counter.setFirstNameMember(null);
            counter.setLastNameMember(null);
            counter.setFullNameMember(null);
            counter.setMemberId(null);
            counter.setOrderNumber(null);
            counter.setWaitingCustomerIds(null);
            counter.setServiceName(null);
            counter.setServiceId(null);
            counter.setFullNameCustomer(null);

            counterRepository.save(counter);
        }

    }
}
