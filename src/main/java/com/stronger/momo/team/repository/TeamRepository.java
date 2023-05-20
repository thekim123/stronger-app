package com.stronger.momo.team.repository;

import com.stronger.momo.team.entity.Team;
import com.stronger.momo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByIsOpenTrue();

    List<Team> findByOwner(User user);

    Optional<Team> findByTeamCode(String teamCode);
}
