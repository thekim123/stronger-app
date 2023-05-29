package com.stronger.momo.report.repository;

import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.report.entity.Feedback;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("select f from Feedback f" +
            " where  f.checkDate between :startDate and :endDate" +
            " and f.plan.id = :planId")
    Optional<Feedback> getFeedbackForReport(
            Long planId, LocalDate startDate, LocalDate endDate);
}
