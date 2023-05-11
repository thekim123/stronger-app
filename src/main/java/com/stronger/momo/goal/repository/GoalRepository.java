package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g WHERE g.owner= :owner AND g.startDate <= :today AND g.endDate >= :today")
    List<Goal> mFindTodoList(User owner, @Param("today") LocalDate today);

    @Query("SELECT g FROM Goal g WHERE :now BETWEEN g.startDate AND g.endDate AND g.owner= :user")
    List<Goal> mfindByOwnerAndDate(TeamMember user, LocalDate now);
}
