package com.stronger.momo.plan.repository;

import com.stronger.momo.plan.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
