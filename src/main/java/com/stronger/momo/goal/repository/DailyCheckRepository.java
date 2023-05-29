package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.DailyCheck;
import com.stronger.momo.goal.entity.Goal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCheckRepository extends JpaRepository<DailyCheck, Long> {
    Optional<DailyCheck> findByGoalAndCheckDate(Goal goal, LocalDate checkDate);

    @EntityGraph(attributePaths = {"goal.id"})
    List<DailyCheck> findByGoalInAndCheckDate(List<Goal> goals, LocalDate checkDate);


    @Query("select dc " +
            "from DailyCheck dc " +
            "left join fetch dc.goal " +
            "where dc.goal.plan.id = :planId " +
            "and dc.checkDate between :startDate and :endDate "
    )
    List<DailyCheck> findByPlanId(
            @Param("planId") Long planId, LocalDate startDate, LocalDate endDate);
}
