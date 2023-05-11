package com.stronger.momo.team.repository;

import com.stronger.momo.team.entity.Team;
import com.stronger.momo.team.entity.TeamMember;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByUserAndTeam(User user, Team team);

    List<TeamMember> findByUser(User user);

    List<TeamMember> findByTeam(Team team);

}
