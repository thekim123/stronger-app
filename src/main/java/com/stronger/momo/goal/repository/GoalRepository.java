package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
//    @Query("SELECT g FROM Goal g WHERE g.owner= :owner AND g.startDate <= :today AND g.endDate >= :today")
//    List<Goal> mFindTodoList(User owner, @Param("today") LocalDate today);

    List<Goal> findByOwner(TeamMember user);
}
