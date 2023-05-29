package com.stronger.momo.report.repository;

import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.report.entity.SelfFeedback;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface SelfFeedbackRepository extends JpaRepository<SelfFeedback, Long> {

    @Query("select s from SelfFeedback s" +
            " where  s.checkDate between :startDate and :endDate" +
            " and s.plan.id = :planId")
    Optional<SelfFeedback> getSelfFeedbackForReport(Long planId, LocalDate startDate, LocalDate endDate);
}
