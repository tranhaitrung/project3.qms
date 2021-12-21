package com.hust.qms.controller;

import com.hust.qms.entity.Feedback;
import com.hust.qms.exception.ServiceResponse;
import com.hust.qms.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin(value = "*", maxAge = 3600)
public class FeedbackController {
    @Autowired
    private FeedBackService feedBackService;

    @PostMapping("/customer")
    public ResponseEntity feedbackCustomer(@RequestBody Feedback dto) {
        ServiceResponse response = feedBackService.feedbackCustomer(dto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/get-list-score")
    public ResponseEntity<?> listScore() {
        ServiceResponse response = feedBackService.totalFeedbackScore();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/statistic-around-seven-day")
    public ResponseEntity<?> feedbackStatisticAroundSevenDay() {
        ServiceResponse response = feedBackService.feedbackStatisticAroundSevenDay();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}
