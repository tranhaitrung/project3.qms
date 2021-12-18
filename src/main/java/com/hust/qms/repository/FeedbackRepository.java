package com.hust.qms.repository;

import com.hust.qms.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback getFeedbackByTicketIdAndCustomerId(Long ticketId, Long customerId);

    Feedback getFeedbackByTicketId(Long ticketId);
}
