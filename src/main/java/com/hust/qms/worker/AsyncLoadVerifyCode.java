package com.hust.qms.worker;

import com.hust.qms.entity.VerifyCode;
import com.hust.qms.repository.VerifyCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

import static com.hust.qms.common.Const.Status.ACTIVE;
import static com.hust.qms.common.Const.Status.EXPIRED;

@Slf4j
@Component
public class AsyncLoadVerifyCode {

    @Autowired
    private VerifyCodeRepository verifyCodeRepository;

    @Scheduled(fixedRate = 60000) //1 phút load lại 1 lần
    protected void updateStatusVerifyCode(){
        List<VerifyCode> verifyCodeList = verifyCodeRepository.findAllByStatus(ACTIVE);

        for (VerifyCode verifyCode : verifyCodeList) {
            long currentTime = System.currentTimeMillis();
            long expiredTime = verifyCode.getExpiredAt().getTime();

            if (currentTime >= expiredTime) {
                verifyCode.setStatus(EXPIRED);
                verifyCode.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                verifyCodeRepository.save(verifyCode);
            }
        }
    }
}
