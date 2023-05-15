package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Feedback;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByMemberAndCheckDateBetween(
            TeamMember member,
            LocalDate startDate,
            LocalDate endDate);

}
