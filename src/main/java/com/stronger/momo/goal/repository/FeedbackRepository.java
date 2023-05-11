package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
