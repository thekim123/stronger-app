package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Feedback;
import com.stronger.momo.goal.entity.SelfFeedback;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SelfFeedbackRepository extends JpaRepository<SelfFeedback, Long> {

    Optional<SelfFeedback> findByMemberAndCheckDateBetween(TeamMember member, LocalDate startDate, LocalDate endDate);

}
