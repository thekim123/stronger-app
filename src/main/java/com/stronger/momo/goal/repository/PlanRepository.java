package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @EntityGraph(attributePaths = {"goalList", "member"})
    List<Plan> findByMember(TeamMember teamMember);

}
