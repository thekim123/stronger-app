package com.stronger.momo.plan.repository;

import com.stronger.momo.plan.entity.DailyCheck;
import com.stronger.momo.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {
    Optional<DailyCheck> findByPlanAndCheckDate(Plan plan, LocalDate checkDate);
}
