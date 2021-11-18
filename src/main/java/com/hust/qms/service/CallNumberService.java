package com.hust.qms.service;

import com.hust.qms.dto.CounterDTO;
import com.hust.qms.entity.Counter;
import com.hust.qms.entity.Member;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.CounterRepository;
import com.hust.qms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static com.hust.qms.common.Const.Status.ACTIVE;
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

    @PreAuthorize("@checkRolesService.authorizeRole('ADMIN,EMPOYEE,MANAGER')")
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
}
