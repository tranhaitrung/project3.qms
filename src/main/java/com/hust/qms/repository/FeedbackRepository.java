package com.hust.qms.repository;

import com.hust.qms.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback getFeedbackByTicketIdAndCustomerId(Long ticketId, Long customerId);

    Feedback getFeedbackByTicketId(Long ticketId);

    @Query(value = "select count(id) as total from feedback where score = :score", nativeQuery = true)
    Integer totalEachCoreFeedback(Integer score);

    @Query(value = "select count(id) as total, Date(created_at) as feedbackAt from feedback where score is not null and score != 0 group by Date(created_at) order by created_at desc limit 7", nativeQuery = true)
    List<Map<String, Object>> feedbackStatisticAroundSevenDay();

    @Query(value = "SELECT * FROM feedback WHERE score != 0 and (:search is null or LOCATE(:search, CONCAT_WS(', ', member_fullname, service_name))) and (:score is null or score = :score) ", nativeQuery = true)
    Page<Feedback> listFeedback(String search, Integer score, Pageable pageable);

    @Query(value = "select count(id) as totalScore, score from feedback where service_id = :serviceId and score is not null and score != 0 group by score", nativeQuery = true)
    List<Map<String,Object>> statisticScoreService(Long serviceId);
}
