package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Goal;
import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    @Query("SELECT g FROM Goal g " +
            "JOIN FETCH g.plan p " +
            "JOIN FETCH p.member m " +
            "JOIN FETCH m.user " +
            "WHERE g.id = :goalId")
    Optional<Goal> findGoalWithOwner(@Param("goalId") Long goalId);

    List<Goal> findByPlan(Plan plan);


    @Query("select g " +
            "from Goal g " +
            "join fetch g.plan p " +
            "join fetch p.member m " +
            "join fetch m.user " +
            "where m.user = :loginUser "
    )
    List<Goal> getTodoList(@Param("loginUser") User loginUser);

}
