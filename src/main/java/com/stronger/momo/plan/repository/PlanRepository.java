package com.stronger.momo.plan.repository;

import com.stronger.momo.plan.entity.Plan;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query("SELECT p FROM Plan p WHERE p.owner= :owner AND p.startDate <= :today AND p.endDate >= :today")
    List<Plan> mFindTodoList(User owner, @Param("today") LocalDate today);

    @Query("SELECT p FROM Plan p WHERE :now BETWEEN p.startDate AND p.endDate AND p.owner= :user")
    List<Plan> mfindByOwnerAndDate(TeamMember user, LocalDate now);
}
