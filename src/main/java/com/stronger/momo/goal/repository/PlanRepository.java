package com.stronger.momo.goal.repository;

import com.stronger.momo.goal.entity.Plan;
import com.stronger.momo.team.entity.TeamMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    @EntityGraph(attributePaths = {"goalList", "member"})
    List<Plan> findByMember(TeamMember teamMember);

    @Query("select p from Plan p " +
            "left join fetch p.goalList " +
            "where p.member.id = :memberId")
    List<Plan> findAllByMemberId(@Param("memberId") Long memberId);



}
