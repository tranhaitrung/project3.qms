package com.hust.qms.service;

import com.hust.qms.entity.Feedback;
import com.hust.qms.entity.UserServiceQMS;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.repository.FeedbackRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (feedback.getScore() != null) {
            if (feedback.getScore() != 0) {
                return BAD_RESPONSE("Your ticket has been evaluated");
            }
        }


        feedback.setScore(dto.getScore());
        feedback.setComment(dto.getComment());
        feedback.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        feedbackRepository.save(feedback);
        return SUCCESS_RESPONSE("SUCCESS", feedback);
    }

    public ServiceResponse totalFeedbackScore() {
        Integer totalScore1 = feedbackRepository.totalEachCoreFeedback(1);
        Integer totalScore2 = feedbackRepository.totalEachCoreFeedback(2);
        Integer totalScore3 = feedbackRepository.totalEachCoreFeedback(3);
        Integer totalScore4 = feedbackRepository.totalEachCoreFeedback(4);
        Integer totalScore5 = feedbackRepository.totalEachCoreFeedback(5);

        Map<String, Integer> totalFeedbackScore = new HashMap<>();
        totalFeedbackScore.put("score1", totalScore1);
        totalFeedbackScore.put("score2", totalScore2);
        totalFeedbackScore.put("score3", totalScore3);
        totalFeedbackScore.put("score4", totalScore4);
        totalFeedbackScore.put("score5", totalScore5);

        return SUCCESS_RESPONSE("SUCCESS", totalFeedbackScore);
    }

    public ServiceResponse feedbackStatisticAroundSevenDay(){
        List<Map<String, Object>> mapList = feedbackRepository.feedbackStatisticAroundSevenDay();
        int mapSize = mapList.size();
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 1; i < 8; i++) {
            String keyDate = "date"+i;
            String keyTotal = "total"+i;
            if (mapSize < i) {
                map.put(keyDate, 0);
                map.put(keyTotal, 0);
                continue;
            } else {
                map.put(keyTotal, mapList.get(i-1).get("total"));
                map.put(keyDate, mapList.get(i-1).get("feedbackAt"));
            }

        }
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", map);
    }

    public ServiceResponse listFeedback(String search, Integer score , int pageNo, int pageSize) {
        pageNo = pageNo > 0 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        search = StringUtils.isBlank(search) ? null : search;
        Page<Feedback> list = feedbackRepository.listFeedback(search, score, pageable);
        return ServiceResponse.SUCCESS_RESPONSE("SUCCESS", list);
    }
}
