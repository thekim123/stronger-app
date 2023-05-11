package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {
    Optional<DailyCheck> findByGoalAndCheckDate(Goal goal, LocalDate checkDate);
}
