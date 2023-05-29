package com.stronger.momo.team.repository;

import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByUserAndTeam(User user, Team team);

    List<TeamMember> findByUser(User user);

    List<TeamMember> findAllByUser(User loginUser);

    @EntityGraph(attributePaths = {"user.nickname", "user.id", "team.id", "team.name"})
    List<TeamMember> findByTeam(Team team);

    @Query("select tm.grade, tm.user.username, p " +
            "from Plan p inner join TeamMember tm on p.member.id = tm.id " +
            "where p.id = ?1")
    Optional<TeamMember> getMemberPlanAndGoals(Long planId);

}
