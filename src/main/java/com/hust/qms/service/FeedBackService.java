package com.hust.qms.service;

import com.hust.qms.entity.Feedback;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.FeedbackRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import static com.hust.qms.exception.ServiceResponse.*;

@Service
public class FeedBackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired BaseService baseService;

    public ServiceResponse feedbackCustomer(Feedback dto) {
        Long userId = baseService.getCurrentId();
        Feedback feedback = feedbackRepository.getFeedbackByTicketIdAndCustomerId(dto.getTicketId(), userId);

        if (feedback == null) {
            return BAD_RESPONSE("Your ticket not found!");
        }

        if (feedback.getScore() != 0) {
            return BAD_RESPONSE("Your ticket has been evaluated");
        }

        feedback.setScore(dto.getScore());
        feedback.setComment(dto.getComment());
        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        feedbackRepository.save(feedback);
        return SUCCESS_RESPONSE("SUCCESS", feedback);
    }
}
